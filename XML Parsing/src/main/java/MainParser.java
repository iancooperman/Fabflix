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
        catcodeToGenreName.put("Susp", "Thriller");
        catcodeToGenreName.put("CnR", "Crime");
        catcodeToGenreName.put("Dram", "Drama");
        catcodeToGenreName.put("West", "Western");
        catcodeToGenreName.put("Myst", "Mystery");
        catcodeToGenreName.put("S.F.", "Sci-Fi");
        catcodeToGenreName.put("Advt", "Adventure");
        catcodeToGenreName.put("Horr", "Horror");
        catcodeToGenreName.put("Romt", "Romance");
        catcodeToGenreName.put("Comd", "Comedy");
        catcodeToGenreName.put("Musc", "Musical");
        catcodeToGenreName.put("Docu", "Documentary");
        catcodeToGenreName.put("Porn", "Pornography");
        catcodeToGenreName.put("Noir", "Noir");

        catcodeToGenreName.put("Ctxx", "Uncategorized");
        catcodeToGenreName.put("Actn", "Action");
        catcodeToGenreName.put("Camp now", "Camp");
        catcodeToGenreName.put("Disa", "Disaster");
        catcodeToGenreName.put("Epic", "Epic");
        catcodeToGenreName.put("ScFi", "Sci-Fi");
        catcodeToGenreName.put("Cart", "Animation");
        catcodeToGenreName.put("Surl", "sureal");
        catcodeToGenreName.put("AvGa", "Avant Garde");
        catcodeToGenreName.put("Hist", "History");

        catcodeToGenreName.put("Romt Comd", "Romantic Comedy");




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

                    Element tTag = (Element) filmTag.getElementsByTagName("t").item(0);
                    String title = tTag.getTextContent();
//                    System.out.println(director + ": " + title);

                    ArrayList genreNames = new ArrayList();
                    NodeList catTags = filmTag.getElementsByTagName("cat");
                    for (int k = 0; k < catTags.getLength(); k++) {
                        Element catTag = (Element) catTags.item(k);
                        String genreName = catTag.getTextContent();
                        System.out.println(genreName);
                    }

                }

            }
        }
    }

}
