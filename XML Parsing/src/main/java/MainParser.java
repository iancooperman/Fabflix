import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MainParser {
    private DataSource dataSource;
    private Connection dbcon;
    private Document dom;

    private int maxMovieId;


    public static void main(String[] args) {
        // create an instance
        MainParser mp = new MainParser();

        mp.run();
    }

    public MainParser() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbcon = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", DBLoginInfo.username, DBLoginInfo.password);
            System.out.println("Connected");

            // get the max movieId for easy incrementation and retrieval
            String maxMovieIdQuery = "SELECT max(id) FROM movies";
            Statement maxMovieIdStatement = dbcon.createStatement();
            ResultSet maxMovieIdSet = maxMovieIdStatement.executeQuery(maxMovieIdQuery);

            if (maxMovieIdSet.next()) {
                maxMovieId = Integer.parseInt(maxMovieIdSet.getString("max(id)").substring(2));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void run() {
        parseXMLFile();
        parseDocument();

    }

    private void parseXMLFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            dom = db.parse("stanford-movies/mains243.xml");
        }
        catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        catch (SAXException se) {
            se.printStackTrace();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        // set up mapping between category codes and genre names
        HashMap<String, String> catcodeToGenreName = new HashMap<String, String>();
        catcodeToGenreName.put("susp", "Thriller");
        catcodeToGenreName.put("cnr", "Crime");
        catcodeToGenreName.put("dram", "Drama");
        catcodeToGenreName.put("west", "Western");
        catcodeToGenreName.put("myst", "Mystery");
        catcodeToGenreName.put("s.f.", "Sci-Fi");
        catcodeToGenreName.put("advt", "Adventure");
        catcodeToGenreName.put("horr", "Horror");
        catcodeToGenreName.put("romt", "Romance");
        catcodeToGenreName.put("comd", "Comedy");
        catcodeToGenreName.put("musc", "Musical");
        catcodeToGenreName.put("docu", "Documentary");
        catcodeToGenreName.put("porn", "Pornography");
        catcodeToGenreName.put("noir", "Noir");

        catcodeToGenreName.put("ctxx", "Uncategorized");
        catcodeToGenreName.put("actn", "Action");
        catcodeToGenreName.put("camp now", "Camp");
        catcodeToGenreName.put("disa", "Disaster");
        catcodeToGenreName.put("epic", "Epic");
        catcodeToGenreName.put("scfi", "Sci-Fi");
        catcodeToGenreName.put("cart", "Animation");
        catcodeToGenreName.put("surl", "Surreal");
        catcodeToGenreName.put("avga", "Avant Garde");
        catcodeToGenreName.put("hist", "History");

        catcodeToGenreName.put("romt comd", "Romantic Comedy");
        catcodeToGenreName.put("fant", "Fantasy");
        catcodeToGenreName.put("fant ", "Fantasy");
        catcodeToGenreName.put("biop", "Biographical Picture");
        catcodeToGenreName.put("scif", "Sci-Fi");
        catcodeToGenreName.put("faml", "Family");
        catcodeToGenreName.put("cnrb", "Crime");
        catcodeToGenreName.put("road", "Genre");
        catcodeToGenreName.put("docu dram", "Docudrama");
        catcodeToGenreName.put("act", "Action");
        catcodeToGenreName.put("anti-dram", "Drama");
        catcodeToGenreName.put("romt dram", "Romance");
        catcodeToGenreName.put("surr", "Surreal");
        catcodeToGenreName.put("dram docu", "Docudrama");
        catcodeToGenreName.put("romt. comd", "Romantic Comedy");
        catcodeToGenreName.put("sati", "Satire");
        catcodeToGenreName.put("axtn", "Action");
        catcodeToGenreName.put("hor", "Horror");
        catcodeToGenreName.put("comdx", "Comedy");
        catcodeToGenreName.put("biopx", "Biographical Picture");
        catcodeToGenreName.put("biopp", "Biographical Picture");
        catcodeToGenreName.put("kinky", "Pornography");
        catcodeToGenreName.put("sports", "Sports");





        // get the root element
        Element docEle = (Element) dom.getElementsByTagName("movies").item(0);

        // get a nodelist of <directorfilms> elements
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element directorfilmsTag = (Element) nl.item(i);

                // collect director
                Element directorTag = (Element) directorfilmsTag.getElementsByTagName("director").item(0);

                String director = null;
                try {
                    Element dirnameTag = (Element) directorTag.getElementsByTagName("dirname").item(0);
                    director = dirnameTag.getTextContent();
//                    System.out.println(directorName);
                }
                // most likely happens when <director> doesn't have a <dirname> child
                catch (NullPointerException e) {
                    System.out.println("<director> did not have a <dirname> as a child");
                    continue;
                }

                Element filmsTag = (Element) directorfilmsTag.getElementsByTagName("films").item(0);
                NodeList filmTags = filmsTag.getElementsByTagName("film");
                for (int j = 0; j < filmTags.getLength(); j++) {
                    Element filmTag = (Element) filmTags.item(j);

                    // collect title
                    Element tTag = (Element) filmTag.getElementsByTagName("t").item(0);
                    String title = tTag.getTextContent();
//                    System.out.println(director + ": " + title);

                    // collect year
                    Element yearTag = (Element) filmTag.getElementsByTagName("year").item(0);
                    Element releasedTag = (Element) yearTag.getElementsByTagName("released").item(0);
                    String year;
                    if (releasedTag != null) {
                        year = releasedTag.getTextContent();
                    }
                    else {
                        year = yearTag.getTextContent();
                    }


                    // if year length if greater than 4, it probably picked up the rereleased tag
                    if (year.length() > 4) {
                        year = year.substring(0, 4);
                    }

                    // if the year is too short to make sense, give up. Year is a required attribute for a movie
                    if (year.length() < 4) {
                        System.out.println(director + "'s \"" + title + "\" from year " + year + " doesn't compute.");
                        continue;
                    }


                    // collect genres
                    ArrayList<String> genreNames = new ArrayList<String>();
                    NodeList catTags = filmTag.getElementsByTagName("cat");
                    for (int k = 0; k < catTags.getLength(); k++) {
                        Element catTag = (Element) catTags.item(k);
                        String catcode = catTag.getTextContent().toLowerCase().trim();

                        if (catcodeToGenreName.containsKey(catcode)) {
                            genreNames.add(catcodeToGenreName.get(catcode));
                        }
                    }

                    addMovieToDB(title, year, director, genreNames);
                }
            }
        }
    }

    private void addMovieToDB(String title, String year, String director, ArrayList<String> genreNames) {


        try {
            // prepare movie info for insertion into DB
            String movieInsertQuery = "INSERT INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
            PreparedStatement movieInsertStatement = dbcon.prepareStatement(movieInsertQuery);

            // increment maxMovieId/create new movieId;
            maxMovieId++;
            String newMovieId = "tt" + String.format("%7s", Integer.toString(maxMovieId)).replace(' ', '0');

            movieInsertStatement.setString(1, newMovieId);
            movieInsertStatement.setString(2, title);
            movieInsertStatement.setInt(3, Integer.parseInt(year));
            movieInsertStatement.setString(4, director);
            movieInsertStatement.executeUpdate();

            movieInsertStatement.close();


            for (String genreName : genreNames) {
                int genreId = getGenreId(genreName);
                // if the genre does not exist in the DB
                if (genreId == -1) {
                    genreId = addGenreToDB(genreName);
                }

                linkGenreToMovie(genreId, newMovieId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getGenreId(String genreName) {
        try {
            String query = "SELECT * FROM genres WHERE name = ?";
            PreparedStatement preparedStatement = dbcon.prepareStatement(query);
            preparedStatement.setString(1, genreName);
            ResultSet resultSet = preparedStatement.executeQuery();

            // if there's a matching record, return the genreId;
            if (resultSet.next()) {
                int genreId = resultSet.getInt("genreId");

                preparedStatement.close();
                resultSet.close();
                return genreId;
            }
            // otherwise, return -1 to let the calling function know there's no matching record
            else {
                preparedStatement.close();
                resultSet.close();

                return -1;
            }
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return -1;
        }
    }

    // INCOMPLETE; add genre to genres table and return the new genreId
    private int addGenreToDB(String genreName) {
        try {
            String query = "SELECT max(id) FROM genres";
            Statement statement = dbcon.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                int maxGenreId = resultSet.getInt("genreId");
                int newGenreId = maxGenreId + 1;

                String insertQuery = "INSERT INTO genres (id, name) VALUES (?, ?);";
                PreparedStatement insertStatement = dbcon.prepareStatement(insertQuery);
                insertStatement.setInt(1, newGenreId);
                insertStatement.setString(2, genreName);
                insertStatement.executeUpdate();

                insertStatement.close();
            }

            statement.close();
            resultSet.close();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

    }

    private void linkGenreToMovie(int genreId, String movieId) {
        try {
            String query = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
            PreparedStatement preparedStatement = dbcon.prepareStatement(query);
            preparedStatement.setInt(1, genreId);
            preparedStatement.setString(2, movieId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
