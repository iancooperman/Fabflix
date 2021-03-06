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
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private boolean useConnectionPooling;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long TJ = 0;
        long TJstartTime;
        long TJendTime;
        long TSstartTime = System.nanoTime();

        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            // Retrieving parameters
            String q = request.getParameter("q");

            String title = request.getParameter("title"); // A LIKE pattern; "" = no title specified
            String year = request.getParameter("year"); // An integer > 0 but relatively close to 2020; 0 = no year specified
            String director = request.getParameter("director"); // A LIKE pattern; "" = no star specified
            String star = request.getParameter("star"); // A LIKE pattern; "" = no star specified
            String genre = request.getParameter("genre"); // An integer corresponding to the genreId
            String limit = request.getParameter("limit"); // 10, 25, 50, or 100; default: 10
            String page = request.getParameter("page"); // An integer > 0; default: 1
            String sortBy = request.getParameter("sortBy"); // title_asc, title_desc, rating_asc, or rating_desc; default: rating_desc

            // Toggle connection pooling through url parameters
            String cp = request.getParameter("cp");
            System.out.println("cp=" + cp);
            if (cp == null) {
                useConnectionPooling = true;
            }
            else useConnectionPooling = !cp.equals("false");

            // pass parameters to session
            HttpSession session = request.getSession();
            HashMap<String, String> parameterMap = new HashMap<String, String>();
            parameterMap.put("q", q);
            parameterMap.put("title", title);
            parameterMap.put("year", year);
            parameterMap.put("director", director);
            parameterMap.put("star", star);
            parameterMap.put("genre", genre);
            parameterMap.put("limit", limit);
            parameterMap.put("page", page);
            parameterMap.put("sortBy", sortBy);
            session.setAttribute("movielistParameters", parameterMap);

