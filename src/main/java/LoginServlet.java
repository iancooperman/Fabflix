package main.java;

import com.google.gson.JsonObject;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        StrongPasswordEncryptor strongPasswordEncryptor = new StrongPasswordEncryptor();

        JsonObject responseJsonObject = new JsonObject();

        try {
            Connection dbcon = dataSource.getConnection();

            // compile queries
            String customerQuery = "SELECT * FROM customers WHERE email = ?;";
            PreparedStatement customerStatement = dbcon.prepareStatement(customerQuery);
            String employeeQuery = "SELECT * FROM customers WHERE email = ?;";
            PreparedStatement employeeStatement = dbcon.prepareStatement(employeeQuery);


            customerStatement.setString(1, email);
            ResultSet userRS = customerStatement.executeQuery();
            if (userRS.next()) {
                String dbFirstName = userRS.getString("firstName");
                String dbLastName = userRS.getString("lastName");
                String dbEmail = email;
                String dbEncryptedPassword = userRS.getString("password");
                if (strongPasswordEncryptor.checkPassword(password, dbEncryptedPassword)) {
                    // Login success:

                    // set this user into the session
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", new User(dbFirstName + " " + dbLastName, dbEmail, false));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                } else {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect password. Please try again.");
                }

                else {
                    // check employee table
                }
            }
            else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Incorrect email. Please try again.");
            }

            dbcon.close();
            customerStatement.close();
            employeeStatement.close();
            userRS.close();
        }
        catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
        }

        // Transmit data
        PrintWriter out = response.getWriter();
        out.write(responseJsonObject.toString());
        out.close();
    }
}