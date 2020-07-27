package com.pb.stratus.controller.geometry;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MeasureTest
{
    @Test
    public void testForEquality()
    {
        Measure measure1 = new Measure("sometype", 1.00, "someunit");
        Measure measure2 = new Measure("sometype", 1.00, "someunit");
        assertEquals(measure1, measure2);
        // not equals
        Measure measure3 = new Measure("sometype", 2.00, "someunit");
        assertFalse(measure1.equals(measure3));
    }

    @Test
    public void testForHashCode()
    {
        Measure measure1 = new Measure("sometype", 1.00, "someunit");
        Measure measure2 = new Measure("sometype", 1.00, "someunit");
        assertEquals(measure1.hashCode(), measure2.hashCode());
        // not equals
        Measure measure3 = new Measure("sometype", 2.00, "someunit");
        assertTrue(measure1.hashCode() != measure3.hashCode());

    }
}
