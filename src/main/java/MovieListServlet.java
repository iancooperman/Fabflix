package main.java;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            Connection dbcon = dataSource.getConnection();

            Statement statement = dbcon.createStatement();

            String query = "SELECT title, year, director, rating " +
                            "FROM movies, ratings " +
                             "WHERE movies.id = ratings.movieId " +
                            "ORDER BY rating DESC " +
                            "LIMIT 20;";
        }
    }
}
