package com.pb.stratus.controller.action;

import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.compositor.MapCompositor;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.Marker;
import com.pb.stratus.controller.print.content.LayerBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArraysAreSameLength;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MapImageActionTest extends ControllerActionTestBase
{ 
    private MapImageAction action;
    
    private MapCompositor mockMapCompositor;
    private BoundingBox mockBoundingBox;
    private Dimension mockDimension;
    List<LayerBean> expectedLayers;
    List<Marker> emptyMarkersList;
    List<Annotation> emptyAnnotationsList;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception
    {
        super.setUp();
        MockHttpServletRequest mockRequest = getRequest();
        mockRequest.addParameter(MapImageRequestParameters.NORTH_PARAM, "40");
        mockRequest.addParameter(MapImageRequestParameters.SOUTH_PARAM, "20");
        mockRequest.addParameter(MapImageRequestParameters.WEST_PARAM, "30");
        mockRequest.addParameter(MapImageRequestParameters.EAST_PARAM, "50");
        mockRequest.addParameter(MapImageRequestParameters.SRS_PARAM, 
                "someSrs");
        mockRequest.addParameter(
                MapImageRequestParameters.MAP_CONFIG_NAME_PARAM, 
                "someMapConfig");
        mockRequest.addParameter(MapImageAction.PIXEL_WIDTH_PARAM, "161");
        mockRequest.addParameter(MapImageAction.OUTPUT_PARAM, "jpg");

        mockMapCompositor = mock(MapCompositor.class);
        action = new MapImageAction(mockMapCompositor);
        BufferedImage mockImage = new BufferedImage(161, 161, 
                BufferedImage.TYPE_4BYTE_ABGR);
        mockBoundingBox = new BoundingBox(40,20, 30, 50, "someSrs");
        mockDimension = new Dimension(161,161);
        expectedLayers = Collections.emptyList();
        emptyMarkersList = Collections.emptyList();
        emptyAnnotationsList = Collections.emptyList();

        when(mockMapCompositor.renderMap(mockBoundingBox, 
            mockDimension, "someMapConfig",expectedLayers,
                emptyMarkersList, emptyAnnotationsList, null, null,null, 0, "METER")).thenReturn(mockImage);
    }
    
    @Test
    public void shouldUseMapCompositorToRenderImage() throws Exception
    {
        action.execute(getRequest(), getResponse());
        BoundingBox expectedBoundingBox = new BoundingBox(40, 20, 30, 50, 
                "someSrs");
        Dimension expectedImageSize = new Dimension(161, 161);
        verify(mockMapCompositor).renderMap(expectedBoundingBox,
                expectedImageSize, "someMapConfig", expectedLayers, emptyMarkersList, emptyAnnotationsList, null, null,null, 0,
                "METER");
    }
    
    @Test
    public void shouldCopyComposedImageIntoResponseAsPng() throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // Note that the expected image doesn't have an alpha channel 
        BufferedImage expectedImage = new BufferedImage(161, 161, 
                BufferedImage.TYPE_3BYTE_BGR);
        ImageIO.write(expectedImage, "jpg", bos);
        action.execute(getRequest(), getResponse());
        byte[] expectedContent = bos.toByteArray();
        assertArraysAreSameLength(expectedContent, getResponse().getContentAsByteArray(), "Verify Array Length");
        assertEquals("image/jpg", getResponse().getContentType());
    }
}
