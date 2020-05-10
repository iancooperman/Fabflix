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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private Connection dbcon;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        try {
            dbcon = dataSource.getConnection();

            String movieTitle = request.getParameter("movie_title");
            String movieYear = request.getParameter("movie_year");
            String movieDirector = request.getParameter("movie_director");
            String movieStar = request.getParameter("movie_star");
            String movieGenre = request.getParameter("movie_genre");

            CallableStatement addMovieCall = dbcon.prepareCall("{call add_movie(?, ?, ?, ?, ?, ?)}");
            addMovieCall.setString(1, movieTitle);
            addMovieCall.setInt(2, Integer.parseInt(movieYear));
            addMovieCall.setString(3, movieDirector);
            addMovieCall.setString(4, movieStar);
            addMovieCall.setString(5, movieGenre);
            addMovieCall.registerOutParameter(6, Types.VARCHAR);

            addMovieCall.execute();

            String message = addMovieCall.getNString(6);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("message", message);

            // send status message to front end
            out.write(jsonObject.toString());
            response.setStatus(200);

            // close objects
            dbcon.close();
            addMovieCall.close();
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());

            // send status message to front end
            out.write(jsonObject.toString());
            response.setStatus(500);
        }

        out.close();
    }
}
