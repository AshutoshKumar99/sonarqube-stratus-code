package com.pb.stratus.controller.annotation;

/**
 * Allows creation of Style element that is associated with a typical geometry.
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 19/6/12
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationStyle {

    private String fillColor;
    private double fillOpacity;
    private String strokeColor;
    private String strokeWidth;
    private String strokePattern;
    //Polygon,circle,ellipse,sq,rectangle Related properties ;
    private String externalGraphic;
    private String fill;
    private String spatialGraphicCode = "2"; //default case :no style applied

    public AnnotationStyle(String fillColor){
        this.fillColor = fillColor;
    }

    public AnnotationStyle(String strokeColor, double fillOpacity, String strokeWidth) {
        this.strokeColor = strokeColor;
        this.fillOpacity = fillOpacity;
        this.strokeWidth = strokeWidth;
    }

    public AnnotationStyle(String fillColor, double fillOpacity, String strokeColor,
                           String strokeWidth, String strokePattern) {
        this.fillColor = fillColor;
        this.fillOpacity = fillOpacity;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.strokePattern = strokePattern;
    }

    public String getFillColor() {

        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public double getFillOpacity() {
        return fillOpacity;
    }

    public void setFillOpacity(double fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public String getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(String strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getStrokePattern() {
        return strokePattern;
    }

    public void setStrokePattern(String strokePattern) {
        this.strokePattern = strokePattern;
    }
    // private String graphic;
    // private String graphicFormat;

    public String getSpatialGraphicCode() {
        return spatialGraphicCode;
    }

    public void setSpatialGraphicCode(String spatialGraphicCode) {
        this.spatialGraphicCode = spatialGraphicCode;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public String getExternalGraphic() {
        return externalGraphic;
    }

    public void setExternalGraphic(String externalGraphic) {
        this.externalGraphic = externalGraphic;
    }

}
