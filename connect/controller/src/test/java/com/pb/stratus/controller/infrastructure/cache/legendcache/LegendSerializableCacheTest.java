package com.pb.stratus.controller.infrastructure.cache.legendcache;

import com.pb.stratus.controller.infrastructure.cache.CacheManager;
import com.pb.stratus.controller.infrastructure.cache.CacheType;
import com.pb.stratus.controller.legend.LegendItem;
import com.pb.stratus.controller.legend.OverlayLegend;
import com.pb.stratus.controller.legend.SingleLegendItem;
import com.pb.stratus.core.configuration.Tenant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static com.pb.stratus.core.common.Constants.LEGEND_CACHE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: sh003bh
 * Date: 9/15/11
 * Time: 1:36 PM
 */
public class LegendSerializableCacheTest
{
    private LegendSerializableCache legendSerializableCache;
    private CacheManager cacheManager;
    private Tenant mockTenant;
    private File tenantPath;
    private File legendRepository;

    @Before
    public void setUp()
    {
        cacheManager = CacheManager.getInstance();
        mockTenant = mock(Tenant.class);
        when(mockTenant.getTenantName()).thenReturn("dummy");
        when(mockTenant.getTenantPath()).thenReturn(".");
        // not creating the instance directly as we need to create cache
        // repository as well.
        legendSerializableCache = (LegendSerializableCache)cacheManager.
                getCacheForTenant(mockTenant, CacheType.LEGEND_CACHE);
        tenantPath  = new File(".");
        legendRepository = new File(tenantPath, LEGEND_CACHE);
    }

    @After
    public void tearDown()
    {
        legendRepository.delete();
    }

    @Test
    public void isInstanceOfLegendSerializableCache()
    {
        assertTrue(legendSerializableCache instanceof  LegendSerializableCache);
    }

    @Test
    public void shouldCreateANewFileForEachKey()
    {
        String key1 = "overlay1";
        legendSerializableCache.put(mockTenant, key1, getOverlayLegend());
        File cacheFile = new File(legendRepository, "overlay1");
        assertTrue(cacheFile.exists());
        cacheFile.delete();
    }

    @Test
    public void shouldRetrieveCachedData()
    {
        String key1 = "overlay1";
        legendSerializableCache.put(mockTenant, key1, getOverlayLegend());
        OverlayLegend actual = (OverlayLegend)legendSerializableCache.get(mockTenant,
            "overlay1");
        OverlayLegend expected = getOverlayLegend();
        assertEquals(expected.getTitle(), actual.getTitle());
        File cacheFile = new File(legendRepository, "overlay1");
        cacheFile.delete();
    }

    private OverlayLegend getOverlayLegend()
    {
        SingleLegendItem singleLegendItem = new SingleLegendItem("legend1",
                new byte[]{});
        OverlayLegend overlayLegend = new OverlayLegend("overlay",
                Collections.<LegendItem>singletonList(singleLegendItem));
        return overlayLegend;
    }
}
