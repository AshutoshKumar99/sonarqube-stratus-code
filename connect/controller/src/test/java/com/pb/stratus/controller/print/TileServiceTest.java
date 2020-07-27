package com.pb.stratus.controller.print;

import com.pb.stratus.controller.tile.service.MiDevTileService;
import com.pb.stratus.controller.tile.service.TileService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.pb.stratus.controller.util.ImageAssertUtils.assertImagesEquivalentAsPng;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TileServiceTest
{
    
    private MockTileRepository mockRepo;
    
    private TileService service;
    
    private BoundingBox maxBounds;
    

    @Before
    public void setUp()
    {
        mockRepo = new MockTileRepository();
        service = new MiDevTileService(mockRepo, Integer.MAX_VALUE);
        maxBounds = mockRepo.describe("someLayer").getMaxBounds();
    }
    
    @Test
    public void shouldGetOneTileIfMaxBoundsRequestedAtLevel0() throws Exception
    {
        TileSet actual = service.getTileSet("someLayer", 
                maxBounds, 0, null);
        BufferedImage tile = mockRepo.getTile("someLayer", 0, 0, 0, null);
        TileSet expected = new TileSet(Arrays.asList(tile), 1, 1, 
                new Rectangle(0, 0, 256, 256), 256);
        assertEquals(expected, actual);
    }
    
    @Test
    public void shouldGetAll16TilesIfMaxBoundsRequestedAtLevel2() 
            throws Exception
    {
        TileSet actual = service.getTileSet("someLayer", maxBounds, 2, null);
        List<BufferedImage> tiles = new LinkedList<BufferedImage>();
        for (int row = 0; row < 4; row++)
        {
            for (int col = 0; col < 4; col++)
            {
                tiles.add(mockRepo.getTile("someLayer", row, col, 2, null));
            }
        }
        TileSet expected = new TileSet(tiles, 4, 4, 
                new Rectangle(0, 0, 1024, 1024), 256);
        assertEquals(expected, actual);
    }
    
    @Test
    public void shouldGetExpectedTilesForSmallerThanMaxBounds() 
            throws Exception
    {
        BoundingBox bounds = new BoundingBox(50, 10, -40, 50, "someSrs");
        TileSet actual = service.getTileSet("someLayer", bounds, 2, null);
        assertEquals(2, actual.getRows());
        assertEquals(3, actual.getCols());
        assertEquals(mockRepo.getTile("someLayer", 0, 1, 2, null), 
                actual.getTile(0, 0));
        assertEquals(mockRepo.getTile("someLayer", 0, 2, 2, null), 
                actual.getTile(0, 1));
        assertEquals(mockRepo.getTile("someLayer", 0, 3, 2, null), 
                actual.getTile(0, 2));
        assertEquals(mockRepo.getTile("someLayer", 1, 1, 2, null), 
                actual.getTile(1, 0));
        assertEquals(mockRepo.getTile("someLayer", 1, 2, 2, null), 
                actual.getTile(1, 1));
        assertEquals(mockRepo.getTile("someLayer", 1, 3, 2, null), 
                actual.getTile(1, 2));
    }
    
    @Test
    public void shouldGetExpectedOffsetForSmallerThanMaxBounds() 
            throws Exception
    {
        BoundingBox bounds = new BoundingBox(50, 10, -40, 55, "someSrs");
        TileSet actual = service.getTileSet("someLayer", bounds, 2, null);
        Rectangle expectedPixelBounds = new Rectangle(28, 228, 540, 228);
        assertEquals(expectedPixelBounds, actual.getPixelBounds());
    }
    
    @Test
    public void shouldFailIfBoundsRequestedInWrongProjectionSystem() 
            throws Exception 
    {
        try 
        {
            service.getTileSet("someLayer", 
                    new BoundingBox(2, 1, 1, 2, "someOtherSrs"), 0, null);
            fail("No IllegalArgumentException thrown");
        }
        catch (IllegalArgumentException ix)
        {
            assertEquals("Requested SRS different from SRS of tile layer " +
            		"'someLayer'", ix.getMessage());
            // expected
        }
    }
    
    @Test
    @Ignore
    public void shouldNotFailIfBoundsRequestedInProjectionSystemWithDifferentCase() 
            throws Exception 
    {
        service.getTileSet("someLayer", 
                new BoundingBox(2, 1, 1, 2, "Somesrs"), 0, null);
    }
    
    @Test
    public void shouldFailIfTooManyTilesRequested() throws ServletException, IOException
    {
        service = new MiDevTileService(mockRepo, 10);
        try 
        {
            service.getTileSet("someLayer", maxBounds, 2, null);
            fail("No MaxNumberOfTilesExceededException thrown");
        } 
        catch(MaxNumberOfTilesExceededException x)
        {
            assertEquals(10, x.getMax());
            assertEquals(16, x.getActual());
        }
    }

    @Test
    public void shouldReturnBlankImagesIfMaxBoundsExceeded() throws Exception
    {
        MockTileRepository mockRepository = new MockTileRepository(128);
        service = new MiDevTileService(mockRepository, 100);
        BoundingBox bounds = new BoundingBox(95, -95, -95, 95, "someSrs");
        TileSet tileSet = service.getTileSet("someLayer", bounds, 1, null);
        assertEquals(4, tileSet.getCols());
        assertEquals(4, tileSet.getRows());
        BufferedImage expectedTile = createBlankTile(128);
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(0, 0));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(0, 1));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(0, 2));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(0, 3));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(1, 0));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(2, 0));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(3, 0));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(3, 1));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(3, 2));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(3, 3));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(1, 3));
        assertImagesEquivalentAsPng(expectedTile, tileSet.getTile(2, 3));
    }
    
    private BufferedImage createBlankTile(int tileSize) 
    {
        BufferedImage expectedImage = new BufferedImage(tileSize, tileSize, 
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = expectedImage.createGraphics();
        try 
        {
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, tileSize, tileSize);
        }
        finally 
        {
            g.dispose();
        }
        return expectedImage;
    }
    
    @Test
    public void shouldReturnCorrectPixelBoundsIfMaxBoundsExceeded() throws Exception 
    {
        BoundingBox bounds = new BoundingBox(85, -95, -185, 85, "someSrs");
        TileSet tileSet = service.getTileSet("someLayer", bounds, 1, null);
        Rectangle expected = new Rectangle(242, 14, 768, 512);
        assertEquals(expected, tileSet.getPixelBounds());
    }

    @Test
    public void shouldUseImageSizeFromTileRepositoryForBlankImages() throws Exception
    {
        BoundingBox bounds = new BoundingBox(95, -95, -95, 95, "someSrs");
        TileSet tileSet = service.getTileSet("someLayer", bounds, 1, null);
        BufferedImage actualImage = tileSet.getTile(0, 0);
        assertEquals(256, actualImage.getHeight());
        assertEquals(256, actualImage.getWidth());
    }

}
