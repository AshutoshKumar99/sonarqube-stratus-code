package com.pb.stratus.controller.annotation;

/**
 * This class encapsulates point annotation specific properties
 * Created by ar009sh on 25-05-2015.
 */
public class PointAnnotationStyle extends AnnotationStyle {

    private String graphicName;
    private String pointRadius;

    public PointAnnotationStyle(String graphicName, String color, String pointRadius) {
        super(color);
        this.graphicName = graphicName;
        this.pointRadius = pointRadius;
    }

    public String getPointRadius() {
        return pointRadius;
    }

    public String getGraphicName() {
        return graphicName;
    }

    /**
     * we will always hasve a halo effect in the point annotation
     * @return
     */
    public String getBorder() {
       return "halo";
    }

    public String getSpatialGraphicPointCode() {
        String graphicName = this.getGraphicName();

        switch (graphicName) {
            case "circle":
                return "35";

            case "square":
                return "33";

            case "triangle":
                return "37";

            case "cross":
                return "50";

            case "x":
                return "51";

            default:
                return "36";

        }

    }


}
