/**
 * FeatureGeometryConverterTest
 * User: GU003DU
 * Date: 7/1/14
 * Time: 2:28 PM
 */
package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.geometries.v1.*;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FeatureGeometryConverterTest {

    private FeatureGeometryConverter converter;
    private static final String srs = "epsg:27700";

    @Test
    public void testLineStringConversion() throws Exception {
        LineString lineString = new LineString();
        converter = new FeatureGeometryConverter(lineString, srs);
        Geometry geometry = converter.convert();

        // verify
        assertNotNull(geometry);
        assertTrue(geometry instanceof FeatureGeometry);
        assertTrue(geometry instanceof MultiCurve);
    }

    @Test
    public void testPolygonConversion() throws Exception {
        Polygon polygon = new Polygon();
        converter = new FeatureGeometryConverter(polygon);
        Geometry geometry = converter.convert();

        // verify
        assertNotNull(geometry);
        assertTrue(geometry instanceof FeatureGeometry);
        assertTrue(geometry instanceof MultiPolygon);
    }
}
