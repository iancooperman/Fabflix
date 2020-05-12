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
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/star")
public class SingleStarServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        String starId = request.getParameter("id");

        try {
            Connection dbcon = dataSource.getConnection();

            String mainQuery = "SELECT name, birthYear " +
                                "FROM stars " +
                                "WHERE id = ?";
            PreparedStatement mainStatement = dbcon.prepareStatement(mainQuery);
            mainStatement.setString(1, starId);
            ResultSet nameBirthYear = mainStatement.executeQuery();

            JsonObject jsonObject = new JsonObject();
            if (nameBirthYear.next()) {
                String name = nameBirthYear.getString("name");
                String birthYear = nameBirthYear.getString("birthYear");
                jsonObject.addProperty("star_name", name);
                jsonObject.addProperty("star_birth_year", birthYear);
            }
            else {
                throw new Exception("lol wat?");
            }




            String movieQuery = "SELECT movieId, title " +
                                "FROM stars, stars_in_movies, movies " +
                                "WHERE stars.id = stars_in_movies.starId " +
                                    "AND stars_in_movies.movieId = movies.id " +
                                    " AND starId = ? " +
                                    " ORDER BY movies.year DESC, movies.title ASC";
            PreparedStatement movieStatement = dbcon.prepareStatement(movieQuery);
            movieStatement.setString(1, starId);
            ResultSet filmography = movieStatement.executeQuery();

            JsonArray filmographyArray = new JsonArray();
            while(filmography.next()) {
                JsonObject movieInfo = new JsonObject();
                String movieId = filmography.getString("movieId");
                String movieTitle = filmography.getString("title");
                movieInfo.addProperty("movie_id", movieId);
                movieInfo.addProperty("movie_title", movieTitle);
                filmographyArray.add(movieInfo);
            }

            jsonObject.add("filmography", filmographyArray);

            // Retrieve MovieList parameters from session
            HttpSession session = request.getSession();
            HashMap<String, String> parameterMap = (HashMap<String, String>) session.getAttribute("movielistParameters");
            String title = parameterMap.get("title");
            String year = parameterMap.get("year");
            String director = parameterMap.get("director");
            String genre = parameterMap.get("genre");
            String star = parameterMap.get("star");
            String page = parameterMap.get("page");
            String limit = parameterMap.get("limit");
            String sortBy = parameterMap.get("sortBy");

            JsonObject parameterObject = new JsonObject();
            parameterObject.addProperty("title", title);
            parameterObject.addProperty("year", year);
            parameterObject.addProperty("director", director);
            parameterObject.addProperty("genre", genre);
            parameterObject.addProperty("star", star);
            parameterObject.addProperty("page", page);
            parameterObject.addProperty("limit", limit);
            parameterObject.addProperty("sortBy", sortBy);

            jsonObject.add("movielist_parameters", parameterObject);


            out.write(jsonObject.toString());
            response.setStatus(200);

            mainStatement.close();
            nameBirthYear.close();
            movieStatement.close();
            filmography.close();

            dbcon.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
    }
}
