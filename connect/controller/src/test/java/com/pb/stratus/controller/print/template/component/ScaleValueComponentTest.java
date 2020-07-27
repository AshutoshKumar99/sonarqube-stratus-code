package com.pb.stratus.controller.print.template.component;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by SA021SH on 11/7/2014.
 */
public class ScaleValueComponentTest {
    private ScaleValueComponent scaleValueComponent;
    private ContentHandler mockHandler;

    private String mockText = "1:15000";


    @Before
    public void setUp()
    {
        scaleValueComponent = new ScaleValueComponent(mockText);
        mockHandler = mock(ContentHandler.class);
    }

    @Test
    public void shouldRenderEmptyStringIfConstructedWithNullValue()
            throws Exception
    {
        scaleValueComponent = new ScaleValueComponent(null);
        scaleValueComponent.generateSAXEvents(mockHandler);
        verify(mockHandler).characters(new char[0], 0, 0);
    }

    @Test
    public void shouldGenerateCorrectText() throws Exception
    {
        scaleValueComponent.generateSAXEvents(mockHandler);
        verify(mockHandler).characters(eq(mockText.toCharArray()), eq(0),
                eq(mockText.length()));
    }
    
}
