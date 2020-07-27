package com.pb.stratus.controller.legend;


import com.pb.stratus.core.common.Preconditions;

import java.util.List;

/**
 * In case of legends there will be cases where partial data is present in
 * the cache and partial is absent. This class encapsulates the LegendData
 * for the overlays that are present in the cache and maintains a list of
 * overlays that were not found in the cache.
 */
public class LegendCacheData
{
    // Contains the data for the overlays that are present in the cache.
    private LegendData legendData;

    // using a list to keep the overlays that are not present in the cache.
    // manipulation of list is easier than an array, so using a list.
    private List<String> legendsNotPresentInCache;

    public LegendData getLegendData()
    {
        return legendData;
    }

    public void setLegendData(LegendData legendData)
    {
        Preconditions.checkNotNull(legendData, "LegendData cannot be null");
        this.legendData = legendData;
    }

    public List<String> getLegendsNotPresentInCache()
    {
        return legendsNotPresentInCache;
    }

    public void setLegendsNotPresentInCache(List<String> legendsNotPresentInCache)
    {
        Preconditions.checkNotNull(legendsNotPresentInCache,
                "legendsNotPresentInCache cannot be null");
        this.legendsNotPresentInCache = legendsNotPresentInCache;
    }

    /**
     * A convenient method to return an array of overlays as many of the API
     * use varagrs as argument.
     * @return String[].. array containing overlay names.
     */
    public String[] getOverlaysNotPresentInCacheAsArray()
    {
        String [] data = new String[legendsNotPresentInCache.size()];
        legendsNotPresentInCache.toArray(data);
        return data;
    }
}
