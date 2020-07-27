package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.geocoder.GGMRequestParams;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.service.AddressService;
import com.pb.stratus.core.configuration.TenantNameHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class GeocodeAddressesActionTest extends ControllerActionTestBase
{
    private GeocodeAddressesAction action;

    private AddressService mockAddressService;

    private List<Address> mockResult;

    private SearchParameters mockParams;

    private TenantNameHolder mockTenantNameHolder;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockAddressService = mock(AddressService.class);
        mockTenantNameHolder = mock(TenantNameHolder.class);

        action = new GeocodeAddressesAction(mockAddressService, mockTenantNameHolder);
        getRequest().addParameter("query", "someQuery");
    }

    @Test
    public void shouldUseExpectedSearchParams() throws Exception
    {
        when(mockTenantNameHolder.getTenantName()).thenReturn("testTenantName");
        getRequest().addParameter("count", "10");
        getRequest().addParameter("srs", "someSrs");
        getRequest().addParameter("gazetteerName", "someGazetteer");
        getRequest().addParameter("gazetteerService", "GGM");
        getRequest().addParameter("addressArray", new String[]{"Address1", "Address2"});
        getRequest().addParameter("mapcfg", "myMap");
        action.execute(getRequest(), getResponse());

        ArgumentCaptor<GGMRequestParams> arg = ArgumentCaptor.forClass(GGMRequestParams.class);
        verify(mockAddressService).geocodeAddresses(arg.capture());

        GGMRequestParams actualParams = arg.getValue();
        assertEquals(2, actualParams.getAddressList().size());
        assertEquals("Address1", actualParams.getAddressList().get(0));
        assertEquals("Address2", actualParams.getAddressList().get(1));
        assertEquals(10, actualParams.getMaxRecords());
        assertEquals("someGazetteer", actualParams.getGazetteerName());
        assertEquals("GGM", actualParams.getGazetteerService());
        assertEquals("testTenantName", actualParams.getTenantName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGazetteerName_useServiceDefault() throws Exception
    {

        when(mockTenantNameHolder.getTenantName()).thenReturn("testTenantName");
        getRequest().addParameter("count", "10");
        getRequest().addParameter("srs", "someSrs");
        getRequest().addParameter("gazetteerService", "GGM");
        getRequest().addParameter("addressArray", new String[]{"Address1", "Address2"});
        getRequest().addParameter("mapcfg", "myMap");
        // gazetteerName is not set
        action.execute(getRequest(), getResponse());
    }

}
