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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    private Connection dbcon;
    private StrongPasswordEncryptor strongPasswordEncryptor;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // initialization
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        System.out.println("Request made.");

        String email = request.getParameter("email");
        String password = request.getParameter("password");


        strongPasswordEncryptor = new StrongPasswordEncryptor();

        JsonObject responseJsonObject = new JsonObject();

        try {
            dbcon = dataSource.getConnection();

            User customer = checkCustomersTable(email, password);
            User employee = checkEmployeesTable(email, password);

            // If a corresponding customer account is found
            if (customer != null) {
                HttpSession httpSession = request.getSession();
                httpSession.setAttribute("user", customer);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            else if (employee != null) {
                HttpSession httpSession = request.getSession();
                httpSession.setAttribute("user", employee);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Incorrect email or password. Please try again.");
            }

            // Have to close DB connection here so SQLException is handled
            dbcon.close();


            // verify recaptcha checked
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            System.out.println(gRecaptchaResponse);

            RecaptchaVerifyUtils.verify(gRecaptchaResponse);

        }
        catch (Exception e) {
            e.printStackTrace();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
        }

        // Transmit data
        PrintWriter out = response.getWriter();
        out.write(responseJsonObject.toString());
        out.close();

    }


    private User checkCustomersTable(String email, String password) throws SQLException {
        User user = null;

        String customerQuery = "SELECT * FROM customers WHERE email = ?;";
        PreparedStatement customerStatement = dbcon.prepareStatement(customerQuery);
        customerStatement.setString(1, email);
        ResultSet customerRS = customerStatement.executeQuery();

        // check customers table
        if (customerRS.next()) {
            String dbFirstName = customerRS.getString("firstName");
            String dbLastName = customerRS.getString("lastName");
            String dbEmail = email;
            String dbEncryptedPassword = customerRS.getString("password");
            // check password
            if (strongPasswordEncryptor.checkPassword(password, dbEncryptedPassword)) {
                // customer login success
                user = new User(dbEmail, dbFirstName + " " + dbLastName, false);
            }
        }

        // finalization
        customerStatement.close();
        customerRS.close();
        return user;
    }

    private User checkEmployeesTable(String email, String password) throws SQLException {
        User user = null;

        String employeeQuery = "SELECT * FROM employees WHERE email = ?;";
        PreparedStatement employeeStatement = dbcon.prepareStatement(employeeQuery);
        employeeStatement.setString(1, email);
        ResultSet employeeRS = employeeStatement.executeQuery();

        if (employeeRS.next()) {
            String dbFullname = employeeRS.getString("fullname");
            String dbEmail = employeeRS.getString("email");
            String dbEncryptedPassword = employeeRS.getString("password");
            if (strongPasswordEncryptor.checkPassword(password, dbEncryptedPassword)) {
                // employee login success
                user = new User(dbEmail, dbFullname, true);
            }
        }

        // finalization
        employeeStatement.close();
        employeeRS.close();
        return user;
    }
}