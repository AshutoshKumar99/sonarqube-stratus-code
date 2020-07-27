package com.pb.stratus.controller.json;

import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class DistanceStrategyTest
{
    private DistanceStrategy strat;

    @Before
    public void setUp()
    {
        strat = new DistanceStrategy();
    }

    @Test
    public void testConversion()
    {
        StringBuilder b = new StringBuilder();
        Distance distance = new Distance(){
            @Override
            public double getValue()
            {
                return 5.0;
            }
            @Override
            public DistanceUnit getUom()
            {
                return DistanceUnit.KILOMETER;
            }
        };
        strat.processValue(distance, b);
        String expectedResult = "{\"value\":\"5.0\"," +
                "\"unit\":\"Kilometer\"}";

        assertNotNull(b.toString());
        assertNotSame("", b.toString());
        assertEquals(expectedResult, b.toString());


    }
}
