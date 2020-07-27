package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.geocoder.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


/**
 * Created by ar009sh on 31-08-2015.
 */
public class GeoServiceTest {

    private GeoSearchParams egmParams;
    EGMHelper egm_helper = EGMHelper.getInstance();
    private GeoService serviceInstance = new GeoService();
    private SearchParameters searchParameters = new SearchParameters();

    @Before
    public void setUp() throws Exception {
        egmParams = new GeoSearchParams();
        egmParams.setEndPoint("http://myserver:8080/rest/GeocodeAddressGBR/");
        egmParams.setPassword("admin");
        egmParams.setUsername("admin");
        egmParams.setCountry("GBR");
        egmParams.setOptions(new ArrayList<>());
        searchParameters.setMaxRecords(20);
        searchParameters.setSearchString("milton keynes");
    }

    @Test
    public void testEGMBuildURL() throws UnsupportedEncodingException {
        String url = egm_helper.buildURL(egmParams, searchParameters);
        Assert.assertEquals("http://myserver:8080/rest/GeocodeAddressGBR/results.json?" +
                "Data.AddressLine1=milton+keynes&Data.Country=GBR&Option.GBR.KeepMultimatch=Y&Option.GBR.MaxCandidates=20", url);
    }

    @Test
    public void testEGMSearch() throws Exception {
        String jsonString = "\n" +
                "{  \n" +
                "   \"output_port\":[  \n" +
                "      {  \n" +
                "         \"AddressLine1\":\"The Crescent\",\n" +
                "         \"LastLine\":\"Perth PH1 3\",\n" +
                "         \"Latitude\":\"-3.232323\",\n" +
                "         \"Longitude\":\"235365\",\n" +
                "         \"CoordinateSystem\":\"epsg:4326\",\n" +
                "         \"user_fields\":[  \n" +
                "\n" +
                "         ]\n" +
                "      }\n" +
                "   ]\n" +
                "}";

        GeoService spy = spy(serviceInstance);
        spy.setGeoHelper(egm_helper);
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchString("edgebaston");

        URL url = new URL(egm_helper.buildURL(egmParams, searchParameters));
        doReturn(jsonString).when(spy).fireRequest(url, egmParams);

        List<Address> addressList = spy.search(egmParams, searchParameters);
        Assert.assertEquals(addressList.size(), 1);
    }

}

