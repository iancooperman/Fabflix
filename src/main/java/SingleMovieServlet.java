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
            String mainQuery = "SELECT title, year, director FROM movies WHERE id = '" + movieId + "'";
            Statement mainStatement = dbcon.createStatement();
            ResultSet titleYearDirector = mainStatement.executeQuery(mainQuery);

            mainStatement.close();

            String title = titleYearDirector.getString("title");
            String year = titleYearDirector.getString("year");
            String director = titleYearDirector.getString("director");

            JsonObject jsonObject = new JsonObject();
            JsonArray genreArray = new JsonArray();
            JsonArray starsArray = new JsonArray();
            jsonObject.addProperty("movie_title", title);
            jsonObject.addProperty("movie_year", year);
            jsonObject.addProperty("movie_directory", director);




            titleYearDirector.close();
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
