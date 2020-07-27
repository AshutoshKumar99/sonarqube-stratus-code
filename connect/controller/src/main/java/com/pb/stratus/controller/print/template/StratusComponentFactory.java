package com.pb.stratus.controller.print.template;

import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.compositor.MapCompositor;
import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.legend.LegendData;
import com.pb.stratus.controller.legend.LegendService;
import com.pb.stratus.controller.legend.OverlayLegend;
import com.pb.stratus.controller.print.*;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.print.content.*;
import com.pb.stratus.controller.print.render.WMSRendererHelper;
import com.pb.stratus.controller.print.template.component.*;
import com.pb.stratus.controller.print.template.component.TextComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.xml.sax.Attributes;
import uk.co.graphdata.utilities.contract.Contract;

import javax.xml.namespace.QName;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static com.pb.stratus.controller.print.TemplateContentHandler.NAMESPACE;

/**
 * The StratusComponentFactory is responsible for creating components that
 * are populated with the necessary data to print MapInfo Stratus-specific
 * information. It retrieves the necessary data from a PdfContent instance
 * that has to be provided at construction time.
 */
public class StratusComponentFactory implements ComponentFactory {
    private static final Logger logger = LogManager.getLogger(StratusComponentFactory.class);

    private static QName ADDRESS_ELEMENT = new QName(NAMESPACE, "address");

    private static QName CALLOUT_ELEMENT = new QName(NAMESPACE, "callout-info");

    private static QName MAP_ELEMENT = new QName(NAMESPACE, "map");

    private static final Object FMN_RESULTS_ELEMENT = new QName(NAMESPACE, "fmn-results");

    private static final Object LEGEND_ELEMENT = new QName(NAMESPACE, "legend");

    private static QName SCALE_BAR_ELEMENT = new QName(NAMESPACE, "scale-bar");

    private static QName SCALE_VALUE_ELEMENT
            = new QName(NAMESPACE, "scale-value");

    private static QName FORMATTED_DATE_ELEMENT = new QName(NAMESPACE, "date");

    private MapCompositor mapCompositor;

    private LegendService legendService;

    private ScaleBarFactory scaleBarFactory;

    private BoundingBoxCalculator boundingBoxCalculator;

    private Resolution resolution = new Resolution(150);

    private MapConfigRepository mapConfigRepository;

    public StratusComponentFactory(MapCompositor mapCompositor,
                                   LegendService legendService, ScaleBarFactory scaleBarFactory,
                                   BoundingBoxCalculator boundingBoxCalculator,
                                   MapConfigRepository mapConfigRepository) {
        this.mapCompositor = mapCompositor;
        this.legendService = legendService;
        this.scaleBarFactory = scaleBarFactory;
        this.boundingBoxCalculator = boundingBoxCalculator;
        this.mapConfigRepository = mapConfigRepository;
    }

