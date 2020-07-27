package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BoundingBoxCalculatorTest
{
    private BoundingBoxCalculator boundingBoxCalculator;
    
    private BoundingBox initialBounds;
    
    private Dimension imageSize;

    @Before
    public void setUp() throws Exception
    {
        initialBounds = new BoundingBox(1234, -6543, -7654, 4738, "espg:2770");
        imageSize = new Dimension(1000, 500);
        boundingBoxCalculator = new BoundingBoxCalculator();
    }

    @Test
    public void shouldPreserveInitialBoundingBoxCenterPoint()
    {
        BoundingBox calculatedBounds = boundingBoxCalculator.calculate(
                initialBounds, imageSize); 
        assertEquals(initialBounds.getCenter(), calculatedBounds.getCenter());
    }
    
    @Test
    public void shouldContainEntireInitialBoundingBox()
    {
        BoundingBox calculatedBounds = boundingBoxCalculator.calculate(
                initialBounds, imageSize); 
        assertTrue("Initial bounding box not contained in calculated " 
                + "bounding box", calculatedBounds.contains(initialBounds));
    }
    
    @Test
    public void shouldBeSameSrs()
    {
        BoundingBox calculatedBounds = boundingBoxCalculator.calculate(
                initialBounds, imageSize);
        assertEquals(initialBounds.getSrs(), calculatedBounds.getSrs());
    }
    
   
    @Test
    public void shouldReturnVerticallyFittedBoundingBox()
    {
        BoundingBox calculatedBounds = boundingBoxCalculator.calculate(
                initialBounds, imageSize);
        assertEquals(initialBounds.getHeight(), 
                calculatedBounds.getHeight(), 0d);
        assertTrue(initialBounds.getWidth() < calculatedBounds.getWidth());
    }
    
    @Test
    public void shouldReturnHorizontallyFittedBoundingBox()
    {
        initialBounds = new BoundingBox(1, 0, 0, 1000, "someSrs");
        BoundingBox calculatedBounds = boundingBoxCalculator.calculate(
                initialBounds, imageSize);
        assertEquals(initialBounds.getWidth(), 
                calculatedBounds.getWidth(), 0d);
        assertTrue(initialBounds.getHeight() < calculatedBounds.getHeight());
    }
    
    @Test
    public void shouldReturnBoundingBoxWithSameAspectRatioAsImage()
    {
        BoundingBox calculatedBounds = boundingBoxCalculator.calculate(
                new BoundingBox(1, 0, 0, 1000, "someSrs"), imageSize);
        assertEquals((double) imageSize.width / imageSize.height, 
                calculatedBounds.getWidth() / calculatedBounds.getHeight(), 
                0d);
    }
    
}
