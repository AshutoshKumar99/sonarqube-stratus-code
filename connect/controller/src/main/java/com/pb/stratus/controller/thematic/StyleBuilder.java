package com.pb.stratus.controller.thematic;

import com.mapinfo.midev.service.style.v1.*;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/29/14
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 * This class is used for thematics rendering as well as annotation style rendering
 */
public class StyleBuilder {

    public static MapBasicPointStyle createPointStyle(String shape, String fontColor, String fontSize, String fontName, String border) {
        MapBasicPointStyle pointStyle = new MapBasicPointStyle();
        MapBasicFontSymbol mapBasicFontSymbol = new MapBasicFontSymbol();
        mapBasicFontSymbol.setShape(Integer.valueOf(shape)); //Font Symbol - should have integer value of 32 or more
        mapBasicFontSymbol.setColor(fontColor);  //	Font Color
        mapBasicFontSymbol.setSize(Short.valueOf(fontSize));  //Font Size
        mapBasicFontSymbol.setFontName(fontName);
        mapBasicFontSymbol.setBorder(border);
        pointStyle.setMapBasicSymbol(mapBasicFontSymbol);
        return pointStyle;
    }

    /**
     * Create a Default Point Style with the given Colors
     * @param fontColor
     * @return
     */
    public static MapBasicPointStyle createPointStyle(String fontColor) {
        return createPointStyle("35", fontColor, "12", "Mapinfo Symbols", "normal");
    }

    public static MapBasicLineStyle createLineStyle(String color, String borderPattern, String borderWidth) {
        MapBasicLineStyle lineStyle = new MapBasicLineStyle();
        MapBasicPen mapBasicPen = new MapBasicPen();
        mapBasicPen.setColor(color);
        //CONN-19816 Hard coding this for now TODO Need to replace this with patten when pattern becomes available
        mapBasicPen.setPattern(Short.valueOf(borderPattern));
        mapBasicPen.setWidth(Short.valueOf(borderWidth));
        lineStyle.setMapBasicPen(mapBasicPen);
        return lineStyle;
    }

    /**
     * Will create a Default Line Style with the Given Color
     * @param color
     * @return
     */
    public static MapBasicLineStyle createLineStyle(String color) {
        return createLineStyle(color, "2", "2");
    }

    public static MapBasicAreaStyle createPolygonStyle(String fillColor, String fillPattern, String borderColor, String borderPattern, String borderWidth) {
        MapBasicAreaStyle polygonStyle = new MapBasicAreaStyle();
        MapBasicBrush mapBasicBrush = new MapBasicBrush();
        mapBasicBrush.setForegroundColor(fillColor);
        mapBasicBrush.setPattern(Short.valueOf(fillPattern));

        MapBasicPen mapBasicPen = new MapBasicPen();
        mapBasicPen.setColor(borderColor);
        mapBasicPen.setPattern(Short.valueOf(borderPattern));
        mapBasicPen.setWidth(Short.valueOf(borderWidth));

        polygonStyle.setMapBasicBrush(mapBasicBrush);
        polygonStyle.setMapBasicPen(mapBasicPen);
        return polygonStyle;
    }

    /**
     * Create a Default Polygon Style with the given color
     * @param color
     * @return
     */
    public static MapBasicAreaStyle createPolygonStyle(String color) {
        return createPolygonStyle(color, "2", color, "2", "2");
    }

    public static MapBasicCompositeStyle createCompositeStyle(MapBasicAreaStyle polygonStyle, MapBasicLineStyle lineStyle, MapBasicPointStyle pointStyle) {
        MapBasicCompositeStyle compositeStyle = new MapBasicCompositeStyle();
        compositeStyle.setAreaStyle(polygonStyle);
        compositeStyle.setLineStyle(lineStyle);
        compositeStyle.setPointStyle(pointStyle);
        return compositeStyle;
    }

    /**
     * Create a Default Text Style
     * @param color
     * @return
     */
    public static MapBasicTextStyle createTextStyle(String color) {
        return createTextStyle(color, 4, color, null, 2);
    }

    /**
     *
     * @param backgroundColor
     * @param size
     * @param foregroundColor
     * @param fontName
     * @param style
     * @return
     */
    public static MapBasicTextStyle createTextStyle(String backgroundColor, int size, String foregroundColor,
                                                    String fontName, int style) {
        MapBasicTextStyle textStyle = new MapBasicTextStyle();
        MapBasicFontStyle mapBasicFontStyle = new MapBasicFontStyle();
        mapBasicFontStyle.setBackgroundColor(backgroundColor);
        mapBasicFontStyle.setSize(size);
        mapBasicFontStyle.setForegroundColor(foregroundColor);
        mapBasicFontStyle.setFontName(fontName);
        mapBasicFontStyle.setStyle(style);
        textStyle.setMapBasicFontStyle(mapBasicFontStyle);
        return textStyle;
    }
}
