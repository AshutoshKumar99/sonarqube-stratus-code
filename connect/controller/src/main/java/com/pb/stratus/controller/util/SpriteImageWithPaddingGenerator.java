package com.pb.stratus.controller.util;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class extends SpriteImageGenerator. This class specializes in adding a
 * 1 px transparent on either side of each individual BufferedImage that
 * constitute the sprite.
 *
 * this is needed for legend sprite to work correctly in both desktop and
 * ipad.
 * see CONN-14263 for complete details.
 * i also don't see any reason to override equals and hash code as the parent
 * equals and hash code should suffice our need.
 */
public class SpriteImageWithPaddingGenerator extends SpriteImageGenerator
{
    private BufferedImage BORDER_IMAGE;
    private final Dimension BORDER_IMAGE_DIMENSION;
    private static final Logger logger = LogManager.getLogger(
            SpriteImageWithPaddingGenerator.class.getName());

    public SpriteImageWithPaddingGenerator(Dimension spriteSize)
    {
        super(spriteSize);
        InputStream is =  SpriteImageWithPaddingGenerator.class.
                getResourceAsStream("tansparent.png");
        try {
            this.BORDER_IMAGE = ImageIO.read(is);
            is.close();
        }
        catch (IOException e)
        {
            this.BORDER_IMAGE = null;
            logger.error("unable to read image.");
        }

        BORDER_IMAGE_DIMENSION = new Dimension(1, 16);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof SpriteImageWithPaddingGenerator))
        {
            return false;
        }
        else
        {
            return super.equals(obj);
        }
    }

    @Override
    protected BufferedImage createCanvas()
    {
        return new BufferedImage(getWidthOfCanvas(),
                spriteSize.height, BufferedImage.TYPE_4BYTE_ABGR);
    }

    @Override
    protected void addImagesToCanvas(Graphics2D graphics)
    {
        int index = 1;
        // add the first border image
        stampOnCanvas(graphics, BORDER_IMAGE,
                createScaleOperation(BORDER_IMAGE), 0, 0);
        for (BufferedImage image : images)
        {
            stampOnCanvas(graphics, image, index++,
                    createScaleOperation(image));
            // add transparent border after each image
            stampOnCanvas(graphics, BORDER_IMAGE, index++,
                    createScaleOperation(BORDER_IMAGE));
        }
    }

    @Override
    protected void stampOnCanvas(Graphics2D graphics,
            BufferedImage image, int index, BufferedImageOp imageOp)
    {
        stampOnCanvas(graphics, image,  imageOp, calculateX(index), 0);
    }

    private int calculateX(int index)
    {
        int x = 0;
        for(int i=0; i < index; i++)
        {
            if(i % 2 == 0)
            {
                x += BORDER_IMAGE_DIMENSION.width;
            }
            else
            {
                x += spriteSize.width;
            }
        }
        return x;
    }

    private int getWidthOfCanvas()
    {
        return (spriteSize.width * Math.max(1, images.size())) +
                (BORDER_IMAGE_DIMENSION.width * Math.max(2, images.size() + 1));
    }
}
