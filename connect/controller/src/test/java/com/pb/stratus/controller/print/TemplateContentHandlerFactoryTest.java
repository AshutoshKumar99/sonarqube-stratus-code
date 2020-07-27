package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.template.ComponentFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.junit.Test;
import org.xml.sax.helpers.DefaultHandler;

import java.io.OutputStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemplateContentHandlerFactoryTest
{
    
    @Test
    public void shouldCreateExpectedTemplateContentHandler() throws Exception
    {
        ComponentFactory mockComponentFactory = mock(ComponentFactory.class);
        FopFactory mockFopFactory = mock(FopFactory.class);
        Fop mockFop = mock(Fop.class);
        when(mockFopFactory.newFop(any(String.class), any(OutputStream.class)))
                .thenReturn(mockFop);
        DefaultHandler mockHandler = mock(DefaultHandler.class);
        when(mockFop.getDefaultHandler()).thenReturn(mockHandler);
        
        DocumentParameters mockParams = mock(DocumentParameters.class);
        TemplateContentHandlerFactory factory 
                = new TemplateContentHandlerFactory(mockComponentFactory, 
                        mockFopFactory);
        TemplateContentHandler expectedHandler = new TemplateContentHandler(
                mockParams, mockComponentFactory, mockFopFactory);
        TemplateContentHandler actualHandler = factory
                .createTemplateContentHandler(mockParams);
        assertTrue(expectedHandler.isEquivalent(actualHandler));
        
    }

}
