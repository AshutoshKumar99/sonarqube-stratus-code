/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pb.stratus.controller.print.template;

import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.compositor.MapCompositor;
import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.legend.LegendData;
import com.pb.stratus.controller.legend.LegendService;
import com.pb.stratus.controller.print.*;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.print.config.MapConfigRepositoryImpl;
import com.pb.stratus.controller.print.content.*;
import com.pb.stratus.controller.print.template.component.*;
import com.pb.stratus.controller.print.template.component.TextComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static com.pb.stratus.controller.print.TemplateContentHandler.NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class StratusComponentFactoryTest 
{
    
    private StratusComponentFactory componentFactory;
    
    private DocumentParameters params;
    
    private MapCompositor mockMapCompositor;

    private List<Marker> findMyNearestMarkers;

    private List<Annotation> annotations;

    private List<WMSMap> wmsMapList ;

    private List<ThematicMap> thematicMapList ;

    private Marker locatorMarker;

    private Marker callOutMarker;

    private LegendService mockLegendService;

    private Locale oldLocale;

    private ScaleBarFactory mockScaleBarFactory;

    private BufferedImage mockMapImage;

    private Dimension expectedMapImageSize;

    private List<QueryResultOverlayMap> mockQueryResultOverlayMapList;

    private BoundingBoxCalculator mockCalculator;
    
    private MapConfigRepository mockRepository;
    
    private MapConfig mockMapConfig;

    private Map<String, List<LayerInfoBean>> namedLayersInfoMap;
    
    int zoomLevel = 3;

    String displayUnit = "METER";
    
    @Before
    public void setUp()
    {
        locatorMarker = mock(Marker.class);

        findMyNearestMarkers = Arrays.asList(mock(Marker.class), mock(Marker.class));
        FmnResult r1 = new FmnResult(null, null, null, null, null, 
                findMyNearestMarkers.get(0));
        FmnResult r2 = new FmnResult(null, null, null, null, null,
                findMyNearestMarkers.get(1));
        FmnResultsCollection fmnResults = new FmnResultsCollection(null,
                Arrays.asList(r1, r2));

        annotations = Arrays.asList(mock(Annotation.class));
        wmsMapList = Arrays.asList(mock(WMSMap.class));
        thematicMapList = Arrays.asList(mock(ThematicMap.class));
        mockQueryResultOverlayMapList = Arrays.asList(mock(QueryResultOverlayMap.class));
        namedLayersInfoMap = new HashMap<String, List<LayerInfoBean>>();
        populateMockDataForNamedLayersInfo(namedLayersInfoMap);
        params = new DocumentParameters("someTitle", null, null, true, "someLegendTitle",
                Arrays.asList(new LayerBean("baseMap1"), new LayerBean("overlay1"), new LayerBean("overlay2")),
                new BoundingBox(2, 1, 1, 2, "someSrs"), "someMapConfig", fmnResults, annotations, locatorMarker,
                wmsMapList,thematicMapList, mockQueryResultOverlayMapList, 3, false, 150, null, displayUnit, true, namedLayersInfoMap);
        mockMapCompositor = mock(MapCompositor.class);
        mockLegendService = mock(LegendService.class);
        mockScaleBarFactory = mock(ScaleBarFactory.class);
        mockCalculator = mock(BoundingBoxCalculator.class);
        mockRepository = mock(MapConfigRepositoryImpl.class);
        mockMapConfig = mock(MapConfig.class);
        
        when(mockRepository.getMapConfig(any(String.class)))
            .thenReturn(mockMapConfig);
        
        componentFactory = new StratusComponentFactory(mockMapCompositor,  
                mockLegendService, mockScaleBarFactory, mockCalculator, 
                mockRepository);
        oldLocale = LocaleResolver.getLocale();
        expectedMapImageSize = new Resolution(150).calculatePixelDimensions(
                "1cm", "2cm");
    }

    private void populateMockDataForNamedLayersInfo(Map<String, List<LayerInfoBean>> layersInfoMap) {

        for(int j=0; j < 2; j++) {
            List<LayerInfoBean> layersList = new ArrayList<LayerInfoBean>();
            for (int i=0; i<3; i++) {
                LayerInfoBean layerInfo = new LayerInfoBean();
                layerInfo.setLayerIndex(i);
                layerInfo.setLayerName("NamedLayer"+i);
                layerInfo.setLayerMapPath("LayerMapName");
                layerInfo.setLayerOpacity(1);
                layerInfo.setLayerVisibility(true);

                layersList.add(layerInfo);
            }
            layersInfoMap.put("someMap"+j, layersList);
        }
    }
    
    @After
    public void tearDown()
    {
        LocaleResolver.setLocale(oldLocale);
    }
    
    
    @Test
    public void shouldReturnNullComponentIfElementUnknown()
    {
        QName qname = new QName("qwerty", "qwerty");
        Component result = componentFactory.createComponent(qname, null, 
                params);
        assertTrue(result instanceof NullComponent);
    }

    @Test
    public void shouldReturnTextComponentOnAddressElement()
    {
        Component comp = componentFactory.createComponent(
                new QName(NAMESPACE, "address"), null, params);
        assertEquals(comp, new TextComponent("someTitle"));
    }
    
    @Test
    public void shouldReturnImageComponentOnMapElement() throws IOException
    {
        setUpMockMapCompositor();
        Component comp = createImageComponent();
        assertEquals(new ImageComponent(mockMapImage, "1cm", "2cm"), comp);
        verify(mockMapCompositor).renderMap(any(BoundingBox.class),
                eq(expectedMapImageSize), eq(params.getMapConfigName()),
                eq(params.getLayers()), eq(params.getNamedLayersInfo()), eq(findMyNearestMarkers), eq(annotations),
                eq(callOutMarker), eq(locatorMarker),eq(wmsMapList), eq(zoomLevel), eq(displayUnit), eq(thematicMapList),
                eq(mockQueryResultOverlayMapList));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldUseBoundingBoxCalculatorToAdaptBoundsForMapImage()
    {
        setUpMockMapCompositor();
        BoundingBox mockBoundingBox = mock(BoundingBox.class);
        when(mockCalculator.calculate(any(BoundingBox.class), 
                any(Dimension.class))).thenReturn(mockBoundingBox);
        createImageComponent();
        verify(mockCalculator).calculate(params.getBoundingBox(),
                expectedMapImageSize);
        verify(mockMapCompositor).renderMap(eq(mockBoundingBox), 
                any(Dimension.class), any(String.class), any(List.class), any(Map.class),
                any(List.class), any(List.class), any(Marker.class),any(Marker.class), any(List.class),anyInt(),
                anyString(),any(List.class), any(List.class));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldBeAbleToCreateImageComponentIfNoFmnResultsInParams()
    {
        setUpMockMapCompositor();
        params = spy(params);
        doReturn(null).when(params).getFmnResults();
        createImageComponent();
        List<Marker> emptyMarkerList = Collections.emptyList();
        List<Annotation> emptyAnnotationList = Collections.emptyList();
        verify(mockMapCompositor).renderMap(any(BoundingBox.class), 
                any(Dimension.class), any(String.class), any(List.class), any(Map.class),
                any(List.class), any(List.class), any(Marker.class), any(Marker.class), any(List.class), anyInt(),
                anyString(), any(List.class), any(List.class));
    }
    
    @SuppressWarnings("unchecked")
    private void setUpMockMapCompositor()
    {
        mockMapImage = new BufferedImage(10, 20, 
                BufferedImage.TYPE_3BYTE_BGR);
        when(mockMapCompositor.renderMap(any(BoundingBox.class), 
                any(Dimension.class), any(String.class), any(List.class), any(Map.class),
                any(List.class), any(List.class), any(Marker.class), any(Marker.class), any(List.class), anyInt(),
                anyString(),any(List.class), any(List.class))).thenReturn(mockMapImage);
    }
    
    private Component createImageComponent()
    {
        Attributes attrs = XslFoUtils.createAttribute("width", "1cm", 
                "height", "2cm");
        return componentFactory.createComponent(new QName(NAMESPACE, "map"), 
                attrs, params);
    }
    
    @Test
    public void shouldReturnFmnResultsComponentOnFmnResultsElement()
    {
        Component comp = componentFactory.createComponent(
                new QName(NAMESPACE, "fmn-results"), 
                XslFoUtils.createAttribute(XslFoUtils.MASTER_REFERENCE_ATTR, 
                        "someMaster"), params);
        Component expectedComponent = new FmnResultsComponent(
                params.getFmnResults(), "someMaster");
        assertEquals(expectedComponent, comp);
    }
    
    @Test
    public void shouldReturnLegendComponentOnLegendElement()
    {
        LegendData mockLegendData =  mock(LegendData.class);
        when(mockLegendService.getLegendData(any(Locale.class), anyListOf(WMSMap.class),anyListOf(ThematicMap.class),
                anyListOf(QueryResultOverlayMap.class), any(Map.class), (String[]) anyVararg())).thenReturn(mockLegendData);
        Locale locale = new Locale("de", "AT");
        LocaleResolver.setLocale(locale);
        Component comp = componentFactory.createComponent(
                new QName(NAMESPACE, "legend"), 
                XslFoUtils.createAttribute(), params);
        Component expectedComponent = new LegendComponent(
                params.getLegendTitle(),  mockMapConfig,
                mockLegendData);
        assertEquals(expectedComponent, comp);
        verify(mockLegendService).getLegendData(locale,wmsMapList,thematicMapList, mockQueryResultOverlayMapList,
                namedLayersInfoMap, new String[] {"overlay1", "overlay2"});
    }


    
    @Test
    public void shouldReturnScaleBarComponentOnScaleBarElement()
    {
        ScaleBar mockScaleBar = mock(ScaleBar.class);
        when(mockScaleBarFactory.createScaleBar(any(BoundingBox.class), 
                any(Dimension.class), any(Resolution.class), anyBoolean()))
                .thenReturn(mockScaleBar);
        Component comp = createScaleBarComponent();
        assertEquals(new ScaleBarComponent(mockScaleBar, null), comp);
        verify(mockScaleBarFactory).createScaleBar(any(BoundingBox.class), 
                eq(new Dimension(591, 650)), eq(new Resolution(150)), eq(true));
    }
    
    @Test
    public void shouldUseBoundingBoxCalculatorForScaleBar()
    {
        BoundingBox mockBoundingBox = mock(BoundingBox.class);
        when(mockCalculator.calculate(any(BoundingBox.class), 
                any(Dimension.class))).thenReturn(mockBoundingBox);
        createScaleBarComponent();
        verify(mockScaleBarFactory).createScaleBar(eq(mockBoundingBox), 
                any(Dimension.class), any(Resolution.class), anyBoolean());
    }
    
    private Component createScaleBarComponent()
    {
        return componentFactory.createComponent(
                new QName(NAMESPACE, "scale-bar"), 
                XslFoUtils.createAttribute("map-width", "10cm", 
                        "map-height", "11cm"), params);
    }

}
