package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private Connection dbcon;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        try {
            dbcon = dataSource.getConnection();

            // Retrieve search query
            String q = request.getParameter("q");
            String[] words = q.split(" ");

            String sql = "SELECT id, title, year FROM movies ";

            // create inner string for AGAINST
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i] + "*";
            }
            String againstParameters = String.join(" ", words);
            System.out.println(againstParameters);
            sql += "WHERE MATCH(movies.title) AGAINST(? IN BOOLEAN MODE) ";

            PreparedStatement statement = dbcon.prepareStatement(sql);
            statement.setString(1, againstParameters);
            ResultSet resultSet = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            while(resultSet.next()) {
                // retrieve results from resultSet
                String movieId = resultSet.getString("id");
                String movieTitle = resultSet.getString("title");
                String movieYear = resultSet.getString("year");

                // place results in a new JsonObject which will be put in the jsonArray
                JsonObject movieObject = new JsonObject();
                movieObject.addProperty("movie_id", movieId);
                movieObject.addProperty("movie_title", movieTitle);
                movieObject.addProperty("movie_year", movieYear);
                jsonArray.add(movieObject);
            }

            response.setStatus(200);
            // close objects
            statement.close();
            resultSet.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());

            response.setStatus(500);
            out.write(jsonObject.toString());
        }

        // close the outgoing PrintWriter
        out.close();
    }
}
