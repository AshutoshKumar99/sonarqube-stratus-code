package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;

/**
 * A description of a tile layer. 
 */
public class TileLayerDescription
{
    
    private BoundingBox maxBounds;
    
    private int tileSize;
    
    
    public TileLayerDescription(BoundingBox maxBounds, int tileSize)
    {
        this.maxBounds = maxBounds;
        this.tileSize = tileSize;
    }
    
    /**
     * @return the maximum bounds of the tile layer. 
     */
    public BoundingBox getMaxBounds()
    {
        return maxBounds;
    }
    
    /**
     * The size of each tile contained in the described layer. All tiles
     * are required to be square and have the same size. 
     */
    public int getTileSize()
    {
        return tileSize;
        
    }
    
    public boolean equals(Object o) 
    {
        if (this == o)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (this.getClass() != o.getClass())
        {
            return false;
        }
        TileLayerDescription that = (TileLayerDescription) o;
        if (!ObjectUtils.equals(this.maxBounds, that.maxBounds))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.tileSize, that.tileSize))
        {
            return false;
        }
        return true;
    }
    
    public int hashCode() 
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, maxBounds);
        hc = ObjectUtils.hash(hc, tileSize);
        return hc;
    }

}
