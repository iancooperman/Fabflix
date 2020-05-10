package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addStar")
public class AddStarServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private Connection dbcon;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        String starName = request.getParameter("star_name");
        String birthYear = request.getParameter("birth_year");
        if (birthYear.equals("")) {
            birthYear = null;
        }

        JsonObject jsonObject = new JsonObject();

        try {
            // initialize database connection
            dbcon = dataSource.getConnection();

            String newId = getNextId("nm");

            String insertionQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            PreparedStatement insertionStatement = dbcon.prepareStatement(insertionQuery);
            insertionStatement.setString(1, newId);
            insertionStatement.setString(2, starName);
            insertionStatement.setString(3, birthYear);
            insertionStatement.executeUpdate();

            response.setStatus(200);

            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("message", "Star added.");

            dbcon.close();

        }
        catch (Exception e) {
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());

            response.setStatus(500);
        }

        out.write(jsonObject.toString());
        out.close();
    }

    private String getNextId(String prefix) throws SQLException {
        String nextId = null;

        String idQuery = "SELECT max(id) FROM stars";
        Statement idStatement = dbcon.createStatement();
        ResultSet resultSet = idStatement.executeQuery(idQuery);

        // if statement for safety
        if (resultSet.next()) {
            String maxId = resultSet.getString("max(id)");
            int start = prefix.length();
            int idNumber = Integer.parseInt(maxId.substring(start));
            int newIdNumber = idNumber + 1;
            nextId = prefix + newIdNumber;
        }

        return nextId;
    }
}
