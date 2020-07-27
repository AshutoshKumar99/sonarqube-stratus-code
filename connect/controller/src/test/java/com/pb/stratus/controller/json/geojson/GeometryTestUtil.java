package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.*;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class GeometryTestUtil extends Assert
{
    public static Pos createPos(double x, double y)
    {
        Pos pos = new Pos();
        pos.setX(x);
        pos.setY(y);
        return pos;
    }

    public static Point createPoint(String srsName, double x, double y)
    {
        Point point = new Point();
        point.setPos(createPos(x, y));
        point.setSrsName(srsName);
        return point;
    }

    public static MultiPoint createMultiPoint(String srsName, Point... points)
    {
        MultiPoint multiPoint = new MultiPoint();
        multiPoint.setSrsName(srsName);
        for (Point point : points)
        {
            multiPoint.getPoint().add(point);
        }

        return multiPoint;
    }

    public static LineString createLineString(String srsName, Pos... posList)
    {
        LineString lineString = new LineString();
        lineString.setSrsName(srsName);
        for (Pos pos : posList)
        {
            lineString.getPos().add(pos);
        }
        return lineString;
    }

    public static Ring createRing(String srsName, double... cords)
    {
        Ring r = new Ring();
        r.setSrsName(srsName);
        r.getLineString().add(createLineString(srsName, createXYPosArray(cords)));
        return r;
    }

    public static Curve createCurve(String srsName, double...cords)
    {
        Curve c = new Curve();
        c.getLineString().add(createLineString(srsName, createXYPosArray(cords)));
        return c;
    }

    public static MultiPolygon createMultiPolygon(String srsName,
            Polygon... polygons)
    {
        MultiPolygon multiPolygon = new MultiPolygon();
        multiPolygon.setSrsName(srsName);
        for (Polygon polygon : polygons)
        {
            multiPolygon.getPolygon().add(polygon);
        }
        return multiPolygon;
    }

    public static InteriorList createInteriorList(Ring... rings)
    {
        InteriorList interiorList = new InteriorList();
        for (Ring ring : rings)
        {
            interiorList.getRing().add(ring);
        }
        return interiorList;
    }
    
    public static Pos[] createXYPosArray(double...cords)
    {
        List<Pos> posArr = new ArrayList<Pos>();
        for(int index = 0; index < cords.length; index+=2)
        {
            posArr.add(createPos(cords[index], cords[index + 1]));
        }
        return posArr.toArray(new Pos[0]);
    }

    public static Polygon createPolygon(String srsName, Ring exterior,
            InteriorList interiorList)
    {
        Polygon polygon = new Polygon();
        polygon.setSrsName(srsName);
        polygon.setInteriorList(interiorList);
        polygon.setExterior(exterior);
        return polygon;
    }

    public static void assertGeometry(Geometry expected, Geometry actual)
    {
        if (!(actual.getClass() == expected.getClass()))
        {
            fail();
        }

        if (expected instanceof Point)
        {
            assertPoint((Point) expected, (Point) actual);
        }
        else if (expected instanceof MultiPoint)
        {
            assertMultiPoint((MultiPoint) expected, (MultiPoint) actual);
        }
        else if (expected instanceof LineString)
        {
            assertLineString((LineString) expected, (LineString) actual);
        }
        else if (expected instanceof Polygon)
        {
            assertPolygon((Polygon) expected, (Polygon) actual);
        }
        else if (expected instanceof MultiPolygon)
        {
            assertMultiPolygon((MultiPolygon) expected, (MultiPolygon) actual);
        }
        else
        {
            fail("Unable to assert");
        }
    }

    public static void assertMultiPolygon(MultiPolygon actual,
            MultiPolygon expected)
    {
        assertEquals(expected.getSrsName(), actual.getSrsName());

        for (int index = 0; index < actual.getPolygon().size(); index++)
        {
            assertPolygon(expected.getPolygon().get(index), actual.getPolygon()
                    .get(index));
        }
    }

    public static void assertPolygon(Polygon expected, Polygon actual)
    {
        assertEquals(expected.getSrsName(), actual.getSrsName());
        assertRing(expected.getExterior(), actual.getExterior());
    }

    public static void assertRing(Ring expected, Ring actual)
    {
        assertEquals(actual.getSrsName(), expected.getSrsName());

        assertEquals(expected.getLineString().size(), actual.getLineString()
                .size());
        for (int index = 0; index < actual.getLineString().size(); index++)
        {
            assertLineString(expected.getLineString().get(index), actual
                    .getLineString().get(index));
        }
    }

    public static void assertMultiPoint(MultiPoint expected, MultiPoint actual)
    {
        assertEquals(expected.getSrsName(), actual.getSrsName());
        for (int index = 0; index < expected.getPoint().size(); index++)
        {
            assertPoint(expected.getPoint().get(index), actual.getPoint().get(
                    index));
        }
    }

    public static void assertLineString(LineString expected, LineString actual)
    {
        assertEquals(expected.getSrsName(), actual.getSrsName());

        assertEquals(expected.getPos().size(), actual.getPos().size());
        for (int index = 0; index < actual.getPos().size(); index++)
        {
            assertPos(expected.getPos().get(index), actual.getPos().get(index));
        }
    }

    public static void assertPoint(Point expected, Point actual)
    {
        assertEquals(expected.getSrsName(), actual.getSrsName());
        assertPos(expected.getPos(), actual.getPos());
    }

    public static void assertPos(Pos expected, Pos actual)
    {
        assertEquals(expected.getX(), actual.getX());
        assertEquals(expected.getY(), actual.getY());
        assertEquals(expected.getZ(), actual.getZ());
        assertEquals(expected.getMValue(), actual.getMValue());
    }
}
