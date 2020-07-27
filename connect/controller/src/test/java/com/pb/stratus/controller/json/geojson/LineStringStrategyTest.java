package com.pb.stratus.controller.json.geojson;


import com.mapinfo.midev.service.geometries.v1.LineString;
import com.pb.stratus.controller.json.TypedConverter;
import org.junit.Before;
import org.junit.Test;

import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createLineString;
import static com.pb.stratus.controller.json.geojson.GeometryTestUtil.createXYPosArray;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LineStringStrategyTest
{
    private LineStringStrategy lineStringStrategy;

    @Before
    public void setUp()
    {
        lineStringStrategy = new LineStringStrategy();
        TypedConverter conv = new TypedConverter();
        lineStringStrategy.setOwner(conv);
    }

    @Test (expected = NullPointerException.class)
    public void shouldThrowNPE()
    {
        StringBuilder sb = new StringBuilder();
        lineStringStrategy.processCoordinates(null, sb);

    }

    @Test
    public void shouldAcceptOnlyLineString()
    {
        StringBuilder sb = new StringBuilder();
        LineString lineString = new LineString();
        // should not throw exception
        try{
        lineStringStrategy.processCoordinates(lineString, sb);
        }
        catch(IllegalArgumentException e)
        {
            fail("illegal argument should not be thrown");
        }

        // should throw exception
        try{
            lineStringStrategy.processCoordinates(new String(), sb);
            // control should not come here.
            fail("illegal argument should not be thrown");
        }
        catch(IllegalArgumentException e)
        {
           // pass.
        }
    }

    @Test
    public void testLineString()
    {
        StringBuilder b = new StringBuilder();
        LineString l = createLineString("epsg:27700", createXYPosArray(123, 456,
                150, 456, 150, 600, 123, 600));
        lineStringStrategy.processValue(l, b);
        String result = b.toString().replaceAll("\\s+", "");
        String expectedResult = "{\"type\":\"LineString\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"epsg:27700\"}},\"coordinates\":[[123.0,456.0],[150.0,456.0],[150.0,600.0],[123.0,600.0]]}";
        assertEquals(expectedResult, result);
    }
}
