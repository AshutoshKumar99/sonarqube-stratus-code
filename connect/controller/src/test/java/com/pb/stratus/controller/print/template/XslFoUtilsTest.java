package com.pb.stratus.controller.print.template;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class XslFoUtilsTest
{
    
    private ContentHandler mockHandler;
    
    @Before
    public void setUp()
    {
        mockHandler = mock(ContentHandler.class);
    }
    
    @Test
    public void startElementShouldPassCorrectLocalName() throws Exception
    {
        XslFoUtils.startElement(mockHandler, "someElement");
        verify(mockHandler).startElement((String) any(), eq("someElement"), 
                (String) any(), (Attributes) any());
        
    }
     
    @Test
    public void shouldCreateEmptyAttributesOnStartElementWithoutAttrs() 
            throws Exception
    {
        XslFoUtils.startElement(mockHandler, "someElement");
        ArgumentCaptor<Attributes> arg = ArgumentCaptor
                .forClass(Attributes.class);
        verify(mockHandler).startElement((String) any(), (String) any(), 
                (String) any(), arg.capture());
        Attributes attrs = arg.getValue();
        assertEquals(0, attrs.getLength());
    }
    
    @Test
    public void shouldPassCorrectAttributesOnStartElement() throws Exception
    {
        AttributesImpl attrs = new AttributesImpl();
        XslFoUtils.startElement(mockHandler, "someElement", attrs);
        verify(mockHandler).startElement((String) any(), (String) any(), 
                (String) any(), eq(attrs));
    }
    
    //XXX more to go

}
