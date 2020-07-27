package com.pb.stratus.controller.print;


import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.content.LayerBean;

import java.awt.*;
import java.util.List;

public class LayerRenderParams
{
    private BoundingBox boundingBox;
    private Dimension imageSize;
    private MapConfig mapConfig;
    private int zoomLevel;
    private LayerBean layer;
    private String layerName;
    private List<LayerInfoBean> namedLayersList;
    private boolean renderLabelLayer;

    public LayerRenderParams(String layerName, BoundingBox boundingBox, Dimension imageSize, MapConfig mapConfig, int zoomLevel,LayerBean layer)
    {
        this.layerName = layerName;
        this.boundingBox = boundingBox;
        this.imageSize = imageSize;
        this.mapConfig = mapConfig;
        this.zoomLevel = zoomLevel;
        this.layer = layer;
    }

    public LayerRenderParams(List<LayerInfoBean> namedLayersList, BoundingBox boundingBox, Dimension imageSize, MapConfig mapConfig, int zoomLevel
    , boolean renderLabelLayer,LayerBean layer)
    {
        this.namedLayersList = namedLayersList;
        this.boundingBox = boundingBox;
        this.imageSize = imageSize;
        this.mapConfig = mapConfig;
        this.zoomLevel = zoomLevel;
        this.renderLabelLayer = renderLabelLayer;
        this.layer = layer;
        this.layerName = this.layer.getName();
    }

    public LayerRenderParams(String layerName, BoundingBox boundingBox, Dimension imageSize, MapConfig mapConfig,LayerBean layer)
    {
        this(layerName, boundingBox, imageSize, mapConfig, 0,layer);
    }

    public BoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public Dimension getImageSize()
    {
        return imageSize;
    }

    public MapConfig getMapConfig()
    {
        return this.mapConfig;
    }

    public int getZoomLevel()
    {
        return this.zoomLevel;
    }

    public String getLayerName()
    {
        return this.layerName;
    }

    public LayerBean getLayer()
    {
        return this.layer;
    }

    public List<LayerInfoBean> getNamedLayersList() {
        return namedLayersList;
    }

    public boolean isRenderLabelLayer() {
        return renderLabelLayer;
    }


}
