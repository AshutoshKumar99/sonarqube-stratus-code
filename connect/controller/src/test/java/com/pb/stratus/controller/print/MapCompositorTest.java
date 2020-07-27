package com.pb.stratus.controller.print;

import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.compositor.LayerCompositor;
import com.pb.stratus.controller.compositor.LayerCompositorImpl;
import com.pb.stratus.controller.compositor.MapCompositor;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.Watermark;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.print.config.MapConfigRepositoryImpl;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.print.content.ThematicMap;
import com.pb.stratus.controller.print.content.WMSMap;
import com.pb.stratus.controller.print.render.AnnotationRenderer;
import com.pb.stratus.controller.print.render.CopyrightRenderer;
import com.pb.stratus.core.configuration.ConfigReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import static com.pb.stratus.controller.util.ImageAssertUtils.assertImagesEquivalentAsPng;
import static com.pb.stratus.controller.util.ImageAssertUtils.assertPixel;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * We use mock images with a unique pixel set to find out if they were rendered
 * into the final image.
 */
public class MapCompositorTest {

    private MapCompositor compositor;

    private MarkerRenderer mockMarkerRenderer;

    private WatermarkRenderer mockWatermarkRenderer;

    private CopyrightRenderer mockCopyrightRenderer;

    private MapConfigRepository mockMapConfigRepo;

    private AnnotationRenderer annotationRenderer;

    private ConfigReader mockConfigReader;

    private List<Marker> mockMarkers;

    private List<Annotation> mockAnnotations;

    private Marker mockLocatorMarker;

    private MapConfig mockMapConfig;

    private LayerCompositor mockLayerCompositor;

    private BoundingBox mockBoundingBox;

    private Dimension mockImageSize;

    private List<LayerBean> mockLayers;

    private Map<String, List<LayerInfoBean>> mockNamedLayerInfoMap = new HashMap<String, List<LayerInfoBean>>();

    private List<WMSMap> mockWMSMapList;

    private List<ThematicMap> mockThematicMapList;

    private List<QueryResultOverlayMap> mockQueryResultOverlayMapList;

    private BingAttributionRenderer mockBingAttributionRenderer;

    private OpenStreetMapAttributionRenderer mockOpenStreetMapAttributionRenderer;

    private int zoomLevel = 3;

    private String displayUnit = "METER";

    private MapConfig.MapDefinition mockMapDefinition;

    @Before
    public void setUp() throws Exception {
        setUpDefaultParams();
        setUpLayerCompositor();
        setUpWatermarkRenderer();
        setUpFindMyNearestMarkerRenderer();
        setUpLocatorMarkerRenderer();
        setUpCopyrightRenderer();
        setUpMapConfigRepoAndResourceResolver();
        setUpBingAttributionRenderer();
        setUpOpenStreetAttributionRenderer();
        setUpAnnotationRenderer();
        //TODO Need to create Mocks for GeometryRenderer
        compositor = new MapCompositor(mockLayerCompositor,
                mockWatermarkRenderer, mockMarkerRenderer,
                mockCopyrightRenderer, mockBingAttributionRenderer, mockOpenStreetMapAttributionRenderer,
                mockMapConfigRepo, mockConfigReader, annotationRenderer);
        mockMarkers = Arrays.asList(mock(Marker.class), mock(Marker.class));
        mockLocatorMarker = mock(Marker.class);
    }

    private void setUpBingAttributionRenderer() {
        mockBingAttributionRenderer = mock(BingAttributionRenderer.class);
        when(mockBingAttributionRenderer.isBingBasemap(mockMapConfig)).thenReturn(true);
        BufferedImage mockAttributionImage = createMockImageWithOnePixelSet(4);
        when(mockBingAttributionRenderer.renderAttribution(any(Dimension.class),
                any(BoundingBox.class), any(List.class),
                any(MapConfig.class),anyInt())).thenReturn(mockAttributionImage);
    }

