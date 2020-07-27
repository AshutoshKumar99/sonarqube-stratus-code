package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.InteriorList;
import com.mapinfo.midev.service.geometries.v1.Polygon;
import com.mapinfo.midev.service.geometries.v1.Ring;
import com.pb.stratus.controller.json.TypedConverter;
import org.junit.Before;
import org.junit.Test;

import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createRing;
import static junit.framework.Assert.assertEquals;

public class PolygonStrategyTest
{

    private PolygonStrategy strat;
    private String srsName = "epsg:27700";

    @Before
    public void setUp()
    {
        strat = new PolygonStrategy();
        TypedConverter conv = new TypedConverter();
        strat.setOwner(conv);
    }

    /**
     * Tests whether a polygon without internal rings can be transformed successfully
     */
    @Test
    public void testExteriorPolygon()
    {
        Polygon p = new Polygon();
        Ring r = createRing("epsg:27700", 0, 0, 1, 0, 1, 1, 0, 1, 0, 0);
        p.setExterior(r);
        StringBuilder b = new StringBuilder();
        strat.processValue(p, b);
        String result = b.toString().replaceAll("\\s+", "");
        String expected = "{\"type\":\"Polygon\",\"coordinates\":"
                + "[[[0.0,0.0],[1.0,0.0],[1.0,1.0],[0.0,1.0],[0.0,0.0]]]}";
        assertEquals(expected, result);
    }

    /**
     * Tests whether a polygon with multiple inner rings can be transformed successfully
     */
    @Test
    public void testMultipleInteriorsPolygon()
    {
        Polygon p = new Polygon();
        Ring r = createRing(this.srsName, 0, 0, 5, 0, 5, 5, 0, 5, 0, 0);
        p.setExterior(r);
        p.setInteriorList(new InteriorList());
        r = createRing(this.srsName, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1);
        p.getInteriorList().getRing().add(r);
        r = createRing(this.srsName, 1, 3, 1, 3, 2, 4, 2, 3, 1, 3);
        p.getInteriorList().getRing().add(r);
        r = createRing(this.srsName, 3, 1, 3, 2, 4, 2, 4, 1, 3, 1);
        p.getInteriorList().getRing().add(r);

        StringBuilder b = new StringBuilder();
        strat.processValue(p, b);
        String result = b.toString().replaceAll("\\s+", "");
        String expected = "{\"type\":\"Polygon\",\"coordinates\":"
                + "[[[0.0,0.0],[5.0,0.0],[5.0,5.0],[0.0,5.0],[0.0,0.0]],"
                + "[[1.0,1.0],[1.0,2.0],[2.0,2.0],[2.0,1.0],[1.0,1.0]],"
                + "[[1.0,3.0],[1.0,3.0],[2.0,4.0],[2.0,3.0],[1.0,3.0]],"
                + "[[3.0,1.0],[3.0,2.0],[4.0,2.0],[4.0,1.0],[3.0,1.0]]]}";
        assertEquals(expected, result);
    }

}
