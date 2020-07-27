package com.pb.stratus.controller.action;

import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.annotation.AnnotationParser;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.*;
import com.pb.stratus.controller.print.content.*;
import com.pb.stratus.controller.print.template.TemplateRenderer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 * This class tests the functionality of Print Action
 *
 */

public class PrintActionTest
{
    private PrintAction printAction;

    private MockHttpServletRequest mockRequest;

    private MockHttpServletResponse mockResponse;

    private TemplateRepository mockTemplateRepository;

    private TemplateRenderer mockTemplateRenderer;

    private FmnResultsCollectionParser mockFMNParser;

    private AnnotationParser mockAnnotationParser;

    private LocatorMarkerParser mockLocatorParser;

    private FeatureService mockFeatureService;

    private WMSMapParser mockWmsMapParser;

    @Before
    public void setUp()
    {
        mockTemplateRepository = mock(
                TemplateRepository.class);
        mockTemplateRenderer = mock(TemplateRenderer.class);
        InputStream is = new ByteArrayInputStream("someContent".getBytes());
        when(mockTemplateRenderer.render(any(Template.class),
                any(DocumentParameters.class))).thenReturn(is);
        mockFMNParser = mock(FmnResultsCollectionParser.class);
        mockAnnotationParser = mock(AnnotationParser.class);
        mockLocatorParser = mock(LocatorMarkerParser.class);
        mockFeatureService = mock(FeatureService.class);
        mockWmsMapParser =  mock(WMSMapParser.class);
        Marker mockMarker = mock(Marker.class);
        MockHttpServletRequest request = mock(MockHttpServletRequest.class);
        when(mockLocatorParser.createMarker(request.getParameter("addressx"), request.getParameter("addressy"), true, null))
                .thenReturn(mockMarker);
        //TODO Need to create Mocks for Mapping Service
        printAction = new PrintAction(mockTemplateRepository,
                mockTemplateRenderer, mockFMNParser, mockLocatorParser, mockAnnotationParser, mockFeatureService,mockWmsMapParser);
        setUpMockRequest();
        mockResponse = new MockHttpServletResponse();
    }

    private void setUpMockRequest()
    {
        mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("POST");
        mockRequest.addParameter(PrintAction.TEMPLATE_NAME_PARAM, 
                "someTemplate");
        mockRequest.addParameter(MapImageRequestParameters.LAYERS_PARAM,
                "[{name:'basemap1'},{name:'overlay1'},{name:'overlay2'}]");
        mockRequest.addParameter(MapImageRequestParameters.NORTH_PARAM, "2");
        mockRequest.addParameter(MapImageRequestParameters.SOUTH_PARAM, "1");
        mockRequest.addParameter(MapImageRequestParameters.WEST_PARAM, "3");
        mockRequest.addParameter(MapImageRequestParameters.EAST_PARAM, "4");
        mockRequest.addParameter(MapImageRequestParameters.SRS_PARAM, 
                "someSrs");

        String jsonStr = "{\"MUSHousingCensusMap2\":[{\"layerName\":\"Percent of Property Owned Outright\"," +
                 "\"layerPath\":\"/MyLayers/Percent of Property Owned Outright\"," +
                "\"layerOpacity\":null,\"layerIndex\":0,\"layerVisibility\":true,\"layerMapPath\":\"MUSHousingCensusMap2\"}]," +
                "\"Undergground-Stations\":[{\"layerName\":\"Undergground-Stations_Underground Stations\"," +
                "\"layerPath\":\"/MyLayers/Undergground-Stations_Underground Stations\"," +
                "\"layerOpacity\":null,\"layerIndex\":0,\"layerVisibility\":true,\"layerMapPath\":\"Undergground-Stations\"}]," +
                "\"Railway Stations Map\":[{\"layerName\":\"Railway Stations Map_Railway Stations\",\"layerOpacity\":null," +
                "\"layerIndex\":0,\"layerVisibility\":true,\"layerMapPath\":\"Railway Stations Map\"}]}";
        mockRequest.addParameter("namedLayersInfo" , jsonStr);
    }

    @Test
    public void shouldGetTemplateNameFromRequestParams() throws Exception
    {
        execute();
        verify(mockTemplateRepository).getTemplate("someTemplate");
    }

    @Test
    public void shouldCopyRenderedDocumentToResponse() throws Exception
    {
        execute();
        assertEquals("someContent",
                mockResponse.getContentAsString());
    }

