package com.pb.stratus.controller.thematic;

import com.fasterxml.jackson.databind.JsonNode;
import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.mapinfo.midev.service.style.v1.*;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.mapinfo.midev.service.table.v1.Table;
import com.mapinfo.midev.service.theme.v1.*;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/29/14
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class RangeThematicBuilder extends AbstractThematicBuilder {

    public Map getMap(JsonNode treeNode) {

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
        Theme theme = getRangeTheme(treeNode);
        themeList.getTheme().add(theme);
        featureLayer.setThemeList(themeList);
        map.getLayer().add(featureLayer);
        return map;
    }


    /**
     * Creates range theme object based on geometry type
     */
    private RangeTheme getRangeTheme(JsonNode treeNode) {
        String startColor;
        String endColor;
        JsonNode metaDataNode = treeNode.get(THEMATIC_META_DATA);
        JsonNode styleNode = metaDataNode.get(STYLE_OBJ);
        RangeTheme rangeTheme = new RangeTheme();
        String geometryType = getValue(metaDataNode, GEOMETRY_TYPE);
        if (POINT_TYPE.equalsIgnoreCase(geometryType))
        {
            startColor = getValue(styleNode, POINT_START_COLOR);
            endColor = getValue(styleNode, POINT_END_COLOR);
            MapBasicPointStyle pointStartStyle = StyleBuilder.createPointStyle(getValue(styleNode, POINT_SHAPE), startColor,
                    getValue(styleNode, POINT_SIZE), getValue(styleNode, POINT_NAME), getValue(styleNode, POINT_BORDER));
            MapBasicPointStyle pointEndStyle = StyleBuilder.createPointStyle(getValue(styleNode, POINT_SHAPE), endColor,
                    getValue(styleNode, POINT_SIZE), getValue(styleNode, POINT_NAME), getValue(styleNode, POINT_BORDER));
            MapBasicCompositeStyle compositeStartStyle = StyleBuilder.createCompositeStyle(StyleBuilder.createPolygonStyle(startColor),
                    StyleBuilder.createLineStyle(startColor), pointStartStyle);
            Style compositeEndStyle = StyleBuilder.createCompositeStyle(StyleBuilder.createPolygonStyle(endColor),
                    StyleBuilder.createLineStyle(endColor), pointEndStyle);
            rangeTheme.setStartStyle(compositeStartStyle);
            rangeTheme.setEndStyle(compositeEndStyle);
        }
        else if (LINE_TYPE.equalsIgnoreCase(geometryType))
        {
            startColor = getValue(styleNode, LINE_START_COLOR);
            endColor = getValue(styleNode, LINE_END_COLOR);
            MapBasicLineStyle lineStartStyle = StyleBuilder.createLineStyle(startColor, getValue(styleNode, LINE_PATTERN),
                    getValue(styleNode, LINE_WIDTH));
            MapBasicLineStyle lineEndStyle = StyleBuilder.createLineStyle(endColor, getValue(styleNode, LINE_PATTERN),
                    getValue(styleNode, LINE_WIDTH));
            MapBasicCompositeStyle compositeStartStyle = StyleBuilder.createCompositeStyle(StyleBuilder.createPolygonStyle(startColor),
                    lineStartStyle, StyleBuilder.createPointStyle(startColor));
            Style compositeEndStyle = StyleBuilder.createCompositeStyle(StyleBuilder.createPolygonStyle(endColor),
                    lineEndStyle, StyleBuilder.createPointStyle(endColor));
            rangeTheme.setStartStyle(compositeStartStyle);
            rangeTheme.setEndStyle(compositeEndStyle);
        }
        else if (POLYGON_TYPE.equalsIgnoreCase(geometryType))
        {
            //PolyGon Style
            startColor = getValue(styleNode, POLYGON_START_COLOR);
            endColor = getValue(styleNode, POLYGON_END_COLOR);

            MapBasicAreaStyle polygonStartStyle = StyleBuilder.createPolygonStyle(startColor,
                    getValue(styleNode, POLYGON_PATTERN),
                    getValue(styleNode, LINE_COLOR),
                    getValue(styleNode, LINE_PATTERN),
                    getValue(styleNode, LINE_WIDTH));

            MapBasicAreaStyle polygonEndStyle = StyleBuilder.createPolygonStyle(endColor,
                    getValue(styleNode, POLYGON_PATTERN),
                    getValue(styleNode, LINE_COLOR),
                    getValue(styleNode, LINE_PATTERN),
                    getValue(styleNode, LINE_WIDTH));

            MapBasicCompositeStyle compositeStartStyle = StyleBuilder.createCompositeStyle(
                    polygonStartStyle, StyleBuilder.createLineStyle(startColor),
                    StyleBuilder.createPointStyle(startColor));

            Style compositeEndStyle = StyleBuilder.createCompositeStyle(polygonEndStyle,
                    StyleBuilder.createLineStyle(endColor), StyleBuilder.createPointStyle(endColor));
            rangeTheme.setStartStyle(compositeStartStyle);
            rangeTheme.setEndStyle(compositeEndStyle);
        }
        else
        {
           // In case we get all the styles from client
            rangeTheme = createRangeThemeForAllGeometries(styleNode);
        }
        rangeTheme.setRangeThemeProperties(getRangeThemeProperties(treeNode, metaDataNode));
        return rangeTheme;
    }

    private RangeTheme createRangeThemeForAllGeometries(JsonNode styleNode)
    {
        RangeTheme rangeTheme = new RangeTheme();
        String startColor;
        String endColor;
        // In case we get all the styles from client
//        createRangeThemeForAllGeometries(styleNode);
        MapBasicPointStyle mapBasicPointStartStyle = null;
        MapBasicPointStyle mapBasicPointEndStyle = null;
        MapBasicLineStyle mapBasicLineStartStyle = null;
        MapBasicLineStyle mapBasicLineEndStyle = null;
        MapBasicAreaStyle mapBasicAreaStartStyle = null;
        MapBasicAreaStyle mapBasicAreaEndStyle = null;
        JsonNode pointStyleNode = styleNode.get("pointStyle");
        JsonNode lineStyleNode = styleNode.get("lineStyle");
        JsonNode polygonStyleNode = styleNode.get("polygonStyle");
        if (pointStyleNode != null)
        {
            startColor = getValue(pointStyleNode, POINT_START_COLOR);
            endColor = getValue(pointStyleNode, POINT_END_COLOR);
            mapBasicPointStartStyle = StyleBuilder.createPointStyle(getValue(pointStyleNode, POINT_SHAPE), startColor,
                    getValue(pointStyleNode, POINT_SIZE), getValue(pointStyleNode, POINT_NAME), getValue(pointStyleNode, POINT_BORDER));
            mapBasicPointEndStyle = StyleBuilder.createPointStyle(getValue(pointStyleNode, POINT_SHAPE), endColor,
                    getValue(pointStyleNode, POINT_SIZE), getValue(pointStyleNode, POINT_NAME), getValue(pointStyleNode, POINT_BORDER));
        }
        if (lineStyleNode != null)
        {
            startColor = getValue(lineStyleNode, LINE_START_COLOR);
            endColor = getValue(lineStyleNode, LINE_END_COLOR);
            mapBasicLineStartStyle = StyleBuilder.createLineStyle(startColor, getValue(lineStyleNode, LINE_PATTERN),
                    getValue(lineStyleNode, LINE_WIDTH));
            mapBasicLineEndStyle = StyleBuilder.createLineStyle(endColor, getValue(lineStyleNode, LINE_PATTERN),
                    getValue(lineStyleNode, LINE_WIDTH));
        }
        if (polygonStyleNode != null)
        {
            startColor = getValue(polygonStyleNode, POLYGON_START_COLOR);
            endColor = getValue(polygonStyleNode, POLYGON_END_COLOR);

            mapBasicAreaStartStyle = StyleBuilder.createPolygonStyle(startColor,
                    getValue(polygonStyleNode, POLYGON_PATTERN),
                    getValue(polygonStyleNode, LINE_COLOR),
                    getValue(polygonStyleNode, LINE_PATTERN),
                    getValue(polygonStyleNode, LINE_WIDTH));

            mapBasicAreaEndStyle = StyleBuilder.createPolygonStyle(endColor,
                    getValue(polygonStyleNode, POLYGON_PATTERN),
                    getValue(polygonStyleNode, LINE_COLOR),
                    getValue(polygonStyleNode, LINE_PATTERN),
                    getValue(polygonStyleNode, LINE_WIDTH));
        }
        MapBasicCompositeStyle compositeStartStyle = StyleBuilder.createCompositeStyle(
                mapBasicAreaStartStyle, mapBasicLineStartStyle, mapBasicPointStartStyle);

        Style compositeEndStyle = StyleBuilder.createCompositeStyle(mapBasicAreaEndStyle,
                mapBasicLineEndStyle, mapBasicPointEndStyle);
        rangeTheme.setStartStyle(compositeStartStyle);
        rangeTheme.setEndStyle(compositeEndStyle);

        return rangeTheme;
    }


    /**
     *
     * @param treeNode
     * @param metaDataNode
     * @return
     */
    private RangeThemeProperties getRangeThemeProperties(JsonNode treeNode, JsonNode metaDataNode) {
        RangeThemeProperties rangeThemeProperties = new RangeThemeProperties();
        //Set RangeThemeType
        String rangeThemeType = getValue(metaDataNode, RANGE_THEME_TYPE);
        rangeThemeProperties.setRangeType(RangeThemeType.fromValue(rangeThemeType));
        //Set column name of table
        String tableColumn = getValue(treeNode, TABLE_COLUMN);
        rangeThemeProperties.setExpression(tableColumn);

        if (rangeThemeType.equalsIgnoreCase(RangeThemeType.BI_QUANTILE.value())) {
            rangeThemeProperties.setQuantileExpression(tableColumn);
        }
        //Set No.of Ranges
        String rangeCount = getValue(metaDataNode, RANGE_COUNT);
        rangeThemeProperties.setNumRanges(Integer.valueOf(rangeCount).intValue());
        return rangeThemeProperties;
    }
}
