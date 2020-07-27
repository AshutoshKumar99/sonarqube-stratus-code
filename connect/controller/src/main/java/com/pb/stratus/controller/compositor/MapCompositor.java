package com.pb.stratus.controller.compositor;

import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.geometries.v1.Polygon;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.*;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.Watermark;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.print.content.ThematicMap;
import com.pb.stratus.controller.print.content.WMSMap;
import com.pb.stratus.controller.print.render.AnnotationRenderer;
import com.pb.stratus.controller.print.render.CopyrightRenderer;
import com.pb.stratus.core.configuration.ConfigReader;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Creates a composite image by combining a composite layer image, watermark,
 * markers, and a copyright.
 */
public class MapCompositor {
    private final int COPYRIGHT_RESOLUTION = 150;

    private WatermarkRenderer watermarkRenderer;

    private MarkerRenderer markerRenderer;

    private CopyrightRenderer copyrightRenderer;

    private MapConfigRepository repo;

    private ConfigReader configReader;

    private LayerCompositor layerCompositor;

    private BingAttributionRenderer bingAttributionRenderer;

    private OpenStreetMapAttributionRenderer openStreetMapAttributionRenderer;

    private AnnotationRenderer annotationRenderer;

    public MapCompositor(LayerCompositor layerCompositor,
                         WatermarkRenderer watermarkRenderer, MarkerRenderer markerRenderer,
                         CopyrightRenderer copyrightRenderer,
                         BingAttributionRenderer bingAttributionRenderer,
                         OpenStreetMapAttributionRenderer openStreetMapAttributionRenderer,
                         MapConfigRepository repo, ConfigReader configReader,
                         AnnotationRenderer annotationRenderer) {
        this.layerCompositor = layerCompositor;
        this.copyrightRenderer = copyrightRenderer;
        this.markerRenderer = markerRenderer;
        this.repo = repo;
        this.watermarkRenderer = watermarkRenderer;
        this.configReader = configReader;
        this.bingAttributionRenderer = bingAttributionRenderer;
        this.openStreetMapAttributionRenderer = openStreetMapAttributionRenderer;
        this.annotationRenderer = annotationRenderer;
    }

