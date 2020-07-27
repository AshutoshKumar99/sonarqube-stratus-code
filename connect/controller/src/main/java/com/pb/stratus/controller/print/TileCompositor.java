package com.pb.stratus.controller.print;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileCompositor
{
    /**
     * Takes a TileSet and stitches the tiles together to form one large 
     * BufferedImage, then crops that image to the desired size taking into 
     * account the pixel bounds of the cropped image within the larger image.
     * 
     * @param tileSet
     * @param imageSize
     * @return
     */
    public BufferedImage compose(TileSet tileSet, Dimension imageSize)
    {
        BufferedImage bigImage = stitchTiles(tileSet);
        BufferedImage croppedImage = cropImage(bigImage, 
                tileSet.getPixelBounds());
        BufferedImage scaledImage = scaleImage(croppedImage, imageSize);
        return scaledImage;
    }

    private BufferedImage stitchTiles(TileSet tileSet)
    {
        int numTilesAcross = tileSet.getCols();
        int numTilesDown = tileSet.getRows();
        int tileSize = tileSet.getTileSize();
        int pixelsWide = numTilesAcross * tileSize;
        int pixelsHigh = numTilesDown * tileSize;
        BufferedImage bigImage = new BufferedImage(pixelsWide, pixelsHigh, 
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bigImage.createGraphics();
        for (int i = 0; i < numTilesAcross; i++)
        {
            for (int j = 0; j < numTilesDown; j++)
            {
                BufferedImage tile = tileSet.getTile(j, i);
                //Stamp the tile onto our bigImage in the correct place.
                int x = tileSize * i;
                int y = tileSize * j;
                g.drawImage(tile, null, x, y);
            }
        }
         return bigImage;
    }

    private BufferedImage cropImage(BufferedImage bigImage, Rectangle pixelBounds)
    {
        BufferedImage cropped = bigImage.getSubimage(
                pixelBounds.x, 
                pixelBounds.y, 
                pixelBounds.width, 
                pixelBounds.height);
        return cropped;
    }

    private BufferedImage scaleImage(BufferedImage before, Dimension desiredSize)
    {
        BufferedImage after = new BufferedImage(desiredSize.width, 
                desiredSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = after.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        try
        {
            g.drawImage(before, 0, 0, desiredSize.width, desiredSize.height, null);
            return after;
        }
        finally 
        {
            g.dispose();
        }
    }
}
