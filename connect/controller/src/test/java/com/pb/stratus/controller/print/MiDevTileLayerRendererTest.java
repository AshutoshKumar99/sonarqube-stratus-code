package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.content.WMSMap;
import com.pb.stratus.controller.print.render.MiDevTileLayerRenderer;
import com.pb.stratus.controller.tile.service.TileService;
import org.junit.Test;

import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MiDevTileLayerRendererTest
{
    
    @Test
    public void shouldReturnImageFromTileCompositor() throws ServletException, IOException 
    {
        BoundingBox bounds = new BoundingBox(2, 1, 1, 2, "someSrs");
        Dimension imageSize = new Dimension(1, 2);
        String layerName = "someLayer";
        String imageMimeType = "someMimeType";
        WMSMap wmsMap= null;
        int zoomLevel = 3;
        TileSet tileSet = mock(TileSet.class);
        
        TileCompositor mockCompositor = mock(TileCompositor.class);
        TileService mockTileService = mock(TileService.class);
        MiDevTileLayerRenderer renderer = new MiDevTileLayerRenderer(
                mockCompositor, mockTileService);
        
        when(mockTileService.getTileSet(layerName, bounds, zoomLevel, 
                imageMimeType)).thenReturn(tileSet);
        BufferedImage expectedImage = mock(BufferedImage.class);
        when(mockCompositor.compose(tileSet, imageSize)).thenReturn(
                expectedImage);
        MapConfig cfg = new MapConfig();
        MapDefinition def = cfg.createMapDefinition();
        cfg.setMapDefinitions(Arrays.asList(def));
        def.setMapName("someLayer");
        def.setImageMimeType(imageMimeType);
        LayerRenderParams params = new LayerRenderParams(layerName, 
                bounds, imageSize, cfg, zoomLevel+1,new LayerBean(layerName));
        BufferedImage actualImage = renderer.render(params);
        
        assertEquals(expectedImage, actualImage);
    }

}
