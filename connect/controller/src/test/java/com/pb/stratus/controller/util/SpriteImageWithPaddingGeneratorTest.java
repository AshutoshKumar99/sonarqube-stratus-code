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

public class SpriteImageWithPaddingGeneratorTest
{
    private SpriteImageWithPaddingGenerator spriteImageWithPaddingGenerator;


    @Before
    public void setUp()
    {
        spriteImageWithPaddingGenerator =
                new SpriteImageWithPaddingGenerator(new Dimension(24, 24));
    }

    @Test
    public void shouldReturnBlankImageWithSpriteSizeIfNoImagesAdded()
            throws Exception
    {
        BufferedImage blankPixel = loadImage("blankImageWithPadding.png");
        assertEqualImages(blankPixel, spriteImageWithPaddingGenerator.createSpriteImage());
    }

    @Test
    public void shouldReturnPaddedImageIfOneImageAdded() throws Exception
    {
        BufferedImage monkey = loadImage("monkey.png");
        spriteImageWithPaddingGenerator.addImage(monkey);
        BufferedImage expected  =  loadImage("monkeyWithPadding.png");
        assertEqualImages(expected, spriteImageWithPaddingGenerator.createSpriteImage());
    }

    @Test
    public void shouldMergeMultipleImagesWithPadding() throws Exception
    {
        BufferedImage monkey = loadImage("monkey.png");
        spriteImageWithPaddingGenerator.addImage(monkey);
        BufferedImage smiley = loadImage("smiley.png");
        spriteImageWithPaddingGenerator.addImage(smiley);
        BufferedImage expected = loadImage("mergedImageWithPadding.png");
        assertEqualImages(expected, spriteImageWithPaddingGenerator.createSpriteImage());
    }

    @Test
    public void shouldReturnScaledDownImageIfOversizeImageAdded()
            throws Exception
    {
        BufferedImage monkey = loadImage("monkey.png");
        spriteImageWithPaddingGenerator =
                new SpriteImageWithPaddingGenerator(new Dimension(16, 16));
        spriteImageWithPaddingGenerator.addImage(monkey);
        BufferedImage image = spriteImageWithPaddingGenerator.createSpriteImage();
        // width with padding
        assertEquals(18, image.getWidth());
        assertEquals(16, image.getHeight());
    }


    private BufferedImage loadImage(String name) throws IOException
    {
        InputStream is = SpriteImageWithPaddingGeneratorTest.class.getResourceAsStream(
                name);
        BufferedImage img = ImageIO.read(is);
        is.close();
        return img;
    }
}
