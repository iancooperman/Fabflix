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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
            String title = request.getParameter("title"); // A LIKE pattern; "" = no title specified
            String year = request.getParameter("year"); // An integer > 0 but relatively close to 2020; 0 = no year specified
            String director = request.getParameter("director"); // A LIKE pattern; "" = no star specified
            String star = request.getParameter("star"); // A LIKE pattern; "" = no star specified
            String genre = request.getParameter("genre"); // An integer corresponding to the genreId
            String limit = request.getParameter("limit"); // 10, 25, 50, or 100; default: 10
            String page = request.getParameter("page"); // An integer > 0; default: 1
            String sortBy = request.getParameter("sortBy"); // title_asc, title_desc, rating_asc, or rating_desc; default: rating_desc

            // pass parameters to session
            HttpSession session = request.getSession();
            HashMap<String, String> parameterMap = new HashMap<String, String>();
            parameterMap.put("title", title);
            parameterMap.put("year", year);
            parameterMap.put("director", director);
            parameterMap.put("star", star);
            parameterMap.put("genre", genre);
            parameterMap.put("limit", limit);
            parameterMap.put("page", page);
            parameterMap.put("sortBy", sortBy);
            session.setAttribute("movielistParameters", parameterMap);

            System.out.println("REQUEST:");
            System.out.println("title: " + title);
            System.out.println("year: " + year);
            System.out.println("director: " + director);
            System.out.println("star: " + star);
            System.out.println("genre: " + genre);
            System.out.println("limit: " + limit);
            System.out.println("page: " + page);
            System.out.println("sortBy: " + sortBy);


            // input validation
            ArrayList<String> validParameters = new ArrayList<String>();

            year = yearValidation(year);

            String sortBy = sortByValidation(sortBy);
            String limit = limitValidation(limit);
            String offset = calculateOffset(page, limit);

            String director = directorValidation(director);
            String star = starSQL(star);
            String genre = genreSQL(genre);

            // DB setup
            Connection dbcon = dataSource.getConnection();


            // Main query construction
            StringBuffer mainQuery = new StringBuffer();
            StringBuffer rowCountQuery = new StringBuffer();
            mainQuery.append("SELECT movies.id, movies.title, movies.year, movies.director, ratings.rating ");
            mainQuery.append("FROM movies LEFT JOIN ratings ON movies.id = ratings.movieId ");
            mainQuery.append("WHERE TRUE ");

            rowCountQuery.append("SELECT count(*)");
            rowCountQuery.append("FROM movies LEFT JOIN ratings ON movies.id = ratings.movieId ");
            rowCountQuery.append("WHERE TRUE ");

            // search parameters
            if (year != null) {
                validParameters.add(year);
                mainQuery.append("AND movies.year = ? ");
                rowCountQuery.append("AND movies.year = ? ");
            }

            if (director != null) {
                validParameters.add(director);
                mainQuery.append("AND movies.director LIKE ? ");
                rowCountQuery.append("AND movies.director LIKE ? ");
            }

            if (star != null) {
                validParameters.add(star);
                mainQuery.append("AND EXISTS (SELECT * FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId AND stars.name LIKE ? AND movies.id = stars_in_movies.movieId) ");
                rowCountQuery.append("AND EXISTS (SELECT * FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId AND stars.name LIKE ? AND movies.id = stars_in_movies.movieId) ");
            }

            if (genre != null) {
                validParameters.add(genre);
                mainQuery.append("AND EXISTS (SELECT * FROM genres_in_movies WHERE genreId = ? AND movies.id = genres_in_movies.movieId) ");
                rowCountQuery.append("AND EXISTS (SELECT * FROM genres_in_movies WHERE genreId = ? AND movies.id = genres_in_movies.movieId) ");
            }

            if (titleIsValid(title)) {
                if (titleIsStar(title)) {
                    mainQuery.append("AND movies.title NOT REGEXP '^[a-zA-Z0-9].*$' ");
                    rowCountQuery.append("AND movies.title NOT REGEXP '^[a-zA-Z0-9].*$' ");
                }
                else {
                    validParameters.add(title);
                    mainQuery.append("AND movies.title LIKE ? ");
                    rowCountQuery.append("AND movies.title LIKE ? ");
                }
            }

            // everything else
            validParameters.add(sortBy);
            mainQuery.append("ORDER BY ? ");
            rowCountQuery.append("ORDER BY ? ");

            validParameters.add(limit);
            mainQuery.append("LIMIT ? ");
            rowCountQuery.append("LIMIT ? ");

            validParameters.add(offset);
            mainQuery.append("OFFSET ?;");
            rowCountQuery.append("OFFSET ?;");

            System.out.println(mainQuery);

            // create statements
            PreparedStatement mainStatement = dbcon.prepareStatement(mainQuery.toString());
            PreparedStatement rowCountStatement = dbcon.prepareStatement(rowCountQuery.toString());
            Statement genreStatement = dbcon.createStatement();
            Statement starStatement = dbcon.createStatement();


            // add parameters to PreparedStatements
            for (int i = 0; i < validParameters.size(); i++) {
                mainStatement.setString(i + 1, validParameters.get(i));
                rowCountStatement.setString(i + 1, validParameters.get(i));
            }


            ResultSet mainResultSet = mainStatement.executeQuery();
            ResultSet rowCountResultSet = rowCountStatement.executeQuery();

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

                // Calculate the price of the movie based on the year
                String movie_price = Utility.yearToPrice(movie_year);

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
                jsonObject.addProperty("movie_price", movie_price);
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

    private String directorValidation(String directorOption) {
        // no director pattern specified
        if (directorOption.equals("")) {
            return null;
        }
        else {
            return directorOption;
        }
    }

    // limit input validation
    private String limitValidation(String limitOption) {
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
    private String sortByValidation(String sortByOption) {
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
    private boolean titleIsValid(String titleOption) {
        return (!titleOption.equals(""));
    }

    private boolean titleIsStar(String titleOption) {
        return (titleOption.equals("*"));
    }

    // year input validation
    private String yearValidation(String yearOption) {
        // no year specified
        if (yearOption.equals("0")) {
            return null;
        }

        int yearInt = Integer.parseInt(yearOption);
        if (yearInt > 0) {
            return yearOption;
        }
        else {
            return "2020";
        }
    }
}
