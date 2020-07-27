package com.pb.stratus.controller.marker;

import com.pb.stratus.controller.print.Marker;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MarkerComparatorTest
{
    
    @Test
    public void shouldSortMarkersFurtherNorthToFrontRegardlessOfWestEastLocation()
    {
        Marker m1 = new Marker(null, new Point(0, 0), new Point(0, 0));
        Marker m2 = new Marker(null, new Point(0, 0), new Point(0, 2));
        Marker m3 = new Marker(null, new Point(0, 0), new Point(-1, 1));
        List<Marker> markers = Arrays.asList(m1, m2, m3);
        Collections.sort(markers, new MarkerComparator());
        assertEquals(Arrays.asList(m2, m3, m1), markers);
    }
    
    @Test
    public void shouldSortMarkersFurtherEastToFront()
    {
        Marker m1 = new Marker(null, new Point(0, 0), new Point(0, 0));
        Marker m2 = new Marker(null, new Point(0, 0), new Point(1, 0));
        List<Marker> markers = Arrays.asList(m1, m2);
        Collections.sort(markers, new MarkerComparator());
        assertEquals(Arrays.asList(m2, m1), markers);
    }

}
