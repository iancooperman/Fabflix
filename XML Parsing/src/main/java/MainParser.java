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
                Element directorfilm = (Element) nl.item(i);

                Element director = (Element) directorfilm.getElementsByTagName("director").item(0);

                try {
                    Element dirname = (Element) director.getElementsByTagName("dirname").item(0);
                    String directorName = dirname.getTextContent();
                    System.out.println(directorName);
                }
                // most likely happens when <director? doesn't have a <dirname> child
                catch (NullPointerException e) {

                }


            }
        }
    }

}
