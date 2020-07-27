package com.pb.stratus.controller.thematic;

import com.fasterxml.jackson.databind.JsonNode;
import com.mapinfo.midev.service.featurecollection.v1.*;
import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.mapinfo.midev.service.style.v1.MapBasicAreaStyle;
import com.mapinfo.midev.service.style.v1.MapBasicLineStyle;
import com.mapinfo.midev.service.style.v1.MapBasicPointStyle;
import com.mapinfo.midev.service.style.v1.Style;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.mapinfo.midev.service.table.v1.Table;
import com.mapinfo.midev.service.table.v1.ViewTable;
import com.mapinfo.midev.service.theme.v1.*;
import com.pb.stratus.controller.IllegalRequestException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/29/14
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndividualValueThematicBuilder extends AbstractThematicBuilder {

    private StyleBuilder styleBuilder;

    private static final String BIN_LIST = "binList";
    private static final String POLYGON_STYLE_COLOR = "polygonStyleColor";
    private static final String POINT_STYLE_COLOR = "pointStyleColor";
    private static final String LINE_STYLE_COLOR = "lineStyleColor";
    private static final String EXPRESSION_VALUE = "expressionValue";
    private static final String ALL_OTHERS = "allOthers";
    private static final String DEFAULT_VALUE_1 = "1";
    private static final String DEFAULT_VALUE_2 = "2";
    private static final String DEFAULT_VALUE_24 = "24";
    private static final String DEFAULT_VALUE_42 = "42";
    private static final String DEFAULT_POLYGON_COLOR = "#33CCCC";
    private static final String DEFAULT_LINE_COLOR = "#003366";
    private static final String DEFAULT_POINT_COLOR = "#FF0000";
    private static final String DEFAULT_POINT_BORDER = "normal";
    private static final String DEFAULT_POINT_NAME = "MapInfo Symbols";

    public IndividualValueThematicBuilder() {
        this.styleBuilder = new StyleBuilder();
    }

    public Map getMap (JsonNode treeNode){
        Map map = new Map();
        FeatureLayer featureLayer = new FeatureLayer();

        if(treeNode.get(SQL_PARAM_NAME) != null){
            setSqlQueryToViewTable(treeNode, featureLayer);
        } else {
            Table table = new NamedTable();
            table.setName(getValue(treeNode, TABLE_PATH));
            featureLayer.setTable(table);
        }

        ThemeList themeList = new ThemeList();
        Theme theme = getIndividualValueTheme(treeNode);
        themeList.getTheme().add(theme);
        featureLayer.setThemeList(themeList);
        map.getLayer().add(featureLayer);
        return map;
    }


    /**
     *  Creates individual theme object based on geometry type in request
     * */
    private IndividualValueTheme getIndividualValueTheme(JsonNode treeNode){
        IndividualValueTheme individualValueTheme = new IndividualValueTheme();

        individualValueTheme.setExpression(getValue(treeNode, TABLE_COLUMN));
        AttributeDataType tableColumnType = AttributeDataType.valueOf(getValue(treeNode, TABLE_COLUMN_TYPE));
        JsonNode metaDataNode = treeNode.get(THEMATIC_META_DATA);
        String geometryType = getValue(metaDataNode, GEOMETRY_TYPE);
        JsonNode binListNode = metaDataNode.get(BIN_LIST);
        Iterator<JsonNode> binListNodeIterator = binListNode.elements();

        BinList binList = new BinList();
        while(binListNodeIterator.hasNext()){
            JsonNode binNode = binListNodeIterator.next();
            Bin bin = new Bin();
            bin.setValue(getExpressionValue(binNode, tableColumnType));
            bin.setStyle(getStyleForGivenGeometry(binNode, geometryType));
            binList.getBin().add(bin);
        }
        individualValueTheme.setBinList(binList);
        JsonNode allOthersNode = metaDataNode.get(ALL_OTHERS);

        // AllOthers is mandatory column for IndividualValueTheme
        // Set the default scheme if it is not present in the incoming JSON
        if (null != allOthersNode) {
            individualValueTheme.setAllOthers(getStyleForGivenGeometry(allOthersNode, geometryType));
        } else {
            individualValueTheme.setAllOthers(getDefaultStyleForGivenGeometry(geometryType));
        }

        return individualValueTheme;
    }

    private AttributeValue getExpressionValue(JsonNode binNode, AttributeDataType tableColumnType) {

        switch (tableColumnType){
            case BOOLEAN:
                BooleanValue booleanValue = new BooleanValue();
                booleanValue.setValue(binNode.get(EXPRESSION_VALUE).booleanValue());
                return booleanValue;

            case DECIMAL:
            case FLOAT:
            case DOUBLE:
                DoubleValue doubleValue = new DoubleValue();
                doubleValue.setValue(binNode.get(EXPRESSION_VALUE).asDouble());
                return doubleValue;

            case SHORT:
            case INT:
                IntValue intValue = new IntValue();
                intValue.setValue(binNode.get(EXPRESSION_VALUE).asInt());
                return intValue;
            case LONG:
                LongValue longValue = new LongValue();
                longValue.setValue(binNode.get(EXPRESSION_VALUE).asLong());
                return longValue;

            case DATE:
                DateValue dateValue = new DateValue();
                XMLGregorianCalendar xmlDate = null;
                try {
                    xmlDate= DatatypeFactory.newInstance().newXMLGregorianCalendar(binNode.get(EXPRESSION_VALUE).asText());
                } catch (DatatypeConfigurationException e) {
                    throw new IllegalRequestException(e);
                }
                dateValue.setValue(xmlDate);
                return dateValue;
            case TIME:
                TimeValue timeValue = new TimeValue();
                XMLGregorianCalendar xmlTime = null;
                try {
                    xmlTime= DatatypeFactory.newInstance().newXMLGregorianCalendar(binNode.get(EXPRESSION_VALUE).asText());
                } catch (DatatypeConfigurationException e) {
                    throw new IllegalRequestException(e);
                }
                timeValue.setValue(xmlTime);
                return timeValue;
            case DATE_TIME:
                DateTimeValue dateTimeValue = new DateTimeValue();
                XMLGregorianCalendar xmlDateTime = null;
                try {
                    xmlDateTime= DatatypeFactory.newInstance().newXMLGregorianCalendar(binNode.get(EXPRESSION_VALUE).asText());
                } catch (DatatypeConfigurationException e) {
                    throw new IllegalRequestException(e);
                }
                dateTimeValue.setValue(xmlDateTime);
                return dateTimeValue;
            default:
                StringValue sValue = new StringValue();
                sValue.setValue(binNode.get(EXPRESSION_VALUE).asText());
                return sValue;
        }
    }

    private Style getStyleForGivenGeometry(JsonNode node, String geometryType) {
        Style style = null;
        JsonNode styleNode = node.get(STYLE_OBJ);
        if(geometryType.equalsIgnoreCase(POLYGON_TYPE)){
            String polygonColor = getValue(styleNode, POLYGON_STYLE_COLOR);
            MapBasicAreaStyle polygonStyle = StyleBuilder.createPolygonStyle(polygonColor,
                    getValue(styleNode, POLYGON_PATTERN),
                    getValue(styleNode, LINE_STYLE_COLOR),
                    getValue(styleNode, LINE_PATTERN),
                    getValue(styleNode, LINE_WIDTH));

            style = StyleBuilder.createCompositeStyle(
                    polygonStyle,
                    StyleBuilder.createLineStyle(polygonColor),
                    StyleBuilder.createPointStyle(polygonColor));
        }
        else if(geometryType.equalsIgnoreCase(LINE_TYPE)){
            String lineColor = getValue(styleNode, LINE_COLOR);
            MapBasicLineStyle lineStyle = StyleBuilder.createLineStyle(lineColor,
                    getValue(styleNode, LINE_PATTERN),
                    getValue(styleNode, LINE_WIDTH));

            style = StyleBuilder.createCompositeStyle(
                    StyleBuilder.createPolygonStyle(lineColor),
                    lineStyle,
                    StyleBuilder.createPointStyle(lineColor));
        }
        else if(geometryType.equalsIgnoreCase(POINT_TYPE)){
            String pointColor = getValue(styleNode, POINT_STYLE_COLOR);
            MapBasicPointStyle pointStyle = StyleBuilder.createPointStyle(getValue(styleNode, POINT_SHAPE),
                    pointColor, getValue(styleNode, POINT_SIZE),
                    getValue(styleNode, POINT_NAME),
                    getValue(styleNode, POINT_BORDER));

            style = StyleBuilder.createCompositeStyle(
                    StyleBuilder.createPolygonStyle(pointColor),
                    StyleBuilder.createLineStyle(pointColor),
                    pointStyle);
        }else{
            style = createStyleForAllGeometries(styleNode);

        }
        return style;
    }

    private Style createStyleForAllGeometries(JsonNode styleNode) {
        JsonNode pointStyleNode = styleNode.get("pointStyle");
        JsonNode polygonStyleNode = styleNode.get("polygonStyle");
        JsonNode lineStyleNode = styleNode.get("lineStyle");
        MapBasicAreaStyle polygonStyle = null;
        MapBasicPointStyle pointStyle = null;
        MapBasicLineStyle lineStyle = null;
        String color = "";
        if(polygonStyleNode !=null) {
            String polygonColor = getValue(polygonStyleNode, POLYGON_STYLE_COLOR);
            if(color == null){
                color = polygonColor;
            }
            polygonStyle = StyleBuilder.createPolygonStyle(polygonColor,
                    getValue(polygonStyleNode, POLYGON_PATTERN),
                    getValue(polygonStyleNode, LINE_STYLE_COLOR),
                    getValue(polygonStyleNode, LINE_PATTERN),
                    getValue(polygonStyleNode, LINE_WIDTH));
        }
        if(lineStyleNode !=null){
            String lineColor = getValue(lineStyleNode, LINE_COLOR);
            if(color == null){
                color = lineColor;
            }
            lineStyle = StyleBuilder.createLineStyle(lineColor,
                    getValue(lineStyleNode, LINE_PATTERN),
                    getValue(lineStyleNode, LINE_WIDTH));

        }
        if(pointStyleNode !=null) {
            String pointColor = getValue(pointStyleNode, POINT_STYLE_COLOR);
            if(color == null ){
                color = pointColor;
            }
            pointStyle = StyleBuilder.createPointStyle(getValue(pointStyleNode, POINT_SHAPE),
                    pointColor, getValue(pointStyleNode, POINT_SIZE),
                    getValue(pointStyleNode, POINT_NAME),
                    getValue(pointStyleNode, POINT_BORDER));
        }

        // Set style if any of the geometry node is not specified
        return StyleBuilder.createCompositeStyle(polygonStyle, lineStyle, pointStyle);
    }

    private Style getDefaultStyleForGivenGeometry(String geometryType) {
        Style style = null;
        if(geometryType.equalsIgnoreCase(POLYGON_TYPE)){
            MapBasicAreaStyle polygonStyle = StyleBuilder.createPolygonStyle(DEFAULT_POLYGON_COLOR, DEFAULT_VALUE_2,
                    DEFAULT_POLYGON_COLOR, DEFAULT_VALUE_1, DEFAULT_VALUE_1);
            style = StyleBuilder.createCompositeStyle(polygonStyle, StyleBuilder.createLineStyle(DEFAULT_POLYGON_COLOR),
                    StyleBuilder.createPointStyle(DEFAULT_POLYGON_COLOR));
        }
        else if(geometryType.equalsIgnoreCase(LINE_TYPE)){
            MapBasicLineStyle lineStyle = StyleBuilder.createLineStyle(DEFAULT_LINE_COLOR, DEFAULT_VALUE_2,
                    DEFAULT_VALUE_2);
            style = StyleBuilder.createCompositeStyle(StyleBuilder.createPolygonStyle(DEFAULT_LINE_COLOR), lineStyle,
                    StyleBuilder.createPointStyle(DEFAULT_LINE_COLOR));
        }
        else if(geometryType.equalsIgnoreCase(POINT_TYPE)){
            MapBasicPointStyle pointStyle = StyleBuilder.createPointStyle(DEFAULT_VALUE_42, DEFAULT_POINT_COLOR,
                    DEFAULT_VALUE_24, DEFAULT_POINT_NAME, DEFAULT_POINT_BORDER);
            style = StyleBuilder.createCompositeStyle(StyleBuilder.createPolygonStyle(DEFAULT_POINT_COLOR),
                    StyleBuilder.createLineStyle(DEFAULT_POINT_COLOR), pointStyle);
        }
        return style;
    }
}
