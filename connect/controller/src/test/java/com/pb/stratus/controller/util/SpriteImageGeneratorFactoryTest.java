package com.pb.stratus.controller.util;

import org.junit.Test;

import java.awt.*;

import static junit.framework.Assert.assertEquals;

public class SpriteImageGeneratorFactoryTest
{
    
    @Test
    public void shouldCreateSpriteImageGenerator()
    {
        SpriteImageGeneratorFactory factory 
                = new SpriteImageGeneratorFactory();
        Dimension d = new Dimension(123, 456);
        SpriteImageGenerator generator = factory.createSpriteImageGenerator(d);
        // see CONN-14263 and comment of class SpriteImageWithPaddingGenerator
        assertEquals(new SpriteImageWithPaddingGenerator(d), generator);
    }

}
