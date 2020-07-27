package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;

public class BoundingBoxTest
{

    private BoundingBox instance;

    @Before
    public void setUp()
    {
        instance = new BoundingBox(15.0d, 5.0d, 7.0d, 27.0d, "epsg:0000");
    }

    @Test
    public void testGetNorth()
    {
        assertEquals(15.0d, instance.getNorth(), 0.0);
    }

    @Test
    public void testGetSouth()
    {
        assertEquals(5.0d, instance.getSouth(), 0.0);
    }

    @Test
    public void testGetWest()
    {
        assertEquals(7.0d, instance.getWest(), 0.0);
    }

    @Test
    public void testGetEast()
    {
        assertEquals(27.0, instance.getEast(), 0.0);
    }

    @Test
    public void testGetSrs()
    {
        assertEquals("epsg:0000", instance.getSrs());
    }

    @Test
    public void testGetWidth()
    {
        assertEquals(20.0, instance.getWidth(), 0.0);
    }

    @Test
    public void testGetHeight()
    {
        assertEquals(10.0, instance.getHeight(), 0.0);
    }

    @Test
    public void testGetCenter()
    {
        assertEquals(new Point2D.Double(17, 10), instance.getCenter());
    }

}