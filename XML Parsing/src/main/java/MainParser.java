import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

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
        // get the root element
        Element docEle = (Element) dom.getElementsByTagName("movies").item(0);

        // get a nodelist of <directorfilms> elements
        NodeList nl = docEle.getElementsByTagName("directorfilms");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                Element directorfilmsTag = (Element) nl.item(i);

                Element directorTag = (Element) directorfilmsTag.getElementsByTagName("director").item(0);

                String directorName = null;
                try {
                    Element dirnameTag = (Element) directorTag.getElementsByTagName("dirname").item(0);
                    directorName = dirnameTag.getTextContent();
//                    System.out.println(directorName);
                }
                // most likely happens when <director> doesn't have a <dirname> child
                catch (NullPointerException e) {
                    System.out.println("<director> did not have a <dirname> as a child");
                }

                Element filmsTag = (Element) directorfilmsTag.getElementsByTagName("films").item(0);
                NodeList filmTags = filmsTag.getElementsByTagName("film");
                for (int j = 0; j < filmTags.getLength(); j++) {
                    Element filmTag = (Element) filmTags.item(j);

                    Element tTag = (Element) filmTag.getElementsByTagName("t").item(0);
                    String title = tTag.getTextContent();
                    System.out.println(directorName + ": " + title);
                }

            }
        }
    }

}
