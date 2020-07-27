package com.pb.stratus.controller.print.template.component;

import org.junit.Test;
import org.xml.sax.ContentHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;


public class NullComponentTest
{

    @Test
    public void shouldNotDoAnything() throws Exception
    {
        ContentHandler mockHandler = mock(ContentHandler.class);
        NullComponent comp = NullComponent.INSTANCE;
        comp.generateSAXEvents(mockHandler);
        verifyZeroInteractions(mockHandler);
    }
}
