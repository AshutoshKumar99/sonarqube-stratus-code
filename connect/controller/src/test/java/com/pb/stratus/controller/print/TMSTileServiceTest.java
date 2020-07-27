package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapConfigDefinition;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.controller.tile.service.XYZTileService;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TMSTileServiceTest {

    private static final String LAYER_NAME = "TMSTest";
	private static final int ZOOM_LEVEL = 2;
    public static final double MAX_NORTH_BOUND = 20037508;
    public static final double MAX_SOUTH_BOUND = -20037508;
    public static final double MAX_EAST_BOUND = 20037508;
    public static final double MAX_WEST_BOUND = -20037508;
    public static final String SRS = "epsg:3857";
	
	private BoundingBox boundingBox;
	private XYZTileService target;
	private ControllerConfiguration mockConfig;
	private ImageReader mockImageReader;
	private BufferedImage mockImage;
    private List<String> mockUrlArray;
	private MapConfig mockMapConfig;
	private MapDefinition mockMapDefinition;
	private MapConfigDefinition mockMapConfigDefinition;
	private LayerRenderParams mockLayerRenderParams;

	@Before
	public void setUp() throws Exception {

        mockUrlArray = new ArrayList<String>();
        mockUrlArray.add("http://test1.tiles/${z}/${x}/${y}.png");
        mockUrlArray.add("http://test2.tiles/${z}/${x}/${y}.png");

        mockConfig = mock(ControllerConfiguration.class);
        when(mockConfig.getMaximumNumberOfPrintableTiles()).thenReturn(300);
        mockImageReader = mock(ImageReader.class);
        mockImage = mock(BufferedImage.class);
        mockMapConfig = mock(MapConfig.class);
        mockLayerRenderParams = mock(LayerRenderParams.class);
        mockMapDefinition = mock(MapDefinition.class);
        mockMapConfigDefinition = mock(MapConfigDefinition.class);
        target = new XYZTileService(mockConfig);
        target.setImageReader(mockImageReader);

        when(mockLayerRenderParams.getLayerName()).thenReturn(LAYER_NAME);
        when(mockLayerRenderParams.getZoomLevel()).thenReturn(ZOOM_LEVEL);

        when(mockLayerRenderParams.getMapConfig()).thenReturn(mockMapConfig);
        when(mockMapConfig.getMapConfigDefinition()).thenReturn(mockMapConfigDefinition);
        when(mockMapConfig.getMapDefinitionByMapName(LAYER_NAME)).thenReturn(mockMapDefinition);

        when(mockMapConfigDefinition.getMaxBoundsBottom()).thenReturn(MAX_SOUTH_BOUND);
        when(mockMapConfigDefinition.getMaxBoundsLeft()).thenReturn(MAX_WEST_BOUND);
        when(mockMapConfigDefinition.getMaxBoundsRight()).thenReturn(MAX_EAST_BOUND);
        when(mockMapConfigDefinition.getMaxBoundsTop()).thenReturn(MAX_NORTH_BOUND);
        when(mockMapConfigDefinition.getProjection()).thenReturn(SRS);

        when(mockImageReader.readFromUrl(any(URL.class))).thenReturn(mockImage);
	}


    @Test
    public void getTileSetForFullMap() throws IOException, ServletException
    {
        boundingBox = new BoundingBox(MAX_NORTH_BOUND,MAX_SOUTH_BOUND,MAX_WEST_BOUND,MAX_EAST_BOUND,SRS);
        when(mockLayerRenderParams.getBoundingBox()).thenReturn(boundingBox);

        LayerBean mockLayer = new LayerBean(LAYER_NAME,"TMS-Tile-Service",mockUrlArray,true,"png");
        when(mockLayerRenderParams.getLayer()).thenReturn(mockLayer);


        TileSet tilesSet = target.getTileSet(mockLayerRenderParams);
        assertEquals(4, tilesSet.getCols());
        assertEquals(4, tilesSet.getRows());
    }

    @Test
    public void getTileSetForSubsetOfMap() throws IOException, ServletException
    {
        boundingBox = new BoundingBox(MAX_NORTH_BOUND/2,MAX_SOUTH_BOUND/2,MAX_WEST_BOUND/2,MAX_EAST_BOUND/2,SRS);
        when(mockLayerRenderParams.getBoundingBox()).thenReturn(boundingBox);

        LayerBean mockLayer = new LayerBean(LAYER_NAME,"TMS-Tile-Service",mockUrlArray,true,"png");
        when(mockLayerRenderParams.getLayer()).thenReturn(mockLayer);

        TileSet tilesSet = target.getTileSet(mockLayerRenderParams);
        assertEquals(2, tilesSet.getCols());
        assertEquals(2, tilesSet.getRows());
    }



}
