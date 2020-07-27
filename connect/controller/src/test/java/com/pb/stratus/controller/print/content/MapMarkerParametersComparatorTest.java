package com.pb.stratus.controller.print.content;

import com.pb.stratus.controller.print.MapMarkerParameters;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MapMarkerParametersComparatorTest
{
    
    private MapMarkerParametersComparator comp;
    
    @Before
    public void setUp()
    {
        comp = new MapMarkerParametersComparator();
    }

    @Test
    public void markersFurtherSouthShouldBeGreaterThanMarkersFurtherNorth()
    {
        assertTrue(comp.compare(createParams(1, 1), createParams(1, 2)) > 0);
    }
    
    @Test
    public void markersFurtherWestShouldBeGreaterThanMarkersFurtherEast()
    {
        assertTrue(comp.compare(createParams(1, 1), createParams(2, 1)) > 0);
    }
    
    @Test
    public void northSouthShouldTakePrecedenceOverWestEast()
    {
        assertTrue(comp.compare(createParams(2, 1), createParams(1, 2)) > 0);
    }
    
    @Test
    public void equalMarkersShouldBeTreatedEqually()
    {
        assertTrue(comp.compare(createParams(2, 1), createParams(2, 1)) == 0);
    }
    
    private MapMarkerParameters createParams(int x, int y)
    {
        return new MapMarkerParameters(x, y, "testSrs");
    }

}
