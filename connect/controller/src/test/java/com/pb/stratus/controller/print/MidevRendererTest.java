package com.pb.stratus.controller.print;

import com.mapinfo.midev.service.mapping.v1.MapImage;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.mapping.v1.Rendering;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.render.MidevRenderer;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.service.RenderMapParams;
import com.pb.stratus.controller.util.ImageAssertUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MidevRendererTest
{
    private BoundingBox boundingBox;
    private LayerBean layer;
    private List<LayerInfoBean> namedLayersInfoList;
    private Dimension imageSize;
    private BufferedImage redImage;
    private LayerRenderParams layerRenderParams;
    
    private String imageMimeType = "someMimeType";
    private int zoomLevel = 3;
    
    public MidevRendererTest()
    {
    }

    @Before
    public void setUp()
    {
        layer = new LayerBean("testMap");
        namedLayersInfoList = new ArrayList<LayerInfoBean>();
        populateMockDataForNamedLayersInfo(namedLayersInfoList);
        boundingBox = createBoundingBox();
        imageSize = createImageSize();
        redImage = createRedBufferedImage(imageSize);
        MapConfig cfg = new MapConfig();
        MapDefinition def = cfg.createMapDefinition();
        cfg.setMapDefinitions(Arrays.asList(def));
        def.setMapName("testMap");
        def.setImageMimeType(imageMimeType);
        layerRenderParams = new LayerRenderParams(namedLayersInfoList,
        		boundingBox, imageSize, cfg, zoomLevel,false,layer);
    }

    private void populateMockDataForNamedLayersInfo(List<LayerInfoBean> layersList) {
        for (int i=0; i<3; i++) {
            LayerInfoBean layerInfo = new LayerInfoBean();
            layerInfo.setLayerIndex(i);
            layerInfo.setLayerName("NamedLayer"+i);
            layerInfo.setLayerPath("/MyLayers/NamedLayer"+i);
            layerInfo.setLayerMapPath("/MyMaps/testMap");
            layerInfo.setLayerOpacity(1);
            layerInfo.setLayerVisibility(true);

            layersList.add(layerInfo);
        }
    }

    @After
    public void tearDown()
    {
        layer = null;
        boundingBox = null;
        imageSize = null;
        redImage = null;
    }

    @Test
    public void testRenderReturnsCorrectBufferedImage() throws Exception
    {             
        MapImage mapImage = getMapImage(redImage);
        RenderMapResponse mockResponse =
                mock(RenderMapResponse.class);
        when(mockResponse.getMapImage()).thenReturn(mapImage);
        MappingService mockMappingService = mock(MappingService.class);
        when(mockMappingService.getLayerMap(any(RenderMapParams.class)))
                .thenReturn(mockResponse);
        MidevRenderer instance = new MidevRenderer(mockMappingService);
        BufferedImage result = instance.render(layerRenderParams);
        ImageAssertUtils.assertImagesEquivalentAsPng(redImage, result);
    }
    
    @Test
    public void testRenderCreatesCorrectRenderNamedMapParams() throws Exception
    {      
        MapImage mapImage = getMapImage(redImage);
        RenderMapResponse mockResponse =
                mock(RenderMapResponse.class);
        when(mockResponse.getMapImage()).thenReturn(mapImage);
        MappingService mockMappingService = mock(MappingService.class);
        when(mockMappingService.getLayerMap(any(RenderMapParams.class)))
                .thenReturn(mockResponse);
        MidevRenderer instance = new MidevRenderer(mockMappingService);
        instance.render(layerRenderParams);
        ArgumentCaptor<RenderMapParams> argument =
                ArgumentCaptor.forClass(RenderMapParams.class);
        verify(mockMappingService).getLayerMap(argument.capture());
        RenderMapParams actualParams = argument.getValue();

        assertTrue(actualParams.isReturnImage());
        assertEquals("image/png", actualParams.getImageMimeType());
        assertEquals(boundingBox.getSrs(), actualParams.getSrs());
        assertNamedLayers(actualParams);
        Point2D expectedCenter = boundingBox.getCenter();
        assertEquals(expectedCenter.getX(), actualParams.getXPos(), 0.0);
        assertEquals(expectedCenter.getY(), actualParams.getYPos(), 0.0);
        assertEquals(imageSize.getWidth(), actualParams.getWidth(), 0.0);
        assertEquals(imageSize.getHeight(), actualParams.getHeight(), 0.0);
        assertEquals(boundingBox.getWidth(), actualParams.getZoom(), 0.0);
        assertEquals(boundingBox.getWest(), actualParams.getBoundsLeft(), 0.0);
        assertEquals(boundingBox.getEast(), actualParams.getBoundsRight(), 0.0);
        assertEquals(boundingBox.getNorth(), actualParams.getBoundsTop(), 0.0);
        assertEquals(boundingBox.getSouth(), actualParams.getBoundsBottom(), 0.0);
        // For some reason we can't use assertEquals for the "Rendering" enum.
        assertTrue(Rendering.QUALITY == actualParams.getRendering());
    }

    private void assertNamedLayers(RenderMapParams actualParams) {
        List<String> layerInfoList = new ArrayList<String>();
        for (LayerInfoBean layerInfo : namedLayersInfoList) {
            layerInfoList.add(layerInfo.getLayerPath());
        }
        assertArrayEquals(layerInfoList.toArray(), actualParams.getMapLayers().toArray());
    }

    @Test
    public void shouldRequireSpecificResolution()
    {
        MappingService mockMappingService = mock(MappingService.class);
        MidevRenderer instance = new MidevRenderer(mockMappingService);
        assertFalse(instance.supportsSpecificResolutions());
    }

    @Test
    public void shouldNotReturnResolutions()
    {
        MappingService mockMappingService = mock(MappingService.class);
        MidevRenderer instance = new MidevRenderer(mockMappingService);
        assertNull(instance.getResolutions());
    }

    private BoundingBox createBoundingBox()
    {
        double north = 400.0;
        double south = 200.0;
        double west = 200.0;
        double east = 400.0;
        String epsg = "epsg:0000";
        return new BoundingBox(north, south, west, east, epsg);
    }

    private Dimension createImageSize()
    {
        int width = 200;
        int height = 100;
        return new Dimension(width, height);
    }

    private MapImage getMapImage(BufferedImage bufferedImage) throws IOException
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteStream);
        MapImage mapImage = new MapImage();
        mapImage.setImage(byteStream.toByteArray());
        byteStream.close();
        return mapImage;
    }

    // Creates a buffered image 100x200 pixels, filled with red.
    private BufferedImage createRedBufferedImage(Dimension dimension)
    {
        BufferedImage buffImage = new BufferedImage(dimension.width,
                dimension.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = buffImage.createGraphics();
        Rectangle s = new Rectangle(0, 0, dimension.width, dimension.height);
        g2.setColor(Color.RED);
        g2.fill(s);
        g2.draw(s);
        g2.dispose();
        return buffImage;
    }
}