	public BufferedImage renderMap(BoundingBox boundingBox,
			Dimension imageSize, String mapConfigName, List<LayerBean> layers,
			List<Marker> markers, List<Annotation> annotations, Marker callOutMarker, Marker locatorMarker,
            List<WMSMap> wmsMapList, int zoomLevel, String displayUnit) {
		MapConfig mapConfig = getMapConfig(mapConfigName);
		BufferedImage canvas = new BufferedImage(imageSize.width,
				imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = canvas.createGraphics();
		try {
			addLayers(g, layers, mapConfig, boundingBox, imageSize, zoomLevel, wmsMapList);
			addWatermark(g, imageSize, mapConfig);
			addFindMyNearestMarkers(g, imageSize, boundingBox, markers);
            addMarker(g, imageSize, boundingBox, callOutMarker);
            addMarker(g, imageSize, boundingBox, locatorMarker);
            addCopyright(g, imageSize, layers, mapConfig);
            addAttribution(g, imageSize, boundingBox, layers, mapConfig,
                    zoomLevel);
            addAnnotations(g, imageSize, boundingBox, annotations, displayUnit);
        } finally {
            g.dispose();
        }
        return canvas;
    }

    /**
     * Renders map and NamedLayers by passing layers list for NamedLayers
     * @param boundingBox
     * @param imageSize
     * @param mapConfigName
     * @param layers
     * @param layerInfoMap
     * @param markers
     * @param annotations
     * @param callOutMarker
     * @param locatorMarker
     * @param wmsMapList
     * @param zoomLevel
     * @param displayUnit
     * @return bufferedImage
     */
    public BufferedImage renderMap(BoundingBox boundingBox, Dimension imageSize, String mapConfigName,
                                   List<LayerBean> layers, Map<String,List<LayerInfoBean>> layerInfoMap,
                                   List<Marker> markers, List<Annotation> annotations, Marker callOutMarker,
                                   Marker locatorMarker, List<WMSMap> wmsMapList, int zoomLevel, String displayUnit,
                                   List<ThematicMap> thematicMapList, List<QueryResultOverlayMap> queryResultOverlayMapList) {
        MapConfig mapConfig = getMapConfig(mapConfigName);
        BufferedImage canvas = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = canvas.createGraphics();
        try {

            addLayers(g, layers, layerInfoMap, mapConfig, boundingBox, imageSize, zoomLevel, wmsMapList, false,
                    thematicMapList, queryResultOverlayMapList);

            addWatermark(g, imageSize, mapConfig);
            addFindMyNearestMarkers(g, imageSize, boundingBox, markers);
            addMarker(g, imageSize, boundingBox, callOutMarker);
            addMarker(g, imageSize, boundingBox, locatorMarker);
            addCopyright(g, imageSize, layers, mapConfig);
            addAttribution(g, imageSize, boundingBox, layers, mapConfig,
                    zoomLevel);
            addAnnotations(g, imageSize, boundingBox, annotations, displayUnit);
            addLabelLayers(g, layers, layerInfoMap, mapConfig, boundingBox, imageSize, zoomLevel, null, true);

        } finally {
            g.dispose();
        }
        return canvas;
    }

    private void addAnnotations(Graphics2D g, Dimension imageSize,
                                BoundingBox boundingBox, List<Annotation> annotations, String displayUnit)
    {
        BufferedImage annotationLayer = this.annotationRenderer.renderAnnotations(
                imageSize, boundingBox, annotations, displayUnit);
        g.drawImage(annotationLayer, null, 0, 0);
    }

    /**
     * Dummy method to create a Geometry
     * @return
     */
    private Geometry createMockGeometry() {
        Polygon polygon = new Polygon();
        Ring ring = new Ring();
        LineString lineString = new LineString();


        Pos pos1 = new Pos(), pos2 = new Pos(), pos3 = new Pos(), pos4 = new Pos(), pos5 = new Pos();
        pos1.setX(527952.62976134);
        pos1.setY(184096.44189527);

        pos2.setX(527951.95837462);
        pos2.setY(184052.4660652);

        pos3.setX(528034.20324766);
        pos3.setY(184049.78051832);

        pos4.setX(528033.19616759);
        pos4.setY(184095.77050855);

        pos5.setX(527952.62976134);
        pos5.setY(184096.44189527);

        lineString.getPos().add(pos1);
        lineString.getPos().add(pos2);
        lineString.getPos().add(pos3);
        lineString.getPos().add(pos4);
        lineString.getPos().add(pos5);

        ring.setSrsName("epsg:27700");
        ring.getLineString().add(lineString);
        polygon.setSrsName("epsg:27700");
        polygon.setExterior(ring);
        return polygon;
    }

    private MapConfig getMapConfig(String name) {
        return repo.getMapConfig(name);
    }

	private void addLayers(Graphics2D g, List<LayerBean> layers,
			MapConfig mapConfig, BoundingBox boundingBox, Dimension imageSize,
			int zoomLevel, List<WMSMap> wmsMapList)
    {
        BufferedImage layersLayer = layerCompositor.compose(layers, mapConfig,
                boundingBox, imageSize, zoomLevel, wmsMapList);
        g.drawImage(layersLayer, null, 0, 0);
    }

    /**
     * Adds maps layers while handles namedLayers maps in a different way
     * @param g
     * @param layers
     * @param layerInfoMap
     * @param mapConfig
     * @param boundingBox
     * @param imageSize
     * @param zoomLevel
     * @param wmsMapList
     */
    private void addLayers(Graphics2D g, List<LayerBean> layers, Map<String,List<LayerInfoBean>> layerInfoMap,
                           MapConfig mapConfig, BoundingBox boundingBox, Dimension imageSize, int zoomLevel,
                           List<WMSMap> wmsMapList,boolean renderLabelLayer, List<ThematicMap> thematicMapList,
                           List<QueryResultOverlayMap> queryResultOverlayMapList)
    {

        BufferedImage layersLayer = layerCompositor.compose(layers, layerInfoMap, mapConfig,
                boundingBox, imageSize, zoomLevel, wmsMapList ,renderLabelLayer,thematicMapList, queryResultOverlayMapList);
        g.drawImage(layersLayer, null, 0, 0);
    }


    private void addLabelLayers(Graphics2D g, List<LayerBean> layers, Map<String,List<LayerInfoBean>> layerInfoMap,
                                MapConfig mapConfig, BoundingBox boundingBox, Dimension imageSize,
                                int zoomLevel, List<WMSMap> wmsMapList,boolean renderLabelLayer)
    {
        //override all layers with label layers
        layers = configHasLabelLayer(mapConfig, layers , layerInfoMap);
        if(layers.size() > 0){
            BufferedImage layersLayer = layerCompositor.compose(layers, layerInfoMap, mapConfig,
                    boundingBox, imageSize, zoomLevel, wmsMapList ,renderLabelLayer, null, null);
            g.drawImage(layersLayer, null, 0, 0);
        }
    }

    private void addWatermark(Graphics2D g, Dimension imageSize, MapConfig mapConfig)
    {
        Watermark watermarkConfig = mapConfig.getWatermark();
        if (watermarkConfig != null && StringUtils.isNotBlank(watermarkConfig.getImagePath()))
        {
            BufferedImage watermark = getWatermark(watermarkConfig
                    .getImagePath());
            if(watermark != null)
            {
                BufferedImage watermarkLayer = watermarkRenderer.renderWatermark(
                        watermark, watermarkConfig.getOpacity(), imageSize);
                g.drawImage(watermarkLayer, null, 0, 0);
            }
        }
    }

    /**
     *
     * @param imagePath
     * @return
     */
    private BufferedImage getWatermark(String imagePath)
    {
        try
        {
            InputStream is = null;
            if(imagePath.indexOf("theme/") != -1)
            {
                String[] relativePath = imagePath.split("theme/");
                String watermarkPath = "/theme/"+ relativePath[1];
                is = configReader.getConfigFile(watermarkPath);
                return ImageIO.read(is);
            }
            else
            {
                is = new URL(imagePath).openStream();
                return ImageIO.read(is);
            }
        }
        catch (Exception ex)
        {
            throw new RenderException(ex);
        }
    }

    private void addFindMyNearestMarkers(Graphics2D g, Dimension imageSize,
                                         BoundingBox boundingBox, List<Marker> markers) {
        if (markers != null && markers.size() > 0) {
            BufferedImage markerLayer = markerRenderer.renderMarkers(imageSize,
                    boundingBox, markers);
            g.drawImage(markerLayer, null, 0, 0);
        }
    }

    private void addMarker(Graphics2D g, Dimension imageSize,
                           BoundingBox boundingBox, Marker marker) {
        if (marker != null) {
            List<Marker> markerList = new ArrayList<Marker>();
            markerList.add(marker);
            BufferedImage markerLayer = markerRenderer.renderMarkers(imageSize,
                    boundingBox, markerList);
            g.drawImage(markerLayer, null, 0, 0);
        }
    }

	private void addCopyright(Graphics2D g, Dimension imageSize,
			List<LayerBean> layers, MapConfig mapConfig) {
		BufferedImage copyrightLayer = copyrightRenderer.renderCopyright(
				imageSize, new Resolution(COPYRIGHT_RESOLUTION), layers,
				mapConfig);
		g.drawImage(copyrightLayer, null, 0, 0);
	}

	private void addAttribution(Graphics2D g, Dimension imageSize,
			BoundingBox boundingBox, List<LayerBean> layers, MapConfig mapConfig,
			int zoomLevel) {
		if (bingAttributionRenderer.isBingBasemap(mapConfig)) {
			BufferedImage attributionLayer = bingAttributionRenderer
					.renderAttribution(imageSize, boundingBox, layers,
							mapConfig, zoomLevel);
			g.drawImage(attributionLayer, null, 0, 0);
		}
		if (openStreetMapAttributionRenderer.isOpenStreetBasemap(mapConfig)) {
			BufferedImage attributionLayer = openStreetMapAttributionRenderer
					.renderAttribution(imageSize, boundingBox, layers,
							mapConfig, zoomLevel);
			g.drawImage(attributionLayer, null, 0, 0);
		}
    }

    private List<LayerBean> configHasLabelLayer(MapConfig mapConfig ,List<LayerBean> layers, Map<String,List<LayerInfoBean>> layerInfoMap)
    {
        List<MapConfig.MapDefinition> mapconfigDefs = mapConfig.getMapDefinitions();
        Collections.reverse(mapconfigDefs);

        List<LayerBean> labelLayers = new ArrayList<>();

        for (MapConfig.MapDefinition mapDef : mapconfigDefs) {
            String mapName = mapDef.getMapName();
            String mapPath = mapDef.getRepositoryPath();

            for (LayerBean layer : layers)
            {
                if(mapName.equalsIgnoreCase(layer.getName()) || mapPath.equalsIgnoreCase(layer.getName()))
                {
                    LayerServiceType layerServiceType = mapConfig.getMapDefinitionByMapName(layer.getName()).getService();
                    if (LayerServiceType.MAPPING.equals(layerServiceType)) {
                        List<LayerInfoBean> layerList = layerInfoMap.get(layer.getName());
                        if (layerList != null) {
                            for (LayerInfoBean layerInfo : layerList) {
                                if (layerInfo.getLayerLabelPath() != null) {
                                    labelLayers.add(layer);
                                }
                            }
                        }
                    }
                }
            }
        }
        return labelLayers;
    }

}
