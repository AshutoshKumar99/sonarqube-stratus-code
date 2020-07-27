package com.pb.stratus.controller.print.content;

import com.mapinfo.midev.service.mapping.v1.Map;

import java.util.Comparator;

/**
 * Class of maps which can be generated dynamically on connect e.g. Thematic Maps and Query Result Overlay Map
 * These maps are not present in mapconfig and are not added via adminconsole
 * Created by vi012gu on 2/27/2015.
 */
public abstract class RuntimeMap {

    protected String name;
    protected String opacity;
    protected String overlayOrder;
    protected Map mapObject;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpacity() {
        return opacity;
    }

    public void setOpacity(String opacity) {
        this.opacity = opacity;
    }

    public String getOverlayOrder() {
        return overlayOrder;
    }

    public void setOverlayOrder(String overlayOrder) {
        this.overlayOrder = overlayOrder;
    }

    public Map getMapObject() {
        return mapObject;
    }

    public void setMapObject(Map mapObject) {
        this.mapObject = mapObject;
    }
}
