package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.*;
import com.pb.stratus.core.util.ObjectUtils;
import org.apache.xml.security.utils.Base64;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Converts an MiDev GetNamedMapLegendResponse into an OverlayLegend object
 */
public class LegendConverter
{
    
    public OverlayLegend convert(String overlayTitle, 
            GetNamedMapLegendsResponse response)
    {
        List<LegendItem> items = new LinkedList<LegendItem>();
        List<Legend> sourceLegends = getPreferredLegends(response);
        for (Legend sourceLegend : sourceLegends)
        {
            items.add(convertLegendToItem(sourceLegend));
        }
        OverlayLegend legend = new OverlayLegend(overlayTitle, items);
        return legend;
    }

    private LegendItem convertLegendToItem(Legend sourceLegend)
    {
        List<LegendRow> rows = sourceLegend.getLegendRow();
        if (rows.size() == 1 && isCartographicOrStyleOverrideLegend(
                sourceLegend))
        {
            return convertToSingleLegendItem(sourceLegend);
        }
        else
        {
            return convertToThematicLegendItem(sourceLegend);
        }
    }
    
    private LegendItem convertToSingleLegendItem(Legend sourceLegend)
    {
        LegendRow row = sourceLegend.getLegendRow().get(0);
        SingleLegendItem item =  new SingleLegendItem(sourceLegend.getLayerName(),
                row.getImage());
        item.setBase64image(Base64.encode(row.getImage()));
        return item;
    }
    
    private LegendItem convertToThematicLegendItem(Legend sourceLegend)
    {
        List<SingleLegendItem> items = new LinkedList<SingleLegendItem>();
        for (LegendRow row : sourceLegend.getLegendRow())
        {
            SingleLegendItem item = new SingleLegendItem(row.getDescription(), 
                    row.getImage());
            item.setBase64image(Base64.encode(row.getImage()));
            items.add(item);
        }
        return new ThematicLegendItem(sourceLegend.getLayerName(), sourceLegend.getLegendType().value(), items);
    }

    private boolean isCartographicOrStyleOverrideLegend(Legend sourceLegend)
    {
        return sourceLegend.getLegendType() == LegendType.CARTOGRAPHIC 
                || sourceLegend.getLegendType() 
                        == LegendType.FEATURE_STYLE_OVERRIDE; 
    }

    private List<Legend> getPreferredLegends(GetNamedMapLegendsResponse response)
    {
        Map<String, Legend> legendsByName = new LinkedHashMap<String, Legend>();
        for (Legend legend : response.getLegend())
        {
            Legend previous = legendsByName.get(legend.getLayerName());
            if (previous != null)
            {
                if (hasHigherPrecedence(legend.getLegendType(), 
                        previous.getLegendType()))
                {
                    legendsByName.put(legend.getLayerName(), legend);
                }
            }
            else
            {
                legendsByName.put(legend.getLayerName(), legend);
            }
        }
        List<Legend> preferredLegends = new LinkedList<Legend>();
        preferredLegends.addAll(legendsByName.values());
        return preferredLegends;
    }
    
    private boolean hasHigherPrecedence(LegendType first, LegendType second)
    {
        if (first == LegendType.CARTOGRAPHIC)
        {
            return false;
        }
        else if (first == LegendType.FEATURE_STYLE_OVERRIDE)
        {
            return second == LegendType.CARTOGRAPHIC;
        }
        else
        {
            return second != LegendType.RANGED_THEME;
        }
    }

    public OverlayLegend convertThematicLegend(String thematicMapName, GetMapLegendsResponse response)
    {
        if (response == null)
            return null;
        List<Legend> responseLegends = response.getLegend();
        List<LegendItem> items = new LinkedList<LegendItem>();
        for (Legend sourceLegend : responseLegends)
        {
            //skipping cartographic legend as we just need thematic legend
            if(sourceLegend.getLegendType() != LegendType.CARTOGRAPHIC) {
                items.add(convertLegendToItem(sourceLegend));
            }
        }
        OverlayLegend legend = new OverlayLegend(thematicMapName, items);
        return legend;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        return obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hash(ObjectUtils.SEED, this.getClass());
    }
}
