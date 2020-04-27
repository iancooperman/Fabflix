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
            // Retrieving parameters
            String titleOption = request.getParameter("title"); // A LIKE pattern; "" = no title specified
            String yearOption = request.getParameter("year"); // An integer > 0 but relatively close to 2020; 0 = no year specified
            String directorOption = request.getParameter("director"); // A LIKE pattern; "" = no star specified
            String starOption = request.getParameter("star"); // A LIKE pattern; "" = no star specified
            String genreOption = request.getParameter("genre"); // An integer corresponding to the genreId
            String limitOption = request.getParameter("limit"); // 10, 25, 50, or 100; default: 10
            String pageOption = request.getParameter("page"); // An integer > 0; default: 1
            String sortByOption = request.getParameter("sortBy"); // title_asc, title_desc, rating_asc, or rating_desc; default: rating_desc

            // input validation
            String sortBy = sortBySQL(sortByOption);
            String limit = limitSQL(limitOption);
            String offset = calculateOffset(pageOption, limit);

            // DB setup
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();

            // Main query construction
            String mainQuery = "SELECT movies.id, movies.title, movies.year, movies.director, ratings.rating " +
                    "FROM movies, ratings " +
                    "WHERE movies.id = ratings.movieId " +
                    "LIMIT " + limit;




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

    // limit input validation
    private String limitSQL(String limitOption) {
        if (!limitOption.equals("10") && !limitOption.equals("25") && !limitOption.equals("50") && !limitOption.equals("100")) {
            return "10";
        }
        else {
            return limitOption;
        }
    }

    // self-explanatory
    private String calculateOffset(String pageOption, String limit) {
        int pageInt = Integer.parseInt(pageOption);
        int limitInt = Integer.parseInt(limit);

        // page input validation
        if (pageInt < 1) {
            pageInt = 1;
        }

        int offset = limitInt * (pageInt - 1);
        return Integer.toString(offset);
    }

    // sortBy input validation
    private String sortBySQL(String sortByOption) {
        switch (sortByOption) {
            case "title_asc":
                return "title ASC, rating ASC";
            case "title_desc":
                return "title DESC, rating DESC";
            case "rating_asc":
                return "rating ASC, title ASC";
            default:
                return "rating DESC, title DESC";
        }
    }
}
