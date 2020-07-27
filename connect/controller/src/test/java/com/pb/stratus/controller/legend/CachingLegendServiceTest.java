package com.pb.stratus.controller.legend;

import com.pb.stratus.controller.infrastructure.cache.CacheHub;
import com.pb.stratus.controller.infrastructure.cache.CacheType;
import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.controller.print.content.WMSMap;
import com.pb.stratus.core.configuration.Tenant;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CachingLegendServiceTest
{
    
    
    private LegendService mockTargetService;
    
    private CachingLegendService legendService;
    
    private Locale mockLocale;

    private List<WMSMap> mockWmsMapList;

    private Tenant mockTenant;

    private CacheHub mockCacheHub;

    private Cacheable mockCacheable;

    private OverlayLegend mockOverlayLegend;

    @Before
    public void setUp()
    {
        mockTargetService = mock(LegendServiceImpl.class);
        mockTenant = mock(Tenant.class);
        mockCacheable = mock(Cacheable.class);
        mockOverlayLegend = mock(OverlayLegend.class);
        mockWmsMapList = Arrays.asList(mock(WMSMap.class));
        when(mockCacheable.get(any(Tenant.class), any(String.class))).
                thenReturn(mockOverlayLegend);
        mockCacheHub = mock(CacheHub.class);
        when(mockCacheHub.getCacheForTenant(mockTenant, CacheType.LEGEND_CACHE)).thenReturn
                (mockCacheable);
        legendService = new CachingLegendService(mockTargetService, mockTenant,
                mockCacheHub);
        mockLocale = Locale.ENGLISH;
    }
    
    @Test
    public void shouldGetLegendDataFromCacheIfPresent()
    {
        LegendData actualLegendData = legendService.getLegendData(mockLocale, null,null,
            "overlay1");
        verify(mockCacheable).get(mockTenant, "overlay1");

        LegendData expectedLegendData = new LegendData(Collections
                .singletonList(mockOverlayLegend));
        assertEquals(expectedLegendData, actualLegendData);
    }
    
    @Test
    public void shouldGetLegendDataFromTargetServiceIfNotPresent()
    {
        
        when(mockCacheable.get(any(Tenant.class), any(String.class))).
                thenReturn(null);
        LegendData legenddata = new LegendData(Collections.singletonList
                (mockOverlayLegend));
        when(mockTargetService.getOverlayLegendData(any(Locale.class),any(Map.class), any(String.class))).thenReturn(legenddata);
        LegendData actualLegendData = legendService.getLegendData(mockLocale, null,null,
            "overlay1");
        assertEquals(legenddata, actualLegendData);
    }
    
    @Test
    public void shouldAddLegendDataToCacheIfNotPresent()
    {
        when(mockOverlayLegend.getTitle()).thenReturn("overlay1");
        when(mockCacheable.get(any(Tenant.class), any(String.class))).
                thenReturn(null);
        when(mockTargetService.getWMSLegendData(mockWmsMapList)).thenReturn(
                new LegendData(Arrays.asList(mock(OverlayLegend.class))));
        LegendData legenddata = new LegendData(Collections.singletonList
                (mockOverlayLegend));
        when(mockTargetService.getOverlayLegendData(any(Locale.class),any(Map.class),
                any(String.class))).thenReturn(legenddata);
        legendService.getLegendData(mockLocale, mockWmsMapList,null, "overlay1");
        verify(mockCacheable).put(mockTenant, "overlay1", mockOverlayLegend);
    }

}
