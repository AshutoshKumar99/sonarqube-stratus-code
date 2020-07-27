package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapConfigDefinition;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.controller.tile.service.OpenStreetTileService;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenStreetTileServiceTest {

	private static final String LAYER_NAME = "OpenStreet Standard";
	private static final String IMAGE_MIME = "image/gif";
	private static final int ZOOM_LEVEL = 5;
	
	private BoundingBox boundingBox;
	private OpenStreetTileService target; 
	private ControllerConfiguration mockConfig;
	private SecurityContext mockContext;
	private BingUrlBuilder mockUrlBuilder;
	private ImageReader mockImageReader;
	private BufferedImage mockImage;
	private MapConfig mockMapConfig;
	private MapDefinition mockMapDefinition;
	private MapConfigDefinition mockMapConfigDefinition;
	
	private LayerRenderParams mockLayerRenderParams;

	@Before
	public void setUp() throws Exception {
		
		//mockBoundingBox = mock(BoundingBox.class);
		mockConfig = mock(ControllerConfiguration.class);
		when(mockConfig.getMaximumNumberOfPrintableTiles()).thenReturn(100);
		mockUrlBuilder = mock(BingUrlBuilder.class);
		mockImageReader = mock(ImageReader.class);
		mockImage = mock(BufferedImage.class);
		mockMapConfig = mock(MapConfig.class);
		mockLayerRenderParams = mock(LayerRenderParams.class);
		mockMapDefinition = mock(MapDefinition.class);
		mockMapConfigDefinition = mock(MapConfigDefinition.class);
		target = new OpenStreetTileService(mockConfig);

		mockContext = mock(SecurityContext.class);
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
		list.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		when(mockContext.getAuthentication()).thenReturn(new AnonymousAuthenticationToken("key", "stratus",list));
		when(mockConfig.getBingServicesPublicApiKey()).thenReturn("testKey");
		SecurityContextHolder.setContext(mockContext);
		
		when(mockUrlBuilder.constructBingMetaDataURL(LAYER_NAME, "testKey")).thenReturn(OpenStreetTileService.class.getResource("openStreetMapMetaData.json"));
		when(mockImageReader.readFromUrl(any(URL.class))).thenReturn(mockImage);
		
		when(mockLayerRenderParams.getLayerName()).thenReturn(LAYER_NAME);
		when(mockLayerRenderParams.getZoomLevel()).thenReturn(ZOOM_LEVEL);
		when(mockLayerRenderParams.getMapConfig()).thenReturn(mockMapConfig);
		
		when(mockMapConfig.getMapConfigDefinition()).thenReturn(mockMapConfigDefinition);
		
		when(mockMapConfig.getMapDefinitionByMapName(LAYER_NAME)).thenReturn(mockMapDefinition);
		when(mockMapDefinition.getImageMimeType()).thenReturn(IMAGE_MIME);
		
		when(mockMapConfigDefinition.getMaxBoundsBottom()).thenReturn(OpenStreetTileService.MAX_SOUTH_BOUND);
		when(mockMapConfigDefinition.getMaxBoundsLeft()).thenReturn(OpenStreetTileService.MAX_WEST_BOUND);
		when(mockMapConfigDefinition.getMaxBoundsRight()).thenReturn(OpenStreetTileService.MAX_EAST_BOUND);
		when(mockMapConfigDefinition.getMaxBoundsTop()).thenReturn(OpenStreetTileService.MAX_NORTH_BOUND);
		when(mockMapConfigDefinition.getProjection()).thenReturn(OpenStreetTileService.OSM_SRS);
	}
	
	@Test(expected=MaxNumberOfTilesExceededException.class)
	public void testMaxNumberOfTilesExceededException() throws IOException, ServletException
	{
		boundingBox = new BoundingBox(OpenStreetTileService.MAX_NORTH_BOUND,
				OpenStreetTileService.MAX_SOUTH_BOUND, OpenStreetTileService.MAX_WEST_BOUND,
				OpenStreetTileService.MAX_EAST_BOUND, OpenStreetTileService.OSM_SRS);
		
		when(mockLayerRenderParams.getBoundingBox()).thenReturn(boundingBox);
		
		target.getTileSet(mockLayerRenderParams);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxBoundAndBoundsHaveEqualSrs() throws IOException, ServletException
	{
		boundingBox = new BoundingBox(OpenStreetTileService.MAX_NORTH_BOUND,
				OpenStreetTileService.MAX_SOUTH_BOUND, OpenStreetTileService.MAX_WEST_BOUND,
				OpenStreetTileService.MAX_EAST_BOUND, "epsg:27700");
		
		when(mockLayerRenderParams.getBoundingBox()).thenReturn(boundingBox);
		
		target.getTileSet(mockLayerRenderParams);
		
	}
}
