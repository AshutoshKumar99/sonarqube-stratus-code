package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Curve;
import com.mapinfo.midev.service.geometries.v1.LineString;
import com.mapinfo.midev.service.geometries.v1.MultiCurve;
import com.pb.stratus.controller.json.TypedConverter;
import org.junit.Before;
import org.junit.Test;

import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createPos;
import static junit.framework.Assert.assertEquals;

/**
 * @author pr003sa
 * 
 */
public class MultiCurveStrategyTest
{

    private MultiCurveStrategy strat;

    @Before
    public void setUp()
    {
        strat = new MultiCurveStrategy();
        TypedConverter conv = new TypedConverter();
        strat.setOwner(conv);
    }

    @Test
    public void testMuliCurve()
    {
        MultiCurve mc = new MultiCurve();
        Curve c = new Curve();
        LineString ls = new LineString();
        mc.getCurve().add(c);
        c.getLineString().add(ls);
        ls.getPos().add(createPos(123, 456));
        ls.getPos().add(createPos(150, 455));
        c = new Curve();
        ls = new LineString();
        mc.getCurve().add(c);
        c.getLineString().add(ls);
        ls.getPos().add(createPos(132, 478));
        ls.getPos().add(createPos(170, 572));

        StringBuilder b = new StringBuilder();
        strat.processValue(mc, b);
        String result = b.toString().replaceAll("\\s+", "");
        String expected = "{\"type\":\"MultiLineString\",\"coordinates\":[[[123.0,456.0],[150.0,455.0]],[[132.0,478.0],[170.0,572.0]]]}";
        assertEquals(expected, result);
    }

}
