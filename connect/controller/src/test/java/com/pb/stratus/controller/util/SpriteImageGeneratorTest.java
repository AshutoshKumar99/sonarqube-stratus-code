package com.pb.stratus.controller.util;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static com.pb.stratus.util.TestUtils.assertEqualImages;
import static org.junit.Assert.assertEquals;

public class SpriteImageGeneratorTest
{
    
    private SpriteImageGenerator imageGenerator;
    
    @Before
    public void setUp()
    {
        imageGenerator = new SpriteImageGenerator(new Dimension(24, 24));
    }
    
    @Test
    public void shouldReturnBlankImageWithSpriteSizeIfNoImagesAdded() 
            throws Exception
    {
        BufferedImage blankPixel = loadImage("blankImage.png");
        assertEqualImages(blankPixel, imageGenerator.createSpriteImage());
    }
    
    @Test
    public void shouldReturnOriginalImageIfOneImageAdded() throws Exception
    {
        BufferedImage monkey = loadImage("monkey.png");
        imageGenerator.addImage(monkey);
        assertEqualImages(monkey, imageGenerator.createSpriteImage());
    }
    
    @Test
    public void shouldMergeMultipleImages() throws Exception
    {
        BufferedImage monkey = loadImage("monkey.png");
        imageGenerator.addImage(monkey);
        BufferedImage smiley = loadImage("smiley.png");
        imageGenerator.addImage(smiley);
        BufferedImage expected = loadImage("merged.png");
        assertEqualImages(expected, imageGenerator.createSpriteImage());
    }
    
    @Test
    public void shouldReturnScaledDownImageIfOversizeImageAdded() 
            throws Exception
    {
        BufferedImage monkey = loadImage("monkey.png");
        imageGenerator = new SpriteImageGenerator(new Dimension(16, 16));
        imageGenerator.addImage(monkey);
        BufferedImage image = imageGenerator.createSpriteImage();
        assertEquals(16, image.getWidth());
        assertEquals(16, image.getHeight());
    }
    
    private BufferedImage loadImage(String name) throws IOException 
    {
        InputStream is = SpriteImageGeneratorTest.class.getResourceAsStream(
                name);
        BufferedImage img = ImageIO.read(is);
        is.close();
        return img;
    }

}
