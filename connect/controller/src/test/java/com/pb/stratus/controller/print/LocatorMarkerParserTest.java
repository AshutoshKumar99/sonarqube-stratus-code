package com.pb.stratus.controller.print;

import com.pb.stratus.controller.util.ImageAssertUtils;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.pb.stratus.controller.print.LocatorMarkerParser.ADDRESS_X_PARAM;
import static com.pb.stratus.controller.print.LocatorMarkerParser.ADDRESS_Y_PARAM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class LocatorMarkerParserTest
{
    private MockHttpServletRequest mockRequest;

    private ControllerConfiguration mockConfig;

    private LocatorMarkerParser markerParser;

    private ConfigReader mockConfigReader;

    private BufferedImage mockBufferedImage;

    @Before
    public void setUp() throws Exception
    {        
        mockConfig = mock(ControllerConfiguration.class);
        when(mockConfig.
            getLocatorImageForPrint()).thenReturn("/some/path/to/image.png");
        mockRequest = new MockHttpServletRequest();
        mockRequest.addParameter(ADDRESS_X_PARAM, "123");
        mockRequest.addParameter(ADDRESS_Y_PARAM, "456");
        mockConfigReader = mock(ConfigReader.class);
        mockBufferedImage = new BufferedImage(45, 67, 
                BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(mockBufferedImage, "png", bos);
        InputStream is = new ByteArrayInputStream(bos.toByteArray());
        when(mockConfigReader.getConfigFile(any(String.class))).thenReturn(is);
        markerParser = new LocatorMarkerParser(mockConfigReader, mockConfig);
    }
    
    @Test
    public void shouldCreateMarkerWithCoordinatesFromRequest()
    {
        Marker marker = markerParser.createMarker(mockRequest.getParameter(ADDRESS_X_PARAM), mockRequest.getParameter(ADDRESS_Y_PARAM), true, null);
        assertEquals(new Point(123, 456), marker.getLocation());
    }
    
    @Test
    public void shouldCreateMarkerWithImageFromCustomerConfigurations()
            throws Exception
    {
        Marker marker = markerParser.createMarker(mockRequest.getParameter(ADDRESS_X_PARAM), mockRequest.getParameter(ADDRESS_Y_PARAM), true, null);
        ImageAssertUtils.assertImagesEquivalentAsPng(mockBufferedImage, 
                marker.getIcon());
        verify(mockConfigReader).getConfigFile("/some/path/to/image.png");
    }
    
    @Test
    public void shouldReturnNullIfXOrYParamsNotSet()
    {
        mockRequest.removeParameter(ADDRESS_X_PARAM);
        Marker marker = markerParser.createMarker(mockRequest.getParameter(ADDRESS_X_PARAM), mockRequest.getParameter(ADDRESS_Y_PARAM), true, null);
        assertNull(marker);
    }
    
}
