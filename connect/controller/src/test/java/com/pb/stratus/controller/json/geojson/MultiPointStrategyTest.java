package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.MultiPoint;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.pb.stratus.controller.json.TypedConverter;
import org.junit.Before;
import org.junit.Test;

import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createPos;
import static junit.framework.Assert.assertEquals;

public class MultiPointStrategyTest
{

    private MultiPointStrategy strat;

    @Before
    public void setUp()
    {
        strat = new MultiPointStrategy();
        TypedConverter conv = new TypedConverter();
        strat.setOwner(conv);
    }

    @Test
    public void testPoint()
    {
        MultiPoint mp = new MultiPoint();
        Point p = new Point();
        mp.getPoint().add(p);
        p.setPos(createPos(123, 456));
        p = new Point();
        mp.getPoint().add(p);
        p.setPos(createPos(234, 567));
        p = new Point();
        mp.getPoint().add(p);
        p.setPos(createPos(345, 678));

        StringBuilder b = new StringBuilder();
        strat.processValue(mp, b);
        String result = b.toString().replaceAll("\\s+", "");
        String expected = "{\"type\":\"MultiPoint\",\"coordinates\":[[123.0,456.0],[234.0,567.0],[345.0,678.0]]}";
        assertEquals(expected, result);
    }
}