//            System.out.println("REQUEST:");
//            System.out.println("title: " + title);
//            System.out.println("year: " + year);
//            System.out.println("director: " + director);
//            System.out.println("star: " + star);
//            System.out.println("genre: " + genre);
//            System.out.println("limit: " + limit);
//            System.out.println("page: " + page);
//            System.out.println("sortBy: " + sortBy);


            // input validation
            ArrayList<String> stringParameters = new ArrayList<String>();

            year = yearValidation(year);

            sortBy = sortByValidation(sortBy);
            limit = limitValidation(limit);
            String offset = calculateOffset(page, limit);

            director = directorValidation(director);
            star = starSQL(star);
            genre = genreSQL(genre);

            // DB setup
            TJstartTime = System.nanoTime();
            Connection dbcon;
            if (useConnectionPooling) {
                System.out.println("Using connection pooling.");
                dbcon = dataSource.getConnection();
            }
            else {
                System.out.println("Not using connection pooling.");
                Class.forName("org.gjt.mm.mysql.Driver");
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "mypassword");
            }
            TJendTime = System.nanoTime();
            TJ += TJendTime - TJstartTime;



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
                stringParameters.add(year);
                mainQuery.append("AND movies.year = ? ");
                rowCountQuery.append("AND movies.year = ? ");
            }

            if (director != null) {
                stringParameters.add(director);
                mainQuery.append("AND movies.director LIKE ? ");
                rowCountQuery.append("AND movies.director LIKE ? ");
            }

            if (star != null) {
                stringParameters.add(star);
                mainQuery.append("AND EXISTS (SELECT * FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId AND stars.name LIKE ? AND movies.id = stars_in_movies.movieId) ");
                rowCountQuery.append("AND EXISTS (SELECT * FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId AND stars.name LIKE ? AND movies.id = stars_in_movies.movieId) ");
            }

            if (genre != null) {
                stringParameters.add(genre);
                mainQuery.append("AND EXISTS (SELECT * FROM genres_in_movies WHERE genreId = ? AND movies.id = genres_in_movies.movieId) ");
                rowCountQuery.append("AND EXISTS (SELECT * FROM genres_in_movies WHERE genreId = ? AND movies.id = genres_in_movies.movieId) ");
            }

            if (qIsValid(q)) {
                String[] words = q.split("\\W");

                // create inner string for AGAINST
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i] + "*";
                }
                String againstParameters = String.join(" ", words);
                System.out.println(againstParameters);
                stringParameters.add(againstParameters);
                String sql = "AND MATCH(movies.title) AGAINST(? IN BOOLEAN MODE) ";
                mainQuery.append(sql);
                rowCountQuery.append(sql);
            }
            else if (titleIsValid(title)) {
                if (titleIsStar(title)) {
                    mainQuery.append("AND movies.title NOT REGEXP '^[a-zA-Z0-9].*$' ");
                    rowCountQuery.append("AND movies.title NOT REGEXP '^[a-zA-Z0-9].*$' ");
                }
                else {
                    stringParameters.add(title);
                    mainQuery.append("AND movies.title LIKE ? ");
                    rowCountQuery.append("AND movies.title LIKE ? ");
                }
            }

            // everything else
            stringParameters.add(sortBy);
            mainQuery.append("ORDER BY ? ");
            rowCountQuery.append("ORDER BY ? ");

            mainQuery.append("LIMIT ? ");
            rowCountQuery.append("LIMIT ? ");

            mainQuery.append("OFFSET ?;");
            rowCountQuery.append("OFFSET ?;");

            System.out.println(mainQuery);

            // create statements
            PreparedStatement mainStatement = dbcon.prepareStatement(mainQuery.toString());
            PreparedStatement rowCountStatement = dbcon.prepareStatement(rowCountQuery.toString());



            // add parameters to PreparedStatements
            int i = 0;
            for (; i < stringParameters.size(); i++) {
                mainStatement.setString(i + 1, stringParameters.get(i));
                rowCountStatement.setString(i + 1, stringParameters.get(i));

                System.out.println(stringParameters.get(i));
            }
            mainStatement.setInt(i + 1, Integer.parseInt(limit));
            rowCountStatement.setInt(i + 1, Integer.parseInt(limit));

            i++;

            mainStatement.setInt(i + 1, Integer.parseInt(offset));
            rowCountStatement.setInt(i + 1, Integer.parseInt(offset));


            TJstartTime = System.nanoTime();
            ResultSet mainResultSet = mainStatement.executeQuery();
            ResultSet rowCountResultSet = rowCountStatement.executeQuery();
            TJendTime = System.nanoTime();
            TJ += TJendTime - TJstartTime;

            JsonObject mainJsonObject = new JsonObject();
            // get row count
            if (rowCountResultSet.next()) {
                String rowCount = rowCountResultSet.getString(1);
                mainJsonObject.addProperty("row_count", rowCount);
            }


            // compile genre query
            StringBuffer genreQuery = new StringBuffer();
            genreQuery.append("SELECT genres.id, genres.name ");
            genreQuery.append("FROM genres, genres_in_movies ");
            genreQuery.append("WHERE genres.id = genreId ");
            genreQuery.append("AND movieId = ? ");
            genreQuery.append("ORDER BY genres.name ASC ");
            genreQuery.append("LIMIT 3");
            PreparedStatement genreStatement = dbcon.prepareStatement(genreQuery.toString());

            // compile star query
            StringBuffer starQuery = new StringBuffer();
            starQuery.append("SELECT stars.id, stars.name, count(stars_in_movies.movieId) ");
            starQuery.append("FROM (SELECT starId FROM stars_in_movies WHERE movieId = ?) AS movie_stars, stars, stars_in_movies ");
            starQuery.append("WHERE stars.id = stars_in_movies.starId AND stars_in_movies.starId = movie_stars.starId ");
            starQuery.append("GROUP BY stars_in_movies.starId ");
            starQuery.append("ORDER BY count(*) DESC ");
            starQuery.append("LIMIT 3");

            PreparedStatement starStatement = dbcon.prepareStatement(starQuery.toString());


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

                // set up genre query
                genreStatement.setString(1, movie_id);

                // set up star query
                starStatement.setString(1, movie_id);

                // execute genre and star queries
                TJstartTime = System.nanoTime();
                ResultSet genreResultSet = genreStatement.executeQuery();
                ResultSet starResultSet = starStatement.executeQuery();
                TJendTime = System.nanoTime();
                TJ += TJendTime - TJstartTime;

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

            TJstartTime = System.nanoTime();
            dbcon.close();
            TJendTime = System.nanoTime();
            TJ += TJendTime - TJstartTime;
        }
        catch (Exception e) {
            e.printStackTrace();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }

        out.close();

        long TSendTime = System.nanoTime();
        long TS = TSendTime - TSstartTime;


        // Write TS and TJ to file
        String contextPath = getServletContext().getRealPath("/");
        String xmlFilePath = contextPath + "\\timelog.csv";
        System.out.println("Writing to: " + xmlFilePath);

        double TSns = Utility.nsToMs((double) TS);
        double TJns = Utility.nsToMs((double) TJ);

        FileWriter fw = new FileWriter(xmlFilePath, true);
        synchronized (fw) {
            fw.append(Double.toString(TSns) + "," + Double.toString(TJns) + "\n");
        }
        fw.close();
    }

    private boolean qIsValid(String q) {
        return (!q.equals(""));
    }

    private String genreSQL(String genreOption) {
        // no star pattern specified
        if (genreOption.equals("0")) {
            return null;
        }
        else {
            return genreOption;
        }
    }

    private String starSQL(String starOption) {
        // no star pattern specified
        if (starOption.equals("")) {
            return null;
        }
        else {
            return starOption;
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
        if (!limitOption.equals("10") && !limitOption.equals("20") && !limitOption.equals("25") && !limitOption.equals("50") && !limitOption.equals("100")) {
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
