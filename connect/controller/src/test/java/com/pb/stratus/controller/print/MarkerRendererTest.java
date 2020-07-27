package com.pb.stratus.controller.print;

import com.pb.stratus.controller.util.ImageAssertUtils;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;

import static com.pb.stratus.controller.util.ImageAssertUtils.assertPixel;

public class MarkerRendererTest
{
    
    private MarkerRenderer renderer;
    
    private Marker mockMarker;

    
    @Before
    public void setUp()
    {
        BufferedImage icon = createImage(Color.BLACK);
        mockMarker = new Marker(icon, new Point(9, 9), 
                new Point(100, 100));
        renderer = new MarkerRenderer();
    }
    
    @Test
    public void shouldRenderMarkerAtExpectedLocation()
    {
        BufferedImage icon = new BufferedImage(10, 10,
                BufferedImage.TYPE_3BYTE_BGR);

        mockMarker = new Marker(icon, new Point(9, 9),
                new Point(500, 500));

        BufferedImage image = renderer.renderMarkers(new Dimension(100, 100),
                new BoundingBox(1000, 0, 0, 1000, "someSrs"),
                Collections.singletonList(mockMarker));
        ImageAssertUtils.writeImageToTempFile(image);

        // Top Left
        assertPixel(new double[] {0, 0, 0, 255}, image, 41, 41); //in
        assertPixel(new double[] {0, 0, 0, 0}, image, 40, 40); //out

        // Top Right
        assertPixel(new double[] {0, 0, 0, 255}, image, 50, 41); //in
        assertPixel(new double[] {0, 0, 0, 0}, image, 51, 40); //out

        // Bottom Left
        assertPixel(new double[] {0, 0, 0, 255}, image, 41, 49); //in
        assertPixel(new double[] {0, 0, 0, 0}, image, 40, 51); //out

        // Bottom Right
        assertPixel(new double[] {0, 0, 0, 255}, image, 50, 49); //in
        assertPixel(new double[] {0, 0, 0, 0}, image, 51, 51); //out
    }
    
    @Test
    public void shouldRenderMarkersInCorrectZOrder() throws Exception
    {
        Marker marker1 = new Marker(createImage(Color.RED), new Point(0, 9), 
                new Point(0, 0));
        Marker marker2 = new Marker(createImage(Color.GREEN), new Point(0, 9), 
                new Point(5, 0));
        Marker marker3 = new Marker(createImage(Color.BLUE), new Point(0, 9), 
                new Point(5, 5));
        BoundingBox bb = new BoundingBox(100, 0, 0, 100, "someSrs");
        BufferedImage image = renderer.renderMarkers(
                new Dimension(100, 100), bb, 
                Arrays.asList(marker1, marker2, marker3));
        
        assertPixel(new double[] {255, 0, 0, 255}, image, 9, 90);
        assertPixel(new double[] {0, 255, 0, 255}, image, 10, 90);
        assertPixel(new double[] {0, 0, 255, 255}, image, 10, 89);
        
    }

    @Test
    public void shouldIncludeSoutWestAndExcludeNorthEast() throws Exception
    {
        Marker marker1 = new Marker(createImage(Color.RED, 1, 1), 
                new Point(0, 0), new Point(0, 0));
        Marker marker2 = new Marker(createImage(Color.RED, 1, 1), 
                new Point(0, 0), new Point(100, 100));
        BoundingBox bb = new BoundingBox(100, 0, 0, 100, "someSrs");
        BufferedImage image = renderer.renderMarkers(
                new Dimension(100, 100), bb, 
                Arrays.asList(marker1, marker2));
        assertPixel(new double[] {255, 0, 0, 255}, image, 0, 99);
        assertPixel(new double[] {0, 0, 0, 0}, image, 99, 0);
    }
    
    private BufferedImage createImage(Color color)
    {
        return createImage(color, 10, 10);
    }
    
    private BufferedImage createImage(Color color, int w, int h)
    {
        BufferedImage icon = new BufferedImage(w, h, 
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = icon.createGraphics();
        g.setBackground(color);
        g.clearRect(0, 0, 10, 10);
        g.dispose();
        return icon;
    }
}
