package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.style.v1.MapBasicAreaStyle;
import com.mapinfo.midev.service.style.v1.MapBasicCompositeStyle;
import com.mapinfo.midev.service.style.v1.MapBasicLineStyle;
import com.mapinfo.midev.service.style.v1.MapBasicPointStyle;
import com.mapinfo.midev.service.table.v1.ViewTable;
import com.mapinfo.midev.service.theme.v1.OverrideTheme;
import com.mapinfo.midev.service.theme.v1.ThemeList;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.annotation.AnnotationParser;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.*;
import com.pb.stratus.controller.print.content.*;
import com.pb.stratus.controller.print.template.TemplateRenderer;
import com.pb.stratus.controller.service.SearchAtPointParams;
import com.pb.stratus.controller.thematic.StyleBuilder;
import com.pb.stratus.controller.thematic.ThematicMapBuilderFactory;
import com.pb.stratus.controller.util.EncodingUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class handles print requests and prints the request output to PDF.
 */
public class PrintAction extends BaseControllerAction
{
    public static final String SRS = "srs";
    private static final String ORIGINAL_REQUEST_URI = "original_request_uri";
    public static final String THEMATIC_OVERLAYS_MAP_PARAM = "thematicOverlays";
    public static final String QUERY_RESULT_OVERLAY_MAP_PARAM = "queryResultOverlay";

    private TemplateRepository repo;

    private TemplateRenderer renderer;

    private FmnResultsCollectionParser fmnParser;

    private LocatorMarkerParser locatorParser;

    /**
     * Annotation parser defined.
     */
    private AnnotationParser annotationParser;

    private WMSMapParser wmsMapParser;

    private FeatureService featureService;

    private ThematicMapBuilderFactory thematicMapBuilderFactory;


    private static final Logger logger = LogManager.getLogger(PrintAction.class.getName());

    public FeatureService getFeatureService() {
        return featureService;
    }

