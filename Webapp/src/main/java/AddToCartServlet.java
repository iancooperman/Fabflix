package main.java;

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
import java.lang.String;
import java.util.HashMap;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/addToCart")
public class AddToCartServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            String movieId = request.getParameter("id");

            HttpSession session = request.getSession();
            HashMap<String, Integer> cart = (HashMap<String, Integer>) session.getAttribute("cart");

            // if there is no movie list yet, create it
            // this happens in instances when no cart have been added to the cart yet
            if (cart == null) {
                cart = new HashMap<String, Integer>();
            }

            // add the movie to the cart
            Utility.defaultHashMapAdd(cart, movieId, 1);
            session.setAttribute("cart", cart);

            // get the title of the movie for success message
            Connection dbcon = dataSource.getConnection();
            String query = "SELECT title FROM movies WHERE id = ?";
            PreparedStatement titleStatement = dbcon.prepareStatement(query);
            titleStatement.setString(1, movieId);
            ResultSet rs = titleStatement.executeQuery();
            String movieTitle = null;
            if (rs.next()) {
                movieTitle = rs.getString("title");
            }
            dbcon.close();
            titleStatement.close();
            rs.close();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("message", "You have added \"" + movieTitle + "\" to your shopping cart.");

            out.write(jsonObject.toString());
            response.setStatus(200);
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());

            out.write(jsonObject.toString());
            response.setStatus(500);
        }

        out.close();
    }
}
