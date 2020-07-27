package com.pb.stratus.controller.json;

import com.pb.stratus.controller.locator.Location;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class CustomJsonConverterTest extends TestCase
{
    
    private TypedConverter converter;
   
    @Override
    protected void setUp()
    {
        converter = new TypedConverter();
    }
    
    public void testToJsonObject()
    {
        Location l = new Location("1", 1.0f, "13A NEW STREET LONDON W1 4WW", 1,
                1, "EPSG:27700");
        String json = converter.toJson(l);
        String expected = "/*{\"id\": \"1\", "
                + "\"name\": \"13A NEW STREET LONDON W1 4WW\", "
                + "\"score\": 1.0, \"srs\": \"EPSG:27700\", \"x\": 1.0, " 
                + "\"y\": 1.0}*/";
        
        assertEquals(expected, json);
    }

    public void testToJsonString()
    {
        String url = "http://lon01395vm1:8080/MappingService/images/map33000";
        String json = converter.toJson(url);
        String expected = "/*\"http:\\/\\/lon01395vm1:8080\\/MappingService\\/images\\/map33000\"*/";
        assertEquals(expected, json);
    }
    
    public void testToJsonArray()
    {
        Location[] locations = new Location[5];
        locations[0] = new Location("1", 1.0f, "Address 1", 10, 50,"EPSG:27700");
        locations[1] = new Location("1", 0.9f, "Address 2", 20, 40,"EPSG:27700");
        locations[2] = new Location("1", 0.8f, "Address 3", 30, 30,"EPSG:27700");
        locations[3] = new Location("1", 0.7f, "Address 4", 40, 20,"EPSG:27700");
        locations[4] = new Location("1", 0.6f, "Address 5", 50, 10,"EPSG:27700");
        String json = converter.toJson(locations);
        String expected = "/*[{\"id\": \"1\", \"name\": \"Address 1\"," 
                + " \"score\": 1.0, \"srs\": \"EPSG:27700\", \"x\": 10.0, \"y\": 50.0},"
                + " {\"id\": \"1\", \"name\": \"Address 2\", \"score\": 0.9," 
                + " \"srs\": \"EPSG:27700\", \"x\": 20.0, \"y\": 40.0},"
                + " {\"id\": \"1\", \"name\": \"Address 3\", \"score\": 0.8," 
                + " \"srs\": \"EPSG:27700\", \"x\": 30.0, \"y\": 30.0},"
                + " {\"id\": \"1\", \"name\": \"Address 4\", \"score\": 0.7," 
                + " \"srs\": \"EPSG:27700\", \"x\": 40.0, \"y\": 20.0},"
                + " {\"id\": \"1\", \"name\": \"Address 5\", \"score\": 0.6," 
                + " \"srs\": \"EPSG:27700\", \"x\": 50.0, \"y\": 10.0}]*/";
        assertEquals(expected, json);
    }

    public void testToJsonList()
    {
        List<String> stringArray = new ArrayList<String>();
        stringArray.add("CamdenPlan");
        stringArray.add("high");
        stringArray.add("LandPlan");
        stringArray.add("LandPlanLBPHOTO");
        stringArray.add("low");
        stringArray.add("MasterMap");
        stringArray.add("MasterMapORIGINAL");
        stringArray.add("medium");
        stringArray.add("MyLayer");
        stringArray.add("MyMap");
        String json = converter.toJson(stringArray);
        String expected="/*[\"CamdenPlan\", \"high\", \"LandPlan\", " 
                + "\"LandPlanLBPHOTO\", \"low\", \"MasterMap\", " 
                + "\"MasterMapORIGINAL\", \"medium\", \"MyLayer\", \"MyMap\"]*/";
        assertEquals(expected, json);
    }     
}
