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

            // compile and execute queries
            String customerQuery = "SELECT * FROM customers WHERE email = ?;";
            PreparedStatement customerStatement = dbcon.prepareStatement(customerQuery);
            customerStatement.setString(1, email);
            ResultSet customerRS = customerStatement.executeQuery();

            String employeeQuery = "SELECT * FROM employees WHERE email = ?;";
            PreparedStatement employeeStatement = dbcon.prepareStatement(employeeQuery);
            employeeStatement.setString(1, email);
            ResultSet employeeRS = employeeStatement.executeQuery();

            if (customerRS.next()) {
                String dbFirstName = customerRS.getString("firstName");
                String dbLastName = customerRS.getString("lastName");
                String dbEmail = email;
                String dbEncryptedPassword = customerRS.getString("password");
                if (strongPasswordEncryptor.checkPassword(password, dbEncryptedPassword)) {
                    // Login success:

                    // set this user into the session
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", new User(dbEmail, dbFirstName + " " + dbLastName, false));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                } else {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Incorrect password. Please try again.");
                }
            }
            else if (employeeRS.next()) {
                String dbFullname = employeeRS.getString("fullname");
                String dbEmail = employeeRS.getString("email");
                String dbEncryptedPassword = employeeRS.getString("password");
                if(strongPasswordEncryptor.checkPassword(password, dbEncryptedPassword)) {
                    HttpSession httpSession = request.getSession();
                    httpSession.setAttribute("user", new User(dbEmail, dbFullname, true));

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
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
            customerRS.close();
            employeeRS.close();
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