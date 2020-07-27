package com.pb.stratus.controller.action;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Base class for reading all configurations and customizable resources
 */
public abstract class BaseConfigProviderAction extends BaseControllerAction {

    private final static Logger logger = LogManager
            .getLogger(BaseConfigProviderAction.class);

    private static final String FILE = "file";
    private static final String TYPE = "type";
    private static final String bingKeyNodeElement = "bing-key";
    private static final String maxFeaturesNodeElement = "max-features";

    private ConfigReader configReader;
    private ControllerConfiguration config;
    private AuthorizationUtils authorizationUtils;

    protected BaseConfigProviderAction(ConfigReader configReader, ControllerConfiguration config,
                                       AuthorizationUtils authorizationUtils) {
        this.configReader = configReader;
        this.config = config;
        this.authorizationUtils = authorizationUtils;
    }

    protected BaseConfigProviderAction(ConfigReader configReader) {
        this.configReader = configReader;
        this.config = null;
    }

    /**
     *
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        OutputStream os = response.getOutputStream();
        response.setContentType(getMimeType());
        String fileName = getFileName(request);
        ConfigFileType type = getType(request);

        InputStream is;
        if (ConfigFileType.CATALOG.equals(type)) {
            CatalogConfigurationHandler catalogConfigHandler = new CatalogConfigurationHandlerImpl();
            if (!catalogConfigHandler.handleAuthorization(request, fileName,
                    type, this.configReader, this.authorizationUtils)) {
                return;
            }
        }

        if ((type != null) && (!ConfigFileType.TENANTSETTINGS.equals(type))) {
            is = configReader.getConfigFile(fileName, type);
        } else {
            if (ConfigFileType.TENANTSETTINGS.equals(type)) {
                fileName = fileName.concat(".xml");
            }
            is = configReader.getConfigFile(fileName);
        }

        if (ConfigFileType.MAP.equals(type)) {
            // CONN-13923
            is = addBingKeyElement(is);
            is = addMaxFeatureSearchResultElement(is);
        }

        try {

            String headerResponseFormat = request.getHeader(Constants.RESPONSE_FORMAT_HEADER);
            logger.info("headerResponseFormat: " + headerResponseFormat);
            String acceptHdr = request.getHeader(Constants.ACCEPT_HEADER);
            if (headerResponseFormat == null && acceptHdr != null &&
                    (acceptHdr.contains(Constants.ACCEPT_HEADER_VALUE_IMAGE) ||
                            getMimeType().contains(Constants.ACCEPT_HEADER_VALUE_IMAGE))) {
                // When no RESPONSE_FORMAT_HEADER header is specified and image is acceptable for the response,
                // return image. This is used for print template icon images.
                sendImage(response, is);
            } else {
                // if RESPONSE-FORMAT header is xml, then pass XML object
                // else convert XML to JSON Object
                if (Constants.ACCEPT_HEADER_VALUE_XML.equals(headerResponseFormat)) {
                    response.setContentType(Constants.ACCEPT_HEADER_VALUE_XML);
                    IOUtils.copy(is, os);
                } else {
                    sendJSON(response, request, is);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void sendImage(HttpServletResponse response, InputStream is) throws IOException {
        response.setContentType(getMimeType());
        IOUtils.copy(is, response.getOutputStream());
    }

    protected void sendJSON(HttpServletResponse response, HttpServletRequest request, InputStream is) throws IOException {
        response.setContentType(Constants.ACCEPT_HEADER_VALUE_JSON);
        OutputStreamWriter osw = null;
        try {
            JSON objJson = new XMLSerializer().readFromStream(is);
            if (logger.isDebugEnabled()) {
                logger.debug("JSON data : " + objJson);
            }
            //write JSON data to OutputStream
            osw = new OutputStreamWriter(response.getOutputStream());
            osw.write("/*");
            objJson.write(osw);
            osw.write("*/");
        } finally {
            if (osw != null) {
                osw.flush();
                osw.close();
            }
        }
    }

    private InputStream addBingKeyElement(InputStream is) throws IOException {
        // This method takes an input stream ,then creates a document out of it .Appends a node
        // to the document.Then returns a fresh new stream created from this doc.

        ByteArrayInputStream inputStream = null;
        try {
            // get the key from the properties file based on authentication .
            String key = (authorizationUtils.isAnonymousUser()) ? config
                    .getBingServicesPublicApiKey() : config.getBingServicesPrivateApiKey();

            logger.debug("key formed is like " + key);

            // write the key to the map-config xml.
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Node mapConfig = doc.getFirstChild();
            Node node = doc.createElement(bingKeyNodeElement);
            node.setTextContent(key);
            mapConfig.appendChild(node);

            DOMSource sourceOne = new DOMSource(doc);
            StringWriter xmlAsWriter = new StringWriter();
            StreamResult resultOne = new StreamResult(xmlAsWriter);
            TransformerFactory.newInstance().newTransformer()
                    .transform(sourceOne, resultOne);
            inputStream = new ByteArrayInputStream(xmlAsWriter.toString()
                    .getBytes("UTF-8"));

        } catch (ParserConfigurationException e) {
            throw new IOException("Unable to parse the map config xml", e);
        } catch (SAXException ex) {
            throw new IOException("SAX exception occurred", ex);
        } catch (TransformerException exx) {

            throw new IOException("Transformer exception occurred", exx);
        } finally {
            is.close();
        }

        return inputStream;

    }

    private InputStream addMaxFeatureSearchResultElement(InputStream is) throws IOException {
        // This method takes an input stream ,then creates a document out of it .Appends a node
        // to the document.Then returns a fresh new stream created from this doc.
        ByteArrayInputStream inputStream = null;
        try {
            int maxNumberOfFeatures = config.getMaxFeatureSearchResults();
            // write the key to the map-config xml.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Node mapConfig = doc.getFirstChild();
            Node node = doc.createElement(maxFeaturesNodeElement);
            node.setTextContent(String.valueOf(maxNumberOfFeatures));
            mapConfig.appendChild(node);
            DOMSource sourceOne = new DOMSource(doc);
            StringWriter xmlAsWriter = new StringWriter();
            StreamResult resultOne = new StreamResult(xmlAsWriter);
            TransformerFactory.newInstance().newTransformer().transform(sourceOne, resultOne);
            inputStream = new ByteArrayInputStream(xmlAsWriter.toString().getBytes("UTF-8"));
        } catch (ParserConfigurationException e) {
            throw new IOException("Unable to parse the map config xml", e);
        } catch (SAXException ex) {
            throw new IOException("SAX exception occurred", ex);
        } catch (TransformerException exx) {
            throw new IOException("Transformer exception occurred", exx);
        } finally {
            is.close();
        }
        return inputStream;
    }

    protected abstract String getMimeType();

    /**
     * @param request
     * @return
     */
    protected String getFileName(HttpServletRequest request) {
        return request.getParameter(FILE);
    }

    /**
     * @param request
     * @return
     */
    protected ConfigFileType getType(HttpServletRequest request) {
        String fileType = request.getParameter(TYPE);
        if (fileType == null) {
            throw new IllegalRequestException("No type parameter specified");
        }
        return ConfigFileType.valueOf(fileType.toUpperCase());
    }

}
