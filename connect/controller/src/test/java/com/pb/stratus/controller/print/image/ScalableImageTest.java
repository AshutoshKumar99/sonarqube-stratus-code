package com.pb.stratus.controller.print.image;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ScalableImageTest
{
    
    private BufferedImage expectedImage;
    
    private ScalableImage image;

    @Before
    public void setUp() throws Exception
    {
        expectedImage = loadPortraitImage();
        image = new ScalableImage(expectedImage);
    }
    
    @Test
    public void shouldNotScaleImageIfNoMaxSideLengthSpecified() 
            throws Exception
    {
        assertEquals(expectedImage, image.getScaledImage());
    }
    
    @Test
    public void shouldReturnSameImageIfSmallerThanMaxSideLength() 
            throws Exception
    {
        image.setMaxSideLength(expectedImage.getHeight());
        assertEquals(expectedImage, image.getScaledImage());
    }
    
    @Test
    public void shouldScalePortraitImageCorrectly()
    {
        int scaledHeight = 100;
        image.setMaxSideLength(scaledHeight);
        BufferedImage scaledImage = image.getScaledImage();
        assertEquals(scaledHeight, scaledImage.getHeight());
        assertTrue(scaledImage.getWidth() < scaledHeight);
    }
    
    @Test
    public void shouldScaleLandscapeImageCorrectly() throws Exception
    {
        expectedImage = loadLandscapeImage();
        image = new ScalableImage(expectedImage);
        int scaledWidth = 100;
        image.setMaxSideLength(scaledWidth);
        BufferedImage scaledImage = image.getScaledImage();
        assertEquals(scaledWidth, scaledImage.getWidth());
        assertTrue(scaledImage.getHeight() < scaledWidth);
    }
    
    @Test
    public void shouldMaintainAspectRatio()
    {
        double aspectRatio = ((double) expectedImage.getWidth()) 
                / expectedImage.getHeight();
        BufferedImage scaledImage = image.getScaledImage();
        double actualAspectRatio = ((double) scaledImage.getWidth()) 
                / scaledImage.getHeight();
        assertEquals(aspectRatio, actualAspectRatio, 0d);
    }

    private BufferedImage loadPortraitImage() throws Exception
    {
        return loadImage("portrait-image.jpg");
    }
    
    private BufferedImage loadLandscapeImage() throws IOException
    {
        return loadImage("landscape-image.jpg");
    }
    
    private BufferedImage loadImage(String resourceName) throws IOException
    {
        InputStream is = ScalableImageTest.class.getResourceAsStream(
                resourceName);
        return ImageIO.read(is);
    }
    
    @Test
    public void shouldNotAllowNonPositiveMaxSideLength()
    {
        assertSetMaxSideLengthFails(0);
        assertSetMaxSideLengthFails(-1);
    }
    
    private void assertSetMaxSideLengthFails(int value)
    {
        try
        {
            image.setMaxSideLength(value);
            fail("No IllegalArgumentException thrown with maxSideLength of " 
                    + value);
        }
        catch (IllegalArgumentException iax)
        {
            // expected
        }
    }
    
    
}
