package com.pb.stratus.controller.service;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.action.CustomTemplateTimestampAction;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This service provides facility to update callout definition xml for table template mapping.
 * Created by ju002ah on 5/30/2018.
 */
public class CustomTemplateTableMappingService {
    private String tenantName;
    private CustomerConfigDirHolder configDirHolder;
    private final static Logger logger = LogManager.getLogger(CustomTemplateTimestampAction.class);
    private ConfigReader configReader;
    StreamResult result = null;
    Transformer transformer = null;

    public CustomTemplateTableMappingService(ConfigReader configReader, CustomerConfigDirHolder customerConfigDirHolder, String tenantName) {
        this.configReader = configReader;
        this.configDirHolder = customerConfigDirHolder;
        this.tenantName = tenantName;
    }

    /**
     * This parses the xml
     *
     * @param mapConfigTableConfiguration
     */
    public void updateTemplateMapConfigTableMappingXML(MapConfigTemplateMappingInfo mapConfigTableConfiguration) throws SAXException, ParserConfigurationException, IOException, Exception {
        String filePath = Constants.CALLOUT_MAPPING_XML;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        InputStream is = null;
        try {
            is = this.configReader.getConfigFile(filePath);
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            //add new element
            addUpdateNewConfigMapping(doc, mapConfigTableConfiguration );

        } finally {
            if (is != null)
                is.close();
        }
    }

    /**
     * This parses the xml
     *
     * @param templateName
     * @param tableRef
     */
    public void updateCustomTemplateTableMappingXML(String templateName, String tableRef, boolean isDefault) throws SAXException, ParserConfigurationException, IOException, Exception {
        String filePath = Constants.CALLOUT_MAPPING_XML;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        InputStream is = null;
        try {
            is = this.configReader.getConfigFile(filePath);
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            //add new element
            addUpdateNewMapping(doc, templateName, tableRef, filePath, isDefault);

        } finally {
            if (is != null)
                is.close();
        }
    }


