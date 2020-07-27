package com.pb.stratus.controller.thematic;

import com.fasterxml.jackson.databind.JsonNode;
import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.mapping.v1.GraduatedSymbolLayer;
import com.mapinfo.midev.service.mapping.v1.Layer;
import com.mapinfo.midev.service.table.v1.ViewTable;
import com.pb.stratus.controller.IllegalRequestException;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 12/9/14
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractThematicBuilder {
    public static final String TABLE_PATH = "table";
    public static final String TABLE_COLUMN = "tableColumn";
    public static final String TABLE_COLUMN_TYPE = "tableColumnType";
    public static final String THEMATIC_META_DATA = "thematicMetaData";
    public static final String STYLE_OBJ = "style";
    public static final String GEOMETRY_TYPE = "geometryType";
    public static final String RANGE_THEME_TYPE = "rangeThemeType";
    public static final String RANGE_COUNT = "rangeCount";
    public static final String POLYGON_TYPE = "Polygon";
    public static final String LINE_TYPE = "Line";
    public static final String POINT_TYPE = "Point";

    public static final String POLYGON_START_COLOR = "polygonStyleStartColor";
    public static final String POLYGON_END_COLOR = "polygonStyleEndColor";
    public static final String POLYGON_PATTERN = "polygonStylePattern";
    public static final String LINE_COLOR = "lineStyleColor";

    public static final String LINE_START_COLOR = "lineStyleStartColor";
    public static final String LINE_END_COLOR = "lineStyleEndColor";
    public static final String LINE_PATTERN = "lineStylePattern";
    public static final String LINE_WIDTH = "lineStyleWidth";

    public static final String POINT_SHAPE = "pointStyleShape";
    public static final String POINT_START_COLOR = "pointStyleStartColor";
    public static final String POINT_END_COLOR = "pointStyleEndColor";
    public static final String POINT_SIZE = "pointStyleFontSize";
    public static final String POINT_NAME = "pointStyleFontName";
    public static final String POINT_BORDER = "pointStyleBorder";
    public static final String POINT_DROP_SHADOW = "pointStyleDropShadow";
    public static final String POINT_ROTATION = "pointStyleRotation";
    public final static String INVALID_PARAMETER_STRING = "The following parameter was not found in request: ";

    public static final String SQL_PARAM_NAME = "querySQL";

    public String getValue(JsonNode node, String nodeName) {
        JsonNode valueNode = node.get(nodeName);
        if (valueNode == null) {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING + nodeName);
        }
        return valueNode.asText();
    }

    public void setSqlQueryToViewTable(JsonNode treeNode, Layer layer){
        ViewTable viewTable = new ViewTable();
        viewTable.setName("outer");
        String sqlQuery = getValue(treeNode, SQL_PARAM_NAME);
        viewTable.setSQL(sqlQuery);
        if(layer instanceof GraduatedSymbolLayer){
            ((GraduatedSymbolLayer) layer).setTable(viewTable);
        } else {
            ((FeatureLayer) layer).setTable(viewTable);
        }
    }
}
