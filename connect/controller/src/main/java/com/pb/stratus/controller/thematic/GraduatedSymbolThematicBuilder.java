package com.pb.stratus.controller.thematic;

import com.fasterxml.jackson.databind.JsonNode;
import com.mapinfo.midev.service.mapping.v1.BaseSize;
import com.mapinfo.midev.service.mapping.v1.FeatureLayer;
import com.mapinfo.midev.service.mapping.v1.GraduatedSymbolLayer;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.mapinfo.midev.service.mappingcommon.v1.GraduationMethod;
import com.mapinfo.midev.service.style.v1.MapBasicPointStyle;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.mapinfo.midev.service.table.v1.Table;
import com.mapinfo.midev.service.theme.v1.RangeTheme;
import com.mapinfo.midev.service.theme.v1.SpreadBy;
import com.mapinfo.midev.service.theme.v1.Theme;
import com.mapinfo.midev.service.theme.v1.ThemeList;
import com.pb.stratus.controller.IllegalRequestException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/29/14
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraduatedSymbolThematicBuilder  extends AbstractThematicBuilder {


    private static final String POINT_STYLE_COLOR = "pointStyleColor";
    private static final String SPATIAL_TABLE_COLUMN = "spatialTableColumn";
    private static final String VALUE_AT_SIZE = "valueAtSize";
    private static final String GRADUATED_METHOD = "graduatedMethod";
    private static final String SHOW_NEGATIVE_SYMBOL = "showNegativeSymbol";
    private static final String SHOW_POSITIVE_SYMBOL = "showPositiveSymbol";
    private static final String MAP_SCALE = "mapScale";
    private static final String USE_SCALE = "useScale";
    private static final String POSITIVE_SYMBOL_STYLE = "positiveSymbolStyle";
    private static final String NEGATIVE_SYMBOL_STYLE = "negativeSymbolStyle";

    public Map getMap (JsonNode treeNode){

        Map map = new Map();
        GraduatedSymbolLayer graduatedSymbolLayer = new GraduatedSymbolLayer();

        if(treeNode.get(SQL_PARAM_NAME) != null){
            setSqlQueryToViewTable(treeNode, graduatedSymbolLayer);
        } else {
            Table table = new NamedTable();
            table.setName(getValue(treeNode, TABLE_PATH));
            graduatedSymbolLayer.setTable(table);
        }

        populateGraduatedLayer(treeNode, graduatedSymbolLayer);
        map.getLayer().add(graduatedSymbolLayer);
        return map;
    }

    /**
     * Populates the GraduatedSymbolLayer object with values from UI
     */
    private GraduatedSymbolLayer populateGraduatedLayer(JsonNode treeNode,  GraduatedSymbolLayer layer) {

        //Set column name of table
        String tableColumn = getValue(treeNode, TABLE_COLUMN);
        layer.setValueExpression(tableColumn);

        // set metadata properties
        JsonNode metaDataNode = treeNode.get(THEMATIC_META_DATA);
/*
        String spatialTableColumn = getValue(metaDataNode, SPATIAL_TABLE_COLUMN);
        layer.setSpatialExpression(spatialTableColumn);*/

        String valueAtSize = getValue(metaDataNode, VALUE_AT_SIZE);
        layer.setValueAtSize(Double.valueOf(valueAtSize));

        String graduatedMethod =  getValue(metaDataNode, GRADUATED_METHOD);
        layer.setGraduationMethod(GraduationMethod.fromValue(graduatedMethod));

        String showPositiveSymbolString =  getValue(metaDataNode, SHOW_POSITIVE_SYMBOL);
        Boolean showPositiveSymbol = Boolean.valueOf(showPositiveSymbolString);
        layer.setShowPositiveSymbol(showPositiveSymbol);
        String showNegativeSymbolString =  getValue(metaDataNode, SHOW_NEGATIVE_SYMBOL);
        Boolean showNegativeSymbol = Boolean.valueOf(showNegativeSymbolString);
        layer.setShowNegativeSymbol(showNegativeSymbol);

        if(showPositiveSymbol){
            // set positive symbol style
            JsonNode positiveStyleNode = metaDataNode.get(POSITIVE_SYMBOL_STYLE);
            MapBasicPointStyle positiveSymbolStyle = StyleBuilder.createPointStyle(
                    getValue(positiveStyleNode, POINT_SHAPE),
                    getValue(positiveStyleNode, POINT_STYLE_COLOR),
                    getValue(positiveStyleNode, POINT_SIZE),
                    getValue(positiveStyleNode, POINT_NAME),
                    getValue(positiveStyleNode, POINT_BORDER));
            layer.setPositiveSymbol(positiveSymbolStyle);
        }

        if(showNegativeSymbol){
            // set negative symbol style
            JsonNode negativeStyleNode = metaDataNode.get(NEGATIVE_SYMBOL_STYLE);
            MapBasicPointStyle negativeSymbolStyle = StyleBuilder.createPointStyle(
                    getValue(negativeStyleNode, POINT_SHAPE),
                    getValue(negativeStyleNode, POINT_STYLE_COLOR),
                    getValue(negativeStyleNode, POINT_SIZE),
                    getValue(negativeStyleNode, POINT_NAME),
                    getValue(negativeStyleNode, POINT_BORDER));
            layer.setNegativeSymbol(negativeSymbolStyle);
        }

        BaseSize baseSize = new BaseSize();
        boolean useScale = false;
        try{
            useScale = Boolean.valueOf(getValue(metaDataNode, USE_SCALE));
        }catch(IllegalRequestException ex){
            useScale = false;
        }
        if(useScale){
            baseSize.setUseScale(true);
            String mapScale =  getValue(metaDataNode, MAP_SCALE);
            baseSize.setMapScale(Double.valueOf(mapScale).doubleValue());
        }else{
            baseSize.setUseScale(false);
        }
        layer.setSymbolBaseSize(baseSize);

        return layer;
    }


}