    /**
     * This adds or updates default table template mapping.
     *
     * @param doc
     * @param templateName
     * @param tableRef
     * @param filePath
     */
    private void addUpdateNewMapping(Document doc, String templateName, String tableRef, String filePath, Boolean isDefault) throws TransformerConfigurationException,
            TransformerException, Exception {
        NodeList tableTemplateMappings = doc.getElementsByTagName(Constants.TABLE_TEMPLATE_MAPPING_NODE);
        Element tableTemplateMapping = null;
        Element tempMap = null;
        Boolean mappingFound = false;
        Element tempMappingElement = null;

        // getting template mapping node
        tableTemplateMapping = (Element) tableTemplateMappings.item(0);
        if (tableTemplateMapping != null) {
            // iterating over mapping node
            NodeList mappings = tableTemplateMapping.getElementsByTagName(Constants.MAPPING);
            for (int i = 0; i < mappings.getLength(); i++) {
                tempMap = (Element) mappings.item(i);
                if (tempMap != null) {
                    tempMappingElement = (Element) tempMap.getElementsByTagName(Constants.TABLE).item(0);
                    if (tempMappingElement != null) {
                        Node tableName = tempMappingElement.getFirstChild();
                        if(tableName !=null) {
                            String matchTableRef = tableName.getNodeValue();
                            if (tableRef.equals(matchTableRef)) {
                                mappingFound = true;
                                if (isDefault) {
                                    Node matchTemplateNode = tempMap.getElementsByTagName(Constants.TEMPLATE).item(0).getFirstChild();
                                    matchTemplateNode.setNodeValue(templateName + Constants.FILE_EXT_TYPESCRIPT);
                                } else {
                                    tableTemplateMapping.removeChild(tempMap);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            // add a new mapping if it is not found in xml.
            if (!mappingFound && isDefault) {
                Element newMapping = doc.createElement(Constants.MAPPING);
                Element table = doc.createElement(Constants.TABLE);
                table.appendChild(doc.createTextNode(tableRef));
                Element template = doc.createElement(Constants.TEMPLATE);
                template.appendChild(doc.createTextNode(templateName + Constants.FILE_EXT_TYPESCRIPT));
                newMapping.appendChild(table);
                newMapping.appendChild(template);
                tableTemplateMapping.appendChild(newMapping);
            }
        }
        try {
            // write the content into xml file
            writeXMLFile(doc);
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * This adds or updates default table template mapping.
     *
     * @param doc
     * @param mapConfigObj
     */
    private void addUpdateNewConfigMapping(Document doc, MapConfigTemplateMappingInfo mapConfigObj) throws TransformerConfigurationException,
            TransformerException, Exception {
        NodeList mapConfigNameElements = doc.getElementsByTagName(Constants.MAPCONFIG);
        NodeList MapConfigTableTemplateMappings = doc.getElementsByTagName(Constants.MAP_CONFIG_TABLETEMPLATE_MAPPINGS);
        if(mapConfigNameElements.getLength()>0){
            for (int temp = 0; temp < mapConfigNameElements.getLength(); temp++) {
                Node nNode = mapConfigNameElements.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (nNode.getFirstChild()!= null && nNode.getFirstChild().getNodeValue().equals(mapConfigObj.getProjectName())){
                        // get the parent node and replace with the new node 
                       Node mappingNode =  nNode.getParentNode();
                        Node prev = mappingNode.getPreviousSibling();
                        if (prev != null &&
                                prev.getNodeType() == Node.TEXT_NODE &&
                                prev.getNodeValue().trim().length() == 0) {
                            mappingNode.getParentNode().removeChild(prev);
                        }
                        mappingNode.getParentNode().removeChild(mappingNode);
                        break;
                    }
                }
            }
        }
        // add new nodes only if there are templates to add
        if(mapConfigObj.gettableTemplateArray().size()>0) {
            Element MapConfigTableTemplateMappingList = doc.createElement(Constants.MAPPINGS);
            Element MapConfigTableTemplateMapping = doc.createElement(Constants.MAP_CONFIG_TABLETEMPLATE_MAPPING);
            Element MapConfig = doc.createElement(Constants.MAPCONFIG);
            MapConfig.appendChild(doc.createTextNode(mapConfigObj.getProjectName()));
            for (int mappingItem = 0; mappingItem < mapConfigObj.gettableTemplateArray().size(); mappingItem++) {
                // Now add a new mapping Node
                Element Mapping = doc.createElement(Constants.MAPPING);
                Element table = doc.createElement(Constants.TABLE);
                table.appendChild(doc.createTextNode(mapConfigObj.gettableTemplateArray().get(mappingItem).getTableName()));
                Element template = doc.createElement(Constants.TEMPLATE);
                template.appendChild(doc.createTextNode(mapConfigObj.gettableTemplateArray().get(mappingItem).getTemplateName() + Constants.FILE_EXT_TYPESCRIPT));
                Mapping.appendChild(table);
                Mapping.appendChild(template);
                MapConfigTableTemplateMappingList.appendChild(Mapping);
            }
            MapConfigTableTemplateMapping.appendChild(MapConfig);
            MapConfigTableTemplateMapping.appendChild(MapConfigTableTemplateMappingList);
            if(MapConfigTableTemplateMappings!= null) {
                MapConfigTableTemplateMappings.item(0).appendChild(MapConfigTableTemplateMapping);
            }
        }
            try {
                // write the content into xml file
                writeXMLFile(doc);
            } catch (Exception e) {
                throw e;
            }


    }

    /**
     * This updates/writes the xml with updated table template mapping
     *
     * @param doc
     * @throws Exception
     */
    private void writeXMLFile(Document doc) throws Exception {
        DOMSource source = new DOMSource(doc);
        String xmlFilePath = this.configDirHolder.getCustomerConfigDir().getAbsolutePath() +
                File.separator + this.tenantName + File.separator + Constants.CALLOUT_MAPPING_XML;
        result  = new StreamResult(new File(xmlFilePath));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        transformer = transformerFactory.newTransformer();
        //beautify xml while writing and updating
        transformer.setOutputProperty(OutputKeys.INDENT, Constants.YES);
        transformer.setOutputProperty(Constants.XML_INDENT, "2");
        transformer.transform(source, result);
    }
}
