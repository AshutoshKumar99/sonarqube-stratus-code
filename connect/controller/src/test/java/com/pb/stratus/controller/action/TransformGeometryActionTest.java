package com.pb.stratus.controller.action;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.pb.stratus.controller.geometry.GeometryCollection;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

public class TransformGeometryActionTest
{
    private TransformGeometryAction transformGeometryAction;
    private GeometryService mockGeometryService;
    private HttpServletRequest mockHttpServletRequest;
    private List<com.mapinfo.midev.service.geometries.v1.Geometry> mockGeometries;
    private Geometry mockGeometry;

    @Before
    public void setUp()
    {
        mockGeometries = mock(List.class);
        mockGeometry = mock(Geometry.class);
        when(mockGeometries.get(0)).thenReturn(mockGeometry);
        mockGeometryService = mock(GeometryService.class);
        mockHttpServletRequest = mock(HttpServletRequest.class);
        when(mockHttpServletRequest.getParameterValues("geometries")).
                thenReturn(getGeoJsonGeometries());
        when(mockHttpServletRequest.getParameter("sourceSrs")).
                thenReturn("EPSG:27700");
        when(mockHttpServletRequest.getParameter("targetSrs")).
                thenReturn("EPSG:4326");
        transformGeometryAction = new TransformGeometryAction(
                mockGeometryService);
    }

    @Test (expected = IllegalStateException.class)
    public void geometriesCannotBeNull() throws IOException, ServletException {
        when(mockHttpServletRequest.getParameterValues("geometries")).
                thenReturn(null);
        transformGeometryAction.createObject(mockHttpServletRequest);
    }

    @Test (expected = IllegalStateException.class)
    public void geometriesCannotBeEmpty() throws IOException, ServletException {
        when(mockHttpServletRequest.getParameterValues("geometries")).
                thenReturn(new String[0]);
        transformGeometryAction.createObject(mockHttpServletRequest);
    }

    @Test (expected = IllegalStateException.class)
    public void sourceSrsCannotBeNull() throws IOException, ServletException {
        when(mockHttpServletRequest.getParameter("sourceSrs")).
                thenReturn(null);
        transformGeometryAction.createObject(mockHttpServletRequest);
    }

    @Test (expected = IllegalStateException.class)
    public void sourceSrsCannotBeEmpty() throws IOException, ServletException {
        when(mockHttpServletRequest.getParameter("sourceSrs")).
                thenReturn(" ");
        transformGeometryAction.createObject(mockHttpServletRequest);
    }

    @Test (expected = IllegalStateException.class)
    public void targetSrsCannotBeNull() throws IOException, ServletException {
        when(mockHttpServletRequest.getParameter("targetSrs")).
                thenReturn(null);
        transformGeometryAction.createObject(mockHttpServletRequest);
    }

    @Test (expected = IllegalStateException.class)
    public void targetSrsCannotBeEmpty() throws IOException, ServletException {
        when(mockHttpServletRequest.getParameter("targetSrs")).
                thenReturn(" ");
        transformGeometryAction.createObject(mockHttpServletRequest);
    }

    @Test
    public void shouldPassTransformedGeometryAndTargetSrsToGeometryService()
            throws IOException, ServletException, ServiceException {
        transformGeometryAction = spy(transformGeometryAction);
        //srs used for test case
        List<com.mapinfo.midev.service.geometries.v1.Geometry> geometries =
                getSSGeometries(getGeoJsonGeometries(), "EPSG:27700");
        when(transformGeometryAction.getSSGeometriesFromGeoJsonGeometries(
                getGeoJsonGeometries(), "EPSG:27700"
        )).thenReturn(geometries) ;
        transformGeometryAction.createObject(mockHttpServletRequest);
        // test data for this test.
        verify(mockGeometryService).transformGeometries(geometries, "EPSG:4326");
    }

    @Test
    public void shouldReturnEmptyCollectionIfGEomServiceThrowsException() throws
            ServiceException, IOException, ServletException {
        doThrow(new ServiceException()).when(mockGeometryService).
                transformGeometries(any(List.class), any(String.class));
        Object result = transformGeometryAction.createObject(mockHttpServletRequest);
        assertTrue(result instanceof GeometryCollection);
        assertTrue(((GeometryCollection) result).getGeometries().size() == 0);
    }

    @Test
    public void shouldReturnTheGeometriesReturnedFromGeometryService() throws
            IOException, ServletException, ServiceException {
        when(mockGeometryService.transformGeometries(any(List.class),
                any(String.class))).thenReturn(mockGeometries);
        Object result = transformGeometryAction.createObject(mockHttpServletRequest);
        assertTrue(result instanceof GeometryCollection);
        assertEquals(mockGeometry,
                ((GeometryCollection)result).getGeometries().get(0));
    }
    private String[] getGeoJsonGeometries()
    {
        String[] geometries = {"{\"type\":\"Polygon\",\"coordinates\":" +
                "[[[527911.17163045,184065.89379877],[527910.5002437399,184101.47729485997]," +
                "[527874.91674765,184100.80590815],[527875.5881343599,184065.22241206004]," +
                "[527911.17163045,184065.89379877]]]}",
                "{\"type\":\"Polygon\",\"coordinates\":" +
                "[[[527983.01000936,184054.81591791],[527983.01000936,184086.37109369005]," +
                "[527951.45483358,184086.37109369002],[527951.45483358,184054.81591790996]," +
                "[527983.01000936,184054.81591791]]]}"};
        return geometries;
    }

    private List<com.mapinfo.midev.service.geometries.v1.Geometry>
            getSSGeometries(String[] geoJsonGeometries, String sourceSrs)
    {
        List<com.mapinfo.midev.service.geometries.v1.Geometry> geometries =
                new ArrayList<com.mapinfo.midev.service.geometries.v1.Geometry>();
        GeoJsonParser parser = new GeoJsonParser(sourceSrs);
        for(String geoJsonGeometry : geoJsonGeometries)
        {
            geometries.add(parser.parseGeometry(geoJsonGeometry));

        }
        return geometries;
    }


}
