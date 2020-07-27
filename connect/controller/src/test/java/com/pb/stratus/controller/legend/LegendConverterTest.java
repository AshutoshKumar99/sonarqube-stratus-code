package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.GetNamedMapLegendsResponse;
import com.pb.stratus.util.JaxbUtil;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.pb.stratus.util.TestUtils.assertEqualImages;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LegendConverterTest
{
    
    private LegendConverter legendConverter;
    
    private String mockTitle;

    @Before
    public void setUp()
    {
        mockTitle = "testOverlay";
        legendConverter = new LegendConverter();
    }
    
    @Test
    public void shouldCreateLegendWithCorrectTitle()
    {
        GetNamedMapLegendsResponse mockResponse 
                = mock(GetNamedMapLegendsResponse.class);
        OverlayLegend legend = legendConverter.convert(mockTitle, mockResponse);
        assertEquals(mockTitle, legend.getTitle());
    }
    
    @Test
    public void shouldConvertLegendWithOneRowToSingleLegendItem() 
            throws Exception
    {
        OverlayLegend legend = legendConverter.convert(mockTitle, 
                createMockResponse("commonLegend.xml"));
        LegendItem item = legend.getLegendItems().get(1);
        assertTrue(item instanceof SingleLegendItem);
        assertEquals("ConservationAreas", item.getTitle());
    }
    
    @Test
    public void shouldConvertMultipleRowsToThematicLegend() throws Exception
    {
        OverlayLegend legend = legendConverter.convert(mockTitle, 
                createMockResponse("commonLegend.xml"));
        assertEquals(ThematicLegendItem.class, 
                legend.getLegendItems().get(0).getClass());
        ThematicLegendItem item = (ThematicLegendItem) 
                legend.getLegendItems().get(0);
        assertEquals("ListedBuildings", item.getTitle());
        assertEquals(3, item.getLegendItems().size());
    }
    
    @Test
    public void shouldPreferStyleOverrideToCartographicLegends() 
            throws Exception
    {
        OverlayLegend legend = legendConverter.convert(mockTitle, 
                createMockResponse("allLegendTypes.xml"));
        List<LegendItem> items = legend.getLegendItems();
        assertEquals(1, items.size());
        assertEquals(ThematicLegendItem.class, items.get(0).getClass());
    }
    
    private GetNamedMapLegendsResponse createMockResponse(String fileName) 
            throws Exception
    {
        return JaxbUtil.createObject(fileName, 
                GetNamedMapLegendsResponse.class, GetLegendTaskTest.class);
    }
    
    @Test
    public void shouldReadIconData() throws Exception
    {
        OverlayLegend legend = legendConverter.convert(mockTitle, 
                createMockResponse("commonLegend.xml"));
        ThematicLegendItem item = (ThematicLegendItem) legend.getLegendItems().get(0);
        BufferedImage actualIcon = item.getLegendItems().get(0).getIcon();
        BufferedImage expectedIcon = ImageIO.read(
                LegendConverterTest.class.getResourceAsStream("icon.png"));
        assertEqualImages(expectedIcon, actualIcon);
    }
    
}
