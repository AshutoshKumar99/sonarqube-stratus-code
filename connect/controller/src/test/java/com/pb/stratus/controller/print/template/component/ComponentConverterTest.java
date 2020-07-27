package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.template.Component;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class ComponentConverterTest
{
    
    private ComponentConverter converter;
    
    @Before
    public void setUp()
    {
        converter = new ComponentConverter();
    }
    
    @Test
    public void shouldReturnImageComponentOnBufferedImage()
    {
        BufferedImage mockImage = new BufferedImage(150, 300, 
                BufferedImage.TYPE_3BYTE_BGR);
        Component comp = converter.convertToComponent(mockImage); 
        assertEquals(comp, new ImageComponent(mockImage, "2.54cm", "5.08cm"));
    }
    
    @Test
    public void shouldReturnTextComponentInAllOtherCases()
    {
        Object o = new Object();
        Component comp = converter.convertToComponent(o); 
        assertEquals(comp, new TextComponent(o.toString()));
    }
    
    @Test
    public void shouldReturnNullComponentOnNullObject()
    {
        Component comp = converter.convertToComponent(null);
        assertSame(comp, NullComponent.INSTANCE);
    }
    
    @Test
    public void shoudlReturnFmnResultComponentOnFmnResult()
    {
        FmnResult mockResult = mock(FmnResult.class);
        Component comp = converter.convertToComponent(mockResult);
        assertEquals(new FmnResultComponent(mockResult), comp);
    }

}
