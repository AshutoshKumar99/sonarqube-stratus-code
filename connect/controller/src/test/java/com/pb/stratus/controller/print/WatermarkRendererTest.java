package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import static com.pb.stratus.controller.util.ImageAssertUtils.assertPixel;
import static org.junit.Assert.assertEquals;

public class WatermarkRendererTest
{
    
    private WatermarkRenderer renderer;
    
    private BufferedImage watermark;

    @Before
    public void setUp() throws Exception
    {
        renderer = new WatermarkRenderer();
        InputStream is = WatermarkRendererTest.class.getResourceAsStream(
                "test-watermark.png");
        watermark = ImageIO.read(is);
        is.close();
    }
    
    @Test
    public void shouldTileWatermarkImage()
    {
        BufferedImage image = renderer.renderWatermark(watermark, 
                1, new Dimension(200, 200));
        assertPixel(new double[] {0, 0, 0, 255}, image, 0, 0);
        assertPixel(new double[] {0, 0, 0, 255}, image, 10, 0);
        assertPixel(new double[] {0, 0, 0, 255}, image, 20, 0);
        assertPixel(new double[] {0, 0, 0, 255}, image, 0, 10);
    }
    
    @Test
    public void shouldCreateImageOfTargetSize()
    {
        BufferedImage image = renderer.renderWatermark(watermark, 
                1, new Dimension(200, 300));
        assertEquals(200, image.getWidth());
        assertEquals(300, image.getHeight());
    }
    
    @Test
    public void shouldTakeOpacityIntoAccount()
    {
        BufferedImage image = renderer.renderWatermark(watermark, 
                0.2, new Dimension(200, 300));
        assertPixel(new double[] {0, 0, 0, 51}, image, 0, 0);
    }

}
