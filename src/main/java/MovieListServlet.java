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
import java.text.MessageFormat;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();

            Statement statement = dbcon.createStatement();

            String limit = request.getParameter("limit");

            String query = String.format("SELECT id, title, year, director, rating " +
                    "FROM movies, ratings " +
                    "WHERE movies.id = ratings.movieId " +
                    "ORDER BY rating DESC " +
                    "LIMIT %s;", limit);

            // perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieRating = rs.getString("rating");

                JsonObject jsonObject = new JsonObject();
                JsonArray genreArray = new JsonArray();
                JsonArray starArray = new JsonArray();

                Statement genreStatement = dbcon.createStatement();

                String genreQuery = "SELECT genres.name " +
                                    "FROM genres, genres_in_movies " +
                                    "WHERE genres.id = genres_in_movies.genreId " +
                                        "AND genres_in_movies.movieId = '" + movieId + "'" +
                                    "LIMIT 3";

                ResultSet genreResultSet = genreStatement.executeQuery(genreQuery);

                while (genreResultSet.next()) {
                    String genreName = genreResultSet.getString("name");
                    genreArray.add(genreName);
                }

                genreResultSet.close();

                Statement starStatement = dbcon.createStatement();

                String starQuery = "SELECT stars.id, stars.name " +
                        "FROM stars, stars_in_movies " +
                        "WHERE stars.id = stars_in_movies.starId " +
                        "AND stars_in_movies.movieId = '" + movieId + "'" +
                        "LIMIT 3";

                ResultSet starResultSet = starStatement.executeQuery(starQuery);

                while (starResultSet.next()) {
                    JsonObject starInfo = new JsonObject();
                    String starId = starResultSet.getString("id");
                    String starName = starResultSet.getString("name");
                    starInfo.addProperty("star_id", starId);
                    starInfo.addProperty("star_name", starName);

                    starArray.add(starInfo);
                }

                starResultSet.close();

                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.add("movie_genres", genreArray);
                jsonObject.add("movie_stars", starArray);
                jsonObject.addProperty("movie_rating", movieRating);

                jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);

            rs.close();
            statement.close();
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
