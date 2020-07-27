package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.render.CopyrightRenderer;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Note that this test requires raster image output to be verified. To avoid 
 * image comparison, we just verify selected pixels at selected locations in 
 * the generated image. This is obviously far from exhaustive, but should flag
 * up regression issues.  
 */
public class CopyrightRendererTest
{
    
    private CopyrightRenderer renderer;
    private MapConfig mapConfig;
    private MapConfig bingMapConfig;

    @Before
    public void setUp()
    {
        mapConfig = getBasicMapConfig();
        bingMapConfig = getBingMapConfig();
        renderer = new CopyrightRenderer();
    }

    private MapConfig getBasicMapConfig()
    {
        MapConfig mapCfg = new MapConfig();
        List<MapDefinition> mapDefs = new LinkedList<MapDefinition>();
        mapDefs.add(createMapDefinition(mapCfg, "overlay1", "Overlay 1"));
        mapDefs.add(createMapDefinition(mapCfg, "overlay2", "Overlay 2"));
        mapDefs.add(createMapDefinition(mapCfg, "overlay3", "Overlay 3"));
        mapCfg.setMapDefinitions(mapDefs);
        mapCfg.setDefaultCopyright("Default");
        return mapCfg;
    }

    private MapDefinition createMapDefinition(MapConfig mapCfg, String name,
            String copyright)
    {
        MapDefinition mapDef = mapCfg.createMapDefinition();
        mapDef.setMapName(name);
        mapDef.setCopyright(copyright);
        return mapDef;
    }

    private MapConfig getBingMapConfig()
    {
        MapConfig mapCfg = getBasicMapConfig();
        MapDefinition mapDef = mapCfg.createMapDefinition();
        mapDef.setMapName("bingBaseMap");
        mapDef.setService(LayerServiceType.BING);
        mapCfg.getMapDefinitions().add(mapDef);
        return mapCfg;
    }
    
    @Test
    public void shouldRenderCopyrightBoxWithoutBing() throws Exception
    {
        BufferedImage image = renderImage(Arrays.asList(new LayerBean("overlay1"), new LayerBean("overlay2")),
                mapConfig);
        double[] topLeft = getPixelData(image, 10, 115);
        assertArrayEquals(new double[] {255, 255, 255, 153}, topLeft, 0d);
        double[] bottomRight = getPixelData(image, 74, 189);
        assertArrayEquals(new double[] {255, 255, 255, 153}, bottomRight, 0d);
    }

    @Test
    public void shouldRenderLayerCopyrights()
    {
        BufferedImage image = renderImage(Arrays.asList(new LayerBean("overlay3"), new LayerBean("overlay2"),
                new LayerBean("overlay1")), mapConfig);
        double[] overlay1LastLetterPixel = getPixelData(image, 19, 130);
        assertArrayEquals(new double[] {255, 255,255, 153}, overlay1LastLetterPixel, 
                0d);
        double[] overlay2LastLetterPixel = getPixelData(image, 19, 145);
        assertArrayEquals(new double[] {255, 255,255, 153}, overlay2LastLetterPixel, 
                0d);
    }

    @Test
    public void shouldRenderDefaultCopyright()
    {
        BufferedImage image = renderImage(Arrays.asList(new LayerBean("overlay1"), new LayerBean("overlay2")),
                mapConfig);
        double[] defaultLastLetterPixel = getPixelData(image, 16, 160);
        assertArrayEquals(new double[] {255, 255,255, 153}, defaultLastLetterPixel,0d);
    }

    @Test
    public void shouldExtractLinksAndRemoveHtmlTags()
    {
        String htmlString = "Part of the &lt;a href=&quot;http://www.colchester.gov.uk/maps&quot;&gt;" +
                "Colchester On The Map&lt;/a&gt; series | Contact us &lt;a " +
                "href=&quot;mailto:c-maps@colchester.gov.uk&quot;&gt;c-maps@colchester.gov.uk&lt;/a&gt;" +
                " © Crown copyright and database rights 2013 Ordnance Survey 100023706.";
        String expectedString = "Part of the Colchester On The Map (http://www.colchester.gov.uk/maps) series | " +
                "Contact us c-maps@colchester.gov.uk © Crown copyright and database " +
                "rights 2013 Ordnance Survey 100023706.";

        CopyrightRenderer copyrightRenderer = new CopyrightRenderer();
        try {
            Method targetMethod = CopyrightRenderer.class.getDeclaredMethod("extractLinksAndRemoveHtmlTags", String.class);
            targetMethod.setAccessible(true);
            String actualString = (String)targetMethod.invoke(copyrightRenderer, htmlString);
            assertEquals(expectedString, actualString);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage renderImage(List<LayerBean> visibleLayers,
            MapConfig mapCfg)
    {
        return renderer.renderCopyright(new Dimension(300, 200),
                new Resolution(300), visibleLayers, mapCfg);
    }
    
    private double[] getPixelData(BufferedImage img, int x, int y)
    {
        return img.getData().getPixel(x, y, (double[]) null);
    }
}
