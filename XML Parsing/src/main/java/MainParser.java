import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainParser {
    private Document dom;


    public static void main(String[] args) {
        // create an instance
        MainParser mp = new MainParser();

        mp.run();
    }

    public MainParser() {

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

                    if (year.length() < 4) {

                    }


                    // collect genres
                    ArrayList genreNames = new ArrayList();
                    NodeList catTags = filmTag.getElementsByTagName("cat");
                    for (int k = 0; k < catTags.getLength(); k++) {
                        Element catTag = (Element) catTags.item(k);
                        String catcode = catTag.getTextContent().toLowerCase().trim();

                        if (catcodeToGenreName.containsKey(catcode)) {
                            genreNames.add(catcodeToGenreName.get(catcode));
                        }
                    }

                }

            }
        }
    }

}
