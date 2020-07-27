package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.render.BingRenderer;
import com.pb.stratus.controller.tile.service.TileService;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.awt.*;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class BingRendererTest {

	private static final String LAYER_NAME = "testlayer";
	private static final String IMAGE_MIME = "image/gif";
	private static final int ZOOM_LEVEL = 5;
	
	private TileCompositor mockTileCompositor;
	private TileService mockTileService;
	private LayerRenderParams mockLayerRenderParams;
	private BoundingBox mockBoundingBox;
	private TileSet mockTileSet;
	private Dimension mockDimension;
	private BingRenderer target; 
	private MapConfig mockMapConfig;
	private MapDefinition mockMapDefination; 

	@Before
	public void setUp() throws Exception {
		
		mockTileCompositor = mock(TileCompositor.class);
		mockTileService = mock(TileService.class);
		mockLayerRenderParams = mock(LayerRenderParams.class);
		mockTileSet = mock(TileSet.class);
		mockBoundingBox = mock(BoundingBox.class);
		mockDimension = mock(Dimension.class);
		mockMapConfig = mock(MapConfig.class);
		mockMapDefination = mock(MapDefinition.class);

		when(mockLayerRenderParams.getLayerName()).thenReturn(LAYER_NAME);
		when(mockLayerRenderParams.getImageSize()).thenReturn(mockDimension);
		when(mockLayerRenderParams.getZoomLevel()).thenReturn(ZOOM_LEVEL);
		when(mockLayerRenderParams.getBoundingBox()).thenReturn(mockBoundingBox);
		when(mockTileService.getTileSet(
				mockLayerRenderParams.getLayerName(),
				mockLayerRenderParams.getBoundingBox(),
				mockLayerRenderParams.getZoomLevel(), IMAGE_MIME)).thenReturn(
		mockTileSet);
		when(mockMapConfig.getMapDefinitionByMapName(LAYER_NAME)).thenReturn(mockMapDefination);
		when(mockMapDefination.getImageMimeType()).thenReturn(IMAGE_MIME);
		when(mockLayerRenderParams.getMapConfig()).thenReturn(mockMapConfig);
		target = new BingRenderer(mockTileCompositor,mockTileService);
	}
	
	@Test
	public void testRender() throws IOException, ServletException{
		target.render(mockLayerRenderParams);
		verify(mockTileCompositor).compose( mockTileSet,mockDimension);
		verify(mockTileService).getTileSet(LAYER_NAME, mockBoundingBox, ZOOM_LEVEL, IMAGE_MIME);
		
	}
	
	
}
