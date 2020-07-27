package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.*;
import com.pb.stratus.controller.RemoteAccessException;
import com.pb.stratus.controller.geocoder.*;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddressServiceImplTest {
    private SingleLineAddress mockAddressWebService;

    private BingAddressService mockBingAddressService;

    private AddressServiceImpl addressService;
    private GeoService geoService;
    private GGMService mockGGMService;
    private LIAPIService mockLiapiService;
    private ProjectService mockProjectService;

    @Before
    public void setUp() {
        mockAddressWebService = mock(SingleLineAddress.class);
        mockBingAddressService = mock(BingAddressService.class);
        mockGGMService = mock(GGMService.class);
        mockLiapiService = mock(LIAPIService.class);
        mockProjectService = mock(ProjectService.class);

        GeoServiceHelper mockGeoHelper = mock(GeoServiceHelper.class);
        mockGGMService.setGeoHelper(mockGeoHelper);
        addressService = new AddressServiceImpl(mockAddressWebService,
                mockBingAddressService, geoService, mockGGMService, mockLiapiService, mockProjectService);
    }

    @Test
    public void testFindAddresses_SingleLineAddressService() throws Exception {
        List<Address> mockResult = new LinkedList<Address>();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchString("testExpression");
        searchParameters.setMaxRecords(1234);
        searchParameters.setGazetteerName("a");
        searchParameters.setGazetteerService("a");
        searchParameters.setTenantName("testTenantName");
        when(mockAddressWebService.search(searchParameters)).thenReturn(
                mockResult);
        when(mockBingAddressService.search(searchParameters)).thenThrow(
                new IllegalStateException(
                        "This service should not have been called."));

        mockGazetteerNames(searchParameters);

        List<Address> actualResult = addressService
                .findAddresses(searchParameters);
        assertEquals(mockResult, actualResult);
    }

    @Test
    public void testFindAddresses_Bing() throws Exception {
        List<Address> mockResult = new LinkedList<Address>();
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchString("testExpression");
        searchParameters.setMaxRecords(1234);
        searchParameters.setGazetteerName("InternationalGeocoder");
        when(mockAddressWebService.search(searchParameters)).thenThrow(
                new IllegalStateException(
                        "This service should not have been called."));
        when(mockBingAddressService.search(searchParameters)).thenReturn(
                mockResult);
        List<Address> actualResult = addressService
                .findAddresses(searchParameters);
        assertEquals(mockResult, actualResult);
    }

    @Test
    public void testFindAddressesNoExpression() {
        try {
            SearchParameters searchParameters = new SearchParameters();
            searchParameters.setSearchString(null);
            searchParameters.setMaxRecords(1234);
            searchParameters.setGazetteerName("testGazetteerName");
            addressService.findAddresses(searchParameters);
            fail("No IllegalArgumentException thrown");
        } catch (IllegalArgumentException iax) {
            // expected
        }
    }

    @Test
    public void testFindAddressesNonPositiveCount() {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchString("asdf");
        searchParameters.setGazetteerName("testGazetteerName");
        try {
            searchParameters.setMaxRecords(-1);
            addressService.findAddresses(searchParameters);
            fail("No IllegalArgumentException thrown");
        } catch (IllegalArgumentException iax) {
            // expected
        }
        try {
            searchParameters.setMaxRecords(0);
            addressService.findAddresses(searchParameters);
            fail("No IllegalArgumentException thrown");
        } catch (IllegalArgumentException iax) {
            // expected
        }
    }

    @Test
    public void testFindAddressesGeneralException() throws Exception {
        Exception expectedException = new RuntimeException();
        when(mockAddressWebService.search(any(SearchParameters.class)))
                .thenThrow(expectedException);
        try {
            SearchParameters searchParameters1 = new SearchParameters();
            searchParameters1.setSearchString("asdfasdf");
            searchParameters1.setMaxRecords(10);
            searchParameters1.setGazetteerName("a");
            searchParameters1.setGazetteerService("a");
            searchParameters1.setTenantName("testTenantName");

            mockGazetteerNames(searchParameters1);

            addressService.findAddresses(searchParameters1);
            fail("No RemoteAccessException thrown");
        } catch (RemoteAccessException rax) {
            assertEquals(expectedException, rax.getCause());
        }

    }

    @Test
    public void testGazetteerNameValidity() {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setTenantName("testTenantName");

        mockGazetteerNames(searchParameters);

        String gazetteerName = "a";
        assertTrue(addressService.isValidGazetteerName(gazetteerName,
                searchParameters.getTenantName()));

        gazetteerName = "x";
        assertFalse(addressService.isValidGazetteerName(gazetteerName,
                searchParameters.getTenantName()));
    }

    @Test
    public void testGeocodeAddresses() {
        GGMRequestParams ggmRequestParams = createMockGGMRequestParams();
        GeoSearchParams geoSearchParams = mock(GeoSearchParams.class);
        JSONObject mockResult = new JSONObject();
        try {
            when(mockProjectService.describeGeocodingConfig(anyString(), any(GeoSearchParams.class)))
                    .thenReturn(geoSearchParams);
            when(mockGGMService.geocodeAddresses(geoSearchParams, ggmRequestParams)).thenReturn(
                    mockResult);
            JSONObject actualResult = addressService.geocodeAddresses(ggmRequestParams);
            assertNotNull(actualResult);
            assertEquals(mockResult, actualResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GGMRequestParams createMockGGMRequestParams() {
        GGMRequestParams ggmRequestParams = new GGMRequestParams();
        ggmRequestParams.setTenantName("testTenantName");
        ggmRequestParams.setGazetteerName("GGM_Test");
        ggmRequestParams.setGazetteerService("GGM");
        return ggmRequestParams;
    }

    private void mockGazetteerNames(SearchParameters searchParameters) {
        List<GazetteerInstance> gazetteerNamesList = new ArrayList<GazetteerInstance>();
        GazetteerInstance g1 = new GazetteerInstance();
        g1.setGazetteerName("a");
        gazetteerNamesList.add(g1);
        GazetteerInstance g2 = new GazetteerInstance();
        g2.setGazetteerName("b");
        gazetteerNamesList.add(g2);
        GazetteerInstance g3 = new GazetteerInstance();
        g3.setGazetteerName("c");
        gazetteerNamesList.add(g3);
        GazetteerNames mockGazetteerNames = mock(GazetteerNames.class);
        when(mockGazetteerNames.getGazetteerInstances()).thenReturn(
                gazetteerNamesList);
        try {
            when(
                    mockAddressWebService.getGazetteerNames(searchParameters
                            .getTenantName())).thenReturn(mockGazetteerNames);
        } catch (LocateException_Exception e) {
            e.printStackTrace();
        }
    }

}
