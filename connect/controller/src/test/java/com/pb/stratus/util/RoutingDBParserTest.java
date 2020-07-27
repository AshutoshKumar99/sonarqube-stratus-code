package com.pb.stratus.util;
/**
 * Created by NI010GO on 3/21/2016.
 */

import com.pb.stratus.controller.routing.RoutingDbConfig;
import com.pb.stratus.controller.util.RoutingDBParser;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RoutingDBParserTest{

    RoutingDBParser parser;
    ConfigReader configReader;


    @Before
    public void setUp() throws Exception
    {
        parser = new RoutingDBParser();
        configReader = new FileSystemConfigReader(new File(
                "src" +  File.separatorChar + "test" +  File.separatorChar + "resources" +  File.separatorChar + "com" +  File.separatorChar + "pb" +  File.separatorChar + "stratus" +  File.separatorChar + "controller" +  File.separatorChar + "action")
                .getAbsolutePath(), "stratus");
    }

    @Test
    public void parseRoutingDBFromXML() throws FileNotFoundException {
        InputStream is = createInputStream("WithDBConfig");
        RoutingDbConfig routingDbConfig = parser.parseRoutingDBXml(is);
        assertEquals(routingDbConfig.getDeployment(), "On-Premise");
        assertEquals(routingDbConfig.getDatabases().size(), 1);
        assertEquals(routingDbConfig.getUrl(), null);
        assertEquals(routingDbConfig.getName(), null);
        assertEquals(routingDbConfig.getUser(), null);
        assertEquals(routingDbConfig.getPassword(), null);
    }


    @Test
    public void parseXMLWithoutDB() throws FileNotFoundException {
        InputStream is = createInputStream("WithoutDBConfig");
        RoutingDbConfig routingDbConfig = parser.parseRoutingConfigXML(is);
        assertEquals(routingDbConfig.getUrl(), "http://testhost:8080/rest/GetTravelBoundary/");
        assertEquals(routingDbConfig.getName(), "RoutingTest");
        assertEquals(routingDbConfig.getUser(), "admin");
        assertEquals(routingDbConfig.getPassword(), "admin");
        assertEquals(routingDbConfig.getDeployment(), "On-Premise");
        assertEquals(routingDbConfig.getDatabases().size(), 0);
    }

    @Test
    public void parseXMLWithDBCheckedTrue() throws FileNotFoundException {
        InputStream is = createInputStream("WithDBConfig");
        RoutingDbConfig routingDbConfig = parser.parseRoutingConfigXML(is);
        assertEquals(routingDbConfig.getUrl(), "http://testhost:8080/rest/GetTravelBoundary/");
        assertEquals(routingDbConfig.getName(), "RoutingTest");
        assertEquals(routingDbConfig.getUser(), "admin");
        assertEquals(routingDbConfig.getPassword(), "admin");
        assertEquals(routingDbConfig.getDeployment(), "On-Premise");
        assertEquals(routingDbConfig.getDatabases().size(), 1);
        assertEquals(routingDbConfig.getDatabases().get(0), "Great Britain");
    }

    @Test
    public void parseXMLWithMultipleDBCheckedTrue() throws FileNotFoundException {
        InputStream is = createInputStream("WithMultipleDBConfig");
        RoutingDbConfig routingDbConfig = parser.parseRoutingConfigXML(is);
        assertEquals(routingDbConfig.getDatabases().size(), 2);
        assertEquals(routingDbConfig.getDatabases().get(0), "Great Britain");
        assertEquals(routingDbConfig.getDatabases().get(1), "Aus");
    }
    @Test
    public void parseXMLWithMultipleDBAndOneCheckedTrue() throws FileNotFoundException {
        InputStream is = createInputStream("WithMultipleDBOneTrueConfig");
        RoutingDbConfig routingDbConfig = parser.parseRoutingConfigXML(is);
        assertEquals(routingDbConfig.getDatabases().size(), 1);
        assertEquals(routingDbConfig.getDatabases().get(0), "Great Britain");
    }

    @Test
    public void parseXMLWithHistoricData() throws FileNotFoundException {
        InputStream is = createInputStream("WithHistoricData");
        RoutingDbConfig routingDbConfig = parser.parseRoutingConfigXML(is);
        List<String> historicDbOptions = routingDbConfig.getHistoricDB().getOptions();
        assertEquals(historicDbOptions.size(), 2);
        assertEquals(historicDbOptions.get(0), "None");
        assertEquals(historicDbOptions.get(1), "AMPeak");
    }

    @Test
    public void parseXMLWithOptionsData() throws FileNotFoundException {
        InputStream is = createInputStream("WithOptionsData");
        RoutingDbConfig routingDbConfig = parser.parseRoutingConfigXML(is);
        Map<String, String> routingOptions = routingDbConfig.getOptions();
        assertEquals(routingOptions.size(), 3);
        assertEquals(routingOptions.get("Option.ReturnHoles"), "T");
        assertEquals(routingOptions.get("Option.ReturnIsland"), "T");
        assertEquals(routingOptions.get("Option.SimplificationFactor"), "0.5");
    }

    private InputStream createInputStream(String config) throws FileNotFoundException {
        String path = String.format("locateconfig/routingconfig/%s.xml", config);
        return configReader.getConfigFile(path);
    }
}
