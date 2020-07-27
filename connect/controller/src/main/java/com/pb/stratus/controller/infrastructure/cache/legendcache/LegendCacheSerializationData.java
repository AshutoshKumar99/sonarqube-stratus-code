package com.pb.stratus.controller.infrastructure.cache.legendcache;

import com.pb.stratus.controller.legend.OverlayLegend;

import java.io.Serializable;

public class LegendCacheSerializationData implements Serializable
{
    private static final long serialVersionUID = 3544968485893363958L;

    private OverlayLegend data;

    private long creationTime;

    public LegendCacheSerializationData(OverlayLegend data)
    {
        this.data = data;
        this.creationTime = System.currentTimeMillis();
    }

    public long getCreationTime()
    {
        return this.creationTime;
    }

    public OverlayLegend getOverlayLegend()
    {
        return this.data;
    }
}

