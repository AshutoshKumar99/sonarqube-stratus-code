package com.pb.stratus.controller.util;

import java.awt.*;

/**
 * Creates instances of {@link SpriteImageGenerator}
 */
public class SpriteImageGeneratorFactory
{
    
    public SpriteImageGenerator createSpriteImageGenerator(
            Dimension spriteSize)
    {
        return new SpriteImageWithPaddingGenerator(spriteSize);
    }

}
