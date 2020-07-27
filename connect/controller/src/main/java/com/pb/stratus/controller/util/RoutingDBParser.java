package com.pb.stratus.controller.util;

import com.pb.stratus.controller.routing.HistoricDB;
import com.pb.stratus.controller.routing.RoutingDbConfig;
import com.pb.stratus.core.exception.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NI010GO on 3/7/2016.
 */
public class RoutingDBParser {


    public RoutingDbConfig parseRoutingConfigXML(InputStream is) {
        Document doc = parseXml(is);
        return parseRoutingDbConfig(doc);
    }

    public RoutingDbConfig parseRoutingDBXml(InputStream is){
        Document doc = parseXml(is);
        RoutingDbConfig routingDbConfig = new RoutingDbConfig();
        parseRoutingDBXml(doc, routingDbConfig);
        return routingDbConfig;
    }

    private void parseRoutingDBXml(Document doc, RoutingDbConfig routingDbConfig) {
        Node deploymentElement = doc.getElementsByTagName("deployment").item(0);
        routingDbConfig.setDeployment(deploymentElement.getTextContent());
        Node databasesElement = doc.getElementsByTagName("databases").item(0);
        NodeList databaseList = databasesElement.getChildNodes();
        List<String> dbList = new ArrayList<String>();
        for(int i =0; i<databaseList.getLength();i++){
            if(databaseList.item(i).getNodeName().equalsIgnoreCase("database")){
                Node routingDb = databaseList.item(i);
                NodeList childNodes =routingDb.getChildNodes();
                String name=null ;
                Boolean checked = false;
                for(int j=0; j<childNodes.getLength(); j++){
                    if(childNodes.item(j).getNodeName().equalsIgnoreCase("name")){
                        name = childNodes.item(j).getTextContent();
                    }if(childNodes.item(j).getNodeName().equalsIgnoreCase("checked")){
                        checked = childNodes.item(j).getTextContent().equalsIgnoreCase("true")? true:false;
                    }

                }
                if(checked && name!=null){
                    dbList.add(name);
                }

            }
        }
        routingDbConfig.setDatabases(dbList);
        Node historicTrafficElement = doc.getElementsByTagName("historicTrafficTimeBucket").item(0);
        if(historicTrafficElement!=null){
            HistoricDB historicDB = new HistoricDB();
            List<String> options = new ArrayList<String>();
            NodeList historicOptions = historicTrafficElement.getChildNodes();
            for(int i=0; i<historicOptions.getLength(); i++) {
                Node option = historicOptions.item(i);
                if(option.getNodeName().equalsIgnoreCase("option")){
                    options.add(option.getTextContent());
                }
            }
            historicDB.setOptions(options);
            routingDbConfig.setHistoricDB(historicDB);
        }
        parseOptions(doc, routingDbConfig);
    }

    private void parseOptions(Document doc, RoutingDbConfig routingDbConfig){
        Node options = doc.getElementsByTagName("Options").item(0);
        if(options != null){
            Map<String, String> optionParams = new HashMap<String, String>();
            NodeList optionsChildNodes = options.getChildNodes();
            for(int i=0; i<optionsChildNodes.getLength(); i++) {
                Node option = optionsChildNodes.item(i);
                if(option.getNodeName().equalsIgnoreCase("param")){
                    NamedNodeMap paramAttributes = option.getAttributes();
                    if(paramAttributes.getLength() == 2){
                        String optionName = paramAttributes.getNamedItem("name").getNodeValue();
                        String optionValue = paramAttributes.getNamedItem("value").getNodeValue();
                        optionParams.put(optionName, optionValue);
                    }
                }
            }
            if(!optionParams.isEmpty()){
                routingDbConfig.setOptions(optionParams);
            }
        }
    }

    private RoutingDbConfig parseRoutingDbConfig(Document doc) {
        Node urlElement = doc.getElementsByTagName("url").item(0);
        RoutingDbConfig routingDbConfig = new RoutingDbConfig();
        routingDbConfig.setUrl(urlElement.getTextContent());
        Node nameElement = doc.getElementsByTagName("name").item(0);
        routingDbConfig.setName(nameElement.getTextContent());
        Node userName = doc.getElementsByTagName("username").item(0);
        routingDbConfig.setUser(userName.getTextContent());
        Node passwordElement = doc.getElementsByTagName("password").item(0);
        routingDbConfig.setPassword(passwordElement.getTextContent());
        parseRoutingDBXml(doc, routingDbConfig);
        return routingDbConfig;
    }

    private Document parseXml(InputStream is) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.normalize();
            return doc;
        } catch (Exception x) {
            throw new ParseException(x);
        }
    }
}
