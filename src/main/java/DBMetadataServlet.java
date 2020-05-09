package main.java;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

            // set up queries
            String tableQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'moviedb';";
            Statement tableStatement = dbcon.createStatement();
            ResultSet tableResultSet = tableStatement.executeQuery(tableQuery);

            String columnQuery = " SELECT column_name, column_type FROM information_schema.columns WHERE table_name = ?;";
            PreparedStatement columnStatement = dbcon.prepareStatement(columnQuery);

            JsonArray tableArray = new JsonArray();

            // iterate through tables
            while (tableResultSet.next()) {
                JsonObject tableObject = new JsonObject();
                String tableName = tableResultSet.getString("table_name");
                tableObject.addProperty("table_name", tableName);


                columnStatement.setString(1, tableName);
                ResultSet columnResultSet = columnStatement.executeQuery();

                JsonArray tableColumnsArray = new JsonArray();
                // iterate through columns
                while (columnResultSet.next()) {
                    JsonObject columnObject = new JsonObject();

                    String columnName = columnResultSet.getString("column_name");
                    String columnType = columnResultSet.getString("column_type");

                    columnObject.addProperty("column_name", columnName);
                    columnObject.addProperty("column_type", columnType);

                    tableColumnsArray.add(columnObject);
                }

                tableObject.add("table_columns", tableColumnsArray);

                tableArray.add(tableObject);

                // close the column result set before it's lost to garbage collection
                columnResultSet.close();
            }

            // close the table result set
            tableResultSet.close();
            dbcon.close();
            tableStatement.close();
            columnStatement.close();
        }
        catch (Exception e) {

        }
    }
}
