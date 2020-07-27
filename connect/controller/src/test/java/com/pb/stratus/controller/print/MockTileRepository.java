package com.pb.stratus.controller.print;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class MockTileRepository extends TileRepository
{
    
    private BoundingBox bounds;
    
    private Map<String, BufferedImage> tiles = new LinkedHashMap<String, BufferedImage>();

    private int tileSize;

    public MockTileRepository()
    {
        this(256);
    }

    public MockTileRepository(int tileSize)
    {
        super(null, null, null, null, null);
        this.tileSize = tileSize;
        bounds = new BoundingBox(90, -90, -90, 90, "someSrs");
        tiles.put("0,0,0", mock(BufferedImage.class));
        tiles.put("0,0,1", mock(BufferedImage.class));
        tiles.put("0,1,1", mock(BufferedImage.class));
        tiles.put("1,0,1", mock(BufferedImage.class));
        tiles.put("1,1,1", mock(BufferedImage.class));
        tiles.put("0,0,2", mock(BufferedImage.class));
        tiles.put("0,1,2", mock(BufferedImage.class));
        tiles.put("0,2,2", mock(BufferedImage.class));
        tiles.put("0,3,2", mock(BufferedImage.class));
        tiles.put("1,0,2", mock(BufferedImage.class));
        tiles.put("1,1,2", mock(BufferedImage.class));
        tiles.put("1,2,2", mock(BufferedImage.class));
        tiles.put("1,3,2", mock(BufferedImage.class));
        tiles.put("2,0,2", mock(BufferedImage.class));
        tiles.put("2,1,2", mock(BufferedImage.class));
        tiles.put("2,2,2", mock(BufferedImage.class));
        tiles.put("2,3,2", mock(BufferedImage.class));
        tiles.put("3,0,2", mock(BufferedImage.class));
        tiles.put("3,1,2", mock(BufferedImage.class));
        tiles.put("3,2,2", mock(BufferedImage.class));
        tiles.put("3,3,2", mock(BufferedImage.class));
    }
    
    @Override
    public BufferedImage getTile(String layerName, int row, int col, int level, 
            String imageMimeType)
    {
        assertEquals("someLayer", layerName);
        String key = new StringBuilder().append(row).append(",").append(col)
                .append(",").append(level).toString();
        BufferedImage img = tiles.get(key);
        if (img == null)
        {
            fail(String.format("No tile at row %d, col %d, level %d", 
                    row, col, level));
        }
        return img;
    }

    @Override
    public TileLayerDescription describe(String layerName)
    {
        assertEquals("someLayer", layerName);
        return new TileLayerDescription(bounds, tileSize);
    }
    
    

}
