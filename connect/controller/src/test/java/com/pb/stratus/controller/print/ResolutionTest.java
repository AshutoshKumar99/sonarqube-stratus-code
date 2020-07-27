package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class ResolutionTest
{
    
    private Resolution resolution;

    @Before
    public void setUp()
    {
        resolution = new Resolution(123);
    }
    
    @Test
    public void shouldConvertCmToPixels()
    {
        Dimension actual = resolution.calculatePixelDimensions("3cm", "4cm");
        assertEquals(new Dimension(145, 194), actual);
    }
    
    @Test
    public void shouldFailOnUnknownDistanceUnit()
    {
        try
        {
            resolution.calculatePixelDimensions("3pt", "3cm");
            fail("No IllegalArgumentException thrown");
        }
        catch(IllegalArgumentException iax)
        {
            assertTrue(iax.getMessage().contains("3pt"));
        }
    }
 
    @Test
    public void shouldConvertPixelsToCm()
    {
        // 100 px are 1 cm wide at a resolution of 254 dpi
        resolution = new Resolution(254);
        assertEquals(1, resolution.calculatePixelWidthInCm(100), 0d);
    }

}