    public Component createComponent(QName element, Attributes attrs,
                                     DocumentParameters params) {
        if (element.equals(ADDRESS_ELEMENT)) {
            return new TextComponent(params.getTitle());
        } else if (element.equals(CALLOUT_ELEMENT)) {
            String masterRef = attrs.getValue(
                    XslFoUtils.MASTER_REFERENCE_ATTR);
            // get the WMS callout data
            WMSRendererHelper wmsRendererHelper = new WMSRendererHelper();
            Map<String, List<String>> wmsCalloutURLMap = wmsRendererHelper.constructURLForWMS(params);
            boolean showAllInfoFields = false;
            if (params.getCallOutFeatures() != null && params.getCallOutFeatures().size() > 0) {
                showAllInfoFields = params.getShowAllInfoFields();
            }
            return new CallOutComponent(params.getCallOutFeatures(), params.getCallOutMarker(),
                    showAllInfoFields, masterRef, wmsCalloutURLMap);
        } else if (element.equals(MAP_ELEMENT)) {
            BufferedImage mapImage = renderMapImage(attrs, params);
            String width = attrs.getValue("width");
            String height = attrs.getValue("height");
            return new ImageComponent(mapImage, width, height);
        } else if (element.equals(FMN_RESULTS_ELEMENT)) {
            String masterRef = attrs.getValue(
                    XslFoUtils.MASTER_REFERENCE_ATTR);
            return new FmnResultsComponent(params.getFmnResults(), masterRef);
        } else if (element.equals(LEGEND_ELEMENT)) {
            MapConfig config = mapConfigRepository.getMapConfig(
                    params.getMapConfigName());
            List<String> overlaysNames = new ArrayList<String>();
            for (LayerBean overlay : params.getLayers()) {
                overlaysNames.add(overlay.getName());
            }
            return new LegendComponent(params.getLegendTitle(),
                    config, getLegendData(overlaysNames, params.getWmsMapList(), params.getThematicMapList(),
                    params.getQueryResultOverlayMapsList(), params.getNamedLayersInfo(), config));

        } else if (element.equals(SCALE_BAR_ELEMENT)) {
            String mapWidth = attrs.getValue("map-width");
            String mapHeight = attrs.getValue("map-height");
            Map<String, String> scaleAttribs = new LinkedHashMap<>();

            /**
             * Conn-17239
             */
            scaleAttribs.put(XslFoUtils.MAX_BAR_WIDTH, attrs.getValue(XslFoUtils.MAX_BAR_WIDTH));

            /**
             * Optional params: that we should be processing for this element.
             * font-size; color font-family
             */

            scaleAttribs.put("font-family", attrs.getValue("font-family"));
            scaleAttribs.put("color", attrs.getValue("color"));
            scaleAttribs.put("font-size", attrs.getValue("font-size"));

            if (params.getScaledPrint() && params.getPrintResolution() != 0) {
                resolution = new Resolution(params.getPrintResolution());
            }

            Dimension imageSize = resolution.calculatePixelDimensions(mapWidth,
                    mapHeight);
            ScaleBar scaleBar = createScaleBar(params, imageSize, resolution);

            /**
             * In case the user specifies the optional max bar width we can
             * consider restricting the scale bar.
             */
            if (scaleAttribs.get(XslFoUtils.MAX_BAR_WIDTH) != null) {
                /**
                 * Remove the CM ending.
                 */
                scaleAttribs.put(XslFoUtils.MAX_BAR_WIDTH, ((scaleAttribs.get(XslFoUtils.MAX_BAR_WIDTH).toLowerCase().endsWith("cm")) ? scaleAttribs.get(XslFoUtils.MAX_BAR_WIDTH).substring(0, scaleAttribs.get(XslFoUtils.MAX_BAR_WIDTH).length() - 2) : scaleAttribs.get(XslFoUtils.MAX_BAR_WIDTH)));

            }

            if (scaleAttribs.get("font-size") != null) {
                /**
                 * Remove the CM ending.
                 */
                scaleAttribs.put("font-size", ((scaleAttribs.get("font-size").toLowerCase().endsWith("pt")) ? scaleAttribs.get("font-size").substring(0, scaleAttribs.get("font-size").length() - 2) : scaleAttribs.get("font-size")));

            }

            return new ScaleBarComponent(scaleBar, scaleAttribs);
        } else if (element.equals(FORMATTED_DATE_ELEMENT)) {
            /**
             * Adding a new static date, it shall publish the current
             * server date in the output stream.
             */
            String dFormat = attrs.getValue(XslFoUtils.DATE_FORMAT);
            return new DateComponent(dFormat);
        }
        if (element.equals(SCALE_VALUE_ELEMENT)) {
            return new ScaleValueComponent(params.getPrintScaleText());
        } else {
            return NullComponent.INSTANCE;
        }
    }


    private LegendData getLegendData(List<String> layers, List<WMSMap> wmsMapList, List<ThematicMap> thematicMapList,
            List<QueryResultOverlayMap> queryResultOverlayMapList, Map<String, List<LayerInfoBean>> namedLayersInfo, MapConfig config)
    {
        List<String> overlays = new ArrayList<String>();
        LegendData legendData = null;
        if (layers != null && layers.size() > 0) {
            // Per definition, the first layer in the list is the base map
            if (!doesWMSMapsContainsBaseMap(wmsMapList)) {
                overlays = layers.subList(1, layers.size());
            } else {
                overlays = layers;
            }
        }
        legendData = legendService.getLegendData(LocaleResolver.getLocale(), wmsMapList, thematicMapList,
                queryResultOverlayMapList, namedLayersInfo, overlays.toArray(new String[overlays.size()]));
        List<OverlayLegend> sortedLegends = sortOverlayLegends(legendData.getOverlayLegends(), config);
        legendData.setOverlayLegends(sortedLegends);
        return legendData;
    }

