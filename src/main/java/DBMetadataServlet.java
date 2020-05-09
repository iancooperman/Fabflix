package main.java;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "DBMetadataServlet", urlPatterns = "/api/metadata")
public class DBMetadataServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF8");
        response.setContentType("application/json");

        try {
            // get a connection to the db
            Connection dbcon = dataSource.getConnection();

            String tableQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'moviedb';";
            Statement tableStatement = dbcon.createStatement();
            ResultSet tableResultSet = tableStatement.executeQuery(tableQuery);

            dbcon.createStatement()
        }
        catch (Exception e) {

        }
    }
}
