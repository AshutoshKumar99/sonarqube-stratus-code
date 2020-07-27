package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.print.template.ComponentFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TemplateContentHandlerTest
{
    
    private TemplateContentHandler handler;
    
    private DefaultHandler mockHandler;
    
    private ComponentFactory mockComponentFactory;
    
    private Component mockComponent;
    
    private Attributes mockAttrs;

    private DocumentParameters mockParams;

    private FopFactory mockFopFactory;


    @Before
    public void setUp() throws Exception
    {
        mockParams = mock(DocumentParameters.class);
        mockComponentFactory = mock(ComponentFactory.class);
        mockComponent = mock(Component.class);
        when(mockComponentFactory.createComponent(any(QName.class), 
                any(Attributes.class), any(DocumentParameters.class)))
                .thenReturn(mockComponent);
        mockFopFactory = mock(FopFactory.class);
        Fop mockFop = mock(Fop.class);
        when(mockFopFactory.newFop(any(String.class), 
                any(OutputStream.class))).thenReturn(mockFop);
        mockHandler = mock(DefaultHandler.class);
        when(mockFop.getDefaultHandler()).thenReturn(mockHandler);
        handler = new TemplateContentHandler(mockParams, 
                mockComponentFactory, mockFopFactory);
        mockAttrs = createAttribute("attr", "value");
    }
    
    @Test
    public void shouldForwardEventsToFoProcessor() throws Exception
    {
        handler.startDocument();
        Attributes attrs = createAttribute("attr", "value");
        startElement(handler, "someElement", attrs);
        String expectedText = "someText"; 
        handler.characters(expectedText.toCharArray(), 0, 
                expectedText.length());
        endElement(handler, "someElement");
        handler.endDocument();
        InOrder order = inOrder(mockHandler);
        order.verify(mockHandler).startDocument();
        order.verify(mockHandler).startElement(NS_URI, "someElement", 
                "fo:someElement", attrs);
        order.verify(mockHandler).endDocument();
    }
    
    @Test
    public void shouldExpandStratusElements() throws Exception
    {
        generateStratusElementEvents();
        verify(mockComponent).generateSAXEvents(mockHandler);
    }

    @Test
    public void shouldPassDocumentParamsToComponentFactory() throws Exception
    {
        generateStratusElementEvents();
        verify(mockComponentFactory).createComponent(any(QName.class), 
                any(Attributes.class), eq(mockParams));
    }
    
    @Test
    public void shouldPassElementDetailsToComponentFactory() throws Exception
    {
        generateStratusElementEvents();
        QName expectedQName = new QName(TemplateContentHandler.NAMESPACE, 
                "someElement");
        
        verify(mockComponentFactory).createComponent(eq(expectedQName), 
                eq(mockAttrs), any(DocumentParameters.class));
    }
    
    private void generateStratusElementEvents() throws Exception
    {
        String ns = TemplateContentHandler.NAMESPACE;
        handler.startElement(ns, "someElement", "stratus:someElement", 
                mockAttrs);
        handler.endElement(TemplateContentHandler.NAMESPACE, 
                "someElement", "stratus:someElement");
    }
    
    @Test
    public void shouldGenerateDocumentOfTypePdf() throws Exception
    {
        verify(mockFopFactory).newFop(eq("application/pdf"), 
                any(OutputStream.class));
    }
    
    @Test
    public void shouldReturnInputStreamWithOutputFromFop() throws Exception
    {
        ArgumentCaptor<OutputStream> arg = ArgumentCaptor.forClass(
                OutputStream.class);
        verify(mockFopFactory).newFop(any(String.class), arg.capture());
        OutputStream os = arg.getValue();
        String expectedOutput = "someOutput";
        os.write(expectedOutput.getBytes());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = handler.getDocument();
        IOUtils.copy(is, bos);
        assertEquals(expectedOutput, new String(bos.toByteArray()));
    }
    
}
