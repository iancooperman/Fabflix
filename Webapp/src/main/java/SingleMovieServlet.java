package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

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
            String mainQuery = "SELECT id, title, year, director, rating " +
                                "FROM movies LEFT JOIN ratings ON movies.id = ratings.movieId " +
                                "WHERE id = ?";
            PreparedStatement mainStatement = dbcon.prepareStatement(mainQuery);
            mainStatement.setString(1, movieId);
            ResultSet titleYearDirectorRating = mainStatement.executeQuery();


            titleYearDirectorRating.next();
            String movie_id = titleYearDirectorRating.getString("id");
            String title = titleYearDirectorRating.getString("title");
            String year = titleYearDirectorRating.getString("year");
            String director = titleYearDirectorRating.getString("director");
            String rating = titleYearDirectorRating.getString("rating");

            if (rating == null) {
                rating = "N/A";
            }

            String genreQuery = "SELECT id, name " +
                                "FROM genres, genres_in_movies " +
                                "WHERE genres.id = genres_in_movies.genreId " +
                                    "AND genres_in_movies.movieId = ? " +
                                "ORDER BY name ASC";
            PreparedStatement genreStatement = dbcon.prepareStatement(genreQuery);
            genreStatement.setString(1, movieId);
            ResultSet genres = genreStatement.executeQuery();

//            String starsQuery = "SELECT starId, name " +
//                                "FROM stars, stars_in_movies " +
//                                "WHERE stars.id = stars_in_movies.starId " +
//                                    "AND stars_in_movies.movieId = '" + movieId + "'";

            // query to gather stars in proper order
            StringBuffer starsQuery = new StringBuffer();
            starsQuery.append("SELECT stars.id AS starId, stars.name AS name, count(stars_in_movies.movieId) ");
            starsQuery.append("FROM (SELECT starId FROM stars_in_movies WHERE movieId = ? ) AS movie_stars, stars, stars_in_movies ");
            starsQuery.append("WHERE stars.id = stars_in_movies.starId AND stars_in_movies.starId = movie_stars.starId ");
            starsQuery.append("GROUP BY stars_in_movies.starId ");
            starsQuery.append("ORDER BY count(*) DESC;");


            PreparedStatement starsStatement = dbcon.prepareStatement(starsQuery.toString());
            starsStatement.setString(1, movie_id);
            ResultSet stars = starsStatement.executeQuery();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_title", title);
            jsonObject.addProperty("movie_year", year);
            jsonObject.addProperty("movie_director", director);
            jsonObject.addProperty("movie_rating", rating);

            JsonArray genreArray = new JsonArray();
            while(genres.next()) {
                String id = genres.getString("id");
                String name = genres.getString("name");

                JsonObject genreObject = new JsonObject();
                genreObject.addProperty("genre_id", id);
                genreObject.addProperty("genre_name", name);
                genreArray.add(genreObject);
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

            // Retrieve MovieList parameters from session
            HttpSession session = request.getSession();
            HashMap<String, String> parameterMap = (HashMap<String, String>) session.getAttribute("movielistParameters");
            String qParam = (String) parameterMap.get("q");
            String titleParam = parameterMap.get("title");
            String yearParam = parameterMap.get("year");
            String directorParam = parameterMap.get("director");
            String genreParam = parameterMap.get("genre");
            String starParam = parameterMap.get("star");
            String pageParam = parameterMap.get("page");
            String limitParam = parameterMap.get("limit");
            String sortByParam = parameterMap.get("sortBy");

            JsonObject parameterObject = new JsonObject();
            parameterObject.addProperty("q", qParam);
            parameterObject.addProperty("title", titleParam);
            parameterObject.addProperty("year", yearParam);
            parameterObject.addProperty("director", directorParam);
            parameterObject.addProperty("genre", genreParam);
            parameterObject.addProperty("star", starParam);
            parameterObject.addProperty("page", pageParam);
            parameterObject.addProperty("limit", limitParam);
            parameterObject.addProperty("sortBy", sortByParam);

            jsonObject.add("movielist_parameters", parameterObject);

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
            e.printStackTrace();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }

        out.close();
    }
}
