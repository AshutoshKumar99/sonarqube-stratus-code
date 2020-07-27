package com.pb.stratus.controller.print;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Point2D;

import static org.junit.Assert.assertEquals;

public class BingLevelOneImageComposerTest
{
    private BingLevelOneImageComposer composer;
    
    @Before
    public void setUp() throws Exception
    {
        composer = new BingLevelOneImageComposer();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void shouldReturn512x512WhenDesiredSizeIsLargerThan512()
    {
        Dimension desiredSize = new Dimension(1000, 1000);
        Dimension expected = new Dimension(512, 512);
        Dimension actual = composer.getImageSizeForBingRequest(desiredSize);
        assertEquals(expected, actual);
    }
    
    @Test
    public void shouldReturnSizeFittedToHeight()
    {
        Dimension desiredSize = new Dimension (1000, 500);
        Dimension expected = new Dimension(512, 500);
        Dimension actual = composer.getImageSizeForBingRequest(desiredSize);
        assertEquals(expected, actual);
    }
    
    @Test
    public void shouldReturnSizeFittedToWidth()
    {
        Dimension desiredSize = new Dimension (500, 1000);
        Dimension expected = new Dimension(500, 512);
        Dimension actual = composer.getImageSizeForBingRequest(desiredSize);
        assertEquals(expected, actual);
    }
    
    @Test
    public void shouldReturnExactSizeForSmallerCanvas()
    {
        Dimension desiredSize = new Dimension (500, 500);
        Dimension expected = new Dimension(500, 500);
        Dimension actual = composer.getImageSizeForBingRequest(desiredSize);
        assertEquals(expected, actual);
    }
 
    @Test
    public void shouldReturnCorrectOffsetWhenBorderOnAllSides()
    {
        Dimension desiredSize = new Dimension(1000, 1000);
        Dimension worldSize = new Dimension(512, 512);
        Point2D expected = new Point2D.Double(244, 244);
        Point2D actual = composer.getOffsetInImage(desiredSize, worldSize);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnCorrectOffsetWhenBorderOnSides()
    {
        Dimension desiredSize = new Dimension(1000, 400);
        Dimension levelOneImageSize = new Dimension(512, 400);
        Point2D expected = new Point2D.Double(244, 0);
        Point2D actual = composer.getOffsetInImage(desiredSize, levelOneImageSize);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnCorrectOffsetWhenBorderOnTopAndBottom()
    {
        Dimension desiredSize = new Dimension(400, 1000);
        Dimension levelOneImageSize = new Dimension(400, 512);
        Point2D expected = new Point2D.Double(0, 244);
        Point2D actual = composer.getOffsetInImage(desiredSize, levelOneImageSize);
        assertEquals(expected, actual);
    }

}
