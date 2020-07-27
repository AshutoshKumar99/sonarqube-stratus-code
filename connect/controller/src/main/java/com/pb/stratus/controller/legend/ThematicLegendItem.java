package com.pb.stratus.controller.legend;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A LegendItem that represents a thematic legend. Both, ranges and individual
 * values are represented by this class. 
 */
public class ThematicLegendItem extends LegendItem implements Serializable
{
    private static final long serialVersionUID = -3786022420992871451L;
    
    private List<SingleLegendItem> legendItems;
    
    public ThematicLegendItem(String displayName, String legendType, List<SingleLegendItem>
            legendItems)
    {
        super(displayName, legendType);
        this.legendItems = legendItems;
    }

    public List<SingleLegendItem> getLegendItems()
    {
        return legendItems;
    }

    @Override
    protected Collection<? extends BufferedImage> getIcons()
    {
        List<BufferedImage> icons = new LinkedList<BufferedImage>();
        for (SingleLegendItem item : legendItems)
        {
            icons.add(item.getIcon());
        }
        return icons;
    }
}
