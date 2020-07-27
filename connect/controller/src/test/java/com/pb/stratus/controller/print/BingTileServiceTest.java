package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.controller.tile.service.BingTileService;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
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

public class BingTileServiceTest {

	private static final String LAYER_NAME = "Bing Roads";
	private static final String IMAGE_MIME = "image/gif";
	private static final int ZOOM_LEVEL = 5;
	
	private BoundingBox boundingBox;
	private BingTileService target; 
	private ControllerConfiguration mockConfig;
	private SecurityContext mockContext;
	private BingUrlBuilder mockUrlBuilder;
	private ImageReader mockImageReader;
	private BufferedImage mockImage;
    private AuthorizationUtils mockAuthUtils;

	@Before
	public void setUp() throws Exception {
		
		//mockBoundingBox = mock(BoundingBox.class);
		mockConfig = mock(ControllerConfiguration.class);
		when(mockConfig.getMaximumNumberOfPrintableTiles()).thenReturn(100);
		mockUrlBuilder = mock(BingUrlBuilder.class);
		mockImageReader = mock(ImageReader.class);
		mockImage = mock(BufferedImage.class);
        mockAuthUtils = mock(AuthorizationUtils.class);

		target = new BingTileService(mockConfig, mockImageReader, mockUrlBuilder, mockAuthUtils);

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
		
		when(mockUrlBuilder.constructBingMetaDataURL(LAYER_NAME, "testKey")).thenReturn(BingTileServiceTest.class.getResource("bingMetaData.json"));
		when(mockImageReader.readFromUrl(any(URL.class))).thenReturn(mockImage);
	}
	
	@Test(expected=MaxNumberOfTilesExceededException.class)
	public void testMaxNumberOfTilesExceededException() throws IOException, ServletException
	{
		boundingBox = new BoundingBox(BingTileService.MAX_NORTH_BOUND,
				BingTileService.MAX_SOUTH_BOUND, BingTileService.MAX_WEST_BOUND,
				BingTileService.MAX_EAST_BOUND, BingTileService.BING_SRS);
		
		target.getTileSet(LAYER_NAME, boundingBox, ZOOM_LEVEL, IMAGE_MIME);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMaxBoundAndBoundsHaveEqualSrs() throws IOException, ServletException{
		boundingBox = new BoundingBox(BingTileService.MAX_NORTH_BOUND,
				BingTileService.MAX_SOUTH_BOUND, BingTileService.MAX_WEST_BOUND,
				BingTileService.MAX_EAST_BOUND, "epsg:27700");
		
		target.getTileSet(LAYER_NAME, boundingBox, ZOOM_LEVEL, IMAGE_MIME);
		
	}
	
            }
