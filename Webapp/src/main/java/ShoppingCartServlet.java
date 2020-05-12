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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/cartInfo")
public class ShoppingCartServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();



        try {
            HttpSession session = request.getSession();
            HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("cart");


            // Create connection to DB
            Connection dbcon = dataSource.getConnection();

            // query construction
            String query = "SELECT title, year FROM movies WHERE id = ?;";
            PreparedStatement statement = dbcon.prepareStatement(query);


            JsonArray movieArray = new JsonArray();

            // Iterate through cart keys and values
            Iterator it = cart.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();
                String movieId = pair.getKey();
                Integer movieQuantity = pair.getValue();

                // Query execution
                statement.setString(1, movieId);
                ResultSet resultSet = statement.executeQuery();

                // Begin construction of movie info container
                JsonObject movieObject = new JsonObject();
                movieObject.addProperty("movie_id", movieId);

                if (resultSet.next()) {
                    String movieTitle = resultSet.getString("title");
                    String movieYear = resultSet.getString("year");

                    movieObject.addProperty("movie_title", movieTitle);
                    movieObject.addProperty("movie_year", movieYear);
                    movieObject.addProperty("movie_quantity", movieQuantity);

                    // calculate price and add to movieObject
                    String moviePrice = Utility.yearToPrice(movieYear);
                    movieObject.addProperty("movie_price", moviePrice);
                }

                // add the movie object to the movie array
                movieArray.add(movieObject);


                // close the result set
                resultSet.close();
            }

            // Closers of Objects
            dbcon.close();
            statement.close();

            // send the data off to frontend
            out.write(movieArray.toString());

        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());
            out.write(jsonObject.toString());
        }

        out.close();
    }
}
