package com.pb.gazetteer;

import com.pb.gazetteer.search.SearchLogic;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static com.pb.gazetteer.ConfigurationFileConstants.*;

/**
 * ConfigurationWriter class is used for update the srs and search logic in locate config xml file.
 */
public class ConfigurationWriter {

    private static final Logger logger = LogManager.getLogger(ConfigurationWriter.class);
    Document doc = null;

    public ConfigurationWriter() {
    }

    /**
     * Modifies the configuration file.
     *
     * @param configFile
     * @param populateParameters
     * @throws TransformerException
     */
    public void modifyLocateConfigXMLFile(File configFile,
                                          PopulateParameters populateParameters)
            throws TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            logger.info(" Writing to file: " + configFile.getAbsolutePath());
            doc = docBuilder.parse(configFile);
        } catch (ParserConfigurationException pce) {
            logger.error("configuration error occurred");
            throw new TransformerException(pce);
        } catch (IOException ioe) {
            logger.error("I/O exception occurred");
            throw new TransformerException(ioe);
        } catch (SAXException sae) {
            logger.error("unable to parse config file");
            throw new TransformerException(sae);
        }

        NodeList instancesList = doc.getElementsByTagName(_INSTANCE_);
        for (int i = 0; i < instancesList.getLength(); i++) {
            Element instance = (Element) instancesList.item(i);
            String nameElement = getElementText(instance, _NAME_);
            if (nameElement.equals(populateParameters.getGazetteerName())) {
                updateSRSAndSearchLogic(instance,
                        populateParameters.getProjection(),
                        populateParameters.getSearchLogic());
                break;
            }
        }
        //save the srs & searchLogic in the file.
        saveFile(doc, configFile);
    }

    /**
     * Updated the elements of SearchLogic to use the enum.
     *
     * @param instance
     * @param srs
     * @param searchLogic
     * @throws TransformerException
     */
    private static void updateSRSAndSearchLogic(Element instance,
                                                String srs, SearchLogic searchLogic) throws TransformerException {
        Element configElement = ((Element) instance.getElementsByTagName(
                _CONFIG_).item(0));

        Element proj = (Element) configElement.getElementsByTagName(
                _PROJECTION_ELEMENT_).item(0);
        proj.setTextContent(srs);

        Element logic = (Element) configElement.getElementsByTagName(
                _SEARCH_LOGIC_ELEMENT_).item(0);

        logic.setTextContent(searchLogic.toString());
    }

    /**
     * Gets the content of the tag.
     *
     * @param element
     * @param elementName
     * @return
     */
    private String getElementText(Element element, String elementName) {
        String elementText = "";
        NodeList nodeList = element.getElementsByTagName(elementName);
        if (nodeList != null && nodeList.getLength() > 0) {
            Element elem = (Element) nodeList.item(0);
            elementText = elem.getTextContent();
        }
        return elementText;
    }

    /**
     * Saves the file.
     * @param doc
     * @param configFile
     */
    private void saveFile(Document doc, File configFile) throws TransformerException{
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            String s = writer.toString();
            FileWriter fileWriter = new FileWriter(configFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(s);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception ex) {
            logger.error("error occurred in saving file");
            throw new TransformerException(ex);
        }
    }
}

