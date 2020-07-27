package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.core.configuration.TenantNameHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MappingServiceImplTest
{

    private MappingServiceImpl mappingService;

    private MappingServiceInterface mockMappingWebService;

    private ExecutorService exectuorService;

    private RenderNamedMapResponse mockNamedResponse;
    private RenderMapResponse mockResponse;

    private TenantNameHolder mockTenantNameHolder;

    private String tenantName;

    @Before
    public void setUp() throws Exception
    {
        mockMappingWebService = mock(MappingServiceInterface.class);
        exectuorService = Executors.newFixedThreadPool(1);
        mockTenantNameHolder = mock(TenantNameHolder.class);
        tenantName =  mockTenantNameHolder.getTenantName();
        mappingService = new MappingServiceImpl(mockMappingWebService, tenantName);
        setUpMockGetMapResponse();
        setUpMockGetNamedMapResponse();

        when(
                mockMappingWebService
                        .renderMap(any(RenderMapRequest.class)))
                        .thenReturn(mockResponse);
        when(
                mockMappingWebService
                        .renderNamedMap(any(RenderNamedMapRequest.class)))
                .thenReturn(mockNamedResponse);
    }

    private void setUpMockGetNamedMapResponse()
    {
        byte[] imgArray = {1, 0, 1, 0, 1, 0, 1, 0, 1};
        mockNamedResponse = new RenderNamedMapResponse();
        MapImage mapImage = new MapImage();
        mapImage.setImage(imgArray);
        mockNamedResponse.setMapImage(mapImage);
    }

    private void setUpMockGetMapResponse()
    {
        byte[] imgArray = {1, 0, 1, 0, 1, 0, 1, 0, 1};
        mockResponse = new RenderMapResponse();
        MapImage mapImage = new MapImage();
        mapImage.setImage(imgArray);
        mockResponse.setMapImage(mapImage);
    }

    @After
    public void tearDown()
    {
        exectuorService.shutdownNow();
    }

    @Test
    public void shouldReturnExpectedNamedMapImage() throws Exception
    {
        RenderNamedMapParams params = createRenderNamedMapParams();
        RenderNamedMapResponse actualResponse = mappingService.getNamedMap(params);
        assertEquals(mockNamedResponse.getMapImage().getImage(), actualResponse
                .getMapImage().getImage());
    }

    @Test
    public void shouldReturnExpectedMapImage() throws Exception
    {
        RenderMapParams params = createRenderMapParams();
        RenderMapResponse actualResponse =
                mappingService.getLayerMap(params);
        assertEquals(mockResponse.getMapImage().getImage(), actualResponse
                .getMapImage().getImage());
    }

 @Test
    public void shouldCallRenderNamedMapWithExpectedRequest() throws Exception
    {
        mappingService.getNamedMap(createRenderNamedMapParams());
        ArgumentCaptor<RenderNamedMapRequest> arg =
                ArgumentCaptor.forClass(RenderNamedMapRequest.class);
        verify(mockMappingWebService).renderNamedMap(arg.capture());
        RenderNamedMapRequest actualRequest = arg.getValue();
        ZoomAndCenterMapView actualMapView =
                (ZoomAndCenterMapView) actualRequest.getMapView();
        assertEquals(800, actualMapView.getHeight(), 0d);
        assertEquals(600, actualMapView.getWidth(), 0d);
        assertEquals("someMap", actualRequest.getNamedMap());
        Point actualMapCenter = actualMapView.getMapCenter();
        assertEquals(-71.2345, actualMapCenter.getPos().getX(), 0d);
        assertEquals(42.1345, actualMapCenter.getPos().getY(), 0d);
        assertEquals("someSrs", actualMapCenter.getSrsName());
        assertEquals(200000, actualMapView.getZoomLevel().getValue(), 0d);
        assertEquals(DistanceUnit.METER, actualMapView.getZoomLevel().getUom());
        assertEquals("someMimeType", actualRequest.getImageMimeType());
        assertTrue(actualRequest.isReturnImage());
    }

    @Test
    public void shouldCallRenderMapWithExpectedRequest() throws Exception
    {
        mappingService.getLayerMap(createRenderMapParams());
        ArgumentCaptor<RenderMapRequest> arg =
                ArgumentCaptor.forClass(RenderMapRequest.class);
        verify(mockMappingWebService).renderMap(arg.capture());
        RenderMapRequest actualRequest = arg.getValue();
        ZoomAndCenterMapView actualMapView =
                (ZoomAndCenterMapView) actualRequest.getMapView();
        assertEquals(800, actualMapView.getHeight(), 0d);
        assertEquals(600, actualMapView.getWidth(), 0d);

        Map map =  actualRequest.getMap();
        int index = 1;
        for (Layer layer : map.getLayer()) {
            assertEquals("layer"+index, ((NamedLayer) layer).getName());
            index++;
        }
        Point actualMapCenter = actualMapView.getMapCenter();
        assertEquals(-71.2345, actualMapCenter.getPos().getX(), 0d);
        assertEquals(42.1345, actualMapCenter.getPos().getY(), 0d);
        assertEquals("someSrs", actualMapCenter.getSrsName());
        assertEquals(200000, actualMapView.getZoomLevel().getValue(), 0d);
        assertEquals(DistanceUnit.METER, actualMapView.getZoomLevel().getUom());
        assertEquals("someMimeType", actualRequest.getImageMimeType());
        assertTrue(actualRequest.isReturnImage());
    }

    private RenderNamedMapParams createRenderNamedMapParams()
    {
        RenderNamedMapParams params = new RenderNamedMapParams();
        params.setMapName("someMap");
        params.setSrs("someSrs");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("someMimeType");
        params.setReturnImage(true);
        return params;
    }

    private RenderMapParams createRenderMapParams()
    {
        RenderMapParams params = new RenderMapParams();
        //params.setMapName("someMap");
        List<String> mapLayers = new ArrayList<String>();
        mapLayers.add("layer1");
        mapLayers.add("layer2");
        mapLayers.add("layer3");
        params.setMapLayers(mapLayers);

        params.setSrs("someSrs");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("someMimeType");
        params.setReturnImage(true);
        return params;
    }

    @Test
    public void shouldCalculateCorrectCenterLineForGivenMapViewport()
            throws Exception
    {
        ConvertXYToPointResponse mockResponse = new ConvertXYToPointResponse();
        mockResponse.getPoint().add(createPoint(1, 2, "someSrs"));
        mockResponse.getPoint().add(createPoint(2, 3, "someSrs"));
        when(
                mockMappingWebService
                        .convertXYToPoint(any(ConvertXYToPointRequest.class)))
                .thenReturn(mockResponse);
        MapViewPort mockViewport = new MapViewPort();
        List<Point> result =
                mappingService
                        .getMapScreenCenterLineStartEndEarthCoordinates(mockViewport);
        assertEquals(2, result.size());
        assertEquals(result.get(0), mockResponse.getPoint().get(0));
        assertEquals(result.get(1), mockResponse.getPoint().get(1));
    }

    @Test
    public void shouldCallConvertXYToPointWithExpectedRequest()
            throws Exception
    {
        ConvertXYToPointResponse mockResponse = new ConvertXYToPointResponse();
        when(
                mockMappingWebService
                        .convertXYToPoint(any(ConvertXYToPointRequest.class)))
                .thenReturn(mockResponse);
        MapViewPort mockViewport = createMockViewport();
        mappingService
                .getMapScreenCenterLineStartEndEarthCoordinates(mockViewport);
        ArgumentCaptor<ConvertXYToPointRequest> arg =
                ArgumentCaptor.forClass(ConvertXYToPointRequest.class);
        verify(mockMappingWebService).convertXYToPoint(arg.capture());
        ConvertXYToPointRequest actualRequest = arg.getValue();
        assertExpectedMapView(actualRequest.getMapView());
        XY xy = actualRequest.getXYList().getXY().get(0);
        assertEquals(0, xy.getX(), 0d);
        assertEquals(300, xy.getY(), 0d);
        xy = actualRequest.getXYList().getXY().get(1);
        assertEquals(800, xy.getX(), 0d);
        assertEquals(300, xy.getY(), 0d);
    }

    private MapViewPort createMockViewport()
    {
        MapViewPort mockViewport = new MapViewPort();
        mockViewport.setMapWidth(800);
        mockViewport.setMapHeight(600);
        mockViewport.setMapSrs("someSrs");
        mockViewport.setMapXpos(1234);
        mockViewport.setMapYpos(2345);
        mockViewport.setMapZoom(8762);
        mockViewport.setDistanceUnit(DistanceUnit.CHAIN);
        return mockViewport;
    }

    private void assertExpectedMapView(MapView actual)
    {
        ZoomAndCenterMapView mapView = (ZoomAndCenterMapView) actual;
        assertEquals("someSrs", mapView.getMapCenter().getSrsName());
        assertEquals(1234, mapView.getMapCenter().getPos().getX(), 0d);
        assertEquals(2345, mapView.getMapCenter().getPos().getY(), 0d);
        assertEquals(DistanceUnit.CHAIN, mapView.getZoomLevel().getUom());
        assertEquals(8762, mapView.getZoomLevel().getValue(), 0d);
        assertEquals(800, mapView.getWidth(), 0d);
        assertEquals(600, mapView.getHeight(), 0d);
    }

    private Point createPoint(double x, double y, String srs)
    {
        Point point = new Point();
        Pos pos = new Pos();
        point.setPos(pos);
        pos.setX(x);
        pos.setY(y);
        point.setSrsName(srs);
        return point;
    }

}
