package main.java;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private Connection dbcon;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            dbcon = dataSource.getConnection();

            // Retrieve search query
            String q = request.getParameter("q");
            String[] words = q.split(" ");

            String sql = "SELECT id, title, year FROM movies ";

            // create inner string for AGAINST
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i] + "*";
            }
            String againstParameters = String.join(" ", words);
            System.out.println(againstParameters);
            sql += "WHERE MATCH(movies.title) AGAINST(? IN BOOLEAN MODE) ";

            PreparedStatement statement = dbcon.prepareStatement(sql);
            statement.setString(1, againstParameters);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                
            }


        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }
}
