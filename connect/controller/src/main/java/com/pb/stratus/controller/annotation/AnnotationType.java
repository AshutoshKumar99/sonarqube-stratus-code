package com.pb.stratus.controller.annotation;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 9/27/12
 * Time: 6:25 PM
 * To change this template use File | Settings | File Templates.
 */
public enum AnnotationType {
    CIRCLE("Circle"),
    ELLIPSE("Ellipse"),
    RECTANGLE("Rectangle"),
    SQUARE("Square"),
    TEXT("Text"),
    POINT("Point"),
    LINE("Line"),
    POLYGON("Polygon"),
    MULTIPOLYGON("MultiPolygon"),
    MULTILINESTRING("MultiLineString"),
    FEATURE("Feature"),
    RING("Ring");

    private String type;

    private AnnotationType(String type){
        this.type = type;

    }

}
