package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.model.Option;
import com.pb.stratus.onpremsecurity.analyst.auth.AnalystOAuthProvider;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * @author ekiras
 */
public class LIAPIServiceTest {

    private GeoSearchParams liapiSearchParams;
    private AnalystOAuthProvider analystOAuthProvider = new AnalystOAuthProvider();
    private LIAPIService serviceInstance = new LIAPIService(analystOAuthProvider);
    private LIAPIHelper helper = LIAPIHelper.getInstance();
    private SearchParameters searchParameters = new SearchParameters();
    private GGMRequestParams ggmRequestParams = new GGMRequestParams();
    private LIAPIService spy;
    private String token = "SPYTOKEN";
    private String jsonString = "{\n" +
            "  \"responses\": [\n" +
            "    {\n" +
            "      \"totalPossibleCandidates\": 1,\n" +
            "      \"totalMatches\": 1,\n" +
            "      \"candidates\": [\n" +
            "        {\n" +
            "          \"precisionLevel\": 10,\n" +
            "          \"formattedStreetAddress\": \"\",\n" +
            "          \"formattedLocationAddress\": \"NEW DELHI, NEW DELHI, NATIONAL CAPITAL TERRITORY OF DELHI, IND\",\n" +
            "          \"identifier\": null,\n" +
            "          \"precisionCode\": \"G3\",\n" +
            "          \"sourceDictionary\": \"0\",\n" +
            "          \"matching\": {\n" +
            "            \"matchOnAddressNumber\": false,\n" +
            "            \"matchOnPostCode1\": false,\n" +
            "            \"matchOnPostCode2\": false,\n" +
            "            \"matchOnAreaName1\": false,\n" +
            "            \"matchOnAreaName2\": false,\n" +
            "            \"matchOnAreaName3\": true,\n" +
            "            \"matchOnAreaName4\": false,\n" +
            "            \"matchOnAllStreetFields\": false,\n" +
            "            \"matchOnStreetName\": false,\n" +
            "            \"matchOnStreetType\": false,\n" +
            "            \"matchOnStreetDirectional\": false,\n" +
            "            \"matchOnPlaceName\": false,\n" +
            "            \"matchOnInputFields\": false\n" +
            "          },\n" +
            "          \"geometry\": {\n" +
            "            \"type\": \"Point\",\n" +
            "            \"coordinates\": [\n" +
            "              77.22445,\n" +
            "              28.63576\n" +
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
            "            \"addressLastLine\": \"NEW DELHI, NEW DELHI, NATIONAL CAPITAL TERRITORY OF DELHI, IND\",\n" +
            "            \"placeName\": \"\",\n" +
            "            \"areaName1\": \"NATIONAL CAPITAL TERRITORY OF DELHI\",\n" +
            "            \"areaName2\": \"NEW DELHI\",\n" +
            "            \"areaName3\": \"NEW DELHI\",\n" +
            "            \"areaName4\": \"\",\n" +
            "            \"postCode1\": \"\",\n" +
            "            \"postCode2\": \"\",\n" +
            "            \"country\": \"IND\",\n" +
            "            \"addressNumber\": \"\",\n" +
            "            \"streetName\": \"\",\n" +
            "            \"unitType\": null,\n" +
            "            \"unitValue\": null,\n" +
            "            \"customFields\": {\n" +
            "              \"LANGUAGE\": \"en\",\n" +
            "              \"RESULT_CODE\": \"G3\",\n" +
            "              \"StreetDataType\": \"15\",\n" +
            "              \"CITYRANK\": \"1\"\n" +
            "            }\n" +
            "          },\n" +
            "          \"ranges\": []\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Before
    public void setUp() throws Exception {
        liapiSearchParams = new GeoSearchParams();
        liapiSearchParams.setEndPoint("https://api.pitneybowes.com/location-intelligence/geocode-service/v1/transient/basic/geocode");
        liapiSearchParams.setPassword("secret");
        liapiSearchParams.setUsername("apiKey");
        List<Option> options = new ArrayList<>();
        options.add(new Option(LIAPIConstants.MATCHMODE, "Y"));
        options.add(new Option(LIAPIConstants.FALLBACKTO_POSTAL, "true"));
        options.add(new Option(LIAPIConstants.FALLBACKTO_GEOGRAPHIC, "true"));
        liapiSearchParams.setCountry("GBR");
        liapiSearchParams.setOptions(options);
        searchParameters.setMaxRecords(20);
        searchParameters.setSearchString("edgebaston");
        spy = spy(serviceInstance);
        spy.setGeoHelper(helper);
    }

    @Test
    public void testSearch() throws Exception {

        doReturn(JSONObject.fromObject(jsonString)).when(spy).fireRequest(any(URL.class), any(String.class), any(JSONObject.class));
        doReturn(token).when(spy).getTokenFromSession(anyString());

        List<Address> addressList = spy.search(liapiSearchParams, searchParameters);
        Assert.assertEquals(addressList.size(), 1);
    }

    @Test
    public void testGeocodeAddressesWhenTokenInSession() throws Exception {
        ArrayList<String> mockAddresses = new ArrayList<String>();
        mockAddresses.add("Test Address 1");
        ggmRequestParams.setAddressList(mockAddresses);
        ggmRequestParams.setMaxRecords(1000);
        ggmRequestParams.setGazetteerName("LIAPI Test");
        ggmRequestParams.setTenantName("MockTenant");
        ggmRequestParams.setGazetteerService("LIAPI");

        doReturn(JSONObject.fromObject(jsonString)).when(spy).fireRequest(any(URL.class), any(String.class), any(JSONObject.class));
        doReturn(token).when(spy).getTokenFromSession(anyString());
        JSONObject addressList = spy.geocodeAddresses(liapiSearchParams, ggmRequestParams);
        Assert.assertNotNull(addressList);
        Assert.assertEquals(addressList, JSONObject.fromObject(jsonString));
    }

    @Test
    public void testGeocodeAddressesWhenTokenNotInSession() throws Exception {
        ArrayList<String> mockAddresses = new ArrayList<String>();
        mockAddresses.add("Test Address 1");
        ggmRequestParams.setAddressList(mockAddresses);
        ggmRequestParams.setMaxRecords(1000);
        ggmRequestParams.setGazetteerName("LIAPI Test");
        ggmRequestParams.setTenantName("MockTenant");
        ggmRequestParams.setGazetteerService("LIAPI");

        doReturn(JSONObject.fromObject(jsonString)).when(spy).fireRequest(any(URL.class), any(String.class), any(JSONObject.class));
        doReturn(null).when(spy).getTokenFromSession(anyString());
        doReturn(token).when(spy).getOAuthToken(anyObject(), anyString());

        JSONObject addressList = spy.geocodeAddresses(liapiSearchParams, ggmRequestParams);
        Assert.assertNotNull(addressList);
        Assert.assertEquals(addressList, JSONObject.fromObject(jsonString));
    }

}