    public PrintAction(TemplateRepository repo, TemplateRenderer renderer,
            FmnResultsCollectionParser fmnParser, LocatorMarkerParser locatorParser,
            AnnotationParser annotationParser, FeatureService featureService,
            WMSMapParser wmsMapParser)
    {
        this.repo = repo;
        this.renderer = renderer;
        this.fmnParser = fmnParser;
        this.locatorParser = locatorParser;
        this.annotationParser = annotationParser;
        this.featureService = featureService;
        this.wmsMapParser = wmsMapParser;
        this.thematicMapBuilderFactory = new ThematicMapBuilderFactory();
    }

    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        if(!request.getMethod().equalsIgnoreCase("POST")){
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String templateName = request.getParameter(TEMPLATE_NAME_PARAM);
        Template template = repo.getTemplate(templateName);
        DocumentParameters params = createDocumentParams(request);
        InputStream is = renderer.render(template, params);
        response.setContentType("application/pdf");
        OutputStream os = response.getOutputStream();
        try
        {
            IOUtils.copy(is, os);
        }
        catch (SocketException e)
        {
            logger.error("Client Abort :"+e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    private DocumentParameters createDocumentParams(HttpServletRequest request)
    {
        MapImageRequestParameters mapImageParams
                = new MapImageRequestParameters(request);
        List<Annotation> annotations = null;
        String title = request.getParameter(TITLE_PARAM);
        List<Feature> callOutFeatures =  getCallOutFeatures(request);
        Marker callOutMarker = createMarker(request.getParameter("lastX"), request.getParameter("lastY"), false,null);
        Boolean isShowAllInfoFields = Boolean.parseBoolean(request.getParameter("isShowAllInfoFields"));
        String legendTitle = request.getParameter(LEGEND_TITLE_PARAM);
        BoundingBox bb = mapImageParams.getBoundingBox();
        String mapConfigName = mapImageParams.getMapConfigName();
        String fmnResultsJson = request.getParameter(FMN_RESULTS_PARAM);
        String geometryJson = request.getParameter(ANNOTATION_RESULTS_PARAM);
        String wmsBaseMapJson = request.getParameter(WMS_BASE_MAP_PARAM);
        String wmsOverlaysJson = request.getParameter(WMS_OVERLAYS_MAP_PARAM);
        String thematicOverlaysJson = request.getParameter(THEMATIC_OVERLAYS_MAP_PARAM);
        String queryResultOverlayJson = request.getParameter(QUERY_RESULT_OVERLAY_MAP_PARAM);
        String locatorMarkerImagePath=request.getParameter(LOCATOR_IMAGE_PARAM);
        Marker locatorMarker = createMarker(request.getParameter("addressx"), request.getParameter("addressy"), true,locatorMarkerImagePath);
        int zoomLevel = mapImageParams.getZoomLevel();
        String srs = request.getParameter(SRS);
        String displayUnit = request.getParameter(DISPLAY_UNIT_PARAM);
        Boolean isScaledPrint = Boolean.parseBoolean(request.getParameter("isScaledPrint"));
        int printResolution = mapImageParams.getPrintResolution();
        String printScaleText = request.getParameter("printScaleText");
        FmnResultsCollection fmnResults = null;

        List<LayerBean> layers = mapImageParams.getLayers();
        List<WMSMap> wmsMapsList = new LinkedList<WMSMap>();

        if (!StringUtils.isBlank(fmnResultsJson))
        {
            fmnResults = parseFmnResults(fmnResultsJson);
        }

        if (!StringUtils.isBlank(geometryJson))
        {
            annotations = annotationParser.parseAnnotations(geometryJson, srs);
        }
        if (!StringUtils.isBlank(wmsBaseMapJson))
        {
            wmsMapsList.add(this.wmsMapParser.parseWmsMapJson(wmsBaseMapJson, true));
        }

        if (!StringUtils.isBlank(wmsOverlaysJson))
        {
            this.wmsMapParser.setMapCfg(request.getParameter(MAP_CFG));
            wmsMapsList.addAll(this.wmsMapParser.parseWMSMapJsonArray(
                    wmsOverlaysJson, false));
        }

        Map<String, List<LayerInfoBean>> layerInfoMap = parseLayerInfoBean(request);

        List<ThematicMap> thematicMapsList = parseThematicMapJson(thematicOverlaysJson);
        List<QueryResultOverlayMap> queryResultOverlayMapsList = parseQueryResultOverlayMapJson(request, queryResultOverlayJson);

        return new DocumentParameters(title, callOutFeatures, callOutMarker, isShowAllInfoFields, legendTitle,
                layers, bb, mapConfigName, fmnResults, annotations, locatorMarker, wmsMapsList, thematicMapsList,
                queryResultOverlayMapsList,  zoomLevel,isScaledPrint,printResolution,printScaleText, displayUnit,
                isMetricUnit(request.getParameter(DISPLAY_UNIT_PARAM)), layerInfoMap);
    }

    /**
     * Converts json string into Java Mapping Objects
     * @param request
     * @return Map<String, List<LayerInfoBean>>
     */
    private Map<String, List<LayerInfoBean>> parseLayerInfoBean(HttpServletRequest request) {
        Map<String, List<LayerInfoBean>> layerInfoMap = new HashMap<String, List<LayerInfoBean>>();

        String jsonStr = request.getParameter("namedLayersInfo");

        ObjectMapper mapper = new ObjectMapper();
        try {
            layerInfoMap = mapper.readValue(jsonStr,new TypeReference<HashMap<String, List<LayerInfoBean>>>(){});
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return layerInfoMap;
    }

    /**
     * Converts json string containing  thematic maps details into MAP of thematicMap Names and Map objects
     * @param jsonStr
     * @return Map<String,com.mapinfo.midev.service.mapping.v1.Map>
     */
    private List<ThematicMap> parseThematicMapJson(String jsonStr)  {
        List<ThematicMap> thematicMapsList = new ArrayList();
        if (jsonStr == null) {
            logger.debug("Empty ThematicMapJson in PrintAction");
            return thematicMapsList;
        }
        JSONArray array = JSONArray.fromObject(jsonStr);
        if (array != null)
        {
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject jsonObj = JSONObject.fromObject(array.get(i));
                String mapName = jsonObj.get("name").toString();
                String mapOpacity = jsonObj.get("opacity").toString();
                String mapOverlayOrder =  jsonObj.get("overlayOrder").toString();
                String mapObjJson = jsonObj.get("mapObject").toString();
                try{
                    com.mapinfo.midev.service.mapping.v1.Map thematicMapObj = thematicMapBuilderFactory.createThematicMapFromJson(mapObjJson);
                    ThematicMap thematicMap = new ThematicMap(mapName,mapOpacity, thematicMapObj);
                    thematicMap.setOverlayOrder(mapOverlayOrder);
                    thematicMapsList.add(thematicMap);
                }catch(IOException ex){
                    logger.error("Error parsing thematicMapObject for mapName : "+mapName+" in PrintAction"+ ex);
                }

            }
        }
        return thematicMapsList;
    }

    /**
     * Converts json string containing  query result map details into MAP of thematicMap Names and Map objects
     * @param jsonStr
     * @return Map<String,com.mapinfo.midev.service.mapping.v1.Map>
     */
    private List<QueryResultOverlayMap> parseQueryResultOverlayMapJson(HttpServletRequest request, String jsonStr)  {
        List<QueryResultOverlayMap> queryResultOverlayMapList = new ArrayList();

        if (jsonStr == null) {
            logger.debug("Empty QueryResult Map Json in PrintAction");
            return queryResultOverlayMapList;
        }

        JSONArray array = JSONArray.fromObject(jsonStr);
        for(Object obj : array) {
            JSONObject jsonObj = JSONObject.fromObject(obj);
            String mapName = jsonObj.get("name").toString();
            String mapOpacity = jsonObj.get("opacity").toString();
            String mapQuerySql = jsonObj.get("querySql").toString();
            String mapQueryTable = jsonObj.get("queryTable").toString();
            String mapOverlayOrder =  jsonObj.get("overlayOrder").toString();
            String mapOverlayFillColor =  jsonObj.get("fillColor").toString();
            String mapOverlayLineColor =  jsonObj.get("lineColor").toString();
            String mapOverlayPointFontColor =  jsonObj.get("pointFontColor").toString();

            com.mapinfo.midev.service.mapping.v1.Map mapObject = new com.mapinfo.midev.service.mapping.v1.Map();
            FeatureLayer featureLayer = new FeatureLayer();
            ViewTable viewTable = new ViewTable();
            viewTable.setName("outer");
            //String sqlQuery = EncodingUtil.decodeURIComponent(mapQuerySql);
            String sqlQuery = mapQuerySql;
            viewTable.setSQL(sqlQuery);
            featureLayer.setTable(viewTable);
            ThemeList themeList = new ThemeList();
            themeList.getTheme().add(getDefaultThemeForQueryResultOverlay(mapOverlayFillColor, mapOverlayLineColor,
                    mapOverlayPointFontColor));
            featureLayer.setThemeList(themeList);
            mapObject.getLayer().add(featureLayer);

            QueryResultOverlayMap queryResultOverlayMap = new QueryResultOverlayMap();
            queryResultOverlayMap.setName(mapName);
            queryResultOverlayMap.setOpacity(mapOpacity);
            queryResultOverlayMap.setQuerySql(mapQuerySql);
            queryResultOverlayMap.setQueryTable(mapQueryTable);
            queryResultOverlayMap.setOverlayOrder(mapOverlayOrder);
            queryResultOverlayMap.setMapObject(mapObject);
            queryResultOverlayMap.setFillColor(mapOverlayFillColor);
            queryResultOverlayMap.setLineColor(mapOverlayLineColor);
            queryResultOverlayMap.setPointFontColor(mapOverlayPointFontColor);
            queryResultOverlayMapList.add(queryResultOverlayMap);
        }
        return queryResultOverlayMapList;
    }

    private OverrideTheme getDefaultThemeForQueryResultOverlay (){
        MapBasicAreaStyle polygonStyle= StyleBuilder.createPolygonStyle("#FF4040", "2", "#FF0000", "2", "2");
        MapBasicLineStyle lineStyle = StyleBuilder.createLineStyle("#FF0000","2","2");
        MapBasicPointStyle pointStyle=StyleBuilder.createPointStyle("41","#FF0000","10","MapInfo Cartographic","normal");
        MapBasicCompositeStyle style =StyleBuilder.createCompositeStyle(polygonStyle, lineStyle, pointStyle);
        OverrideTheme overrideTheme= new OverrideTheme();
        overrideTheme.setStyle(style);
        return overrideTheme;
    }

    private OverrideTheme getDefaultThemeForQueryResultOverlay (String fillColor, String lineColor, String pointFontColor){
        MapBasicAreaStyle polygonStyle= StyleBuilder.createPolygonStyle(fillColor, "2", lineColor, "2", "2");
        MapBasicLineStyle lineStyle = StyleBuilder.createLineStyle(lineColor,"2","2");
        MapBasicPointStyle pointStyle=StyleBuilder.createPointStyle("41", pointFontColor, "10", "MapInfo Cartographic", "normal");
        MapBasicCompositeStyle style =StyleBuilder.createCompositeStyle(polygonStyle, lineStyle, pointStyle);
        OverrideTheme overrideTheme= new OverrideTheme();
        overrideTheme.setStyle(style);
        return overrideTheme;
    }

    /**
     *
     * @param request
     * @return
     */
    private List<Feature> getCallOutFeatures(HttpServletRequest request) {
        SearchAtPointParams searchParams = createParams(request);
        if(searchParams == null) {
            return null;
        }
        FeatureSearchResult result = getFeatureService().searchAtPoint(searchParams);
        List<Feature> features = new ArrayList<Feature>();
        if(result != null)
        {
            Map<String, FeatureCollection> featureCollectionMap = result.getFeatureCollections();
            for (Entry<String, FeatureCollection> entry : featureCollectionMap.entrySet())
            {
                FeatureCollection featureCollection = entry.getValue();
                for(Feature feature : featureCollection.getFeatures())
                {
                    if(feature.getProperties().size() > 0)
                    {
                        features.add(feature);
                    }
                }
            }
        }
        return features;
    }

    private SearchAtPointParams createParams(HttpServletRequest request) {
        Point point = createPoint(request);
        if(point == null) {
            return null;
        }
        SearchAtPointParams searchParams = new SearchAtPointParams();
        searchParams.setPoint(point);
        String tables = request.getParameter("tableNames");
        String[] tablesArray = new String[]{};
        if(tables != null)
        {
            tablesArray = getStringTokensArray(tables);
        }
        String widths = request.getParameter("tableWidths");
        String[] widthsArray = new String[]{};
        if(widths != null)
        {
            widthsArray = getStringTokensArray(widths);
        }
        if(widthsArray != null && widthsArray.length > 0)
        {
            for (int i = 0; i < widthsArray.length; i++)
            {
                searchParams.addTable(tablesArray[i], Double.parseDouble(widthsArray[i]));
            }
        }
        String querySQL = request.getParameter("viewTableSQL");
        String[] querySQLArray = new String[]{};
        if(querySQL != null)
        {
            querySQLArray = getStringTokensArray(querySQL);
        }
        if(querySQLArray != null && querySQLArray.length > 0)
        {
            for (int i = 0; i < querySQLArray.length; i++)
            {
                searchParams.addQuerySQL(tablesArray[i], querySQLArray[i]);
            }
        }
        return searchParams;
    }

    private String[] getStringTokensArray(String str)
    {
        if(str == null)
        {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        int numOfTokens = tokenizer.countTokens();
        String[] stringArray = new String[numOfTokens];
        for(int i = 0; i < numOfTokens; i++) {
            stringArray[i] = tokenizer.nextToken();
        }
        return stringArray;
    }

    private Point createPoint(HttpServletRequest request)
    {
        String lastX = request.getParameter("lastX");
        String lastY = request.getParameter("lastY");
        if(lastX == null || lastY == null) {
            return null;
        }
        Point point = new Point();
        Pos pos = new Pos();
        double x = Float.parseFloat(lastX);
        double y = Float.parseFloat(lastY);
        pos.setX(x);
        pos.setY(y);
        point.setPos(pos);
        point.setSrsName(request.getParameter("srs"));
        return point;
    }

    private Marker createMarker(String addressx, String addressy, boolean isLocator,String locatorMarkerPath)
    {
        if(isLocator)
            return locatorParser.createMarker(addressx, addressy,isLocator,locatorMarkerPath);
        return locatorParser.createMarker(addressx, addressy, isLocator, null);
    }

    private FmnResultsCollection parseFmnResults(String json)
    {
        return fmnParser.parse(json);
    }

    private boolean isMetricUnit(String unit)
    {
        return DistanceUnit.METER.name().equals(unit)
                || DistanceUnit.KILOMETER.name().equals(unit);
    }

}
