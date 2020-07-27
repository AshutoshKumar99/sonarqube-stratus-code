package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.service.AddressService;
import com.pb.stratus.core.configuration.TenantNameHolder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SingleLineSearchActionTest extends ControllerActionTestBase
{
    private SingleLineSearchAction action;

    private AddressService mockAddressService;

    private List<Address> mockResult;

    private SearchParameters mockParams;

    private GeometryService mockGeometryService;

    private MapConfigRepository mockMapConfigRepository;

    private TenantNameHolder mockTenantNameHolder;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockAddressService = mock(AddressService.class);
        mockGeometryService = mock(GeometryService.class);
        mockMapConfigRepository = mock(MapConfigRepository.class);
        mockTenantNameHolder = mock(TenantNameHolder.class);

        action = new SingleLineSearchAction(mockAddressService,
                mockGeometryService, mockMapConfigRepository,
                mockTenantNameHolder);
        getRequest().addParameter("query", "someQuery");
    }

    @Test
    public void shouldUseExpectedSearchParams() throws Exception
    {
        when(mockTenantNameHolder.getTenantName()).thenReturn("testTenantName");
        getRequest().addParameter("count", "0");
        getRequest().addParameter("srs", "someSrs");
        getRequest().addParameter("gazetteerName", "someGazetteer");
        getRequest().addParameter("mapcfg", "myMap");
        MapConfig mapcfg = new MapConfig();
        mapcfg.setDefaultGazetteerName("mapcfgGazetteer");
        when(mockMapConfigRepository.getMapConfig("myMap")).thenReturn(mapcfg);
        action.execute(getRequest(), getResponse());

        ArgumentCaptor<SearchParameters> arg = ArgumentCaptor.forClass(SearchParameters.class);
        verify(mockAddressService).findAddresses(arg.capture());

        SearchParameters actualParams = arg.getValue();
        assertEquals("someQuery", actualParams.getSearchString());
        assertEquals(0, actualParams.getMaxRecords());
        assertEquals("someGazetteer", actualParams.getGazetteerName());
        assertEquals("testTenantName", actualParams.getTenantName());
    }


    //FIXME this should move into the service
    @Test
    public void shouldTransformResults() throws Exception
    {
        Address address = new Address();
        address.setX(123);
        address.setY(456);
        address.setSrs("someSrs");
        address.setAddress("someAddress");
        address.setId("abc");
        List<Address> mockAddresses = Arrays.asList(address);
        when(mockAddressService.findAddresses(any(SearchParameters.class))).thenReturn(mockAddresses);

        Point point = new Point();
        Pos pos = new Pos();
        point.setPos(pos);
        pos.setX(987);
        pos.setY(654);
        point.setSrsName("someOtherSrs");
        List<Geometry> mockTransformedPoints = Arrays.asList((Geometry) point);
        when(mockGeometryService.transformPoints(any(List.class), any(String.class))).thenReturn(mockTransformedPoints);

        getRequest().addParameter("srs", "targetSrs");
        getRequest().addParameter("gazetteerName", "someGazetteer");
        getRequest().addParameter("mapcfg", "myMap");
        MapConfig mapcfg = new MapConfig();
        mapcfg.setDefaultGazetteerName("mapcfgGazetteer");
        when(mockMapConfigRepository.getMapConfig("myMap")).thenReturn(mapcfg);
        action.execute(getRequest(), getResponse());

        ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
        verify(mockGeometryService).transformPoints(arg.capture(), eq("targetSrs"));
        List geoms = arg.getValue();
        assertEquals(1, geoms.size());
        Point actualPoint = (Point) geoms.get(0);
        assertEquals(123, actualPoint.getPos().getX(), 0d);
        assertEquals(456, actualPoint.getPos().getY(), 0d);
        assertEquals("someSrs", actualPoint.getSrsName());

        //FIXME change SingleLineSearchAction to inherit from
        //      DataInterchangeFormatControllerAction then it's esier to test the actual result
        assertEquals("/*[{\"id\": \"abc\", \"name\": \"someAddress\", "
                + "\"score\": 0.0, \"srs\": \"someOtherSrs\", \"x\": 987.0, "
                + "\"y\": 654.0}]*/", getResponse().getContentAsString());

    }

    @Test
    public void testGazetteerName_fromMapcfg() throws Exception
    {
        MapConfig mapcfg = new MapConfig();
        mapcfg.setDefaultGazetteerName("mapcfgGazetteer");
        when(mockMapConfigRepository.getMapConfig("myMap")).thenReturn(mapcfg);

        // no gazetteerName specified, use the gazetteerName in the mapconfig
        getRequest().addParameter("mapcfg", "myMap");
        action.execute(getRequest(), getResponse());

        ArgumentCaptor<SearchParameters> arg = ArgumentCaptor.forClass(SearchParameters.class);
        verify(mockAddressService).findAddresses(arg.capture());
        assertEquals("mapcfgGazetteer", arg.getValue().getGazetteerName());
    }

    @Test
    public void testGazetteerName_fromMapcfgWithXmlExtension() throws Exception
    {
        MapConfig mapcfg = new MapConfig();
        mapcfg.setDefaultGazetteerName("mapcfgGazetteer");
        when(mockMapConfigRepository.getMapConfig("myMap.xml")).thenThrow(new IllegalArgumentException());
        when(mockMapConfigRepository.getMapConfig("myMap")).thenReturn(mapcfg);

        // no gazetteerName specified, use the gazetteerName in the mapconfig (remove .xml extension)
        getRequest().addParameter("mapcfg", "myMap.xml");
        action.execute(getRequest(), getResponse());

        ArgumentCaptor<SearchParameters> arg = ArgumentCaptor.forClass(SearchParameters.class);
        verify(mockAddressService).findAddresses(arg.capture());
        assertEquals("mapcfgGazetteer", arg.getValue().getGazetteerName());
    }

    @Test
    public void testGazetteerName_fromGazetteerName() throws Exception
    {
        MapConfig mapcfg = new MapConfig();
        mapcfg.setDefaultGazetteerName("mapcfgGazetteer");
        when(mockMapConfigRepository.getMapConfig("myMap")).thenReturn(mapcfg);

        // gazetteerName overrides mapcfg
        getRequest().addParameter("mapcfg", "myMap");
        getRequest().addParameter("gazetteerName", "otherGazetteer");
        action.execute(getRequest(), getResponse());

        ArgumentCaptor<SearchParameters> arg = ArgumentCaptor.forClass(SearchParameters.class);
        verify(mockAddressService).findAddresses(arg.capture());
        assertEquals("otherGazetteer", arg.getValue().getGazetteerName());
    }

    @Test
    public void testMapConfigName_whenNotSet() throws Exception
    {
        getRequest().addParameter("gazetteerName", "InternationalGeocoder");
        getRequest().addParameter("srs", "someSrs");
        getRequest().addParameter("country","london");
        //mapCfg is not set
        action.execute(getRequest(), getResponse());
        ArgumentCaptor<SearchParameters> arg = ArgumentCaptor.forClass(SearchParameters.class);
        verify(mockAddressService).findAddresses(arg.capture());
        assertEquals("InternationalGeocoder", arg.getValue().getGazetteerName());
        assertEquals("someQuery london", arg.getValue().getSearchString());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGazetteerName_useServiceDefault() throws Exception
    {
        // gazetteerName is not set
        action.execute(getRequest(), getResponse());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGazetteerName_whenNull() throws Exception
    {
        // gazetteerName is not set in request
        getRequest().addParameter("srs", "someSrs");
        getRequest().addParameter("mapcfg", "myMap");
        //A non-null gazetteerName is not found
        when(mockMapConfigRepository.getMapConfig("myMap")).thenReturn(null);
        action.execute(getRequest(), getResponse());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGazetteerName_fromInvalidMapCfg_1() throws Exception
    {
        when(mockMapConfigRepository.getMapConfig("badMap")).thenThrow(new IllegalArgumentException());

        // gazetteerName overrides mapcfg
        getRequest().addParameter("mapcfg", "badMap");
        action.execute(getRequest(), getResponse());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGazetteerName_fromInvalidMapCfg_2() throws Exception
    {
        when(mockMapConfigRepository.getMapConfig("badMap")).thenThrow(new IllegalArgumentException());
        when(mockMapConfigRepository.getMapConfig("badMap.xml")).thenThrow(new IllegalArgumentException());

        // gazetteerName overrides mapcfg
        getRequest().addParameter("mapcfg", "badMap.xml");
        action.execute(getRequest(), getResponse());
    }
}
