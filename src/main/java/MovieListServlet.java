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

            // Retrieving parameters
            String titleOption = request.getParameter("title"); // A LIKE pattern
            String yearOption = request.getParameter("year"); // An integer > 0 but relatively close to 2020
            String directorOption = request.getParameter("director"); // A LIKE pattern
            String starOption = request.getParameter("star"); // A LIKE patten
            String genreOption = request.getParameter("genre"); // An integer corresponding to the genreId
            String limitOption = request.getParameter("limit"); // 10, 25, 50, or 100
            String pageOption = request.getParameter("page"); // An integer > 0
            String sortByOption = request.getParameter("sortBy"); // title_asc, title_desc, rating_asc, or rating_desc





            // Bookkeeping things
            out.write(jsonArray.toString());
            response.setStatus(200);

            resultSet.close();
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
