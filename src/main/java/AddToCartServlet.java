package main.java;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.lang.String;

@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/addToCart")
public class AddToCartServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            String movieId = request.getParameter("id");

            Connection dbcon = dataSource.getConnection();
            Statement titleStatement = dbcon.createStatement();
            String query = "SELECT title FROM movies WHERE id = '" +  + "'";
            ResultSet rs = titleStatement.executeQuery(query);

            HttpSession session = request.getSession();
            ArrayList<String> cart = (ArrayList<String>) session.getAttribute("cart");

            // if there is no movie list yet, create it
            // this happens in instances when no cart have been added to the cart yet
            if (cart == null) {
                cart = new ArrayList<String>();
            }

            cart.add(movieId);
            session.setAttribute("cart", cart);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("message", "");
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());
            out.write(jsonObject.toString());


            response.setStatus(500);
        }


    }
}
