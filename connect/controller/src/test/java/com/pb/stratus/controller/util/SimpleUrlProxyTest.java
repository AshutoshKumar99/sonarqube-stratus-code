package com.pb.stratus.controller.util;

import com.pb.stratus.util.test.Handler;
import org.apache.commons.io.IOUtils;
import org.easymock.Capture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class SimpleUrlProxyTest 
{
    
    private static final String HANDLER_SYS_PROP 
            = "java.protocol.handler.pkgs";
    
    private SimpleUrlProxy proxy;
    
    private byte[] expectedContent;
    
    private HttpServletRequest mockRequest;
    
    private HttpServletResponse mockResponse;
    
    private ByteArrayOutputStream actualResponseStream;
    
    private Capture<String> actualContentType;
    
    private Capture<String> actualEncoding;
    
    private String oldHandlerProp; 
    
    @Before
    public void setUp() throws IOException
    {
        oldHandlerProp = System.getProperty(HANDLER_SYS_PROP);
        System.setProperty(HANDLER_SYS_PROP, "com.pb.stratus.util");
        proxy = new SimpleUrlProxy();
        expectedContent = loadExpectedContent();
        Handler.setCurrentURLConnection(expectedContent, "image/png", null);
    }
    
    @After
    public void tearDown()
    {
        if (oldHandlerProp == null)
        {
            oldHandlerProp = "";
        }
        System.setProperty(HANDLER_SYS_PROP, oldHandlerProp);
    }
    
    private void initMockRequestAndResponse()
    {
        mockResponse = createMock(HttpServletResponse.class);
        actualResponseStream = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream()
        {
            @Override
            public void write(int b) throws IOException
            {
                actualResponseStream.write(b);
            }
        };
        try
        {
            expect(mockResponse.getOutputStream()).andReturn(sos);
        }
        catch (IOException iox)
        {
            // cannot happen
            throw new RuntimeException(iox);
        }
        expectLastCall().anyTimes();
        actualEncoding = new Capture<String>();
        mockResponse.setCharacterEncoding(capture(actualEncoding));
        expectLastCall().anyTimes();
        actualContentType = new Capture<String>();
        mockResponse.setContentType(capture(actualContentType));
        initMockRequest("/dir/getImage", "name=image.gif");
    }
    
    private void initMockRequestAndResponseForJSON()
    {
        mockResponse = createMock(HttpServletResponse.class);
        mockRequest = createMock(HttpServletRequest.class);
    }
   
    private void initMockRequest(String pathInfo, String queryString)
    {
        mockRequest = createMock(HttpServletRequest.class);
        expect(mockRequest.getPathInfo()).andReturn(pathInfo);
        expectLastCall().anyTimes();
        expect(mockRequest.getQueryString()).andReturn(queryString);
        expectLastCall().anyTimes();
    }
    
    
    private void replayMocks()
    {
        replay(mockRequest);
        replay(mockResponse);
    }
    
    private void verifyMocks()
    {
        verify(mockRequest);
        verify(mockResponse);
    }
    
    @Test
    public void testProxy() throws Exception
    {
        initMockRequestAndResponse();
        replayMocks();
        URL baseUrl = new URL("test://host:1234/path");
        URL expectedUrl = new URL(baseUrl.toString() 
                + "/dir/getImage?name=image.gif");
        Handler.setCurrentURLConnection(expectedContent, "image/gif", null);
        proxy.proxy(baseUrl, mockRequest, mockResponse);
        assertArrayEquals(expectedContent, 
                actualResponseStream.toByteArray());
        URL actualUrl = Handler.getLastOpenedURLConnection().getURL();
        assertEquals(expectedUrl.toString(), actualUrl.toString());
        verifyMocks();
    }
    
    @Test
    public void testProxyNonExistingUrl() throws Exception
    {
        initMockRequestAndResponse();
        replayMocks();
        URL baseUrl = new URL("http://this.host.does.hopefully.not.exist:1234/");
        try
        {
            proxy.proxy(baseUrl, mockRequest, mockResponse);
            fail("No IOException thrown");
        }
        catch (IOException iox)
        {
            // expected
        }
        // We don't care about what was called on the mocks
    }
    
    @Test
    public void testProxyCorrectEncoding() throws Exception
    {
        initMockRequestAndResponse();
        replayMocks();
        String expectedString = "\u041f is a non-ASCII character";
        Handler.setCurrentURLConnection(expectedString.getBytes("UTF-8"), 
                "text/plain", "UTF-8");
        proxy.proxy(new URL("test:/asdf/"), mockRequest, mockResponse);
        assertEquals("UTF-8", actualEncoding.getValue());
        String actualString = new String(actualResponseStream.toByteArray(), 
                "UTF-8");
        assertEquals(expectedString, actualString);
    }
    
    @Test
    public void testProxyCorrectContentType() throws Exception
    {
        initMockRequestAndResponse();
        replayMocks();
        Handler.setCurrentURLConnection(expectedContent, "image/gif", null);
        proxy.proxy(new URL("test:/asdf/"), mockRequest, mockResponse);
        assertEquals("image/gif", actualContentType.getValue());
        verifyMocks();
    }
    
    private byte[] loadExpectedContent() throws IOException
    {
        InputStream is = SimpleUrlProxyTest.class
                .getResourceAsStream("image.gif");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        return bos.toByteArray();
    }
    
    @Test
    public void testProxyNullPathInfoAndQueryString() throws Exception
    {
        initMockRequestAndResponse();
        initMockRequest(null, null);
        replayMocks();
        Handler.setCurrentURLConnection(new byte[0], "text/plain", null);
        proxy.proxy(new URL("test:/asdf/asdf"), mockRequest, mockResponse);
        URL actualUrl = Handler.getLastOpenedURLConnection().getURL();
        assertEquals("test:/asdf/asdf", actualUrl.toString());
        
    }

    @Test
    public void testProxyWithUrlAndInputStream() throws Exception
    {
        initMockRequestAndResponseForJSON();
        replayMocks();
        URL baseUrl = new URL("test://host:1234/path");
        Handler.setCurrentURLConnection(getExpectedContent(), "application/json", null);
        InputStream is = proxy.proxy(baseUrl);
        actualResponseStream = new ByteArrayOutputStream();
        IOUtils.copy(is, actualResponseStream);
        assertArrayEquals(getExpectedContent(), 
                actualResponseStream.toByteArray());
        verifyMocks();
}
    
    private byte[] getExpectedContent() throws IOException
    {
        String getDescriptionResult = "{\"EnvinsaResponse\":" +
                "{\"description\":{\"bounds\":{\"maxX\":2.0037508E7," +
                "\"maxY\":2.0037508E7,\"minX\":-2.0037508E7,\"minY\":" +
                "-2.0037508E7},\"coordSys\":\"epsg:3857\",\"description\":" +
                "\"world_map\",\"mapRendering\":\"QUALITY\",\"mapResolution\":" +
                "110,\"maxLevel\":20,\"minLevel\":1,\"name\":\"world_map\"," +
                "\"outputTypes\":[\"image/png\",\"image/jpg\",\"image/gif\"]," +
                "\"tileHeight\":256,\"tileWidth\":256},\"type\":" +
                "\"MapTilingResponse\"}}";
        return getDescriptionResult.getBytes();
    }
    
}
