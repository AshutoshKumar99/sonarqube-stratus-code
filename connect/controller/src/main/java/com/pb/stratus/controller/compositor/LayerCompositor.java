package com.pb.stratus.controller.compositor;

import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.content.ThematicMap;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.print.content.WMSMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public interface LayerCompositor
{
    public BufferedImage compose(List<LayerBean> layers, MapConfig mapConfig,
            BoundingBox boundingBox, Dimension imageSize, int zoomLevel, List<WMSMap> wmsMapList);

    /**
     * Now with list of map layers we also pass the list of namedLayers
     * @param layers
     * @param layerInfoMap
     * @param mapConfig
     * @param boundingBox
     * @param imageSize
     * @param zoomLevel
     * @param wmsMapList
     * @param renderLabelLayer
     * @param thematicMapList
     * @param queryResultOverlayMapsList
     * @return
     */
    public BufferedImage compose(List<LayerBean> layers, Map<String,List<LayerInfoBean>> layerInfoMap, MapConfig mapConfig,
            BoundingBox boundingBox, Dimension imageSize ,int zoomLevel, List<WMSMap> wmsMapList,
            boolean renderLabelLayer, List<ThematicMap> thematicMapList, List<QueryResultOverlayMap> queryResultOverlayMapsList);
}
