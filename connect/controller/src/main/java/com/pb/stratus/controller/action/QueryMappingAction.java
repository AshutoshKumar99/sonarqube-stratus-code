package com.pb.stratus.controller.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.mapping.v1.Rendering;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.mapinfo.midev.service.style.v1.MapBasicAreaStyle;
import com.mapinfo.midev.service.style.v1.MapBasicCompositeStyle;
import com.mapinfo.midev.service.style.v1.MapBasicLineStyle;
import com.mapinfo.midev.service.style.v1.MapBasicPointStyle;
import com.mapinfo.midev.service.table.v1.Table;
import com.mapinfo.midev.service.table.v1.ViewTable;
import com.mapinfo.midev.service.theme.v1.OverrideTheme;
import com.mapinfo.midev.service.theme.v1.ThemeList;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.MapNotFoundException;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.service.RenderMapParams;
import com.pb.stratus.controller.util.EncodingUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.pb.stratus.controller.thematic.StyleBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 2/5/15
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryMappingAction extends BaseControllerAction{

    private MappingService mappingService;
    private ObjectMapper mapper;
    private static final String QUERY_RESULT_MAP_JSON = "queryResultMapJson";
    private final static String INVALID_PARAMETER_STRING = "The following parameter was not found in request: ";
    private static final Logger logger = LogManager.getLogger(QueryMappingAction.class);
    private static final String ERROR_MSG = "Unable to render requested query result map";
    private static final String SRS_PARAM_NAME = "srs";
    private static final String X_PARAM_NAME = "x";
    private static final String Y_PARAM_NAME = "y";
    private static final String ZOOM_PARAM_NAME = "zoom";
    private static final String WIDTH_PARAM_NAME = "width";
    private static final String HEIGHT_PARAM_NAME = "height";
    private static final String OUTPUT_PARAM_NAME = "output";
    private static final String SQL_PARAM_NAME = "querySQL";
    private static final String FILL_COLOR = "fillColor";
    private static final String LINE_COLOR = "lineColor";
    private static final String POINT_FONT_COLOR = "pointFontColor";

    public QueryMappingAction(MappingService mappingService) {
        this.mappingService = mappingService;
        this.mapper = new ObjectMapper();
    }

    private MappingService getMappingService() {
        return mappingService;
    }

    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException
    {
        RenderMapResponse renderMapResponse = getQueryMap(request);
        String output = request.getParameter(OUTPUT_PARAM_NAME);
        if (null == renderMapResponse) {
            throw new MapNotFoundException(ERROR_MSG );
        }
        byte[] image = renderMapResponse.getMapImage().getImage();
        response.setContentType(output);
        response.setHeader("Cache-Control", "no-cache;max-age=0");   // setting no-cache as otherwise mozilla showing incorrect cached response
        response.setContentLength(image.length);
        response.getOutputStream().write(image);
        response.flushBuffer();
    }

    private RenderMapResponse getQueryMap(HttpServletRequest request) throws IOException {
        RenderMapParams renderMapParams = createRenderMapParams(request);
        Map mapObj = getQueryMapObj(request);
        RenderMapResponse renderMapResponse = null;
        if(mapObj != null){
            try {
                renderMapResponse = getMappingService().renderMap(renderMapParams, mapObj);
            } catch (ServiceException e) {
                logger.error("Encountered ServiceException while rendering query result map ",e);
                throw new Error("Encountered ServiceException while rendering query result map " , e);
            }
        }
        return renderMapResponse;
    }

    private Map getQueryMapObj (HttpServletRequest request) throws IOException {

        //read json from request
        String queryJson =  request.getParameter(QUERY_RESULT_MAP_JSON);
        if (StringUtils.isEmpty(queryJson)) {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING + QUERY_RESULT_MAP_JSON);
        }
        //String queryResultMapJson = EncodingUtil.decodeURIComponent(queryJson);
        JsonNode treeNode = mapper.readTree(queryJson);

        Map map = new Map();
        FeatureLayer featureLayer = new FeatureLayer();
        ViewTable viewTable = new ViewTable();
        viewTable.setName("outer");
        String sqlQuery = getValue(treeNode, SQL_PARAM_NAME);
        logger.debug("The query used for displaying query layer on the map: " + sqlQuery);
        viewTable.setSQL(sqlQuery);
        featureLayer.setTable(viewTable);

        ThemeList themeList = new ThemeList();
        themeList.getTheme().add(getDefaultTheme(treeNode));
        featureLayer.setThemeList(themeList);
        map.getLayer().add(featureLayer);
        return map;
    }

    private String getValue(JsonNode node, String nodeName) {
        JsonNode valueNode = node.get(nodeName);
        if (valueNode == null) {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING + nodeName);
        }
        return valueNode.asText();
    }

    private RenderMapParams createRenderMapParams(HttpServletRequest request) {
        RenderMapParams params = new RenderMapParams();
        params.setSrs(request.getParameter(SRS_PARAM_NAME));
        params.setHeight(request.getParameter(HEIGHT_PARAM_NAME));
        params.setWidth(request.getParameter(WIDTH_PARAM_NAME));
        params.setXPos(request.getParameter(X_PARAM_NAME));
        params.setYPos(request.getParameter(Y_PARAM_NAME));
        params.setZoom(request.getParameter(ZOOM_PARAM_NAME));
        params.setImageMimeType(request.getParameter(OUTPUT_PARAM_NAME));
        params.setReturnImage(true);
        params.setRendering(Rendering.QUALITY);
        return params;
    }

    private OverrideTheme getDefaultTheme (JsonNode treeNode){
        MapBasicAreaStyle polygonStyle= StyleBuilder.createPolygonStyle(getValue(treeNode, FILL_COLOR), "2",
                getValue(treeNode, LINE_COLOR),"2","2");
        MapBasicLineStyle lineStyle = StyleBuilder.createLineStyle(getValue(treeNode, LINE_COLOR),"2","2");
        MapBasicPointStyle pointStyle=StyleBuilder.createPointStyle("41", getValue(treeNode, POINT_FONT_COLOR), "10",
                "MapInfo Cartographic", "normal");
        MapBasicCompositeStyle style =StyleBuilder.createCompositeStyle(polygonStyle, lineStyle, pointStyle);
        OverrideTheme overrideTheme= new OverrideTheme();
        overrideTheme.setStyle(style);
        return overrideTheme;

    }
}