    private void setUpOpenStreetAttributionRenderer() {
        mockOpenStreetMapAttributionRenderer = mock(OpenStreetMapAttributionRenderer.class);
        when(mockOpenStreetMapAttributionRenderer.isOpenStreetBasemap(mockMapConfig)).thenReturn(true);
        BufferedImage mockAttributionImage = createMockImageWithOnePixelSet(4);
        when(mockOpenStreetMapAttributionRenderer.renderAttribution(any(Dimension.class),
                any(BoundingBox.class), any(List.class),
                any(MapConfig.class),anyInt())).thenReturn(mockAttributionImage);
    }

    private void setUpDefaultParams() {
        mockBoundingBox = new BoundingBox(2, 1, 1, 2, "someSrs");
        mockImageSize = new Dimension(10, 20);
        mockLayers = Arrays.asList(new LayerBean("basemap1"), new LayerBean("overlay1"),new LayerBean("overlay2"));
        populateMockLayerInfoMap();
        mockWMSMapList = Arrays.asList(mock(WMSMap.class));
        mockThematicMapList = Arrays.asList(mock(ThematicMap.class));
        mockQueryResultOverlayMapList = Arrays.asList(mock(QueryResultOverlayMap.class));
    }

    private void populateMockLayerInfoMap() {
        List<LayerInfoBean> mockNamedLayerInfoList = new ArrayList<LayerInfoBean>();

        LayerInfoBean layerInfoObj = new LayerInfoBean();
        layerInfoObj.setLayerMapPath("overlay1");
        layerInfoObj.setLayerOpacity(0);
        layerInfoObj.setLayerVisibility(true);
        layerInfoObj.setLayerIndex(0);
        layerInfoObj.setLayerName("NamedLayer1");
        mockNamedLayerInfoList.add(layerInfoObj);
        mockNamedLayerInfoMap.put("overlay1", mockNamedLayerInfoList);

        LayerInfoBean layerInfoObj1 = new LayerInfoBean();
        layerInfoObj1.setLayerMapPath("overlay2");
        layerInfoObj1.setLayerOpacity(0);
        layerInfoObj1.setLayerVisibility(true);
        layerInfoObj1.setLayerIndex(0);
        layerInfoObj1.setLayerName("NamedLayer1");
        mockNamedLayerInfoList.add(layerInfoObj1);
        mockNamedLayerInfoMap.put("overlay2",mockNamedLayerInfoList);
    }

    @SuppressWarnings("unchecked")
    private void setUpLayerCompositor() {
        mockLayerCompositor = mock(LayerCompositorImpl.class);
        BufferedImage mockLayerImage = createMockImageWithOnePixelSet(0);
        MapConfig mapConfig = mock(MapConfig.class);
        List<LayerBean> stringList = new LinkedList<>();
        List<WMSMap> wmsMapList = Arrays.asList(mock(WMSMap.class));
        BoundingBox bBox = mock(BoundingBox.class);
        when(
                mockLayerCompositor.compose(stringList, mapConfig, bBox,
                        new Dimension(), new Integer(0),wmsMapList)).thenReturn(
                mockLayerImage);
    }

    private void setUpWatermarkRenderer() {
        mockWatermarkRenderer = mock(WatermarkRenderer.class);
        BufferedImage mockWatermarkImage = createMockImageWithOnePixelSet(1);
        when(
                mockWatermarkRenderer.renderWatermark(any(BufferedImage.class),
                        anyDouble(), any(Dimension.class))).thenReturn(
                mockWatermarkImage);
    }

    @SuppressWarnings("unchecked")
    private void setUpFindMyNearestMarkerRenderer() {
        mockMarkerRenderer = mock(MarkerRenderer.class);
        BufferedImage mockMarkerImage = createMockImageWithOnePixelSet(2);
        when(
                mockMarkerRenderer.renderMarkers(any(Dimension.class),
                        any(BoundingBox.class), any(List.class))).thenReturn(
                mockMarkerImage);
    }

    @SuppressWarnings("unchecked")
    private void setUpAnnotationRenderer() {
        annotationRenderer = mock(AnnotationRenderer.class);
        BufferedImage mockGeometryImage = createMockImageWithOnePixelSet(2);
        when(
                annotationRenderer.renderAnnotations(any(Dimension.class),
                        any(BoundingBox.class), any(List.class), anyString())).thenReturn(
                mockGeometryImage);
    }

