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
            String titleLine = titleSQL(titleOption);
            String yearLine = yearSQL(yearOption);
            String directorLine = directorSQL(directorOption);
            String starLine = starSQL(starOption);
            String genreLine = genreSQL(genreOption);

            // DB setup
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();

            // Main query construction
            StringBuffer mainQuery = new StringBuffer();
            mainQuery.append("SELECT movies.id, movies.title, movies.year, movies.director, ratings.rating ");
            mainQuery.append("FROM movies, ratings ");
            mainQuery.append("WHERE movies.id = ratings.movieId ");

            // search parameters
            mainQuery.append(titleLine);
            mainQuery.append(yearLine);
            mainQuery.append(directorLine);
            mainQuery.append(starLine);
            mainQuery.append(genreLine);

            mainQuery.append("ORDER BY " + sortBy + " ");
            mainQuery.append("LIMIT " + limit + " ");
            mainQuery.append("OFFSET " + offset);

            ResultSet mainResultSet = statement.executeQuery(mainQuery.toString());


            // Compile info
            JsonArray jsonArray = new JsonArray();
            while (mainResultSet.next()) {
                String movie_id = mainResultSet.getString("movies.id");
                String movie_title = mainResultSet.getString("movies.title");
                String movie_year = mainResultSet.getString("movies.year");
                String movie_director = mainResultSet.getString("movies.director");
                String movie_rating = mainResultSet.getString("ratings.rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }




            // Bookkeeping things
            out.write(jsonArray.toString());
            response.setStatus(200);

            // Closing ResultSets
            mainResultSet.close();

            statement.close();
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

    private String genreSQL(String genreOption) {
        // no star pattern specified
        if (genreOption.equals("")) {
            return "";
        }
        else {
            return "AND EXISTS (SELECT * FROM genres_in_movies WHERE genreId = '" + genreOption + "' AND movies.id = genres_in_movies.movieId) ";
        }
    }

    private String starSQL(String starOption) {
        // no star pattern specified
        if (starOption.equals("")) {
            return "";
        }
        else {
            return "AND EXISTS (SELECT * FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId AND stars.name LIKE '" + starOption + "' AND movies.id = stars_in_movies.movieId) ";
        }
    }

    private String directorSQL(String directorOption) {
        // no director pattern specified
        if (directorOption.equals("")) {
            return "";
        }
        else {
            return "AND movies.director LIKE '" + directorOption + "' ";
        }
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
                return "title ASC, rating ASC ";
            case "title_desc":
                return "title DESC, rating DESC ";
            case "rating_asc":
                return "rating ASC, title ASC ";
            default:
                return "rating DESC, title DESC ";
        }
    }

    // title input validation
    private String titleSQL(String titleOption) {
        // no title pattern specified
        if (titleOption.equals("")) {
            return "";
        }

        return "AND movies.title LIKE '" + titleOption + "' ";
    }

    // year input validation
    private String yearSQL(String yearOption) {
        // no year specified
        if (yearOption.equals("0")) {
            return "";
        }

        String sql = "AND movies.year = '";
        int yearInt = Integer.parseInt(yearOption);
        if (yearInt > 0) {
            sql += yearInt + "' ";
        }
        else {
            sql += "2020' ";
        }
        return sql;
    }
}
