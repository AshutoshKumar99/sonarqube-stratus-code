package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.content.FmnResultsCollection;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.util.SaxAssertUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.LinkedList;
import java.util.List;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FmnResultsComponentTest
{
    
    private FmnResultsComponent component;
    
    private ComponentConverter mockConverter;

    private ContentHandler mockContentHandler;

    private InOrder order;

    private FmnResultsCollection mockFmnResults;

    private Component mockComponent;

    private String masterReferenceName = "anything";

    @Before
    public void setUp()
    {
        List<FmnResult> results = new LinkedList<FmnResult>();
        results.add(mock(FmnResult.class));
        results.add(mock(FmnResult.class));
        mockFmnResults = new FmnResultsCollection("title", 
                results);
        mockFmnResults.setTitle("testTitle");
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", MASTER_REFERENCE_ATTR, MASTER_REFERENCE_ATTR,
                "CDATA", masterReferenceName);
        component = new FmnResultsComponent(mockFmnResults, masterReferenceName);
        mockConverter = mock(ComponentConverter.class);
        mockComponent = mock(Component.class);
        when(mockConverter.convertToComponent(any())).thenReturn(
                mockComponent);
        component.setComponentConverter(mockConverter);
        mockContentHandler = mock(ContentHandler.class);
        order = inOrder(mockContentHandler, mockConverter, mockComponent);
    }
    
    @Test
    public void shouldGenerateCorrectTableStructure() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        assertStartElement(TABLE_ELEMENT, createAttribute(
                "width", "100%", 
                "font-size", "10pt", 
                "table-layout", "fixed"));
        assertEmptyElement(TABLE_COLUMN_ELEMENT, createAttribute(
                "column-width", "5%"));
        assertEmptyElement(TABLE_COLUMN_ELEMENT, createAttribute(
                "column-width", "65%"));
        assertEmptyElement(TABLE_COLUMN_ELEMENT, createAttribute(
                "column-width", "30%"));
        assertStartElement(TABLE_BODY_ELEMENT);
        assertEndElement(TABLE_BODY_ELEMENT);
        assertEndElement(TABLE_ELEMENT);
    }
    
    @Test
    public void shouldGenerateTitleInFirstRow() throws SAXException
    {
        component.generateSAXEvents(mockContentHandler);
        assertStartElement(TABLE_BODY_ELEMENT);
        assertStartElement(TABLE_ROW_ELEMENT, createAttribute(
                "background-color", "#EEEEEE", 
                "font-weight", "bold"));
        assertStartElement(TABLE_CELL_ELEMENT, createAttribute(
                "number-columns-spanned", "3"));
        assertStartElement("block", createAttribute(
                "padding", "1mm"));
        String expectedTitle = mockFmnResults.getTitle();
        verify(mockContentHandler).characters(expectedTitle.toCharArray(), 0, 
                expectedTitle.length());
        assertEndElement(BLOCK_ELEMENT);
        assertEndElement(TABLE_CELL_ELEMENT);
        assertEndElement(TABLE_ROW_ELEMENT);
        assertEndElement(TABLE_BODY_ELEMENT);
    }

    @Test
    public void shouldGenerateCorrectPageSequenceElement() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        assertStartElement(PAGE_SEQUENCE_ELEMENT,
                createAttribute(MASTER_REFERENCE_ATTR, masterReferenceName));
        assertStartElement(FLOW_ELEMENT,
                createAttribute(FLOW_NAME_ATTR, "xsl-region-body"));
        assertStartElement(TABLE_ELEMENT);
        assertEndElement(TABLE_ELEMENT);
        assertEndElement(FLOW_ELEMENT);
        assertEndElement(PAGE_SEQUENCE_ELEMENT);
    }
    
    @Test
    public void shouldGenerateOneRowPerFmnResult() throws SAXException
    {
        Component mockComponent2 = mock(Component.class);
        when(mockConverter.convertToComponent(any())).thenReturn(
                mockComponent).thenReturn(mockComponent2);
        order = inOrder(mockContentHandler, mockConverter, mockComponent, 
                mockComponent2);
        component.generateSAXEvents(mockContentHandler);
        assertStartElement(TABLE_ROW_ELEMENT);
        assertEndElement(TABLE_ROW_ELEMENT);
        verify(mockConverter).convertToComponent(mockFmnResults
                .getFmnResults().get(0));
        verify(mockConverter).convertToComponent(mockFmnResults
                .getFmnResults().get(1));
        order.verify(mockComponent).generateSAXEvents(mockContentHandler);
        order.verify(mockComponent2).generateSAXEvents(mockContentHandler);
    }
    
    private void assertStartElement(String elementName) throws SAXException
    {
        SaxAssertUtils.assertStartElement(order, mockContentHandler, 
                elementName);
    }
    
    private void assertStartElement(String elementName, Attributes attrs) 
            throws SAXException
    {
        SaxAssertUtils.assertStartElement(order, mockContentHandler, 
                elementName, attrs);
    }
    
    private void assertEndElement(String elementName) throws SAXException
    {
        order.verify(mockContentHandler).endElement((String) any(), 
                eq(elementName), (String) any());
    }
    
    private void assertEmptyElement(String elementName, Attributes attrs) 
            throws SAXException
    {
        assertStartElement(elementName, attrs);
        //XXX Is there a way to check intermediate interactions?
        assertEndElement(elementName);
    }
    
    @Test
    public void shouldDoNothingIfNoFmnResultsAreSpecified() throws Exception
    {
        component = new FmnResultsComponent(null, null);
        component.generateSAXEvents(mockContentHandler);
        verifyZeroInteractions(mockContentHandler);
    }
}
