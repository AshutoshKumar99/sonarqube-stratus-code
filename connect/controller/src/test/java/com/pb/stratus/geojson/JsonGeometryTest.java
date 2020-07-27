package com.pb.stratus.geojson;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mapinfo.midev.service.geometries.v1.Pos;

public class JsonGeometryTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void testJsonPointDoubleDouble()
    {
        JsonPoint point = new JsonPoint(10.0, 20.0);
        assertEquals(point.getCoordinates()[0], 10.0, 0);
        assertEquals(point.getCoordinates()[1], 20.0, 0);
        assertEquals(point.getType(), "Point");
    }

    @Test
    public void testJsonPointPos()
    {
        Pos pos = new Pos();
        pos.setX(10.0);
        pos.setY(20.0);
        JsonPoint point = new JsonPoint(pos);
        point.setCrs(new JsonCoordinateReferenceSystem("EPSG:27700"));
        assertEquals(point.getCoordinates()[0], 10.0, 0);
        assertEquals(point.getCoordinates()[1], 20.0, 0);
        assertEquals(point.getCrs().getProperties().get("name"), "EPSG:27700");

    }

    @Test
    public void testJsonPointPosString()
    {
        Pos pos = new Pos();
        pos.setX(10.0);
        pos.setY(20.0);
        JsonPoint point = new JsonPoint(pos, "EPSG:27700");
        assertEquals(point.getCoordinates()[0], 10.0, 0);
        assertEquals(point.getCoordinates()[1], 20.0, 0);
        assertEquals(point.getCrs().getProperties().get("name"), "EPSG:27700");
    }

    @Test
    public void testJsonCRS()
    {
        JsonCoordinateReferenceSystem crs = new JsonCoordinateReferenceSystem(
                "EPSG:27700");
        Pos pos = new Pos();

        JsonPoint point = new JsonPoint(pos);

        Double coords[] = { 10.0, 20.0 };
        point.setCrs(crs);
        point.setCoordinates(coords);
        assertEquals(point.getCoordinates()[0], 10.0, 0);
        assertEquals(point.getCoordinates()[1], 20.0, 0);
        assertEquals(point.getCrs().getProperties().get("name"), "EPSG:27700");
    }

    @Test
    public void testJsonLineString()
    {

        JsonLineString line = new JsonLineString();
        Double coords[][] = { { 1.0, 2.0 }, { 3.0, 4.0 } };
        line.setCoordinates(coords);

        assertEquals(line.getCoordinates()[0][0], 1.0, 0);
        assertEquals(line.getCoordinates()[0][1], 2.0, 0);
        assertEquals(line.getCoordinates()[1][0], 3.0, 0);
        assertEquals(line.getCoordinates()[1][1], 4.0, 0);

    }

    @Test
    public void testJsonPositionDoubleDouble()
    {
        JsonPosition pos = new JsonPosition(1.0, 2.0);
        assertEquals(pos.getX(), 1.0, 0);
        assertEquals(pos.getY(), 2.0, 0);
        pos.setX(3.0);
        pos.setY(4.0);
        assertEquals(pos.getX(), 3.0, 0);
        assertEquals(pos.getY(), 4.0, 0);

    }

    @Test
    public void testJsonPositionPos()
    {
        Pos pos = new Pos();
        pos.setX(10.0);
        pos.setY(20.0);
        JsonPosition jpos = new JsonPosition(pos);
        assertEquals(jpos.getX(), 10.0, 0);
        assertEquals(jpos.getY(), 20.0, 0);

        Pos envPos = jpos.toEnvinsa();
        assertEquals(envPos.getX(), 10.0, 0);
        assertEquals(envPos.getY(), 20.0, 0);

    }
}
