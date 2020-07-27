package com.pb.stratus.controller.legend;

import com.pb.stratus.controller.infrastructure.cache.CacheHub;
import com.pb.stratus.controller.infrastructure.cache.CacheType;
import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.controller.print.content.WMSMap;
import com.pb.stratus.controller.util.SpriteImageGenerator;
import com.pb.stratus.controller.util.SpriteImageGeneratorFactory;
import com.pb.stratus.core.configuration.Tenant;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SpriteImageServiceTest
{
    
    private SpriteImageGenerator mockImageGenerator;
    
    private LegendService mockLegendService;

    private SpriteImageService imageService;

    private List<BufferedImage> mockIcons;

    private BufferedImage mockImage;

    private Tenant mockTenant;

    private Cacheable mockCacheable;

    private CacheHub cacheHub;

    private LegendData mockLegendData;

    private   OverlayLegend  mockOverlayLegend1;
    private   OverlayLegend  mockOverlayLegend2;

    @Before
    public void setUp()
    {
        mockLegendService = mock(CachingLegendService.class);
        mockImageGenerator = mock(SpriteImageGenerator.class);
        SpriteImageGeneratorFactory mockFactory 
                = mock(SpriteImageGeneratorFactory.class);
        when(mockFactory.createSpriteImageGenerator(any(Dimension.class)))
                .thenReturn(mockImageGenerator);
        mockTenant = mock(Tenant.class);
        when(mockTenant.getTenantPath()).thenReturn("src/test/resources");
        when(mockTenant.getTenantName()).thenReturn("dummy");
        mockCacheable = mock(Cacheable.class);
        cacheHub =  mock(CacheHub.class);
        when(cacheHub.getCacheForTenant(any(Tenant.class),
                any(CacheType.class))).thenReturn
                (mockCacheable);
        imageService = new SpriteImageService(mockLegendService,
                mockFactory, mockTenant, cacheHub);

        mockLegendData = mock(LegendData.class);
        mockIcons = Arrays.asList(
                mock(BufferedImage.class), mock(BufferedImage.class));
        mockOverlayLegend1 = mock(OverlayLegend.class);
        mockOverlayLegend2 = mock(OverlayLegend.class);
        ArrayList<OverlayLegend> overlayLegends = new
                ArrayList<OverlayLegend>();
        overlayLegends.add(mockOverlayLegend1);
        overlayLegends.add(mockOverlayLegend2);
        when(mockLegendData.getOverlayLegends()).thenReturn(overlayLegends);
        when(mockLegendData.getIcons()).thenReturn(mockIcons);

        when(mockLegendService.getLegendData(any(Locale.class), anyListOf(WMSMap.class),any(Map.class),
                (String[]) anyVararg())).thenReturn(mockLegendData);
        mockImage = mock(BufferedImage.class);
        when(mockCacheable.get(any(Tenant.class), any(String.class))).thenReturn
                (null);
        when(mockImageGenerator.createSpriteImage()).thenReturn(mockImage);
    }
    
    @Test
    public void shouldCreateSpriteImageForAllIconsOfLegendData()
    {
        BufferedImage actualImage = imageService.getImage("overlay1", 
                "overlay2");
        for (BufferedImage image : mockIcons)
        {
            verify(mockImageGenerator).addImage(image);
        }
        assertEquals(mockImage, actualImage);
    }
    
    @Test
    public void shouldGetSpriteImageFromCacheIfPresent()
    {
        OverlayLegend mockOverlayLegend = mock(OverlayLegend.class);
        when(mockCacheable.get(any(Tenant.class), any(String.class))).thenReturn
                (mockOverlayLegend);
        BufferedImage actualImage = imageService.getImage("overlay1", 
                "overlay2");
        assertEquals(mockImage, actualImage);
    }
    
    @Test
    public void shouldAddSpriteImageToCacheIfNotPresent()
    {
        when(mockOverlayLegend1.getTitle()).thenReturn("overlay1");
        imageService.getImage("overlay1");
        when(mockCacheable.get(any(Tenant.class), any(String.class))).
                thenReturn(null);
        LegendData legenddata = new LegendData(Collections.singletonList
                (mockOverlayLegend1));
        when(mockLegendService.getLegendData(any(Locale.class), anyListOf(WMSMap.class),any(Map.class),
                any(String.class))).thenReturn(legenddata);
    
        verify(mockCacheable).get(mockTenant, "overlay1");
        verify(mockCacheable).put(mockTenant, "overlay1", mockOverlayLegend1);
    }
}
