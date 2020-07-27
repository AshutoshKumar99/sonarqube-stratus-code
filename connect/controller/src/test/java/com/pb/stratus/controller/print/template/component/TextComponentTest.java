package com.pb.stratus.controller.print.template.component;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TextComponentTest
{
    
    private TextComponent textComponent;
    
    private ContentHandler mockHandler;
    
    private String mockText = "Some text";
    
    @Before
    public void setUp()
    {
        textComponent = new TextComponent(mockText);
        mockHandler = mock(ContentHandler.class);
    }

    @Test
    public void shouldRenderEmptyStringIfConstructedWithNullValue() 
            throws Exception
    {
        textComponent = new TextComponent(null);
        textComponent.generateSAXEvents(mockHandler);
        verify(mockHandler).characters(new char[0], 0, 0);
    }
    
    @Test
    public void shouldGenerateCorrectText() throws Exception
    {
        textComponent.generateSAXEvents(mockHandler);
        verify(mockHandler).characters(eq(mockText.toCharArray()), eq(0), 
                eq(mockText.length()));
    }
    
}
