package com.pb.stratus.controller.print;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TileCompositorTest {

    private TileSet mockTileSet;
    
    private TileCompositor compositor;

    private final int COLS = 3;
    
    private final int ROWS = 2;
    
    private final int TILESIZE = 256;
    
    private final Rectangle PIXEL_BOUNDS = new Rectangle(10, 10, 736, 480);
    
    @Before
    public void setUp()
    {
        mockTileSet = new TileSet(createTiles(), ROWS, COLS, PIXEL_BOUNDS, 
                TILESIZE);
        compositor = new TileCompositor();
    }

    private List<BufferedImage> createTiles() 
    {
        List<BufferedImage> tiles = new LinkedList<BufferedImage>();
        for (int i = 0; i < 6; i++)
        {
            tiles.add(new BufferedImage(TILESIZE, TILESIZE, 
                    BufferedImage.TYPE_INT_ARGB));
        }
        return tiles;
    }

    @Test
    public void shouldReturnBufferedImageOfCorrectSize()
    {
        Dimension expected = new Dimension(900, 600);
        BufferedImage img = compositor.compose(mockTileSet, expected);
        assertNotNull("BufferedImage returned from TileCompositor was null", img);
        Dimension actual = new Dimension(img.getWidth(), img.getHeight());
        assertEquals(expected, actual);
    }
}
