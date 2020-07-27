package com.pb.stratus.controller.print;

import com.pb.stratus.controller.compositor.LayerCompositor;
import com.pb.stratus.controller.compositor.LayerCompositorImpl;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.print.content.ThematicMap;
import com.pb.stratus.controller.print.render.LayerRenderer;
import com.pb.stratus.controller.print.render.QueryResultRenderer;
import com.pb.stratus.controller.print.render.ThematicRenderer;
import com.pb.stratus.controller.util.ImageAssertUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import static org.mockito.Mockito.*;

public class LayerCompositorImplTest
{
    LayerRenderer renderBing;
    LayerRenderer renderTile;
    LayerRenderer renderSpatial;
    LayerRenderer renderThematic;
    LayerRenderer renderQueryResult;

    MapConfig mockMapConfig;
    
    Map<LayerServiceType, LayerRenderer> map 
            = new HashMap<LayerServiceType, LayerRenderer>(); 
    List<MapDefinition> defs = new ArrayList<MapDefinition>();  
    List<LayerBean> testLayers = new ArrayList<>();
    List<ThematicMap> mockThematicMapList;
    List<QueryResultOverlayMap> mockQueryResultOverlayMapList;
    String baseLayer = "layerBase";
    String overlay = "layerOverlay";
    private String imageMimeType = "someMimeType";
    BoundingBox testBounds;
    Dimension testImageSize;
    int zoomLevel = 3;
    Map<String, List<LayerInfoBean>> namedLayerInfoMap = new HashMap<String, List<LayerInfoBean>>();

    @Before
    public void setUp() throws Exception
    {
        renderBing = mock(LayerRenderer.class);
        map.put(LayerServiceType.BING, renderBing);
        renderTile = mock(LayerRenderer.class);
        map.put(LayerServiceType.TILE, renderTile);
        renderSpatial = mock(LayerRenderer.class);
        map.put(LayerServiceType.MAPPING, renderSpatial);
        renderThematic = mock(ThematicRenderer.class);
        map.put(LayerServiceType.THEMATIC, renderThematic);
        renderQueryResult = mock(QueryResultRenderer.class);
        map.put(LayerServiceType.QUERYRESULT, renderQueryResult);

        testLayers.add(new LayerBean(baseLayer));
        testLayers.add(new LayerBean(overlay));
        testBounds = new BoundingBox(30, 10, 45, 60, "SomeSrs");
        testImageSize = new Dimension(200, 170);

        populateMockLayerInfoMap();

        ThematicMap mockThematicMap = mock(ThematicMap.class);
        mockThematicMapList = Arrays.asList(mockThematicMap);

        QueryResultOverlayMap mockQueryResultOverlayMap = mock(QueryResultOverlayMap.class);
        mockQueryResultOverlayMapList = Arrays.asList(mockQueryResultOverlayMap);
        
        MapConfig cfg = new MapConfig();
        MapDefinition def = cfg.createMapDefinition();
        cfg.setMapDefinitions(Arrays.asList(def));
        def.setMapName("testLayerName");
        def.setImageMimeType(imageMimeType);
        when((renderBing.render((LayerRenderParams)any()))).thenReturn(getIndividualImage("bing.png")); 
        when((renderSpatial.render((LayerRenderParams)any()))).thenReturn(getIndividualImage("spatial.png"));
        
        mockMapConfig = new MapConfig();
    }

    private void populateMockLayerInfoMap() {
        List<LayerInfoBean> mockNamedLayerInfoList = new ArrayList<LayerInfoBean>();

        LayerInfoBean layerInfoObj = new LayerInfoBean();
        layerInfoObj.setLayerMapPath(overlay);
        layerInfoObj.setLayerOpacity(0);
        layerInfoObj.setLayerVisibility(true);
        layerInfoObj.setLayerIndex(0);
        layerInfoObj.setLayerName(overlay+"NamedLayer");
        layerInfoObj.setLayerPath("/MyLayers/NamedLayer");
        mockNamedLayerInfoList.add(layerInfoObj);

        namedLayerInfoMap.put(overlay,mockNamedLayerInfoList);
    }

    private BufferedImage getIndividualImage(String fileName) throws Exception
    {
        InputStream ios = 
            this.getClass().getResourceAsStream(fileName);
        BufferedImage img = ImageIO.read(ios);
        return img;
    }

    private void createMapDefinitionsTransluscentOverlay(MapConfig mockMapConfig)
    {
        List<MapDefinition> defs = new ArrayList<MapDefinition>();
        MapDefinition def = getNewMapDef(mockMapConfig, overlay,
                LayerServiceType.MAPPING, 0.5f);
        defs.add(def);
        def = getNewMapDef(mockMapConfig, baseLayer,
                LayerServiceType.BING, 1.0f);
        defs.add(def);
        mockMapConfig.setMapDefinitions(defs);
    }
    
