package com.pb.stratus.controller.json;

import com.pb.stratus.controller.legend.*;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class LegendDataHolderStrategyTest
{
    
    private LegendDataStrategy strategy;
    
    LegendDataHolder mockHolder;

    @Before
    public void setUp()
    {
        strategy = new LegendDataStrategy();
        LegendItem item1 = new SingleLegendItem("item1", createIcon());
        SingleLegendItem item21 = new SingleLegendItem("item2.1", createIcon());
        SingleLegendItem item22 = new SingleLegendItem("item2.2", createIcon());
        LegendItem item2 = new ThematicLegendItem("item2", "PieTheme",
                Arrays.asList(item21, item22));
        OverlayLegend overlay1 = new OverlayLegend("overlay1", 
                Arrays.asList(item1));
        OverlayLegend overlay2 = new OverlayLegend("overlay2", 
                Arrays.asList(item2));
        LegendData ld = new LegendData(Arrays.asList(overlay1, overlay2));
        mockHolder = new LegendDataHolder(ld, "/path/to/img");
    }

    private byte[] createIcon()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedImage bufImage = new BufferedImage(1, 1,
                BufferedImage.TYPE_3BYTE_BGR);
        try
        {
            ImageIO.write(bufImage, "jpeg", baos);
        } catch (IOException e)
        {
            e.printStackTrace();
    }
        byte[] bytesOut = baos.toByteArray();
        return bytesOut;
    }

    @Test
    public void shouldGenerateExpectedJson()
    {
        StringBuilder b = new StringBuilder();
        strategy.processValue(mockHolder, b);
        assertEquals("{\"overlays\": ["
                + "{\"id\": \"overlay1\", \"legendItems\": ["
                + "{\"type\": \"single\",\"displayName\": \"item1\",\"legendType\": \"single\", \"iconUrl\": \"\\/path\\/to\\/img\",\"iconImage\": \"null\", \"iconIndex\": 0}]}, "
                + "{\"id\": \"overlay2\", \"legendItems\": [{\"type\": \"thematic\",\"displayName\": \"item2\",\"legendType\": \"PieTheme\", \"legendItems\": ["
                + "{\"type\": \"single\",\"displayName\": \"item2.1\",\"legendType\": \"single\", \"iconUrl\": \"\\/path\\/to\\/img\",\"iconImage\": \"null\", \"iconIndex\": 1}, "
                + "{\"type\": \"single\",\"displayName\": \"item2.2\",\"legendType\": \"single\", \"iconUrl\": \"\\/path\\/to\\/img\",\"iconImage\": \"null\", \"iconIndex\": 2}]}]}]}",
                b.toString());

    }

    @Test
    public void shouldGenerateExpectedJsonForThematicMaps()
    {
        StringBuilder b = new StringBuilder();
        SingleLegendItem item1 = new SingleLegendItem("item1", createIcon());
        item1.setBase64image("base64image1");
        SingleLegendItem item2 = new SingleLegendItem("item2", createIcon());
        item2.setBase64image("base64image2");
        LegendItem thematicLegendItem = new ThematicLegendItem("them_item", "RangedTheme",
                Arrays.asList(item1, item2));
        OverlayLegend overlay = new OverlayLegend("overlay1",
                Arrays.asList(thematicLegendItem));
        LegendData ld = new LegendData(Arrays.asList(overlay));
        mockHolder = new LegendDataHolder(ld, "");
        strategy.processValue(mockHolder, b);
        assertEquals("{\"overlays\": ["
                + "{\"id\": \"overlay1\", \"legendItems\": [{\"type\": \"thematic\",\"displayName\": \"them_item\",\"legendType\": \"RangedTheme\", \"legendItems\": ["
                + "{\"type\": \"single\",\"displayName\": \"item1\",\"legendType\": \"single\", \"iconUrl\": \"\",\"iconImage\": \"base64image1\", \"iconIndex\": 0}, "
                + "{\"type\": \"single\",\"displayName\": \"item2\",\"legendType\": \"single\", \"iconUrl\": \"\",\"iconImage\": \"base64image2\", \"iconIndex\": 1}]}]}]}",
                b.toString());
    }

}
