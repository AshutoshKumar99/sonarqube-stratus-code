package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.MultiPolygon;
import com.mapinfo.midev.service.geometries.v1.Polygon;
import com.mapinfo.midev.service.geometries.v1.Ring;
import com.pb.stratus.controller.json.TypedConverter;
import org.junit.Before;
import org.junit.Test;

import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createRing;
import static junit.framework.Assert.assertEquals;

public class MultiPolygonStrategyTest
{

    private MultiPolygonStrategy strat;

    @Before
    public void setUp()
    {
        strat = new MultiPolygonStrategy();
        TypedConverter conv = new TypedConverter();
        strat.setOwner(conv);
    }

    /**
     * Tests whether a polygon without internal rings can be transformed
     * successfully
     */
    @Test
    public void testExteriorPolygon()
    {
        MultiPolygon mp = new MultiPolygon();
        Polygon p = new Polygon();
        mp.getPolygon().add(p);

        Ring r = createRing("epsg:27700", 0, 0, 1, 0, 1, 1, 0, 1, 0, 0);
        p.setExterior(r);
        p = new Polygon();
        mp.getPolygon().add(p);
        r = createRing("epsg:27700", 5, 5, 6, 5, 6, 6, 5, 6, 5, 5);
        p.setExterior(r);

        StringBuilder b = new StringBuilder();
        strat.processValue(mp, b);
        String result = b.toString().replaceAll("\\s+", "");
        // System.out.println(result);
        String expected = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[0.0,0.0],[1.0,0.0],[1.0,1.0],[0.0,1.0],[0.0,0.0]]],[[[5.0,5.0],[6.0,5.0],[6.0,6.0],[5.0,6.0],[5.0,5.0]]]]}";
        assertEquals(expected, result);
    }
    /*
     * XXX: There should be some more tests to test polygon with interior ring etc.
     */
}
