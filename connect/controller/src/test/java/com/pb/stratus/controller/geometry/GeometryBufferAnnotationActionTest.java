package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometry.v1.BufferRequest;
import com.mapinfo.midev.service.geometry.v1.BufferResponse;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.pb.stratus.controller.IllegalRequestException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletException;
import java.io.IOException;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class GeometryBufferAnnotationActionTest {

    private GeometryServiceImpl geometryService;
    private String mockGeometryWithPositiveBuffer = "{'type':'Polygon','coordinates':[[[527856.58229465,184084.58632434],[527831.06959934,184051.01698841],[527897.53688449,184040.61049426],[527912.64308566,184091.63588489],[527856.58229465,184084.58632434]]]}";
    private String mockResolution = "16";
    private String positiveDistanceValue = "10";
    private String negativeDistanceValue = "-5";
    private BufferRequest mockBufferRequest;
    private BufferResponse mockBufferResponse = null;
    GeometryBufferAnnotationAction geometryBufferAnnotationAction;
    MockHttpServletRequest mockRequest;

    @Before
    public void setUp() throws Exception
    {
        geometryService = mock(GeometryServiceImpl.class);
        mockBufferRequest = mock(BufferRequest.class);
        mockBufferResponse = mock(BufferResponse.class);
        when(geometryService.getBufferedGeometry(any(BufferRequest.class))).thenReturn(mockBufferResponse);
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("geometry", mockGeometryWithPositiveBuffer);
        mockRequest.setParameter("resolution", mockResolution);
        mockRequest.setParameter("value", positiveDistanceValue);
        mockRequest.setParameter("unit", "METER");
        mockRequest.setParameter("srs", "EPSG:27700");
        geometryBufferAnnotationAction = new GeometryBufferAnnotationAction(geometryService);
    }

    @Test(expected = IllegalStateException.class)
    public void testBufferingWithoutSRSValue() throws IOException, ServletException
    {
        mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("geometry", mockGeometryWithPositiveBuffer);
        mockRequest.setParameter("resolution", mockResolution);
        mockRequest.setParameter("value", positiveDistanceValue);
        mockRequest.setParameter("unit", "METER");
        geometryBufferAnnotationAction.createObject(mockRequest);
    }

    public void testServiceException() throws IOException, ServiceException, ServletException
    {
        when(geometryService.getBufferedGeometry(any(BufferRequest.class))).thenThrow(new ServiceException("Service Exception "));
        Object bufferRespObject = geometryBufferAnnotationAction.createObject(mockRequest);
        assertNotNull(bufferRespObject);

    }

}