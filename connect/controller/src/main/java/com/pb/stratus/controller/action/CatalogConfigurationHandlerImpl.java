package com.pb.stratus.controller.action;

import com.pb.stratus.controller.exception.CatalogConfigException;
import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

/**
 * This class will handle all the use cases for catalog configuration. We are
 * handling the authorization of catalog configuration and requested table.
 * 
 */
public class CatalogConfigurationHandlerImpl implements
        CatalogConfigurationHandler {
	private static final Logger log = LogManager.getLogger(
	        CatalogConfigurationHandlerImpl.class);

    /**
     * This method will check whether the catalog configuration is authorized or
     * not. If not then we throw exception and show a dialog with a message. If
     * the configuration is authorized but the requested table in case of
     * "showNearest" params is not authorized or not present in the
     * configuration, then we throw the exception and show a information dialog.
     * 
     * @param request
     * @param fileName
     * @param type
     * @param configReader
     * @return void
     * @throws IOException
     */
    public boolean handleAuthorization(HttpServletRequest request,
            String fileName, ConfigFileType type, ConfigReader configReader, AuthorizationUtils authorizationUtils)
            throws IOException {

        // boolean isConfigAuthorized = authorizationUtils.isConfigurationAuthorized(request, fileName, type);
		// CONN-36017 We need not check auth for FMN configs
        boolean isConfigAuthorized = true ;
        String queryString = request.getQueryString();
        InputStream is =null;
        int showNearestIndex = queryString.toLowerCase().indexOf("shownearest");
        if (!isConfigAuthorized && showNearestIndex > -1)
        {
            // Need to show dialog with proper message
            throw new CatalogConfigException("configNotAuthorized");
        }
        else if (!isConfigAuthorized)
        {
            return false;
        }
        else if (isConfigAuthorized && showNearestIndex > -1)
        {
            String tableName = "";
            try
            {
                tableName = getTableNameFromQueryString(queryString);
            } catch (CatalogConfigException ce)
            {
                // Need to show dialog with proper message
                throw new CatalogConfigException("invalidRequestedURL");
            }
            //CONN-16910
            try {
                is = configReader.getConfigFile(fileName, type);
                if (!isTablePresentInConfiguration(is, tableName))
                {
                    // Need to show dialog with proper message
                    throw new CatalogConfigException("requestedTableNotInFMNConfig");
                }
                }
            finally
            {
                IOUtils.closeQuietly(is);
            }

        }
        return true;
    }

    /**
     * In this method we fetch the table name from the requested query string
     * 
     * @param queryString
     * @return tableName
     */
    private String getTableNameFromQueryString(String queryString) {
        String[] splittedQuery = queryString.split("&");
        String tableName = "";
        if (splittedQuery != null)
        {
            for (int i = 0; i < splittedQuery.length; i++)
            {
                if (splittedQuery[i] != null
                        && splittedQuery[i].toLowerCase().indexOf("shownearest") > -1)
                {
                    String[] showNearestString = splittedQuery[i].split("=");
                    if (showNearestString != null && showNearestString.length > 1 && 
                            showNearestString[1] != null)
                    {
                        tableName = showNearestString[1];
                        break;
                    } 
                    else
                    {
                        throw new CatalogConfigException("invalidRequestedURL");
                    }
                }
            }
        }
        return tableName;
    }

    /**
     * In this method we are checking whether the requested table is present in
     * the configuration file or not.
     * 
     * @param is
     * @param tableName
     * @return boolean
     * @throws IOException
     */
    private boolean isTablePresentInConfiguration(InputStream is,
            String tableName) throws IOException {

        
        if (tableName == null || "".equals(tableName.trim())) {
            return false;
        }
        
        tableName  = URLDecoder.decode(tableName, "UTF-8");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder= null;
        Document doc = null;
        try
        {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
        }
        catch (ParserConfigurationException e)
        {
            log.error(e.getMessage());
        }
        catch (SAXException e)
        {
            log.error(e.getMessage());
        }

        if (doc != null)
        {
            NodeList nList = doc.getElementsByTagName("layer");
            for (int temp = 0; temp < nList.getLength(); temp++)
            {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    String fmnTableName = getTagValue("name", eElement);
                    if(fmnTableName.equals(tableName))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private static String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
                Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

}
