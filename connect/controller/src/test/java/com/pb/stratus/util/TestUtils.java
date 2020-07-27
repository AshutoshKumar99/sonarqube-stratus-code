package com.pb.stratus.util;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

public class TestUtils
{
    
    private TestUtils() {}
    
    public static void assertEqualImages(BufferedImage expected, 
            BufferedImage actual) throws Exception
    {
        if (oneIsNullTheOtherIsnt(expected, actual))
        {
            fail("expected: " + expected + " but was: " + actual);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(expected, "png", bos);
        byte[] expectedImageData = bos.toByteArray();
        bos = new ByteArrayOutputStream();
        ImageIO.write(actual, "png", bos);
        byte[] actualImageData = bos.toByteArray();
        assertArrayEquals(expectedImageData, actualImageData);
    }
    
    private static boolean oneIsNullTheOtherIsnt(Object o1, Object o2)
    {
        return o1 == null && o2 != null || o1 != null && o2 == null;
    }
    
}
