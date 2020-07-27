
package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MapMarkerParametersTest
{
    private BufferedImage markerImage;
    private double coordinateX = 1.0;
    private double coordinateY = 1.0;
    private int offsetX = 1;
    private int offsetY = 1;
    private String projection = "src";

    @Before
    public void setUp() throws IOException
    {
        this.markerImage = ImageIO
                .read(MapMarkerParametersTest.class
                        .getResourceAsStream("image.gif"));
    }

    @Test
    public void testValidParameters2() throws Exception
    {      
        try
        {
            MapMarkerParameters mapMarker = 
                    new MapMarkerParameters(this.markerImage, this.projection,
                    this.coordinateX, this.coordinateY);
        }
        catch(Exception e)
        {
            fail("Invalid mapmarker parameters2");
        }

    }
    
    @Test
    public void getAnchorPointShouldReturnDefaultIfNotSetBefore()
    {
        MapMarkerParameters params = new MapMarkerParameters();
        BufferedImage img = new BufferedImage(10, 20, 
                BufferedImage.TYPE_3BYTE_BGR);
        params.setMarker(img);
        Point p = params.getAnchorPoint();
        assertEquals(new Point(5, 19), p);
    }

}
