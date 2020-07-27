package com.pb.stratus.controller.legend;

import com.pb.stratus.core.common.Preconditions;
import com.pb.stratus.core.util.ObjectUtils;
import uk.co.graphdata.utilities.contract.Contract;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A LegendData object contains one or more OverlayLegends. 
 */
public class LegendData
{
    private List<OverlayLegend> legends;

    public LegendData(List<OverlayLegend> legends)
    {
        Contract.pre(legends != null, "OverlayLegends required");
        this.legends = legends;
    }
    
    public List<OverlayLegend> getOverlayLegends()
    {
        return legends;
    }
    public void setOverlayLegends(List<OverlayLegend> legends)
    {
        this.legends = legends;
    }

    
    public boolean equals(Object o)
    {
        if (o == this) 
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (this.getClass() != o.getClass())
        {
            return false;
        }
        LegendData that = (LegendData) o;
        if(!ObjectUtils.equals(this.legends, that.legends))
        {
            return false;
        }
        return true;
    }
    
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, legends);
        return hc;
    }

    public List<BufferedImage> getIcons()
    {
        List<BufferedImage> icons = new LinkedList<BufferedImage>();
        for (OverlayLegend overlayLegend : legends)
        {
            for (LegendItem legendItem : overlayLegend.getLegendItems())
            {
                icons.addAll(legendItem.getIcons());
            }
        }
        return icons;
    }
    
    public void sort(Comparator<OverlayLegend> overlayLegendComparator) 
    {
        Preconditions.checkNotNull(overlayLegendComparator,
                "overlayLegendComparator cannot be null");
        Collections.sort(this.legends, overlayLegendComparator);
    }
    
}
