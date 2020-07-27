package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MarkerTest
{
    
    private Marker marker;

    @Before
    public void setUp()
    {
        BufferedImage icon = new BufferedImage(50, 100, 
                BufferedImage.TYPE_3BYTE_BGR);
        Point anchorPoint = new Point(25, 50);
        Point location = new Point(1234, 5678);
        marker = new Marker(icon, anchorPoint, location);
    }
    
    @Test
    public void shouldAugmentIcon()
    {
        BufferedImage icon = createWhiteImage();
        Marker augmented = marker.augmentWithIcon(new Point(0, 0), icon);
        double[] bottomRightAddedIcon = getPixel(augmented.getIcon(), 9, 9);
        assertArrayEquals(new double[] {255, 255, 255, 255}, 
                bottomRightAddedIcon, 0d);
    }
    
    private double[] getPixel(BufferedImage image, int x, int y)
    {
        return image.getData().getPixel(x, y, (double[]) null);
    }
    
    @Test
    public void shouldAdjustAnchorPointIfAugmentationChangesSize()
    {
        BufferedImage icon = createWhiteImage();
        Marker augmented = marker.augmentWithIcon(new Point(-5, -10), icon);
        assertEquals(55, augmented.getIcon().getWidth());
        assertEquals(110, augmented.getIcon().getHeight());
        assertEquals(new Point(30, 60), augmented.getAnchorPoint());
    }
    
    private BufferedImage createWhiteImage()
    {
        BufferedImage icon = new BufferedImage(10, 10, 
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = icon.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, 10, 10);
        g.dispose();
        return icon;
    }

}