    private void createMapDefinitionsOpaqueOverlay(MapConfig mockMapConfig)
    {
        List<MapDefinition> defs = new ArrayList<MapDefinition>();
        MapDefinition def = getNewMapDef(mockMapConfig, overlay,
                LayerServiceType.MAPPING, 1.0f);
        defs.add(def);
        def = getNewMapDef(mockMapConfig, baseLayer,
                LayerServiceType.BING, 1.0f);
        defs.add(def);
        mockMapConfig.setMapDefinitions(defs);
    }
    
    private MapDefinition getNewMapDef(MapConfig mapConfig, String layer, 
            LayerServiceType service, float opacity)
    {
        MapDefinition def = mockMapConfig.createMapDefinition();
        def.setMapName(layer);
        def.setRepositoryPath(layer);
        def.setService(service);
        def.setOpacity(opacity);
        return def;
    }


    @Test
    public void testRenderTransluscentOverlay() throws Exception
    {
        createMapDefinitionsTransluscentOverlay(mockMapConfig);

        LayerCompositor comp = new LayerCompositorImpl(map);
        BufferedImage composite = comp.compose(testLayers, mockMapConfig, null, new Dimension(100, 100), zoomLevel,null);
        BufferedImage expected = getIndividualImage("bingandspatial.png");
        ImageAssertUtils.assertImagesEquivalentAsPng(expected, composite);
   }

    @Test
    public void testRenderTransluscentOverlayByPassingNamedLayers() throws Exception
    {
        createMapDefinitionsTransluscentOverlay(mockMapConfig);

        for (String key : namedLayerInfoMap.keySet()) {
            for (int i=0; i<namedLayerInfoMap.get(key).size(); i++) {
                namedLayerInfoMap.get(key).get(i).setLayerOpacity(0.5f);
            }
        }

        LayerCompositor comp = new LayerCompositorImpl(map);
        BufferedImage composite = comp.compose(testLayers, namedLayerInfoMap, mockMapConfig, null, new Dimension(100, 100),
                zoomLevel, null, false, mockThematicMapList, mockQueryResultOverlayMapList);
        BufferedImage expected = getIndividualImage("bingandspatial.png");
        ImageAssertUtils.assertImagesEquivalentAsPng(expected, composite);
    }

    @Test
    public void testRenderOpaqueOverlay() throws Exception
    {
        createMapDefinitionsOpaqueOverlay(mockMapConfig);

        LayerCompositor comp = new LayerCompositorImpl(map);
        BufferedImage composite = comp.compose(testLayers, mockMapConfig, null, new Dimension(100, 100), zoomLevel,null);
        BufferedImage expected = getIndividualImage("bingspatialnontransluscent.png");
        ImageAssertUtils.assertImagesEquivalentAsPng(expected, composite);
    }

    @Test
    public void testRenderOpaqueOverlayByPassingNamedLayers() throws Exception
    {
        createMapDefinitionsOpaqueOverlay(mockMapConfig);

        for (String key : namedLayerInfoMap.keySet()) {
            for (int i=0; i<namedLayerInfoMap.get(key).size(); i++) {
                namedLayerInfoMap.get(key).get(i).setLayerOpacity(1.0f);
            }
        }
        LayerCompositor comp = new LayerCompositorImpl(map);
        BufferedImage composite = comp.compose(testLayers, namedLayerInfoMap, mockMapConfig, null, new Dimension(100, 100),
                zoomLevel, null, false, mockThematicMapList, mockQueryResultOverlayMapList);
        BufferedImage expected = getIndividualImage("bingspatialnontransluscent.png");
        ImageAssertUtils.assertImagesEquivalentAsPng(expected, composite);
    }

    @SuppressWarnings("unused")
    private void invokeComposeForBaseAndOverlay()
    {
        createMapDefinitionsOpaqueOverlay(mockMapConfig);
        LayerCompositor comp = new LayerCompositorImpl(map);
        BufferedImage composite = comp.compose(testLayers, mockMapConfig,
                testBounds, testImageSize, zoomLevel,null);
    }
    
    @Test
    public void testIsBoundingBoxCorrectlyPassedToLayerRenderers()
    {
        invokeComposeForBaseAndOverlay();
        verify(renderBing).render(any(LayerRenderParams.class));
        verify(renderSpatial).render(any(LayerRenderParams.class));
    }
    
    @Test
    public void testIsImageSizeCorrectlyPassedToLayerRenderers()
    {
        invokeComposeForBaseAndOverlay();
        verify(renderBing).render(any(LayerRenderParams.class));
        verify(renderSpatial).render(any(LayerRenderParams.class));
    }
    
    @Test
    public void testCorrectRenderersInvokedForTheLayers()
    {
        invokeComposeForBaseAndOverlay();
        verify(renderBing).render(any(LayerRenderParams.class));
        verify(renderSpatial).render(any(LayerRenderParams.class));
    }
}
