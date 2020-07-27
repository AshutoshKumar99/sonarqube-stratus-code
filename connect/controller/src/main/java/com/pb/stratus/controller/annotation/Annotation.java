package com.pb.stratus.controller.annotation;

import com.mapinfo.midev.service.geometries.v1.Geometry;

/**
 * Typical Annotation representation as exist in the system.
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 19/6/12
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Annotation {
    private Geometry geometry;
    private AnnotationStyle style;
    private String name;
    private AnnotationType type;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public AnnotationStyle getStyle() {
        return style;
    }

    public void setStyle(AnnotationStyle style) {
        this.style = style;
    }

    public Annotation(Geometry geometry, AnnotationStyle style)
    {
        this.geometry = geometry;
        this.style = style;
    }

    public Annotation()
    {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnnotationType getType() {
        return type;
    }

    public void setType(AnnotationType type) {
        this.type = type;
    }
}
