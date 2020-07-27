package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.geocoder.*;
import com.pb.stratus.controller.model.Option;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Created by ar009sh on 23-09-2015.
 */
public class GGMServiceTest {

    private GeoSearchParams ggmParams;
    private GGMService serviceInstance = new GGMService();
    private GGMHelper helper = GGMHelper.getInstance();
    private SearchParameters searchParameters = new SearchParameters();
    private GGMRequestParams ggmRequestParams = new GGMRequestParams();
    private GGMService spy;
    String jsonString = "{\n" +
            "  \"responses\": [\n" +
            "    {\n" +
            "      \"totalPossibleCandidates\": 1,\n" +
            "      \"totalMatches\": 1,\n" +
            "      \"candidates\": [\n" +
            "        {\n" +
            "          \"precisionLevel\": 11,\n" +
            "          \"formattedStreetAddress\": \"\",\n" +
            "          \"formattedLocationAddress\": \"\",\n" +
            "          \"precisionCode\": \"G4\",\n" +
            "          \"sourceDictionary\": \"0\",\n" +
            "          \"geometry\": {\n" +
            "            \"type\": \"Point\",\n" +
            "            \"coordinates\": [\n" +
            "              174.836,\n" +
            "              -41.21172\n" +
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
            "            \"areaName1\": \"WELLINGTON\",\n" +
            "            \"areaName2\": \"\",\n" +
            "            \"areaName3\": \"WELLINGTON\",\n" +
            "            \"areaName4\": \"WOODRIDGE\",\n" +
            "            \"postCode1\": \"\",\n" +
            "            \"postCode2\": \"\",\n" +
            "            \"country\": \"NZL\",\n" +
            "            \"addressNumber\": \"\",\n" +
            "            \"streetName\": \"\",\n" +
            "            \"customFields\": {}\n" +
            "          }, \"ranges\": []\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    @Before
    public void setUp() throws Exception {
        ggmParams = new GeoSearchParams();
        ggmParams.setEndPoint("http://myserver:8080/rest/ggm/");
        ggmParams.setPassword("admin");
        ggmParams.setUsername("admin");
        ggmParams.setCountry("GBR");
        List<Option> options = new ArrayList<>();
        options.add(new Option(GGMConstants.PARAM_FALLBACKTO_POSTAL, "true"));
        options.add(new Option(GGMConstants.PARAM_FALLBACKTO_GEOGRAPHIC, "true"));
        options.add(new Option(GGMConstants.PARAM_MATCHMODE, "Y"));
        ggmParams.setOptions(options);
        searchParameters.setMaxRecords(20);
        searchParameters.setSearchString("edgebaston");
        spy = spy(serviceInstance);
        spy.setGeoHelper(helper);
    }

    @Test
    public void testSearch() throws Exception {
        URL url = new URL(helper.buildURL(ggmParams, searchParameters));
        JSONObject requestObj = new JSONObject();
        JSONObject addressObj = new JSONObject();
        JSONArray addressArray = new JSONArray();
        JSONObject prefObject = new JSONObject();

        addressObj.put(GGMConstants.COUNTRY, ggmParams.getCountry());
        addressObj.put(GGMConstants.PARAM_ADDRESS_LINE, searchParameters.getSearchString());
        addressArray.add(addressObj);

        requestObj.put(GGMConstants.PARAM_ADDRESSES, addressArray);

        for (Option option : ggmParams.getOptions()) {
            prefObject.put(option.getName(), option.getValue());
        }

        if (searchParameters.getMaxRecords() > 0) {
            prefObject.put(GGMConstants.PARAM_MAXCANDIDATES, searchParameters.getMaxRecords());
        } else {
            prefObject.put(GGMConstants.PARAM_MAXCANDIDATES, GGMConstants.PARAM_MAXCANDIDATES_DEFAULT_VALUE);
        }

        requestObj.put(GGMConstants.PARAM_PREFERENCES, prefObject);

        doReturn(JSONObject.fromObject(jsonString)).when(spy).fireRequest(url, ggmParams, requestObj);

        List<Address> addressList = spy.search(ggmParams, searchParameters);
        Assert.assertEquals(addressList.size(), 1);
    }

    @Test
    public void testGeocodeAddresses() throws Exception {
        ArrayList<String> mockAddresses = new ArrayList<String>();
        mockAddresses.add("Test Address 1");
        ggmRequestParams.setAddressList(mockAddresses);
        ggmRequestParams.setMaxRecords(1000);
        ggmRequestParams.setGazetteerName("GGM_Test");
        ggmRequestParams.setTenantName("MockTenant");
        ggmRequestParams.setGazetteerService("GGM");
        doReturn(JSONObject.fromObject(jsonString)).when(spy).fireRequest(any(URL.class), any(GeoSearchParams.class), any(JSONObject.class));
        JSONObject addressList = spy.geocodeAddresses(ggmParams, ggmRequestParams);
        Assert.assertNotNull(addressList);
        Assert.assertEquals(addressList, JSONObject.fromObject(jsonString));
    }

    @Test
    public void testGGMBuildURLMethod() throws UnsupportedEncodingException {
        String url = helper.buildURL(ggmParams, searchParameters);
        Assert.assertEquals("http://myserver:8080/rest/ggm/geocode.json", url);
    }


}