    @SuppressWarnings("unchecked")
    private void setUpLocatorMarkerRenderer() {
        mockMarkerRenderer = mock(MarkerRenderer.class);
        BufferedImage mockMarkerImage = createMockImageWithOnePixelSet(2);
        when(
                mockMarkerRenderer.renderMarkers(any(Dimension.class),
                        any(BoundingBox.class), any(List.class))).thenReturn(
                mockMarkerImage);
    }

    @SuppressWarnings("unchecked")
    private void setUpCopyrightRenderer() {
        mockCopyrightRenderer = mock(CopyrightRenderer.class);
        BufferedImage mockCopyrightImage = createMockImageWithOnePixelSet(3);
        when(
                mockCopyrightRenderer.renderCopyright(any(Dimension.class),
                        any(Resolution.class), any(List.class),
                        any(MapConfig.class))).thenReturn(mockCopyrightImage);
    }

    private void setUpMapConfigRepoAndResourceResolver() throws Exception {
        mockMapConfigRepo = mock(MapConfigRepositoryImpl.class);
        mockMapConfig = new MapConfig();
        Watermark mockWatermark = mockMapConfig.createWatermark();
        mockWatermark.setImagePath("/theme/images/watermark/img.png");
        mockWatermark.setOpacity(0.5f);
        mockMapConfig.setWatermark(mockWatermark);

        mockMapDefinition = new MapConfig.MapDefinition();
        List<MapConfig.MapDefinition> list = new ArrayList<>();
        list.add(mockMapDefinition);
        mockMapDefinition.setMapName("overlay_n");
        mockMapDefinition.setRepositoryPath("myPath");

        mockMapConfig.setMapDefinitions(list);
        when(mockMapConfigRepo.getMapConfig("someConfigName")).thenReturn(
                mockMapConfig);
        InputStream watermarkImage = MapCompositorTest.class
                .getResourceAsStream("test-watermark.png");
        mockConfigReader = mock(ConfigReader.class);
        when(mockConfigReader.getConfigFile(any(String.class))).thenReturn(
                watermarkImage);
    }

    private List<Marker> createLocatorMarkerList() {
        List<Marker> locatorMarkers = new ArrayList<Marker>();
        locatorMarkers.add(mockLocatorMarker);
        return locatorMarkers;
    }

    private BufferedImage createMockImageWithOnePixelSet(int x) {
        BufferedImage img = new BufferedImage(5, 1,
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLACK);
        g.drawLine(x, 0, x, 0);
        g.dispose();
        return img;
    }

    @Test
    public void shouldAddLayers() throws Exception {
        BufferedImage image = renderWithDefaultValues();
        assertPixel(new double[] { 0, 0, 0, 0 }, image, 0, 0);
        verify(mockLayerCompositor).compose(mockLayers, mockMapConfig,
                mockBoundingBox, mockImageSize, zoomLevel, mockWMSMapList);
    }

    @Test
    public void shouldAddLayersByPassingNamedLayers() throws Exception {
        BufferedImage image = renderWithDefaultValuesByPassingNamedLayers();
        assertPixel(new double[] { 0, 0, 0, 0 }, image, 0, 0);
        verify(mockLayerCompositor).compose(mockLayers, mockNamedLayerInfoMap, mockMapConfig,
                mockBoundingBox, mockImageSize, zoomLevel,mockWMSMapList,false, mockThematicMapList,
                mockQueryResultOverlayMapList);
    }

