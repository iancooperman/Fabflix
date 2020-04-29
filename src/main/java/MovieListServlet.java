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
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;

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

            // pass parameters to session
            HttpSession session = request.getSession();
            HashMap<String, String> parameterMap = new HashMap<String, String>();
            parameterMap.put("title", titleOption);
            parameterMap.put("year", yearOption);
            parameterMap.put("director", directorOption);
            parameterMap.put("star", starOption);
            parameterMap.put("genre", genreOption);
            parameterMap.put("limit", limitOption);
            parameterMap.put("page", pageOption);
            parameterMap.put("sortBy", sortByOption);
            session.setAttribute("movielistParameters", parameterMap);

            System.out.println("REQUEST:");
            System.out.println("title: " + titleOption);
            System.out.println("year: " + yearOption);
            System.out.println("director: " + directorOption);
            System.out.println("star: " + starOption);
            System.out.println("genre: " + genreOption);
            System.out.println("limit: " + limitOption);
            System.out.println("page: " + pageOption);
            System.out.println("sortBy: " + sortByOption);


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


            // Main query construction
            StringBuffer mainQuery = new StringBuffer();
            StringBuffer rowCountQuery = new StringBuffer();
            mainQuery.append("SELECT movies.id, movies.title, movies.year, movies.director, ratings.rating ");
            rowCountQuery.append("SELECT count(*)");
            mainQuery.append("FROM movies LEFT JOIN ratings ON movies.id = ratings.movieId ");
            rowCountQuery.append("FROM movies LEFT JOIN ratings ON movies.id = ratings.movieId ");
            mainQuery.append("WHERE TRUE ");
            rowCountQuery.append("WHERE TRUE ");

            // search parameters
            mainQuery.append(titleLine);
            rowCountQuery.append(titleLine);
            mainQuery.append(yearLine);
            rowCountQuery.append(yearLine);
            mainQuery.append(directorLine);
            rowCountQuery.append(directorLine);
            mainQuery.append(starLine);
            rowCountQuery.append(starLine);
            mainQuery.append(genreLine);
            rowCountQuery.append(genreLine);

            mainQuery.append("ORDER BY " + sortBy + " ");
            rowCountQuery.append("ORDER BY " + sortBy + " ");
            mainQuery.append("LIMIT " + limit + " ");
            rowCountQuery.append("LIMIT " + limit + " ");
            mainQuery.append("OFFSET " + offset);
            rowCountQuery.append("OFFSET " + offset);
            mainQuery.append(";");
            rowCountQuery.append(";");

            System.out.println(mainQuery);

            // create statements
            Statement mainStatement = dbcon.createStatement();
            Statement rowCountStatement = dbcon.createStatement();
            Statement genreStatement = dbcon.createStatement();
            Statement starStatement = dbcon.createStatement();

            ResultSet mainResultSet = mainStatement.executeQuery(mainQuery.toString());
            ResultSet rowCountResultSet = rowCountStatement.executeQuery(rowCountQuery.toString());

            JsonObject mainJsonObject = new JsonObject();
            // get row count
            if (rowCountResultSet.next()) {
                String rowCount = rowCountResultSet.getString(1);
                mainJsonObject.addProperty("row_count", rowCount);
            }

            // Compile info
            JsonArray movieArray = new JsonArray();
            while (mainResultSet.next()) {
                String movie_id = mainResultSet.getString("movies.id");
                String movie_title = mainResultSet.getString("movies.title");
                String movie_year = mainResultSet.getString("movies.year");
                String movie_director = mainResultSet.getString("movies.director");
                String movie_rating = mainResultSet.getString("ratings.rating");

                // query to gather genres per movieId
                StringBuffer genreQuery = new StringBuffer();
                genreQuery.append("SELECT genres.id, genres.name ");
                genreQuery.append("FROM genres, genres_in_movies ");
                genreQuery.append("WHERE genres.id = genreId ");
                genreQuery.append("AND movieId = '" + movie_id + "' ");
                genreQuery.append("ORDER BY genres.name ASC ");
                genreQuery.append("LIMIT 3");

                // query to gather stars in proper order
                StringBuffer starQuery = new StringBuffer();
                starQuery.append("SELECT stars.id, stars.name, count(stars_in_movies.movieId) ");
                starQuery.append("FROM (SELECT starId FROM stars_in_movies WHERE movieId = '" + movie_id + "') AS movie_stars, stars, stars_in_movies ");
                starQuery.append("WHERE stars.id = stars_in_movies.starId AND stars_in_movies.starId = movie_stars.starId ");
                starQuery.append("GROUP BY stars_in_movies.starId ");
                starQuery.append("ORDER BY count(*) DESC ");
                starQuery.append("LIMIT 3");


                ResultSet genreResultSet = genreStatement.executeQuery(genreQuery.toString());
                ResultSet starResultSet = starStatement.executeQuery(starQuery.toString());

                // genre retrieval
                JsonArray genreArray = new JsonArray();
                while (genreResultSet.next()) {
                    String genreId = genreResultSet.getString("id");
                    String genreName = genreResultSet.getString("name");
                    JsonObject genreObject = new JsonObject();
                    genreObject.addProperty("genre_id", genreId);
                    genreObject.addProperty("genre_name", genreName);
                    genreArray.add(genreObject);
                }

                // star retrieval
                JsonArray starArray = new JsonArray();
                while(starResultSet.next()) {
                    String starId = starResultSet.getString("id");
                    String starName = starResultSet.getString("name");
                    JsonObject starObject = new JsonObject();
                    starObject.addProperty("star_id", starId);
                    starObject.addProperty("star_name", starName);
                    starArray.add(starObject);
                }

                // add all properties to JsonObject
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.add("movie_genres", genreArray);
                jsonObject.add("movie_stars", starArray);

                // Add the JsonObject to the movie array
                movieArray.add(jsonObject);

                // close the per movieId ResultSets
                genreResultSet.close();
                starResultSet.close();
            }
            mainJsonObject.add("movies", movieArray);


            // Bookkeeping things
            out.write(mainJsonObject.toString());
            response.setStatus(200);

            // Closing ResultSets
            mainResultSet.close();
            rowCountResultSet.close();
            genreStatement.close();
            starStatement.close();

            mainStatement.close();
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
        if (genreOption.equals("0")) {
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
            case "title_asc_rating_asc":
                return "title ASC, rating ASC ";
            case "title_asc_rating_desc":
                return "title ASC, rating DESC ";
            case "title_desc_rating_asc":
                return "title DESC, rating ASC ";
            case "title_desc_rating_desc":
                return "title DESC, rating DESC ";
            case "rating_asc_title_asc":
                return "rating ASC, title ASC ";
            case "rating_asc_title_desc":
                return "rating ASC, title DESC ";
            case "rating_desc_title_asc":
                return "rating DESC, title ASC ";
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
        else if (titleOption.equals("*")) {
            System.out.println("Is this getting called?");
            return "AND movies.title NOT REGEXP '^[a-zA-Z0-9].*$' ";
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
