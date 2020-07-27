package com.pb.gazetteer;

import static com.pb.gazetteer.ConfigurationFileConstants.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.pb.gazetteer.search.SearchLogic;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigurationWriterTest {
    private ConfigurationWriter configWriter;
    private PopulateParameters populateParameters;
    private static final String TENANT1 = "Tenant1";
    private static final String GAZ1 = "Gaz1";
    private String configFilePath = "src" + File.separatorChar + "test" + File.separatorChar  + "resources" + File.separatorChar  + "com" + File.separatorChar  + "pb" + File.separatorChar  + "gazetteer" + File.separatorChar  + "testConfiguration.xml";

    @Before
    public void setUp() throws Exception {
        configWriter = new ConfigurationWriter();
        populateParameters = new PopulateParameters();
        populateParameters.setTenantName(TENANT1);
        populateParameters.setGazetteerName(GAZ1);
        populateParameters.setProjection("epsg:27700");
        populateParameters.setSearchLogic(SearchLogic.DEFAULT_LOGIC);

    }

    @Test
    public void testmodifyLocateConfigXMLFile() throws Exception {
        configWriter.modifyLocateConfigXMLFile(new File(configFilePath), populateParameters);
        readXMLFile();
    }

    public void readXMLFile() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(configFilePath);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }


        NodeList instancesList = doc.getElementsByTagName(_INSTANCE_);
        for (int i = 0; i < instancesList.getLength(); i++) {
            Element instance = (Element) instancesList.item(i);
            String nameElement = getElementText(instance, "name");

            if (nameElement.equals(populateParameters.getGazetteerName())) {
                updateSRSAndSearchLogic(instance,
                        populateParameters.getProjection(), populateParameters.getSearchLogic());
                break;
            }

        }
    }

    private static void updateSRSAndSearchLogic(
            Element instance, String srs, SearchLogic searLogic) {

        Element configElement = ((Element) instance.getElementsByTagName(
                "config").item(0));

        Element proj = (Element) configElement.getElementsByTagName(
                "srs").item(0);
        Element searchLogic = (Element) configElement.getElementsByTagName(
                "searchlogic").item(0);

        assertEquals("epsg:27700", proj.getTextContent());
        assertEquals("DEFAULT_LOGIC", searchLogic.getTextContent());
    }

    private String getElementText(Element element, String elementName) {
        String elementText = "";
        NodeList nodeList = element.getElementsByTagName(elementName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element elem = (Element) nodeList.item(0);
            elementText = elem.getTextContent();
        }
        return elementText;
    }
}

