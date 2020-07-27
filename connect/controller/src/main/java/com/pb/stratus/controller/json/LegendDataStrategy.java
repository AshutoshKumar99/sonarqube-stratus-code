package com.pb.stratus.controller.json;

import com.pb.stratus.controller.legend.*;
import org.apache.commons.lang.StringEscapeUtils;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Serialises LegendDataHolder objects into JSON strings. On top of the 
 * properties provided by LegendData and its children, this class generates
 * additional attribute that are necessary on the client side to deal with
 * a single icon sprite image.
 */
public class LegendDataStrategy extends OwnedConverterStrategy
{

    public void processValue(Object value, StringBuilder b)
    {
        LegendDataHolder holder = (LegendDataHolder) value;
        LegendData legendData = holder.getLegendData();
        b.append("{\"overlays\": [");
        String iconUrl = holder.getIconUrl();
        List<BufferedImage> icons = legendData.getIcons();
        int idx = 0;
        for (OverlayLegend overlayLegend : legendData.getOverlayLegends())
        {
            if (idx++ > 0)
            {
                b.append(", ");
            }
            processOverlayLegend(overlayLegend, b, iconUrl, icons);
        }
        b.append("]}");
    }
    
    private void processOverlayLegend(OverlayLegend overlayLegend, 
            StringBuilder b, String iconUrl, List<BufferedImage> icons)
    {
        b.append("{\"id\": \"");
        b.append(escape(overlayLegend.getTitle()));
        b.append("\", \"legendItems\": [");
        int idx = 0;
        for(LegendItem legendItem : overlayLegend.getLegendItems())
        {
            if (idx++ > 0)
            {
                b.append(", ");
            }
            if (legendItem instanceof SingleLegendItem)
            {
                processSingleLegendItem((SingleLegendItem) legendItem, b, 
                        iconUrl, icons);
            }
            else
            {
                processThematicLegendItem((ThematicLegendItem) legendItem, b, 
                        iconUrl, icons);
            }
        }
        b.append("]}");
    }
    
    private void processThematicLegendItem(ThematicLegendItem legendItem, 
            StringBuilder b, String iconUrl, List<BufferedImage> icons)
    {
        b.append("{\"type\": \"thematic\",");
        b.append("\"displayName\": \"");
        b.append(escape(legendItem.getTitle()));
        b.append("\",\"legendType\": \"");
        b.append(escape(legendItem.getLegendType()));
        b.append("\", \"legendItems\": [");
        int idx = 0;
        for (SingleLegendItem singleItem : legendItem.getLegendItems())
        {
            if (idx++ > 0)
            {
                b.append(", ");
            }
            processSingleLegendItem(singleItem, b, iconUrl, icons);
        }
        b.append("]}");
    }

    private void processSingleLegendItem(SingleLegendItem legendItem, 
            StringBuilder b, String iconUrl, List<BufferedImage> icons)
    {
        b.append("{\"type\": \"single\",");
        b.append("\"displayName\": \"");
        b.append(escape(legendItem.getTitle()));
        b.append("\",\"legendType\": \"");
        b.append(escape(legendItem.getLegendType()));
        b.append("\", \"iconUrl\": \"");
        b.append(escape(iconUrl));
        b.append("\",\"iconImage\": \"");
        b.append(escape(legendItem.getBase64image()));
        b.append("\", \"iconIndex\": ");
        b.append(icons.indexOf(legendItem.getIcon()));
        b.append("}");
    }

    private String escape(String value)
    {
        return StringEscapeUtils.escapeJavaScript(value);
    }
}
