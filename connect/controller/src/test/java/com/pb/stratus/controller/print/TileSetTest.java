package com.pb.stratus.controller.print;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TileSetTest
{
    
    @Test
    public void shouldGetExpectedTileFromFlatList() 
    {
        List<BufferedImage> tiles = Arrays.asList(
                mock(BufferedImage.class), 
                mock(BufferedImage.class), 
                mock(BufferedImage.class), 
                mock(BufferedImage.class), 
                mock(BufferedImage.class));
        TileSet tileSet = new TileSet(tiles, 2, 3, 
                new Rectangle(0, 1, 2, 3), 1);
        assertEquals(tiles.get(0), tileSet.getTile(0, 0));
        assertEquals(tiles.get(1), tileSet.getTile(0, 1));
        assertEquals(tiles.get(2), tileSet.getTile(0, 2));
        assertEquals(tiles.get(3), tileSet.getTile(1, 0));
        assertEquals(tiles.get(4), tileSet.getTile(1, 1));
    }

}
