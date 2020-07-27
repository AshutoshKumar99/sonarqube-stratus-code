package com.pb.stratus.controller.util;

import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class ImageAssertUtils
{
    public static boolean areImagesEquivalentAsPng(BufferedImage img1, BufferedImage img2)
    {
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(img1, "png", bos);
            bos.flush();
            byte[] imgData1 = bos.toByteArray();
            bos = new ByteArrayOutputStream();
            ImageIO.write(img2, "png", bos);
            bos.flush();
            byte[] imgData2 = bos.toByteArray();
            return Arrays.equals(imgData1, imgData2);
        }
        catch (IOException iox)
        {
            throw new RuntimeException(iox);
        }
    }
    
    public static void assertImagesEquivalentAsPng(BufferedImage expected, 
            BufferedImage actual)
    {
        assertTrue("Expected '" + expected + "' but was '" + actual + "'", 
                areImagesEquivalentAsPng(expected, actual));
    }
    
    public static double[] getPixel(BufferedImage image, int x, int y)
    {
        double[] pixel = new double[4];
        image.getRaster().getPixel(x, y, pixel);
        return pixel;
    }
    
    public static void writeImageToTempFile(BufferedImage img)
    {
        try
        {
            File f = File.createTempFile("test", ".png");
            ImageIO.write(img, "png", f);
            System.out.println(f);
            
        }
        catch (IOException iox)
        {
            throw new RuntimeException(iox);
        }
        
    }
    
    public static void assertPixel(double[] expectedPixel, 
            BufferedImage actualImage, int x, int y)
    {
        double[] actualPixel = actualImage.getData().getPixel(x, y, 
                (double[]) null);
        assertArrayEquals(expectedPixel, actualPixel, 0.00001d);
    }

    public static void assertImageHasDimensions(BufferedImage image, Dimension expectSize)
    {
        assertTrue("Unexpected image dimensions",
            (image.getHeight() == expectSize.getHeight()) &&
            (image.getWidth() == expectSize.getWidth()));
    }

}
