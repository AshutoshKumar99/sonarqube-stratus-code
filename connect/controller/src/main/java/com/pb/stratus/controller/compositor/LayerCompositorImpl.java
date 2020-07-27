package com.pb.stratus.controller.compositor;

import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.RenderException;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.*;
import com.pb.stratus.controller.print.render.LayerRenderer;
import com.pb.stratus.controller.print.render.QueryResultRenderer;
import com.pb.stratus.controller.print.render.ThematicRenderer;
import com.pb.stratus.controller.print.render.WMSRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class LayerCompositorImpl implements LayerCompositor
{
    private static final Logger logger = LogManager.getLogger(LayerCompositorImpl.class);
    Map<LayerServiceType, LayerRenderer> layerRenderers;

    public LayerCompositorImpl(Map<LayerServiceType, LayerRenderer> layerRenderers)
    {
        this.layerRenderers = layerRenderers;
    }

    public BufferedImage compose(List<LayerBean> layers, MapConfig mapConfig,
            BoundingBox boundingBox, Dimension imageSize, int zoomLevel, List<WMSMap> wmsMapList)
    {
        BufferedImage compositeImage = new BufferedImage(imageSize.width, 
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D graphicsCanvas = (Graphics2D) compositeImage.getGraphics();

        List<MapDefinition> mapconfigDefs = mapConfig.getMapDefinitions();
        Collections.reverse(mapconfigDefs);
        for(MapDefinition mapDef : mapconfigDefs){
            String mapName = mapDef.getMapName();
            // check if map is in  non-wms layers
            for (LayerBean layer : layers)
            {
                if(mapName.equalsIgnoreCase(layer.getName())){
                    LayerRenderer renderer = getLayerRenderer(layer.getName(), mapConfig);
                    LayerRenderParams params = new LayerRenderParams(layer.getName(),
                            boundingBox, imageSize, mapConfig, zoomLevel,layer);
                    BufferedImage image = renderer.render(params);
                    float opacity = getOpacity(layer.getName(), mapConfig);
                    graphicsCanvas = addImageToCanvas(image, graphicsCanvas, opacity, false);
                    break;
                }
            }
            // check if map is in wms layers list
            if(wmsMapList != null && wmsMapList.size() > 0){
                for (WMSMap wmsMap : wmsMapList)
                {
                    if(mapName.equalsIgnoreCase(wmsMap.getName())){
                        LayerRenderParams params = new LayerRenderParams(wmsMap.getName(),
                                boundingBox, imageSize, mapConfig, zoomLevel,null);
                        WMSRenderer renderer = new WMSRenderer(wmsMap);
                        BufferedImage image = renderer.render(params);
                        float opacity = getOpacity(wmsMap.getName(), mapConfig);
                        if(wmsMap.isBaseMap()){
                            graphicsCanvas = addImageToCanvas(image, graphicsCanvas, opacity, true);
                        }else{
                            graphicsCanvas = addImageToCanvas(image, graphicsCanvas, opacity, false);
                        }
                        break;
                    }
                }
            }
        }
        graphicsCanvas.dispose();
        return compositeImage;
    }

    /**
     * We need list of NamedLayers to check un-check the layers of a particular map
     * @param layers
     * @param layerInfoMap
     * @param mapConfig
     * @param boundingBox
     * @param imageSize
     * @param zoomLevel
     * @param wmsMapList
     * @return  buffered Image
     */
    @Override
    public BufferedImage compose(List<LayerBean> layers, Map<String,List<LayerInfoBean>> layerInfoMap,
                                 MapConfig mapConfig, BoundingBox boundingBox, Dimension imageSize, int zoomLevel,
                                 List<WMSMap> wmsMapList, boolean renderLabelLayer, List<ThematicMap> thematicMapList,
                                 List<QueryResultOverlayMap> queryResultOverlayMapsList)
    {
        BufferedImage compositeImage = new BufferedImage(imageSize.width, imageSize.height,
                                                        BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D graphicsCanvas = (Graphics2D) compositeImage.getGraphics();

        List<MapDefinition> mapconfigDefs = mapConfig.getMapDefinitions();
        Collections.reverse(mapconfigDefs);

        for(MapDefinition mapDef : mapconfigDefs){
            String mapName = mapDef.getMapName();
            String mapPath = mapDef.getRepositoryPath();
            // check if map is in  non-wms layers
            for (LayerBean layer : layers)
            {
                if(mapName.equalsIgnoreCase(layer.getName()) || mapPath.equalsIgnoreCase(layer.getName()))
                {
                    LayerServiceType layerServiceType = getLayerServiceType(layer.getName(), mapConfig);
                    LayerRenderer renderer = getLayerRenderer(layerServiceType);

                    LayerRenderParams params;
                    BufferedImage image;
                    float opacity;

                    if (LayerServiceType.MAPPING.equals(layerServiceType)) {
                        if (layerInfoMap.get(layer.getName()) != null) {
                            params = new LayerRenderParams(layerInfoMap.get(layer.getName()),
                                    boundingBox, imageSize, mapConfig, zoomLevel,renderLabelLayer,layer);
                            image = renderer.render(params);
                            opacity = layerInfoMap.get(layer.getName()).get(0).getLayerOpacity();
                        } else {
                            break;
                        }
                    } else {

                        params = new LayerRenderParams(layer.getName(), boundingBox,
                                                        imageSize, mapConfig, zoomLevel,layer);
                        image = renderer.render(params);
                        opacity = getOpacity(layer.getName(), mapConfig);
                    }
                    graphicsCanvas = addImageToCanvas(image, graphicsCanvas, opacity, false);
                    break;
                }
            }
            // check if map is in wms layers list
            if(wmsMapList != null && wmsMapList.size() > 0){
                for (WMSMap wmsMap : wmsMapList)
                {
                    if(mapName.equalsIgnoreCase(wmsMap.getName())){
                        LayerRenderParams params = new LayerRenderParams(wmsMap.getName(),
                                boundingBox, imageSize, mapConfig, zoomLevel,null);
                        WMSRenderer renderer = new WMSRenderer(wmsMap);
                        BufferedImage image = renderer.render(params);
                        float opacity;
                        if (wmsMap.getOpacity() != null && !wmsMap.getOpacity().isEmpty()) {
                            opacity = Float.parseFloat(wmsMap.getOpacity());
                        } else {
                            opacity = getOpacity(wmsMap.getName(), mapConfig);
                        }
                        if(wmsMap.isBaseMap()){
                            graphicsCanvas = addImageToCanvas(image, graphicsCanvas, opacity, true);
                        }else{
                            graphicsCanvas = addImageToCanvas(image, graphicsCanvas, opacity, false);
                        }
                        break;
                    }
                }
            }
        }

        // add all the runtime maps created on the fly on connect (not present in map config xml) e.g. Thematic and
        // query result
        ThematicRenderer thematicRenderer = (ThematicRenderer)getLayerRenderer(LayerServiceType.THEMATIC);
        QueryResultRenderer queryResultRenderer = (QueryResultRenderer)getLayerRenderer(LayerServiceType.QUERYRESULT);

        List<RuntimeMap> runtimeMapsList = new ArrayList<>();

        if (thematicMapList != null && thematicMapList.size() > 0) {
            runtimeMapsList.addAll(thematicMapList);
        }

        if (queryResultOverlayMapsList != null && queryResultOverlayMapsList.size() > 0) {
            runtimeMapsList.addAll(queryResultOverlayMapsList);
        }

        Collections.sort(runtimeMapsList, new RuntimeMapOrderComparator());

        for (RuntimeMap runtimeMap : runtimeMapsList) {
            LayerRenderParams params = new LayerRenderParams(runtimeMap.getName(),
                    boundingBox, imageSize, mapConfig, zoomLevel, null);

            BufferedImage image = null;
            if (runtimeMap instanceof ThematicMap) {
                thematicRenderer.setThematicMap((ThematicMap)runtimeMap);
                image = thematicRenderer.render(params);
            } else if (runtimeMap instanceof QueryResultOverlayMap) {
                queryResultRenderer.setQueryResultOverlayMap((QueryResultOverlayMap)runtimeMap);
                image = queryResultRenderer.render(params);
            }

            float opacity;
            if (runtimeMap.getOpacity() != null && !runtimeMap.getOpacity().isEmpty()) {
                opacity = Float.parseFloat(runtimeMap.getOpacity());
            } else {
                opacity =  Float.parseFloat("0.75"); // default opacity
            }
            graphicsCanvas = addImageToCanvas(image, graphicsCanvas, opacity, false);
        }
        graphicsCanvas.dispose();
        return compositeImage;
    }

    private Graphics2D addImageToCanvas (BufferedImage image,
                            Graphics2D graphicsCanvas, float opacity, boolean addAsBase)
    {
        if(image == null){
            return graphicsCanvas;
        }
        if(addAsBase){
            graphicsCanvas.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.DST_OVER, opacity));
        }else{
            graphicsCanvas.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, opacity));
        }
        graphicsCanvas.drawImage(image, null, 0, 0);
        return graphicsCanvas;
    }

    private LayerServiceType getLayerServiceType(String layer, MapConfig mapConfig) {
        MapDefinition def = mapConfig.getMapDefinitionByMapName(layer);
        return def.getService();
    }

    private LayerRenderer getLayerRenderer(LayerServiceType serviceType)
    {
        LayerRenderer layerRenderer = layerRenderers.get(serviceType);
        if (layerRenderer == null)
        {
            throw new RenderException("Unsupported layer type '"  + serviceType + "'");
        }
        return layerRenderer;
    }

    private LayerRenderer getLayerRenderer(String layer, MapConfig mapConfig)
    {
        MapDefinition def = mapConfig.getMapDefinitionByMapName(layer);

        LayerRenderer layerRenderer = layerRenderers.get(def.getService());
        if (layerRenderer == null)
        {
            throw new RenderException("Unsupported layer type '"  + def.getService() + "'");
        }
        return layerRenderer;
    }

    private float getOpacity(String layer, MapConfig mapConfig)
    {
        MapDefinition def = mapConfig.getMapDefinitionByMapName(layer);
        return def.getOpacity();
    }

    class RuntimeMapOrderComparator implements Comparator<RuntimeMap> {
        @Override
        public int compare(RuntimeMap runtimeMap1, RuntimeMap runtimeMap2) {
            try {
                int runtimeMap1Order = Integer.parseInt(runtimeMap1.getOverlayOrder());
                int runtimeMap2Order = Integer.parseInt(runtimeMap2.getOverlayOrder());

                if (runtimeMap1Order < runtimeMap2Order) {
                    return -1;
                } else {
                    return 1;
                }
            } catch (NumberFormatException nfe) {
                logger.error("Exception while comparing the order of thematic and query result overlays", nfe);
                return 0;
            }

        }
    }
}
