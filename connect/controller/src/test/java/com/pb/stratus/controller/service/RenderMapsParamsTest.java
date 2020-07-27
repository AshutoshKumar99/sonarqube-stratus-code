package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.mapping.v1.Rendering;
import com.pb.stratus.controller.IllegalRequestException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: ar009sh
 * Date: 4/29/14
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class RenderMapsParamsTest {

    RenderMapParams params;
    List<String> maps =null;

    @Before
    public void setUp()
    {
        maps = new ArrayList<>();
        maps.add("namedLayer");
        maps.add("objectLayer");
        maps.add("gridLayer");
        params = new RenderMapParams();

    }

    @After
    public void tearDown()
    {
        maps = null;

    }


    @Test
    public void testValidMapLayers() throws Exception
    {

        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);
    }


/*    @Test
    public void testNullMapLayers() throws Exception
    {
        try
        {
         params.setMapLayers(null);
         params.setMapName("map");
         params.setSrs("epsg:4269");
         params.setHeight(800);
         params.setWidth(600);
         params.setXPos(-71.2345);
         params.setYPos(42.1345);
         params.setZoom(200000.0);
         params.setImageMimeType("image/png");
         params.setReturnImage(true);
         fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }*/




    @Test
    public void testValidMapName() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);
    }

    @Test
    public void testNullMapName() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName(null);
            params.setSrs("epsg:4269");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(-71.2345);
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptyMapName() throws Exception
    {

        try
        {
            params.setMapLayers(maps);
            params.setMapName("");
            params.setSrs("epsg:4269");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(-71.2345);
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testValidSrs() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testNullSrs() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs(null);
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(-71.2345);
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptySrs() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(-71.2345);
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testValidXposDouble() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testValidXposString() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos("-71.2345");
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testNullXPos() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(null);
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptyXPos() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos("");
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testValidYposDouble() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testValidYposString() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos("-71.2345");
        params.setYPos("42.1345");
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testNullYPos() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(42.1345);
            params.setYPos(null);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptyYPos() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos("42.1345");
            params.setYPos("");
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testValidHeightDouble() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testValidHeightString() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight("800");
        params.setWidth(600);
        params.setXPos("-71.2345");
        params.setYPos("42.1345");
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testNullHeight() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(null);
            params.setWidth(600);
            params.setXPos(42.1345);
            params.setYPos(23.2342);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptyHeight() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight("");
            params.setWidth(600);
            params.setXPos("42.1345");
            params.setYPos(34.2342);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testValidWidthDouble() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testValidWidthString() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight("800");
        params.setWidth("600");
        params.setXPos("-71.2345");
        params.setYPos("42.1345");
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testNullWidth() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(800);
            params.setWidth(null);
            params.setXPos(42.1345);
            params.setYPos(23.2342);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptyWidth() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight("800");
            params.setWidth("");
            params.setXPos("42.1345");
            params.setYPos(34.2342);
            params.setZoom(200000.0);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testValidZoomDouble() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testValidZoomString() throws Exception
    {
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight("800");
        params.setWidth("600");
        params.setXPos("-71.2345");
        params.setYPos("42.1345");
        params.setZoom("200000.0");
        params.setImageMimeType("image/png");
        params.setReturnImage(true);

    }

    @Test
    public void testNullZoom() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(42.1345);
            params.setYPos(23.2342);
            params.setZoom(null);
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptyZoom() throws Exception
    {
        try
        {
            params.setMapLayers(maps);
            params.setMapName("Map");
            params.setSrs("");
            params.setHeight("800");
            params.setWidth("600");
            params.setXPos("42.1345");
            params.setYPos(34.2342);
            params.setZoom("");
            params.setImageMimeType("image/png");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testValidImageMimeType() throws Exception
    {
        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);
    }

    @Test
    public void testNullImageMimeType() throws Exception
    {
        try
        {

            params.setMapLayers(maps);
            params.setMapName(null);
            params.setSrs("epsg:4269");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(-71.2345);
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType(null);
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }
    }

    @Test
    public void testEmptyImageMimeType() throws Exception
    {

        try
        {

            params.setMapLayers(maps);
            params.setMapName("");
            params.setSrs("epsg:4269");
            params.setHeight(800);
            params.setWidth(600);
            params.setXPos(-71.2345);
            params.setYPos(42.1345);
            params.setZoom(200000.0);
            params.setImageMimeType("");
            params.setReturnImage(true);
            fail("No IllegalRequestException thrown");
        } catch (IllegalRequestException e)
        {

        }

    }

    @Test
    public void testEmptyRendering() throws Exception
    {

        params.setMapLayers(maps);
        params.setMapName("Map");
        params.setSrs("epsg:4269");
        params.setHeight(800);
        params.setWidth(600);
        params.setXPos(-71.2345);
        params.setYPos(42.1345);
        params.setZoom(200000.0);
        params.setImageMimeType("image/png");
        params.setReturnImage(true);
        assertEquals(Rendering.QUALITY, params.getRendering());

    }



}
