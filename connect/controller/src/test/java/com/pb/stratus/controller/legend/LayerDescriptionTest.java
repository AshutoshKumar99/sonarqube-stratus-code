package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.Legend;
import com.mapinfo.midev.service.mapping.v1.LegendRow;
import com.mapinfo.midev.service.mapping.v1.LegendType;
import com.pb.stratus.controller.i18n.LocaleResolver;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class LayerDescriptionTest
{

    @Test
    public void testConstructorWithParamString()
    {
        LayerDescription layer = new LayerDescription("name");
        layer.setLegendTitle("title");
        List<String>iconsDescriptions= new ArrayList<String>();
        iconsDescriptions.add("image1");
        iconsDescriptions.add("image2");
        layer.setIconDescriptions(iconsDescriptions);
        
        assertEquals(layer.getName(), "name");
        assertEquals(layer.getLegendTitle(), "title");
        assertEquals(layer.getIconDescriptions().get(0), "image1");
        assertEquals(layer.getIconDescriptions().get(1), "image2");

        layer.setName("new_name");
        assertEquals(layer.getName(), "new_name");
    }

    @Test
    public void testConstructorWithParamStringListOfString()
    {
        List<String> icons = new ArrayList<String>();
        icons.add("image1");
        icons.add("image2");
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(2);

        LayerDescription layer = new LayerDescription("name", icons);
        layer.setIconID(ids);
        layer.iconBytes().add(new byte[1]);
        layer.iconBytes().add(new byte[2]);

        assertEquals(layer.getName(), "name");
        assertEquals(layer.getIconDescriptions().get(0), "image1");
        assertEquals(layer.getIconDescriptions().get(1), "image2");
        assertEquals(layer.iconBytes().get(0).length, 1);
        assertEquals(layer.iconBytes().get(1).length, 2);

    }

    @Test
    public void shouldShowMissingLegendIconIfIconIsMissing() throws IOException
    {
        LocaleResolver.setLocale(new Locale("de"));
        Legend legend = new Legend();
        legend.setLayerName("name");
        legend.setLegendType(LegendType.INDIVIDUAL_VALUE_THEME);
        legend.setTitle("Ranges by Population");
        LegendRow row = new LegendRow();
        row.setDescription("Test");
        legend.getLegendRow().add(row);
        IndexCounter counter = new IndexCounter();
        LayerDescription layerDescription = new LayerDescription("name",
                legend, counter);
        byte[] actualimage = layerDescription.iconBytes().get(0);
        assertEquals(actualimage.length, layerDescription.getMissingLegendIcon().length);
    }
    
    @Test
    public void testConstructorWithParamStringLegendIntArray() throws IOException
    {

        Legend legend = new Legend();
        legend.setLegendType(LegendType.RANGED_THEME);
        legend.setLayerName("name");
        legend.setTitle("layer1");
        LegendRow row = new LegendRow();
        row.setImage(new byte[3]);
        row.setDescription("desc1");
        legend.getLegendRow().add(row);        
        LayerDescription layerDescription = new LayerDescription("name", 
                legend, new IndexCounter());
        assertEquals(layerDescription.getName(), "name");
        assertEquals(layerDescription.getLegendTitle(),"layer1");
        assertEquals(layerDescription.getIconDescriptions().get(0),"desc1");
        assertEquals(layerDescription.iconBytes().get(0).length,3);
    }

    @Test
    public void testLocalisedThemeRangedLayer() throws IOException
    {
        LocaleResolver.setLocale(new Locale("de"));
        Legend legend = new Legend();
        legend.setLegendType(LegendType.RANGED_THEME);
        legend.setLayerName("name");
        legend.setTitle("Ranges by Population");
        LegendRow row = new LegendRow();
        row.setImage(new byte[3]);
        row.setDescription("10000.00 to 20000.00");
        legend.getLegendRow().add(row);

        IndexCounter counter = new IndexCounter();
        LayerDescription layerDescription = new LayerDescription("name", 
                legend, counter);
        assertEquals(layerDescription.getName(), "name");
        assertEquals(layerDescription.getLegendTitle(),
                                                "Bereiche durch Population");
        assertEquals(layerDescription.getIconDescriptions().get(0),
                                                "10.000 bis 20.000");
        assertEquals(layerDescription.iconBytes().get(0).length,3);

        

        // try the alternative themed value rather than range
        legend.setTitle("Ind. Value with description");
        layerDescription = new LayerDescription("name",legend,counter);
        assertEquals(layerDescription.getLegendTitle(),
                                            "Kategorisiert nach description");
        assertEquals(layerDescription.getIconDescriptions().get(0),
                                                "10.000 bis 20.000");

        LocaleResolver.setLocale(null);
    }

    

}
