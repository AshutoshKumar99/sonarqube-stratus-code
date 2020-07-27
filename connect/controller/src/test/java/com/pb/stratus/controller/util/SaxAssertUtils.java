package com.pb.stratus.controller.util;

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class SaxAssertUtils
{
    public static void assertStartElement(InOrder order, ContentHandler mock, 
            String elementName) throws SAXException
    {
        order.verify(mock).startElement((String) any(), 
                eq(elementName), (String) any(), (Attributes) any());
    }
    
    public static void assertStartElement(InOrder order, ContentHandler mock,
            String elementName, Attributes expectedAttrs) throws SAXException
    {
        ArgumentCaptor<Attributes> arg = ArgumentCaptor.forClass(
                Attributes.class);
        order.verify(mock).startElement((String) any(), 
                eq(elementName), (String) any(), arg.capture());
        Attributes actualAttrs = arg.getValue();
        assertContainsAllAttributes(expectedAttrs, actualAttrs);
    }
    
    public static void assertContainsAllAttributes(Attributes expected, 
            Attributes actual)
    {
        for (int i = 0; i < expected.getLength(); i++)
        {
            String name = expected.getLocalName(i);
            String expectedValue = expected.getValue(name);
            String actualValue = actual.getValue(name);
            assertEquals("Wrong value for attribute '" + name + "'", 
                    expectedValue, actualValue);
        }
    }
    
    public static void assertEndElement(InOrder order, ContentHandler mock, 
            String elementName) throws SAXException
    {
        order.verify(mock).endElement((String) any(), 
                eq(elementName), (String) any());
    }
}
