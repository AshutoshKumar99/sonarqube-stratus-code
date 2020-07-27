package com.pb.stratus.controller.print.template;

import com.pb.stratus.controller.print.DocumentParameters;
import com.pb.stratus.controller.print.Template;
import com.pb.stratus.controller.print.TemplateContentHandler;
import com.pb.stratus.controller.print.TemplateContentHandlerFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.xml.sax.Attributes;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class TemplateRendererTest
{
    
    private TemplateRenderer renderer;
    
    private Template mockTemplate;
    
    private DocumentParameters mockParams;

    private TemplateContentHandler mockHandler;

    private TemplateContentHandlerFactory mockFactory;
    

    @Before
    public void setUp()
    {
        mockFactory = mock(
                TemplateContentHandlerFactory.class);
        mockHandler = mock(
                TemplateContentHandler.class);
        when(mockFactory.createTemplateContentHandler(
                any(DocumentParameters.class))).thenReturn(mockHandler);
        renderer = new TemplateRenderer(mockFactory);
        mockTemplate = new Template("<ns:someElement " 
                + "xmlns:ns=\"someNamespace\">someText</ns:someElement>");
        mockParams = mock(DocumentParameters.class);
    }
    
    @Test
    public void shouldForwardSaxEventsFromTemplateToTemplateContentHandler() 
            throws Exception
    {
        renderer.render(mockTemplate, mockParams);
        InOrder order = inOrder(mockHandler);
        order.verify(mockHandler).startDocument();
        order.verify(mockHandler).startElement(eq("someNamespace"), 
                eq("someElement"), eq("ns:someElement"), 
                any(Attributes.class));
        order.verify(mockHandler).endElement("someNamespace", "someElement", 
                "ns:someElement");
        order.verify(mockHandler).endDocument();
    }
    
    @Test
    public void shouldUseParametersToCreateTemplateContentHandler()
    {
        renderer.render(mockTemplate, mockParams);
        verify(mockFactory).createTemplateContentHandler(mockParams);
    }
    
    public void shouldReturnInputStreamFromTemplateContentHandler()
    {
        InputStream mockStream = mock(InputStream.class);
        when(mockHandler.getDocument()).thenReturn(mockStream);
        InputStream actualStream = renderer.render(mockTemplate, mockParams);
        assertEquals(mockStream, actualStream);
    }
    
}
