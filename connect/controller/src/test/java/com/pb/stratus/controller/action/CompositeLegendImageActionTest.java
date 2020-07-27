package com.pb.stratus.controller.action;

import com.pb.stratus.controller.legend.SpriteImageService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompositeLegendImageActionTest
{
    private CompositeLegendImageAction imageAction;
    
    private MockHttpServletRequest mockRequest;
    
    private MockHttpServletResponse mockResponse;

    private BufferedImage mockImage;

    @Before
    public void setUp() throws Exception
    {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockRequest.addParameter("overlays", "overlay1,overlay2");
        mockRequest.addParameter("map", "testmap");
        SpriteImageService mockImageService = mock(SpriteImageService.class);
        mockImage = new BufferedImage(123, 456, 
                BufferedImage.TYPE_4BYTE_ABGR);
        when(mockImageService.getImage("overlay1", "overlay2")).thenReturn(
                mockImage);
        imageAction = new CompositeLegendImageAction(mockImageService);
    }  

    @Test
    public void shouldSetCacheControlHeaderToOneMinute() throws Exception
    {
        imageAction.execute(mockRequest, mockResponse);
        assertEquals("max-age=60", mockResponse.getHeader("Cache-Control"));
    }
    
    @Test
    public void shouldWriteExpectedImageToResponse() throws Exception
    {
        imageAction.execute(mockRequest, mockResponse);
        byte[] actualResponse = mockResponse.getContentAsByteArray();
        byte[] expectedResponse = createExpectedResponse();
        assertArrayEquals(expectedResponse, actualResponse);
    }
    
    @Test
    public void shouldSetContentSize() throws Exception
    {
        imageAction.execute(mockRequest, mockResponse);
        assertEquals(createExpectedResponse().length, 
                mockResponse.getContentLength());
    }

    @Test
    public void shouldSetContentTypeToImagePng() throws Exception
    {
        imageAction.execute(mockRequest, mockResponse);
        assertEquals("image/png", mockResponse.getContentType());
    }

    private byte[] createExpectedResponse() throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(mockImage, "png", bos);
        return bos.toByteArray();
    }

}