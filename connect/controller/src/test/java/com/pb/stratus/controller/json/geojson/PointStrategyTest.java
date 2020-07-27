package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.pb.stratus.controller.json.TypedConverter;
import org.junit.Before;
import org.junit.Test;

import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createPos;
import static junit.framework.Assert.assertEquals;

public class PointStrategyTest
{

    private PointStrategy strat;

    @Before
    public void setUp()
    {
        strat = new PointStrategy();
        TypedConverter conv = new TypedConverter();
        strat.setOwner(conv);
    }

    @Test
    public void testPoint()
    {
        StringBuilder b = new StringBuilder();
        Point p = new Point();
        p.setPos(createPos(123, 456));
        strat.processValue(p, b);
        String result = b.toString().replaceAll("\\s+", "");
        String expected = "{\"type\":\"Point\",\"coordinates\":[123.0,456.0]}";
        assertEquals(expected, result);
    }
}
