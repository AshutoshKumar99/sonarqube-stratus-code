package com.pb.stratus.controller.json;

import com.pb.stratus.controller.legend.LegendData;

/**
 * A LegendDataHolder is a wrapper object that is purely required to render
 * A LegendData with the necessary icon information into JSON. 
 */
public class LegendDataHolder
{
    
    private LegendData legendData;
    
    private String iconUrl;
    
    public LegendDataHolder(LegendData legendData, String iconUrl)
    {
        this.legendData = legendData;
        this.iconUrl = iconUrl;
    }

    public LegendData getLegendData()
    {
        return legendData;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
    
    

}
