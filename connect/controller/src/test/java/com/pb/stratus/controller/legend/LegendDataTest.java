package com.pb.stratus.controller.legend;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LegendDataTest
{
    
    private LegendData legendData;
    
    private List<BufferedImage> expectedIcons;
    
    @Before
    public void setUp()
    {
        expectedIcons = new LinkedList<BufferedImage>();
        LegendItem item1 = new SingleLegendItem("item1", createIcon());
        OverlayLegend overlay1 = new OverlayLegend("overlay1", 
                Arrays.asList(item1));
        SingleLegendItem item21 = new SingleLegendItem("item21", createIcon());
        SingleLegendItem item22 = new SingleLegendItem("item22", createIcon());
        LegendItem item2 = new ThematicLegendItem("item2", "PieTheme",
                Arrays.asList(item21, item22));
        LegendItem item3 = new SingleLegendItem("item3", createIcon());
        OverlayLegend overlay2 = new OverlayLegend("overlay2", 
                Arrays.asList(item2, item3));
        legendData = new LegendData(Arrays.asList(overlay1, overlay2));
        
    }
    
    /**
     * Only way to comapre real images is to compare their byte arrays.
     */
    @Test
    public void shouldReturnIconsInCorrectOrder()
    {
        List<BufferedImage> icons = legendData.getIcons();
        for(int i=0;i<icons.size();i++)
        {
            compareImageBytes(getByteArray(expectedIcons.get(i)),
                    getByteArray(icons.get(i)));
    }
    }
    
    private void compareImageBytes(byte[] byte1, byte[] bytes2)
    {
        for(int i=0;i<byte1.length;i++)
        {
            compareBytes(byte1[i], bytes2[i]);
        }
    }

    private void compareBytes(byte byte1, byte byte2)
    {
        assertEquals(byte1, byte2);
    }

    private byte[] getByteArray(BufferedImage image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpeg", baos);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        byte[] bytesOut = baos.toByteArray();
        return bytesOut;
    }
    
    private byte[] createIcon()
    {
        BufferedImage image = new BufferedImage(1, 1,
                BufferedImage.TYPE_3BYTE_BGR);
        expectedIcons.add(image);
        return getByteArray(image);
    }

}
