package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A contiguous set of tiles that cover a specific bounding box. The offset
 * represents the pixel offset of the top-left corner of the bounding box from 
 * the top-left corner of the top-left tile (which is at row 0, column 0).  
 */
public class TileSet
{
    
    private List<BufferedImage> tiles;
    
    private int rows;
    
    private int cols;
    
    private Rectangle pixelBounds;

    private int tileSize;
    
    /**
     * Creates a new tile set
     * 
     * @param tiles the tiles contained in this set represented in a flat list.
     *        The first element is the top-left tile. If the number of columns 
     *        is e.g. 3, then the 2nd element is the tile at row 0, col 2, the 
     *        4th element is the tile at row 1, col 0, etc. 
     * @param rows the number of rows in this tile set
     * @param cols the number of columns in this tile set
     * @param pixelBounds the rectangle within the returned image that 
     *        corresponds to the bounding box this TileSet covers.
     */
    public TileSet(List<BufferedImage> tiles, int rows, int cols, 
            Rectangle pixelBounds, int tileSize)
    {
        this.tiles = tiles;
        this.rows = rows;
        this.cols = cols;
        this.pixelBounds = pixelBounds;
        this.tileSize = tileSize;
    }

    public BufferedImage getTile(int row, int col)
    {
        return tiles.get(row * cols + col);
    }

    public int getRows()
    {
        return rows;
    }

    public int getCols()
    {
        return cols;
    }
    
    public Rectangle getPixelBounds() 
    {
        return pixelBounds;
    }
    
    public int getTileSize()
    {
        return tileSize;
    }

    public boolean equals(Object o) 
    {
        if (o == this) 
        {
            return true;
        }
        if (o == null) 
        {
            return false;
        }
        if (o.getClass() != this.getClass())
        {
            return false;
        }
        TileSet that = (TileSet) o;
        if (!ObjectUtils.equals(this.tiles, that.tiles)) 
        {
            return false;
        }
        if (this.rows != that.rows)
        {
            return false;
        }
        if (this.cols != that.cols) 
        {
            return false;
        }
        if (!ObjectUtils.equals(this.pixelBounds, that.pixelBounds))
        {
            return false;
        }
        if (this.tileSize != that.tileSize)
        {
            return false;
        }
        return true;
    }
    
    public int hashCode() 
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, tiles);
        hc = ObjectUtils.hash(hc, rows);
        hc = ObjectUtils.hash(hc, cols);
        hc = ObjectUtils.hash(hc, pixelBounds);
        hc = ObjectUtils.hash(hc, tileSize);
        return hc;
    }

}
