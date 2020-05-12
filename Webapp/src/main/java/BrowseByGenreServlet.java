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

@WebServlet(name = "BrowseByGenreServlet", urlPatterns = "/api/genres")
public class BrowseByGenreServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();
            String query = "SELECT * FROM genres ORDER BY name ASC;";
            Statement statement = dbcon.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            JsonArray genreArray = new JsonArray();
            while(resultSet.next()) {
                JsonObject idNamePair = new JsonObject();
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                idNamePair.addProperty("genreId", id);
                idNamePair.addProperty("genreName", name);

                // Add the pairing to the array to be returned
                genreArray.add(idNamePair);
            }

            // Sending info to front-end
            out.write(genreArray.toString());

            // Closing DB-related objects
            dbcon.close();
            statement.close();
            resultSet.close();

            // Code green. Everything's fine.
            response.setStatus(200);
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // It is my professional opinion that now is the time to panic!
            response.setStatus(500);
        }

        // close the outward connection
        out.close();
    }
}
