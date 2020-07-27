package com.pb.stratus.controller.util;

import com.pb.stratus.core.util.ObjectUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class to create images that can be used as CSS sprites.
 * This class is no longer used. bus since this is a utility class doesn't make
 * any sense in deprecating it.
 *
 * see CONN-14263 for complete info.
 * for legends using class #link(SpriteImageWithPaddingGenerator) instead.
 */
public class SpriteImageGenerator
{
    
    protected List<BufferedImage> images = new LinkedList<BufferedImage>();

    protected Dimension spriteSize;
    
    public SpriteImageGenerator(Dimension spriteSize)
    {
        this.spriteSize = spriteSize;
    }
    
    public void addImage(BufferedImage image)
    {
        images.add(image);
    }
    
    public BufferedImage createSpriteImage()
    {
        BufferedImage canvas = createCanvas();
        Graphics2D graphics = canvas.createGraphics();
        addImagesToCanvas(graphics);
        graphics.dispose();
        return canvas;
    }

    protected void addImagesToCanvas(Graphics2D graphics)
    {
        int index = 0;
        for (BufferedImage image : images)
        {
            stampOnCanvas(graphics, image, index++,
                    createScaleOperation(image));
        }
    }

    protected BufferedImageOp createScaleOperation(BufferedImage image)
    {
        double sx = spriteSize.getWidth() / image.getWidth();
        double sy = spriteSize.getHeight() / image.getHeight();
        BufferedImageOp imageOp = null;
        if (sx < 1 || sy < 1)
        {
            imageOp = new AffineTransformOp(
                    AffineTransform.getScaleInstance(sx, sy),
                            AffineTransformOp.TYPE_BILINEAR);
        }
        return imageOp;
    }

    protected BufferedImage createCanvas()
    {
        return new BufferedImage(spriteSize.width * Math.max(1, images.size()), 
                spriteSize.height, BufferedImage.TYPE_4BYTE_ABGR);
    }
    
    protected void stampOnCanvas(Graphics2D graphics,
            BufferedImage image, int index, BufferedImageOp imageOp)
    {
        stampOnCanvas(graphics, image,  imageOp, index * spriteSize.width, 0);
    }

    protected void stampOnCanvas(Graphics2D graphics,
            BufferedImage image, BufferedImageOp imageOp, int x, int y)
    {
        graphics.drawImage(image, imageOp, x, y);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        SpriteImageGenerator that = (SpriteImageGenerator) obj;
        if (!ObjectUtils.equals(this.spriteSize, that.spriteSize))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.images, that.images))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, spriteSize);
        hc = ObjectUtils.hash(hc, images);
        return hc;
    }

}
