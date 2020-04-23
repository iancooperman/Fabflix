package main.java;

import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        String email = request.getParameter("email");
        String password = request.getParameter("password");


        try {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            String query = "SELECT password FROM customers WHERE email = '" + email + "';";
            ResultSet passwordRS = statement.executeQuery(query);

            JsonObject responseJsonObject = new JsonObject();
            if (email.equals("anteater") && password.equals("123456")) {
                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(username));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "incorrect username or password");
            }
        }
        catch (Exception e) {

        }




        response.getWriter().write(responseJsonObject.toString());
    }
}