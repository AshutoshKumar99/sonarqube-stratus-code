package com.pb.stratus.controller.legend;

import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.core.configuration.Tenant;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to get and put Legend related data from cache.
 */
public final class LegendCacheHelper
{
    private LegendCacheHelper()
    {}

    /**
     * Merge the cached LegendData and the LegendData from MiDev
     * @param cachedData ... LegendData retrieved from cache.
     * @param nonCachedData ... LegendData retrieved from MiDev.
     */
    public static void mergeLegendData(LegendData cachedData,
           LegendData nonCachedData)
    {
        List<OverlayLegend> legends = cachedData.getOverlayLegends();
        for(OverlayLegend overlayLegend : nonCachedData.getOverlayLegends())
        {
            legends.add(overlayLegend);
        }
    }

    /**
     * Put the OverlayLegend for each overlay into the cache.
     * @param cache
     * @param tenant
     * @param legendData
     */
    public static void putLegendDataInCache(Cacheable cache,Tenant tenant,
           LegendData legendData)
    {
        List<OverlayLegend> overlayLegends = legendData.getOverlayLegends();
        for(OverlayLegend overlayLegend : overlayLegends)
        {
            cache.put(tenant, overlayLegend.getTitle(), overlayLegend);
        }
    }

    /**
     * Recreate LegendData from the OverlayLegend in the cache.
     * @param cache
     * @param tenant
     * @param overlayNames
     * @return
     */
    public static LegendCacheData createLegendDataFromCache(Cacheable cache,
           Tenant tenant, String... overlayNames)
    {
        List<String> overlaysNotPresentInCache = new ArrayList<String>();
        List<OverlayLegend> overlayLegends = new ArrayList<OverlayLegend>();
        for(String overlayName : overlayNames)
        {
            OverlayLegend overlayLegend  = (OverlayLegend)cache.get(tenant,
                    overlayName);
            if(overlayLegend == null)
            {
                overlaysNotPresentInCache.add(overlayName);
            }
            else
            {
                overlayLegends.add(overlayLegend);
            }
        }
        LegendData legendData = new LegendData(overlayLegends);
        LegendCacheData  legendCacheData = new LegendCacheData();
        legendCacheData.setLegendData(legendData);
        legendCacheData.setLegendsNotPresentInCache(overlaysNotPresentInCache);
        return legendCacheData;
    }
}
