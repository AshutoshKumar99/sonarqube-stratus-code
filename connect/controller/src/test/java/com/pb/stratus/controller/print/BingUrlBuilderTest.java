package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class BingUrlBuilderTest
{
    URL expectedURLForRoadLayer;

    URL expectedURLForAerialLayer;

    URL expectedURLForHybridLayer;

    Point2D center;

    Dimension imageSize;

    BingUrlBuilder bingUrlBuilder;

    @Before
    public void setUp() throws Exception
    {
        center = createPoint(0.12345678, 1.23456789);
        imageSize = new Dimension(200, 200);
        bingUrlBuilder = new BingUrlBuilder();

        expectedURLForRoadLayer = new URL("https://dev.virtualearth.net/REST/v1/Imagery/"
            + "Map/Road/1.234568,0.123457/17?mapSize=200,200&key=SomeKey");

        expectedURLForAerialLayer = new URL("https://dev.virtualearth.net/REST/v1"
            + "/Imagery/Map/Aerial/1.234568,0.123457/17?mapSize=200,200&key=SomeKey");

        expectedURLForHybridLayer = new URL("https://dev.virtualearth.net/REST/v1/"
            + "Imagery/Map/AerialWithLabels/1.234568,0.123457/17"
            + "?mapSize=200,200&key=SomeKey");
    }

    @Test
    public void testFormatBingURLForShadedMap()
    {
        URL url = bingUrlBuilder.formatBingURL(
                "Bing Roads", center, imageSize, 17, "SomeKey");
        assertEquals(expectedURLForRoadLayer, url);
    }

    @Test
    public void testFormatBingURLForRoadLayer()
    {
        URL url = bingUrlBuilder.formatBingURL(
                "Road", center, imageSize, 17, "SomeKey");
        assertEquals(expectedURLForRoadLayer, url);
    }

    @Test
    public void testFormatBingURLForAerialMap()
    {        
        URL url = bingUrlBuilder.formatBingURL(
                "Bing Aerial", center, imageSize, 17, "SomeKey");
        assertEquals(expectedURLForAerialLayer, url);
    }

    @Test
    public void testFormatBingURLForAerialLayer()
    {
        URL url = bingUrlBuilder.formatBingURL(
                "Aerial", center, imageSize, 17, "SomeKey");
        assertEquals(expectedURLForAerialLayer, url);
    }

    @Test
    public void testFormatBingURLForHybridMap()
    {
        URL url = bingUrlBuilder.formatBingURL(
                "Bing Hybrid", center, imageSize, 17, "SomeKey");
        assertEquals(expectedURLForHybridLayer, url);
    }

    @Test
    public void testFormatBingURLForAerialWithLabelsLayer()
    {
        URL url = bingUrlBuilder.formatBingURL(
                "AerialWithLabels", center, imageSize, 17, "SomeKey");
        assertEquals(expectedURLForHybridLayer, url);
    }

    private Point2D createPoint(double x, double y)
    {
        return new Point2D.Double(x,y);
    }
}
