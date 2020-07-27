package com.pb.stratus.controller.print;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Creates an image of a given size and tiles it with a given watermark image 
 */
public class WatermarkRenderer
{
    
    public BufferedImage renderWatermark(BufferedImage watermarkImage, 
            double opacity, Dimension targetSize)
    {
        BufferedImage canvas = new BufferedImage(targetSize.width, 
                targetSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = canvas.createGraphics();
        try
        {
            tileImage(g, opacity, watermarkImage, targetSize);
        }
        finally
        {
            g.dispose();
        }
        return canvas;
    }

    private void tileImage(Graphics2D g, double opacity,
            BufferedImage watermarkImage, Dimension targetSize)
    {
        int x = 0;
        int y = 0;
        Composite c = AlphaComposite.getInstance(AlphaComposite.DST_OVER, 
                (float) opacity);
        g.setComposite(c);
        while (x < targetSize.width)
        {
            while (y < targetSize.height)
            {
                stamp(watermarkImage, g, x, y);
                y += watermarkImage.getHeight();
            }
            x += watermarkImage.getWidth();
            y = 0;
        }
    }

    private void stamp(BufferedImage watermarkImage, Graphics2D g, int x,
            int y)
    {
        g.drawImage(watermarkImage, null, x, y);
    }

}
