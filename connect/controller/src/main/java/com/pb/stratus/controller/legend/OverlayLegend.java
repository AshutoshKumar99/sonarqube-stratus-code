package com.pb.stratus.controller.legend;

import java.io.Serializable;
import java.util.List;

/**
 * An OverlayLegend contains all the legend data that describes a single 
 * overlay. 
 */
public class OverlayLegend implements Serializable
{
    
    private static final long serialVersionUID = -3870578450578426032L;

    private String id;
    
    private List<LegendItem> legendItems;

    private String tableName;

    private boolean isEndUserThematic;

    private String overlayOrder;
    
    public OverlayLegend(String path, List<LegendItem> legendItems)
    {
        this.id = path;
        this.legendItems = legendItems;
    }
    
    public List<LegendItem> getLegendItems()
    {
        return legendItems;
    }
    
    public String getTitle()
    {
        return id;
    }

    public String getOverlayName()
    {
        if(id.indexOf('/')!=-1) {
            return id.substring(id.lastIndexOf('/')+1);
        }
        else return id;
    }

    public String getTableName() {
        if(tableName.indexOf('/')!=-1) {
            return tableName.substring(tableName.lastIndexOf('/')+1);
        }
        else return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isEndUserThematic() {
        return isEndUserThematic;
    }

    public void setEndUserThematic(boolean endUserThematic) {
        isEndUserThematic = endUserThematic;
    }

    public String getOverlayOrder() {
        return overlayOrder;
    }

    public void setOverlayOrder(String overlayOrder) {
        this.overlayOrder = overlayOrder;
    }
}
