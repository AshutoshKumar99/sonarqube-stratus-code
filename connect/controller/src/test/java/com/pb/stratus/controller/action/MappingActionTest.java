package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.service.RenderMapParams;
import com.pb.stratus.controller.util.MockSupport;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.util.JaxbUtil;
import org.apache.commons.io.IOUtils;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MappingActionTest extends ControllerActionTestBase {

    private MappingAction mappingAction;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;   
    private MappingService mockMappingWebService;
    private MockSupport mockSupport;
    private ControllerConfiguration mockConfig;
    
    @Before
    public void setUp()
    {
        mockSupport = new MockSupport();
        mockMappingWebService = mockSupport.createMock(
                MappingService.class);        
        mappingAction = new MappingAction(mockMappingWebService);
        mockConfig = mockSupport
        .createMock(ControllerConfiguration.class);
        mappingAction.setConfig(mockConfig);
    }
    
    @Test
    public void testExecute() throws Exception 
    {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("x", "1");
        mockRequest.addParameter("y", "2");
        mockRequest.addParameter("width", "600");
        mockRequest.addParameter("height", "800");
        mockRequest.addParameter("srs", "epsg:3857");
        String layerList = "layer1,layer2,layer3";
        mockRequest.addParameter("layers", layerList);
        mockRequest.addParameter("zoom", "20000");
        mockRequest.addParameter("output", "image/png");
        RenderMapResponse renderResponse = createMapResponse();
        Capture<RenderMapParams> capture = new Capture<RenderMapParams>();
        expect(mockMappingWebService.getLayerMap(capture(capture))).andReturn(renderResponse).times(1);
        replay(mockMappingWebService);
        HttpServletResponse expectedResponse = createExpectedResponse(false);
        mappingAction.execute(mockRequest, mockResponse);
        byte expectedImage[] = new byte[9];
        byte actualImage[] = new byte[9];
        expectedResponse.getOutputStream().write(expectedImage);
        mockResponse.getOutputStream().write(actualImage);
        assertEquals(expectedResponse.getContentType(), mockResponse.getContentType());
        assertArrayEquals(expectedImage,actualImage);
        verify(mockMappingWebService);
    }
    
    private HttpServletResponse createExpectedResponse(boolean  noLayers) throws IOException  {
        HttpServletResponse response = new MockHttpServletResponse();
        InputStream is = null;
        //XXX Dependency on other tests makes this one more fragile.
        if(noLayers)
        {
            is = MappingActionTest.class
                    .getResourceAsStream("transparent.png");
        }else{
            is = MappingActionTest.class
                .getResourceAsStream("baseMap.png");
        }

        ByteArrayOutputStream arr = new ByteArrayOutputStream();
        IOUtils.copy(is, arr);
        byte[]imgArray = arr.toByteArray();
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "max-age=3600");
        response.setContentLength(imgArray.length);
        response.getOutputStream().write(imgArray);
        return response;
    }


    private RenderMapResponse createMapResponse() throws Exception{
        
        RenderMapResponse response = JaxbUtil.createObject(
                "renderMapResponseBusinessMap.xml",
                RenderMapResponse.class, MappingActionTest.class);
        
        return response;
    }

    @Test
    public void testIfNoLayersPresent()throws Exception
    {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("x", "1");
        mockRequest.addParameter("y", "2");
        mockRequest.addParameter("width", "600");
        mockRequest.addParameter("height", "800");
        mockRequest.addParameter("srs", "epsg:3857");
        String layerList = "";
        mockRequest.addParameter("layers", layerList);
        mockRequest.addParameter("zoom", "20000");
        mockRequest.addParameter("output", "image/png");

        replay(mockMappingWebService);
        HttpServletResponse expectedResponse = createExpectedResponse(true);
        mappingAction.execute(mockRequest, mockResponse);
        byte expectedImage[] = new byte[9];
        byte actualImage[] = new byte[9];
        expectedResponse.getOutputStream().write(expectedImage);
        mockResponse.getOutputStream().write(actualImage);
        assertEquals(expectedResponse.getContentType(), mockResponse.getContentType());
        assertArrayEquals(expectedImage,actualImage);
        verify(mockMappingWebService);
    }

    @Test
    public void testIfCacheAgeParameterProvided()throws Exception
    {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("x", "1");
        mockRequest.addParameter("y", "2");
        mockRequest.addParameter("width", "600");
        mockRequest.addParameter("height", "800");
        mockRequest.addParameter("cacheAge", "100");
        mockRequest.addParameter("srs", "epsg:3857");
        String layerList = "";
        mockRequest.addParameter("layers", layerList);
        mockRequest.addParameter("zoom", "20000");
        mockRequest.addParameter("output", "image/png");

        replay(mockMappingWebService);
        HttpServletResponse expectedResponse = createExpectedResponse(true);
        mappingAction.execute(mockRequest, mockResponse);
        byte expectedImage[] = new byte[9];
        byte actualImage[] = new byte[9];
        expectedResponse.getOutputStream().write(expectedImage);
        mockResponse.getOutputStream().write(actualImage);
        assertEquals(expectedResponse.getContentType(), mockResponse.getContentType());
        assertEquals(mockResponse.getHeader("Cache-Control"), "max-age=100");
        assertArrayEquals(expectedImage, actualImage);
        verify(mockMappingWebService);
    }

    @Test
    public void testIfCacheAgeParameterNotProvided()throws Exception
    {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("x", "1");
        mockRequest.addParameter("y", "2");
        mockRequest.addParameter("width", "600");
        mockRequest.addParameter("height", "800");
        //mockRequest.addParameter("cacheAge", "100");
        mockRequest.addParameter("srs", "epsg:3857");
        String layerList = "";
        mockRequest.addParameter("layers", layerList);
        mockRequest.addParameter("zoom", "20000");
        mockRequest.addParameter("output", "image/png");

        replay(mockMappingWebService);
        HttpServletResponse expectedResponse = createExpectedResponse(true);
        mappingAction.execute(mockRequest, mockResponse);
        byte expectedImage[] = new byte[9];
        byte actualImage[] = new byte[9];
        expectedResponse.getOutputStream().write(expectedImage);
        mockResponse.getOutputStream().write(actualImage);
        assertEquals(expectedResponse.getContentType(), mockResponse.getContentType());
        assertEquals(mockResponse.getHeader("Cache-Control"), "max-age=3600");
        assertArrayEquals(expectedImage, actualImage);
        verify(mockMappingWebService);
    }
}
