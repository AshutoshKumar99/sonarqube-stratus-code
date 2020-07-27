package com.pb.stratus.controller.legend;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Collection;

/**
 * A legendItem describes a sub-layer of an overlay. This can be either a 
 * simple icon and description (SingleLegendItem), or a range of icons with
 * corresponding descriptions (ThematicLegendItem).
 */
public abstract class LegendItem implements Serializable
{
    private static final long serialVersionUID = -2276853113812184090L;
    
    private String displayName;

    private String legendType;
    
    protected LegendItem(String friendlyName, String legendType)
    {
        this.displayName = friendlyName;
        this.legendType = legendType;
    }
    
    public String getTitle()
    {
        return displayName;
    }

    public String getLegendType()
    {
        return legendType;
    }

    protected abstract Collection<? extends BufferedImage> getIcons();

}
