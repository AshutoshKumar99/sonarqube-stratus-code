package com.pb.stratus.controller.print.render;

import com.pb.stratus.controller.annotation.Annotation;

import java.util.List;

/**
 * Created by ar009sh on 22-05-2015.
 * This class encapsulates the different types of solid fills,empty polygons and line polygons .
 */
public class AnnotationsRendererHelperBean {

     List<Annotation> solidFillPolygonAnnotations;
     List<Annotation> nonSolidFillPolygonAnnotations;
     List<Annotation> lineAnnotations;

    public List<Annotation> getSolidFillPolygonAnnotations() {
        return solidFillPolygonAnnotations;
    }

    public void setSolidFillPolygonAnnotations(List<Annotation> solidFillPolygonAnnotations) {
        this.solidFillPolygonAnnotations = solidFillPolygonAnnotations;
    }

    public List<Annotation> getNonSolidFillPolygonAnnotations() {
        return nonSolidFillPolygonAnnotations;
    }

    public void setNonSolidFillPolygonAnnotations(List<Annotation> nonSolidFillPolygonAnnotations) {
        this.nonSolidFillPolygonAnnotations = nonSolidFillPolygonAnnotations;
    }

    public List<Annotation> getLineAnnotations() {
        return lineAnnotations;
    }

    public void setLineAnnotations(List<Annotation> lineAnnotations) {
        this.lineAnnotations = lineAnnotations;
    }
}