    @Test
    public void shouldPassCorrectTemplateToRenderer() throws Exception
    {
        Template mockTemplate = mock(Template.class);
        when(mockTemplateRepository.getTemplate(any(String.class))).thenReturn(
                mockTemplate);
        execute();
        verify(mockTemplateRenderer).render(eq(mockTemplate),
                any(DocumentParameters.class));
    }

    @Test
    public void shouldPassCorrectBoundingBoxToRenderer() throws Exception
    {
        DocumentParameters params = executeAndCaptureParams();
        BoundingBox expected = new BoundingBox(2, 1, 3, 4, "someSrs");
        assertEquals(expected, params.getBoundingBox());
    }

    @Test
    public void shouldPassCorrectLayersToRenderer() throws Exception
    {
        DocumentParameters params = executeAndCaptureParams();
        List<LayerBean> expected = Arrays.asList(new LayerBean("basemap1"), new LayerBean("overlay1"),
                new LayerBean("overlay2"));
        assertEquals(expected.get(0).getName(), params.getLayers().get(0).getName());
        assertEquals(expected.get(1).getName(), params.getLayers().get(1).getName());
        assertEquals(expected.get(2).getName(), params.getLayers().get(2).getName());
    }

    @Test
    public void shouldPassCorrectTitleRenderer() throws Exception
    {
        String expectedTitle = "someTitle";
        mockRequest.addParameter(PrintAction.TITLE_PARAM, expectedTitle);
        DocumentParameters params = executeAndCaptureParams();
        assertEquals(expectedTitle, params.getTitle());
    }
    
    @Test
    public void shouldPassCorrectLegendTitleRenderer() throws Exception
    {
        String expectedTitle = "someLegendTitle";
        mockRequest.addParameter(PrintAction.LEGEND_TITLE_PARAM, expectedTitle);
        DocumentParameters params = executeAndCaptureParams();
        assertEquals(expectedTitle, params.getLegendTitle());
    }

    @Test
    public void shouldPassCorrectMapConfigNameToRenderer() throws Exception
    {
        String expectedMapConfigName = "someMapConfig";
        mockRequest.addParameter(MapImageRequestParameters.MAP_CONFIG_NAME_PARAM, 
                expectedMapConfigName);
        DocumentParameters params = executeAndCaptureParams();
        assertEquals(expectedMapConfigName, params.getMapConfigName());
    }

    @Test
    public void shouldParseFmnResults() throws Exception
    {
        mockRequest.addParameter(PrintAction.FMN_RESULTS_PARAM, "someJson");
        FmnResultsCollection mockResults = mock(FmnResultsCollection.class);
        when(mockFMNParser.parse(any(String.class))).thenReturn(mockResults);
        DocumentParameters params = executeAndCaptureParams();
        verify(mockFMNParser).parse("someJson");
        assertEquals(mockResults, params.getFmnResults());
    }

    @Test
    public void shouldNotParseFmnResultsIfNotSpecified() throws Exception
    {
        DocumentParameters params = executeAndCaptureParams();
        verifyZeroInteractions(mockFMNParser);
        assertNull(params.getFmnResults());
    }

    @Test
    public void shouldParseAnnotations() throws Exception {
        String validJSON = "[{\"type\": \"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[527952.62976134,184096.44189527],[527951.95837462,184052.4660652],[528034.20324766,184049.78051832],[528033.19616759,184095.77050855],[527952.62976134,184096.44189527]]]]}, \"properties\": {\"Color\": \"red\", \"Opacity\": \"0.5\", \"Stroke\": \"Solid\"}}]";;
        mockRequest.addParameter(PrintAction.ANNOTATION_RESULTS_PARAM, "someJson");
        List<Annotation> mockAnnotationResults = mock(List.class);

        when(mockAnnotationParser.parseAnnotations(any(String.class), any(String.class))).thenReturn(mockAnnotationResults);
        DocumentParameters params = executeAndCaptureParams();
        /**
         * Need to mock the annotation parser as it is created from within the method.
         */
        assertEquals(mockAnnotationResults, params.getAnnotations());
    }

