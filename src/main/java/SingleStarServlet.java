package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/star")
public class SingleStarServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        String starId = request.getParameter("id");

        try {
            Connection dbcon = dataSource.getConnection();

            String mainQuery = "SELECT name, birthYear " +
                                "FROM stars " +
                                "WHERE id = '" + starId + "'";
            Statement mainStatement = dbcon.createStatement();
            ResultSet nameBirthYear = mainStatement.executeQuery(mainQuery);

            JsonObject jsonObject = new JsonObject();
            if (nameBirthYear.next()) {
                String name = nameBirthYear.getString("name");
                String birthYear = nameBirthYear.getString("birthYear");
                jsonObject.addProperty("star_name", name);
                jsonObject.addProperty("star_birth_year", birthYear);
            }
            else {
                throw new Exception("lol wat?");
            }




            String movieQuery = "SELECT movieId, title " +
                                "FROM stars, stars_in_movies, movies " +
                                "WHERE stars.id = stars_in_movies.starId " +
                                    "AND stars_in_movies.movieId = movies.id " +
                                    " AND starId = '" + starId + "'";
            Statement movieStatement = dbcon.createStatement();
            ResultSet filmography = movieStatement.executeQuery(movieQuery);

            JsonArray filmographyArray = new JsonArray();
            while(filmography.next()) {
                JsonObject movieInfo = new JsonObject();
                String movieId = filmography.getString("movieId");
                String movieTitle = filmography.getString("title");
                movieInfo.addProperty("movie_id", movieId);
                movieInfo.addProperty("movie_title", movieTitle);
                filmographyArray.add(movieInfo);
            }

            jsonObject.add("filmography", filmographyArray);

            out.write(jsonObject.toString());
            response.setStatus(200);

            mainStatement.close();
            nameBirthYear.close();
            movieStatement.close();
            filmography.close();

            dbcon.close();
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
    }
}