    @Test
    public void shouldAddWatermark() throws Exception {
        BufferedImage image = renderWithDefaultValues();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 1, 0);
        ArgumentCaptor<BufferedImage> arg = ArgumentCaptor
                .forClass(BufferedImage.class);
        verify(mockWatermarkRenderer).renderWatermark(arg.capture(), eq(0.5),
                eq(mockImageSize));
        BufferedImage actualWatermark = arg.getValue();
        InputStream is = MapCompositorTest.class
                .getResourceAsStream("test-watermark.png");
        BufferedImage expectedWatermark = ImageIO.read(is);
        is.close();
        assertImagesEquivalentAsPng(expectedWatermark, actualWatermark);
    }

    @Test
    public void shouldAddWatermarkByPassingNamedLayers() throws Exception {
        BufferedImage image = renderWithDefaultValuesByPassingNamedLayers();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 1, 0);
        ArgumentCaptor<BufferedImage> arg = ArgumentCaptor
                .forClass(BufferedImage.class);
        verify(mockWatermarkRenderer).renderWatermark(arg.capture(), eq(0.5),
                eq(mockImageSize));
        BufferedImage actualWatermark = arg.getValue();
        InputStream is = MapCompositorTest.class
                .getResourceAsStream("test-watermark.png");
        BufferedImage expectedWatermark = ImageIO.read(is);
        is.close();
        assertImagesEquivalentAsPng(expectedWatermark, actualWatermark);
    }

    @Test
    public void watermarkRendererShouldNotBeCalledOnNullImagePath()
            throws Exception {
        Watermark mockWatermark = mockMapConfig.createWatermark();
        mockWatermark.setImagePath(null);
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, mockMarkers, mockAnnotations, null, null,null, zoomLevel, displayUnit);
        verify(mockWatermarkRenderer, times(0)).renderWatermark(
                any(BufferedImage.class), anyDouble(), any(Dimension.class));
    }

    @Test
    public void watermarkRendererShouldNotBeCalledOnNullImagePathByPassingNamedLayers()
            throws Exception {
        Watermark mockWatermark = mockMapConfig.createWatermark();
        mockWatermark.setImagePath(null);
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, mockNamedLayerInfoMap, mockMarkers, mockAnnotations, null, null,null, zoomLevel, displayUnit, null, null);
        verify(mockWatermarkRenderer, times(0)).renderWatermark(
                any(BufferedImage.class), anyDouble(), any(Dimension.class));
    }

    @Test
    public void shouldRequestWatermarkFromFromConfigReaderWithImagePath()
            throws Exception {
        renderWithDefaultValues();
        verify(mockConfigReader).getConfigFile(
                "/theme/images/watermark/img.png");
    }

    @Test
    public void shouldRequestWatermarkFromFromConfigReaderWithImagePathByPassingNamedLayers()
            throws Exception {
        renderWithDefaultValuesByPassingNamedLayers();
        verify(mockConfigReader).getConfigFile(
                "/theme/images/watermark/img.png");
    }

    @Test
    public void shouldAddFindMyNearestMarkers() {
        BufferedImage image = renderWithDefaultValues();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 2, 0);
        verify(mockMarkerRenderer).renderMarkers(mockImageSize,
                mockBoundingBox, mockMarkers);
    }

    @Test
    public void shouldAddFindMyNearestMarkersByPassingNamedLayers() {
        BufferedImage image = renderWithDefaultValuesByPassingNamedLayers();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 2, 0);
        verify(mockMarkerRenderer).renderMarkers(mockImageSize,
                mockBoundingBox, mockMarkers);
    }

    @Test
    public void markerRendererShouldNotBeCalledOnNullMarker() {
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, mockMarkers, mockAnnotations, null, null,null, zoomLevel, displayUnit);
        verify(mockMarkerRenderer, times(1)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    public void markerRendererShouldNotBeCalledOnNullMarkerByPassingNamedLayers() {
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, mockNamedLayerInfoMap, mockMarkers, mockAnnotations, null, null,null, zoomLevel, displayUnit,null, null);
        verify(mockMarkerRenderer, times(1)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    public void findMyNearestRendererShouldNotBeCalledOnNullList() {
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, null, mockAnnotations, null, mockLocatorMarker,null, zoomLevel, displayUnit);
        verify(mockMarkerRenderer, times(1)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    public void findMyNearestRendererShouldNotBeCalledOnNullListByPassingNamedLayers() {
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, mockNamedLayerInfoMap, null, mockAnnotations, null, mockLocatorMarker,null, zoomLevel,
                displayUnit, null, null);
        verify(mockMarkerRenderer, times(1)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    public void findMyNearestRendererShouldNotBeCalledOnEmptyList() {
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, new ArrayList<Marker>(), mockAnnotations, null, mockLocatorMarker,null,
                zoomLevel, displayUnit);
        verify(mockMarkerRenderer, times(1)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    public void findMyNearestRendererShouldNotBeCalledOnEmptyListByPassingNamedLayers() {
        compositor.renderMap(mockBoundingBox, mockImageSize, "someConfigName",
                mockLayers, mockNamedLayerInfoMap, new ArrayList<Marker>(), mockAnnotations, null, mockLocatorMarker,null,
                zoomLevel, displayUnit, null, null);
        verify(mockMarkerRenderer, times(1)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    public void shouldAddCopyright() {
        BufferedImage image = renderWithDefaultValues();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 3, 0);
        verify(mockCopyrightRenderer).renderCopyright(
                eq(new Dimension(10, 20)), any(Resolution.class),
                anyListOf(LayerBean.class),
                eq(mockMapConfig));
    }

    @Test
    public void shouldAddCopyrightByPassingNamedLayers() {
        BufferedImage image = renderWithDefaultValuesByPassingNamedLayers();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 3, 0);
        verify(mockCopyrightRenderer).renderCopyright(
                eq(new Dimension(10, 20)), any(Resolution.class),
                eq(mockLayers),
                eq(mockMapConfig));
    }

    @Test
    public void testBingAttribution() {
        BufferedImage image = renderWithDefaultValues();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 4, 0);
        verify(mockBingAttributionRenderer).renderAttribution(
                any(Dimension.class),
                any(BoundingBox.class), any(List.class),
                any(MapConfig.class),anyInt());
    }

    @Test
    public void testBingAttributionByPassingNamedLayers() {
        BufferedImage image = renderWithDefaultValuesByPassingNamedLayers();
        assertPixel(new double[] { 0, 0, 0, 255 }, image, 4, 0);
        verify(mockBingAttributionRenderer).renderAttribution(
                any(Dimension.class),
                any(BoundingBox.class), any(List.class),
                any(MapConfig.class),anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddWatermarkBeforeMarkers() {
        renderWithDefaultValues();
        InOrder order = inOrder(mockWatermarkRenderer, mockMarkerRenderer);
        order.verify(mockWatermarkRenderer).renderWatermark(
                any(BufferedImage.class), anyDouble(), any(Dimension.class));
        order.verify(mockMarkerRenderer, times(2)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    public void shouldAddWatermarkBeforeMarkersByPassingNamedLayers() {
        renderWithDefaultValuesByPassingNamedLayers();
        InOrder order = inOrder(mockWatermarkRenderer, mockMarkerRenderer);
        order.verify(mockWatermarkRenderer).renderWatermark(
                any(BufferedImage.class), anyDouble(), any(Dimension.class));
        order.verify(mockMarkerRenderer, times(2)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddMarkersBeforeCopyright() {
        renderWithDefaultValues();
        InOrder order = inOrder(mockMarkerRenderer, mockCopyrightRenderer);
        order.verify(mockMarkerRenderer, times(2)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
        order.verify(mockCopyrightRenderer).renderCopyright(
                any(Dimension.class), any(Resolution.class), any(List.class),
                any(MapConfig.class));
    }

    @Test
    public void shouldAddMarkersBeforeCopyrightByPassingNamedLayers() {
        renderWithDefaultValuesByPassingNamedLayers();
        InOrder order = inOrder(mockMarkerRenderer, mockCopyrightRenderer);
        order.verify(mockMarkerRenderer, times(2)).renderMarkers(
                any(Dimension.class), any(BoundingBox.class), any(List.class));
        order.verify(mockCopyrightRenderer).renderCopyright(
                any(Dimension.class), any(Resolution.class), any(List.class),
                any(MapConfig.class));
    }

    private BufferedImage renderWithDefaultValues() {
        return compositor.renderMap(mockBoundingBox, mockImageSize,
                "someConfigName", mockLayers, mockMarkers, mockAnnotations, null, mockLocatorMarker,mockWMSMapList,
                zoomLevel, displayUnit);
    }

    private BufferedImage renderWithDefaultValuesByPassingNamedLayers() {
        return compositor.renderMap(mockBoundingBox, mockImageSize,
                "someConfigName", mockLayers, mockNamedLayerInfoMap, mockMarkers, mockAnnotations, null, mockLocatorMarker,mockWMSMapList,
                zoomLevel, displayUnit, mockThematicMapList, mockQueryResultOverlayMapList);
    }
}