    @Test
    public void shouldParseNamedLayerInfoMap() {
        String validJsonStr = "{\"/MyMaps/Map1\":[{\"layerName\":\"Map1_NamedLayer1\"," +
                "\"layerPath\":\"/MyLayers/Map1_NamedLayer1\"," +
                "\"layerOpacity\":0,\"layerIndex\":0,\"layerVisibility\":true,\"layerMapPath\":\"/MyMaps/Map1\"}," +
                "{\"layerName\":\"Map1_NamedLayer2\"," + "\"layerPath\":\"/MyLayers/Map1_NamedLayer2\"," +
                "\"layerOpacity\":0,\"layerIndex\":0,\"layerVisibility\":true,\"layerMapPath\":\"/MyMaps/Map1\"}]," +
                "\"/MyMaps/Map2\":[{\"layerName\":\"Map2_NamedLayer1\"," +
                "\"layerPath\":\"/MyLayers/NamedLayer\"," +
                "\"layerOpacity\":0,\"layerIndex\":0,\"layerVisibility\":true,\"layerMapPath\":\"/MyMaps/Map2\"}," +
                "{\"layerName\":\"Map2_NamedLayer2\"," +   "\"layerPath\":\"/MyLayers/Map2_NamedLayer2\"," +
                "\"layerOpacity\":0,\"layerIndex\":0,\"layerVisibility\":true,\"layerMapPath\":\"/MyMaps/Map2\"}]}";

        mockRequest.setParameter("namedLayersInfo", validJsonStr);

        Map<String, List<LayerInfoBean>> expectedNamedLayerInfoMap = getExpectedNamedLayerInfoMap();

        try {
            Method targetMethod = PrintAction.class.getDeclaredMethod("parseLayerInfoBean", HttpServletRequest.class);
            targetMethod.setAccessible(true);
            Map<String, List<LayerInfoBean>> actualMap = (Map<String, List<LayerInfoBean>>)targetMethod.invoke(printAction,mockRequest);
            assertEquals(expectedNamedLayerInfoMap.keySet(), actualMap.keySet());
            assertEquals(expectedNamedLayerInfoMap.get("/MyMaps/Map1").get(0).getLayerName(), actualMap.get("/MyMaps/Map1").get(0).getLayerName());
            assertEquals(expectedNamedLayerInfoMap.get("/MyMaps/Map1").get(0).getLayerPath(), actualMap.get("/MyMaps/Map1").get(0).getLayerPath());
            assertEquals(expectedNamedLayerInfoMap.get("/MyMaps/Map1").get(0).getLayerMapPath(), actualMap.get("/MyMaps/Map1").get(0).getLayerMapPath());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseLayerInfoBeanIfJsonStrIsEmpty() throws Exception
    {
        String emptyJsonStr = "";

        mockRequest.setParameter("namedLayersInfo", emptyJsonStr);

        Map<String, List<LayerInfoBean>> expectedNamedLayerInfoMap = new HashMap<String, List<LayerInfoBean>>();

        try {
            Method targetMethod = PrintAction.class.getDeclaredMethod("parseLayerInfoBean", HttpServletRequest.class);
            targetMethod.setAccessible(true);
            Map<String, List<LayerInfoBean>> actualMap = (Map<String, List<LayerInfoBean>>)targetMethod.invoke(printAction,mockRequest);
            assertEquals(expectedNamedLayerInfoMap, actualMap);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<LayerInfoBean>> getExpectedNamedLayerInfoMap() {
        Map<String, List<LayerInfoBean>> mockNamedLayerInfoMap = new HashMap<String, List<LayerInfoBean>>();
        for(int j=1; j<3; j++) {
            List<LayerInfoBean> mockNamedLayerInfoList = new ArrayList<LayerInfoBean>();
            for(int i=1; i<3; i++) {
                LayerInfoBean layerInfoObj = new LayerInfoBean();
                layerInfoObj.setLayerPath("/MyLayers/Map"+j+"_NamedLayer"+
                        i);
                layerInfoObj.setLayerMapPath("/MyMaps/Map"+j);
                layerInfoObj.setLayerOpacity(0);
                layerInfoObj.setLayerVisibility(true);
                layerInfoObj.setLayerIndex(0);
                layerInfoObj.setLayerName("Map"+j+"_NamedLayer"+i);
                mockNamedLayerInfoList.add(layerInfoObj);
            }
            mockNamedLayerInfoMap.put("/MyMaps/Map"+j,mockNamedLayerInfoList);
        }
        return mockNamedLayerInfoMap;
    }

    @Test
    public void shouldNotParseAnnotationsIfNotSpecified() throws Exception
    {
        DocumentParameters params = executeAndCaptureParams();
        verifyZeroInteractions(mockAnnotationParser);
        assertNull(params.getAnnotations());
    }

    @Test
    public void shouldParseWMSBaseMap() throws Exception
    {
        mockRequest.addParameter(PrintAction.WMS_BASE_MAP_PARAM, "someJson");
        WMSMap mockWmsBaseMap = mock(WMSMap.class);
        when(mockWmsMapParser.parseWmsMapJson(any(String.class), anyBoolean())).thenReturn(mockWmsBaseMap);
        DocumentParameters params = executeAndCaptureParams();
        verify(mockWmsMapParser).parseWmsMapJson("someJson", true);
        assertEquals(mockWmsBaseMap, params.getWmsMapList().get(0));
    }

    @Test
    public void shouldParseWMSOverlays() throws Exception
    {
        mockRequest.addParameter(PrintAction.WMS_OVERLAYS_MAP_PARAM, "someJson");
        List<WMSMap> mockWmsMapList = new ArrayList<WMSMap>();
        WMSMap mockWmsOverlay1 = mock(WMSMap.class);
        WMSMap mockWmsOverlay2 = mock(WMSMap.class);
        mockWmsMapList.add(mockWmsOverlay1);
        mockWmsMapList.add(mockWmsOverlay2);
        when(mockWmsMapParser.parseWMSMapJsonArray(any(String.class), anyBoolean())).thenReturn(mockWmsMapList);
        DocumentParameters params = executeAndCaptureParams();
        verify(mockWmsMapParser).parseWMSMapJsonArray("someJson",false);
        assertEquals(mockWmsMapList.size(), params.getWmsMapList().size());
        assertEquals(mockWmsMapList.get(0), params.getWmsMapList().get(0));
        assertEquals(mockWmsMapList.get(1), params.getWmsMapList().get(1));
    }

    @Test
    public void shouldNotParseWMSMapIfNotSpecified() throws Exception
    {
        DocumentParameters params = executeAndCaptureParams();
        verifyZeroInteractions(mockWmsMapParser);
        assertEquals(0, params.getWmsMapList().size());
    }
    
    @Test
    public void shouldSetApplicationPdfAsContentType() throws Exception
    {
        execute();
        assertEquals(mockResponse.getContentType(), "application/pdf");
    }

    @Test
    public void shouldParseThematicOverlays() throws Exception
    {
        String json = "[{\"name\":\"Distint_Date_Time_ranged1\",\"opacity\":\"0.30\",\"mapObject\":{\"thematicMapType\":\"Range\",\"table\":\"/QA-Maps/NamedTables/Distint_Date_Time\",\"tableColumn\":\"TInteger\",\"thematicMetaData\":{\"geometryType\":\"Polygon\",\"rangeThemeType\":\"EqualRange\",\"rangeCount\":2,\"style\":{\"polygonStyleStartColor\":\"#FFFF00\",\"polygonStyleEndColor\":\"#0000FF\",\"polygonStylePattern\":\"2\",\"lineStyleColor\":\"#000000\",\"lineStylePattern\":\"1\",\"lineStyleWidth\":\"2\"}}},\"overlayOrder\": 1}]";
        mockRequest.addParameter(PrintAction.THEMATIC_OVERLAYS_MAP_PARAM,json);
        DocumentParameters params = executeAndCaptureParams();
        assertEquals( params.getThematicMapList().size(), 1);
        assertTrue( params.getThematicMapList().get(0) instanceof ThematicMap);
        ThematicMap thematicMap =  params.getThematicMapList().get(0);
        assertEquals(thematicMap.getName(),"Distint_Date_Time_ranged1");
        assertTrue(thematicMap.getMapObject() instanceof com.mapinfo.midev.service.mapping.v1.Map);

    }


    private DocumentParameters executeAndCaptureParams() throws Exception
    {
        execute();
        ArgumentCaptor<DocumentParameters> arg = ArgumentCaptor.forClass(
                DocumentParameters.class);
        verify(mockTemplateRenderer).render(any(Template.class),
                arg.capture());
        return arg.getValue();
    }

    private void execute() throws Exception
    {

        printAction.execute(mockRequest, mockResponse);
    }

}

