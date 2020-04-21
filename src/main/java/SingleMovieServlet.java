package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/movie")
public class SingleMovieServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        String movieId = request.getParameter("id");

        try {
            Connection dbcon = dataSource.getConnection();

            // Query database for relevant info
            String mainQuery = "SELECT title, year, director, rating " +
                                "FROM movies, ratings " +
                                "WHERE movies.id = ratings.movieId " +
                                    "AND id = '" + movieId + "'";
            Statement mainStatement = dbcon.createStatement();
            ResultSet titleYearDirectorRating = mainStatement.executeQuery(mainQuery);


            titleYearDirectorRating.next();
            String title = titleYearDirectorRating.getString("title");
            String year = titleYearDirectorRating.getString("year");
            String director = titleYearDirectorRating.getString("director");
            String rating = titleYearDirectorRating.getString("rating");

            String genreQuery = "SELECT name " +
                                "FROM genres, genres_in_movies " +
                                "WHERE genres.id = genres_in_movies.genreId " +
                                    "AND genres_in_movies.movieId = '" + movieId + "'";
            Statement genreStatement = dbcon.createStatement();
            ResultSet genres = genreStatement.executeQuery(genreQuery);

            String starsQuery = "SELECT starId, name " +
                                "FROM stars, stars_in_movies " +
                                "WHERE stars.id = stars_in_movies.starId " +
                                    "AND stars_in_movies.movieId = '" + movieId + "'";
            Statement starsStatement = dbcon.createStatement();
            ResultSet stars = starsStatement.executeQuery(starsQuery);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_title", title);
            jsonObject.addProperty("movie_year", year);
            jsonObject.addProperty("movie_director", director);
            jsonObject.addProperty("movie_rating", rating);

            JsonArray genreArray = new JsonArray();
            while(genres.next()) {
                genreArray.add(genres.getString(1));
            }
            jsonObject.add("genres", genreArray);

            JsonArray starsArray = new JsonArray();
            while(stars.next()) {
                // Create a new JsonObject to store star id and name
                JsonObject starInfo = new JsonObject();
                starInfo.addProperty("star_id", stars.getString("starId"));
                starInfo.addProperty("star_name", stars.getString("name"));
                starsArray.add(starInfo);
            }
            jsonObject.add("stars", starsArray);

            // write JSON string to output
            out.write(jsonObject.toString());
            response.setStatus(200);

            mainStatement.close();
            genreStatement.close();
            genres.close();
            starsStatement.close();
            stars.close();
            titleYearDirectorRating.close();
            dbcon.close();
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }

        out.close();
    }
}
