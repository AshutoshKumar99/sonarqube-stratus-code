package com.pb.stratus.controller.geometry;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MeasurementEnumTest
{
    @Test
    public void testValueMethod()
    {
        assertEquals("area", MeasurementEnum.AREA.value());
    }

    @Test
    public void getMeasurementShouldReturnCorrectValue()
    {
        assertEquals(MeasurementEnum.LENGTH, MeasurementEnum.getMeasurement("length"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForUnsupportedMeasurement()
    {
        MeasurementEnum.getMeasurement("unsupported");
    }
}
