package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Curve;
import com.pb.stratus.controller.json.TypedConverter;
import org.junit.Before;
import org.junit.Test;

import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createCurve;
import static junit.framework.Assert.assertEquals;

public class CurveStrategyTest
{

    private CurveStrategy strat;

    @Before
    public void setUp()
    {
        strat = new CurveStrategy();
        TypedConverter conv = new TypedConverter();
        strat.setOwner(conv);
    }

    @Test
    public void testCurve()
    {
        StringBuilder b = new StringBuilder();
        Curve c = createCurve("epsg:27700", 123, 456, 150, 456, 150, 600, 123,
                600);
        strat.processValue(c, b);
        String result = b.toString().replaceAll("\\s+", "");
        String expected = "{\"type\":\"LineString\",\"coordinates\":[[123.0,456.0],[150.0,456.0],[150.0,600.0],[123.0,600.0]]}";
        assertEquals(expected, result);
    }

}