    private List<OverlayLegend> sortOverlayLegends(List<OverlayLegend> overlayLegends, MapConfig config) {
        //sort legends in the order present in mapconfig
        List<OverlayLegend> sortedList = new ArrayList<OverlayLegend>();
        for (MapConfig.MapDefinition mapDef : config.getMapDefinitions()) {
            for (OverlayLegend legend : overlayLegends) {
                String legendTitle = legend.getTitle();
                if (legendTitle != null && (mapDef.getMapName().equals(legendTitle) || mapDef.getRepositoryPath().equals(legendTitle))) {
                    sortedList.add(legend);
                    break;
                }
            }
        }
        // thematic legends not present in map config should be appended at the beginning
        //List<OverlayLegend> thematicLegendsList = (List<OverlayLegend>) CollectionUtils.subtract(overlayLegends, sortedList);
        // as in connect UI ,newly created thematics are added above the existing , we need to reverse order in print
        //Collections.reverse(thematicLegendsList);

        List<OverlayLegend> nonMapConfigLegendsList = (List<OverlayLegend>) CollectionUtils.subtract(overlayLegends, sortedList);
        Collections.sort(nonMapConfigLegendsList, new OverlayLegendComparator());
        sortedList.addAll(0, nonMapConfigLegendsList);
        return sortedList;
    }

    /**
     * @param wmsMapList
     * @return
     */
    private boolean doesWMSMapsContainsBaseMap(List<WMSMap> wmsMapList) {
        if (wmsMapList != null && wmsMapList.size() > 0) {
            for (WMSMap wmsMap : wmsMapList) {
                if (wmsMap.isBaseMap())
                    return true;
            }
        }
        return false;
    }

    private BufferedImage renderMapImage(Attributes elementAttrs,
                                         DocumentParameters params) {
        Dimension imageSize = getImageSize(elementAttrs);
        List<Marker> markers =
                extractFindMyNearestMarkers(params.getFmnResults());
        List<Annotation> annotations =
                params.getAnnotations();
        BoundingBox boundsForImageSize = params.getBoundingBox();
        if (!params.getScaledPrint()) {
            boundsForImageSize = boundingBoxCalculator.calculate(
                    params.getBoundingBox(), imageSize);
        }
        return mapCompositor.renderMap(boundsForImageSize, imageSize,
                params.getMapConfigName(), params.getLayers(), params.getNamedLayersInfo(), markers, annotations,
                params.getCallOutMarker(), params.getLocatorMarker(), params.getWmsMapList(), params.getZoomLevel(),
                params.getDisplayUnit(), params.getThematicMapList(), params.getQueryResultOverlayMapsList());
    }

    private List<Marker> extractFindMyNearestMarkers(
            FmnResultsCollection fmnResults) {
        if (fmnResults == null) {
            return Collections.emptyList();
        }
        List<Marker> markers = new LinkedList<Marker>();
        for (FmnResult result : fmnResults.getFmnResults()) {
            markers.add(result.getMarker());
        }
        return markers;
    }

    /**
     * @param elementAttrs
     * @return
     */
    private Dimension getImageSize(Attributes elementAttrs) {
        String widthString = elementAttrs.getValue("width");
        Contract.assrt(widthString != null,
                "Attribute 'width' required in <stratus:map/>");
        String heightString = elementAttrs.getValue("height");
        Contract.assrt(heightString != null,
                "Attribute 'height' required in <stratus:map/>");
        return resolution.calculatePixelDimensions(widthString, heightString);
    }

    private ScaleBar createScaleBar(DocumentParameters params,
                                    Dimension imageSize, Resolution resolution2) {
        BoundingBox bb = params.getBoundingBox();
        if (!params.getScaledPrint()) {
            bb = boundingBoxCalculator.calculate(
                    params.getBoundingBox(), imageSize);
        }
        return scaleBarFactory.createScaleBar(
                bb, imageSize, resolution2,
                params.useMetricDisplayUnits());
    }

    class OverlayLegendComparator implements Comparator<OverlayLegend> {
        @Override
        public int compare(OverlayLegend legend1, OverlayLegend legend2) {
            int overlayOrderLegend1 = Integer.parseInt(legend1.getOverlayOrder());
            int overlayOrderLegend2 = Integer.parseInt(legend2.getOverlayOrder());

            if (overlayOrderLegend1 < overlayOrderLegend2) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
