/**
 * FeatureGeometryConverter
 * Converts a Geometry to a FeatureGeometry
 * User: GU003DU
 * Date: 6/26/14
 * Time: 1:04 PM
 */

package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.geometries.v1.*;

public class FeatureGeometryConverter {

    private Geometry geometry;
    private String srsName;

    public FeatureGeometryConverter(Geometry geometry) {
        this.geometry = geometry;
    }

    public FeatureGeometryConverter(Geometry geometry, String srsName) {
        this(geometry);
        this.srsName = srsName;
    }

    /**
     * Converts a given geometry to FeatureGeometry.
     *
     * @return
     */
    public FeatureGeometry convert() {

        FeatureGeometry featureGeometry = null;
        if (geometry != null) {
            if (geometry instanceof Polygon) {
                MultiPolygon multiPolygon = new MultiPolygon();
                multiPolygon.getPolygon().add((Polygon) geometry);
                featureGeometry = multiPolygon;
            } else if (geometry instanceof LineString) {
                Curve curve = new Curve();
                curve.setSrsName(srsName);
                curve.getLineString().add((LineString) geometry);
                MultiCurve multiCurve = new MultiCurve();
                multiCurve.getCurve().add(curve);
                featureGeometry = multiCurve;
            }
        }

        return featureGeometry;
    }
}
