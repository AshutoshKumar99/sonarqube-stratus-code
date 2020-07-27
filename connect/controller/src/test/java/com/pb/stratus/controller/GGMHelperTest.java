package com.pb.stratus.controller;


import com.pb.gazetteer.webservice.Address;
import com.pb.stratus.controller.geocoder.GGMHelper;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ar009sh on 28-09-2015.
 */
public class GGMHelperTest {

    GGMHelper helper ;

    @Before
    public void setUp() throws Exception
    {
    helper = GGMHelper.getInstance();
    }

    @Test
    public void TestParseAddressWhenMainAddressPresent()
    {
        String candidate =   "{\n" +
                "          \"precisionLevel\": 10,\n" +
                "          \"formattedStreetAddress\": \"HN 3 ,Savitri Bhavan\",\n" +
                "          \"formattedLocationAddress\": \"Pathankot,punjab\",\n" +
                "          \"precisionCode\": \"G3\",\n" +
                "          \"sourceDictionary\": \"0\",\n" +
                "          \"geometry\": {\n" +
                "            \"type\": \"Point\",\n" +
                "            \"coordinates\": [\n" +
                "              -1.60183,\n" +
                "              54.95937\n" +
                "            ],\n" +
                "            \"crs\": {\n" +
                "              \"type\": \"name\",\n" +
                "              \"properties\": {\n" +
                "                \"name\": \"epsg:4326\"\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          \"address\": {\n" +
                "            \"mainAddressLine\": \"SAVITRIBHAVAN\",\n" +
                "            \"addressLastLine\": \"PTK,PB\",\n" +
                "            \"placeName\": \"\",\n" +
                "            \"areaName1\": \"areaName1\",\n" +
                "            \"areaName2\": \"areaName3\",\n" +
                "            \"areaName3\": \"areaName3\",\n" +
                "            \"areaName4\": \"areaName4\",\n" +
                "            \"postCode1\": \"\",\n" +
                "            \"postCode2\": \"\",\n" +
                "            \"country\": \"GBR\",\n" +
                "            \"addressNumber\": \"2313\",\n" +
                "            \"streetName\": \"MyStreet\",\n" +
                "            \"customFields\": {}\n" +
                "          },\n" +
                "          \"ranges\": []\n" +
                "        }";

        Address address = helper.parseAddress(JSONObject.fromObject(candidate));

        Assert.assertEquals(address.getAddress(),"SAVITRIBHAVAN,PTK,PB");
        Assert.assertEquals(String.valueOf(address.getX()),"-1.60183");
        Assert.assertEquals(String.valueOf(address.getY()),"54.95937");
        Assert.assertEquals(address.getSrs(),"epsg:4326");
    }

    @Test
    public void TestParseAddressWhenMainAddressNotPresent()
    {
        String candidate =   "{\n" +
                "          \"precisionLevel\": 10,\n" +
                "          \"formattedStreetAddress\": \"\",\n" +
                "          \"formattedLocationAddress\": \"\",\n" +
                "          \"precisionCode\": \"G3\",\n" +
                "          \"sourceDictionary\": \"0\",\n" +
                "          \"geometry\": {\n" +
                "            \"type\": \"Point\",\n" +
                "            \"coordinates\": [\n" +
                "              -1.60183,\n" +
                "              54.95937\n" +
                "            ],\n" +
                "            \"crs\": {\n" +
                "              \"type\": \"name\",\n" +
                "              \"properties\": {\n" +
                "                \"name\": \"epsg:4326\"\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          \"address\": {\n" +
                "            \"mainAddressLine\": \"\",\n" +
                "            \"addressLastLine\": \"\",\n" +
                "            \"placeName\": \"\",\n" +
                "            \"areaName1\": \"areaName1\",\n" +
                "            \"areaName2\": \"areaName3\",\n" +
                "            \"areaName3\": \"areaName3\",\n" +
                "            \"areaName4\": \"areaName4\",\n" +
                "            \"postCode1\": \"\",\n" +
                "            \"postCode2\": \"\",\n" +
                "            \"country\": \"GBR\",\n" +
                "            \"addressNumber\": \"2313\",\n" +
                "            \"streetName\": \"MyStreet\",\n" +
                "            \"customFields\": {}\n" +
                "          },\n" +
                "          \"ranges\": []\n" +
                "        }";

        Address address = helper.parseAddress(JSONObject.fromObject(candidate));

        Assert.assertEquals(address.getAddress(),"areaName4,areaName3,areaName1,GBR");
        Assert.assertEquals(String.valueOf(address.getX()),"-1.60183");
        Assert.assertEquals(String.valueOf(address.getY()),"54.95937");
        Assert.assertEquals(address.getSrs(),"epsg:4326");
    }
}
