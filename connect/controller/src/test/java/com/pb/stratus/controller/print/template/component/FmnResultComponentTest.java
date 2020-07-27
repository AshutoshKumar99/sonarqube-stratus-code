package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.Marker;
import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.image.ScalableImage;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.util.SaxAssertUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
public class FmnResultComponentTest
{
    
    private InOrder order;
    
    private ContentHandler mockContentHandler;

    private FmnResultComponent component;

    private FmnResult mockResult;
    
    private ComponentConverter mockConverter;
    
    private Component mockComponent;
    
    @Before
    public void setUp()
    {
        setUpMockFmnResult();
        setUpComponent();
        mockContentHandler = mock(ContentHandler.class);
        order = inOrder(mockContentHandler, mockConverter, mockComponent);
    }
    
    private void setUpMockFmnResult()
    {
        mockResult = mock(FmnResult.class);
        when(mockResult.getTitle()).thenReturn("testTitle");
        when(mockResult.getDescription()).thenReturn("testDescription");
        when(mockResult.getKeyValue()).thenReturn("testKeyValue");
        when(mockResult.getLink()).thenReturn("testLink");
        BufferedImage image = new BufferedImage(1, 2, 
                BufferedImage.TYPE_3BYTE_BGR);
        when(mockResult.getImage()).thenReturn(image);
        BufferedImage markerIcon = new BufferedImage(2, 3, 
                BufferedImage.TYPE_3BYTE_BGR);
        Marker marker = new Marker(markerIcon, new Point(1, 2), 
                new Point(123, 456)); 
        when(mockResult.getMarker()).thenReturn(marker);
    }
    
    private void setUpComponent()
    {
        mockConverter = mock(ComponentConverter.class);
        mockComponent = mock(Component.class);
        when(mockConverter.convertToComponent(any())).thenReturn(
                mockComponent);
        component = new FmnResultComponent(mockResult);
        component.setComponentConverter(mockConverter);
    }

    public void shouldGenerateTableRowWithCorrectAttributes() 
            throws SAXException
    {
        component.generateSAXEvents(mockContentHandler);
        assertStartElement(TABLE_ROW_ELEMENT, createAttribute("border-bottom", 
                "solid 1px #D4D4D4"));
        assertEndElement(TABLE_ROW_ELEMENT);
    }
    
    
    @Test
    public void shouldGenerateMarkerImageInLastCell() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        assertStartElement(TABLE_ROW_ELEMENT);
        assertNumberOfTableCellsAndMakeFutureVerificationsFromThere(2);
        assertStartElement(TABLE_CELL_ELEMENT);
        assertStartElement(BLOCK_ELEMENT);
        verify(mockConverter).convertToComponent(
                mockResult.getMarker().getIcon());
        order.verify(mockComponent).generateSAXEvents(mockContentHandler);
        assertEndElement(BLOCK_ELEMENT);
        assertEndElement(TABLE_CELL_ELEMENT);
        assertEndElement(TABLE_ROW_ELEMENT);
    }
    
    @Test
    public void shouldGenerateFmnImage() throws Exception
    {
        Component mockComponent2 = mock(Component.class);
        when(mockConverter.convertToComponent(
                mockResult.getImage())).thenReturn(mockComponent2);
        order = inOrder(mockContentHandler, mockConverter, mockComponent2);
        component.generateSAXEvents(mockContentHandler);
        assertStartElement(TABLE_ROW_ELEMENT);
        assertStartElement(TABLE_CELL_ELEMENT);
        assertEndElement(TABLE_CELL_ELEMENT);
        assertStartElement(TABLE_CELL_ELEMENT);
        assertEndElement(TABLE_CELL_ELEMENT);
        assertStartElement(TABLE_CELL_ELEMENT);
        assertStartElement(BLOCK_ELEMENT, createAttribute("padding", "1mm"));
        order.verify(mockComponent2).generateSAXEvents(mockContentHandler);
        assertEndElement(BLOCK_ELEMENT);
        assertEndElement(TABLE_CELL_ELEMENT);
        assertEndElement(TABLE_ROW_ELEMENT);
    }
    
    @Test
    public void shouldSkipImageIfNoImageSpecified() throws Exception
    {
        when(mockResult.getImage()).thenReturn(null);
        component.generateSAXEvents(mockContentHandler);
        BufferedImage img = mockResult.getMarker().getIcon();
        verify(mockConverter).convertToComponent(img);
        verifyNoMoreInteractions(mockConverter);
    }
    
    @Test
    public void shouldStillRenderTableCellIfNoImageSpecified() throws Exception
    {
        when(mockResult.getImage()).thenReturn(null);
        component.generateSAXEvents(mockContentHandler);
        assertNumberOfTableCellsAndMakeFutureVerificationsFromThere(3);
    }
    
    private void assertNumberOfTableCellsAndMakeFutureVerificationsFromThere(
            int num) throws SAXException
    {
        for (int i = 0; i < num; i++)
        {
            assertStartElement(TABLE_CELL_ELEMENT);
            assertStartElement(BLOCK_ELEMENT);
            assertEndElement(BLOCK_ELEMENT);
            assertEndElement(TABLE_CELL_ELEMENT);
        }
    }
    
    @Test
    public void shouldGenerateTitle() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        Attributes attrs = createAttribute("padding", "1mm", 
                "font-weight", "bold");
        assertTextGeneratedInSecondCell(mockResult.getTitle(), attrs, 0);
    }
    
    @Test
    public void shouldGenerateKeyValue() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        Attributes attrs = createAttribute("padding", "1mm", 
                "color", "#666666");
        assertTextGeneratedInSecondCell(mockResult.getKeyValue(), attrs, 1);
    }
    
    @Test
    public void shouldGenerateLink() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        StringBuilder b = new StringBuilder("url('");
        b.append(mockResult.getLink());
        b.append("')");
        Attributes attrs = createAttribute("external-destination", b.toString(),
                "text-decoration", "underline", "color", "#3366CC");
        assertTextGeneratedInSecondCell(mockResult.getLink(), attrs, 2);
    }
    
    @Test
    public void shouldGenerateDescription() throws Exception
    {
        component.generateSAXEvents(mockContentHandler);
        Attributes attrs = createAttribute("padding", "1mm");
        assertTextGeneratedInSecondCell(mockResult.getDescription(), attrs, 3);
    }
    
    private void assertTextGeneratedInSecondCell(String expectedText, 
            Attributes expectedAttributes, int positionInCell) 
            throws SAXException
    {
        assertNumberOfTableCellsAndMakeFutureVerificationsFromThere(1);
        assertStartElement(TABLE_CELL_ELEMENT);
        if (positionInCell != 2)
        {
            for (int i = 0; i < positionInCell; i++)
            {
               assertStartElement(BLOCK_ELEMENT);
            }
            assertStartElement(BLOCK_ELEMENT, expectedAttributes);
            order.verify(mockContentHandler).characters(
                expectedText.toCharArray(), 0, expectedText.length());
            assertEndElement(BLOCK_ELEMENT);
        }
        else if (positionInCell == 2)
        {
            // test the link element
            assertStartElement(BLOCK_ELEMENT);
            assertStartElement(BASIC_LINK_ELEMENT, expectedAttributes);
             order.verify(mockContentHandler).characters(
                expectedText.toCharArray(), 0, expectedText.length());
            assertEndElement(BASIC_LINK_ELEMENT);
            assertEndElement(BLOCK_ELEMENT);
        }
        assertEndElement(TABLE_CELL_ELEMENT);
    }
    
    private void assertStartElement(String elementName) throws SAXException
    {
        SaxAssertUtils.assertStartElement(order, mockContentHandler, elementName);
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
    
    @Test
    public void shouldScaleImagesIfTooBig() throws Exception
    {
        component = spy(component);
        ScalableImage mockScalabaleImage = mock(ScalableImage.class);
        BufferedImage mockImage = mock(BufferedImage.class);
        when(mockScalabaleImage.getScaledImage()).thenReturn(mockImage);
        doReturn(mockScalabaleImage).when(component).createScaleableImage(
                (BufferedImage) any());
        
        component.generateSAXEvents(mockContentHandler);
        
        verify(mockScalabaleImage).setMaxSideLength(100);
        verify(component).createScaleableImage(mockResult.getImage());
        verify(mockConverter).convertToComponent(mockImage);
    }
    
    
    //XXX improve the following test cases. ATM they don't care which field 
    //    isn't set
    @Test
    public void shouldSkipTitleIfNoTitleSpecified() throws Exception
    {
        when(mockResult.getTitle()).thenReturn(null);
        generateEvensAndAssertCharactersWrittenThreeTimes();
    }
    
    @Test
    public void shouldSkipDescriptionIfNoDescriptionSpecified() 
            throws Exception
    {
        when(mockResult.getDescription()).thenReturn(null);
        generateEvensAndAssertCharactersWrittenThreeTimes();
    }
    
    @Test
    public void shouldSkipKeyValueIfNoKeyValueSpecified() throws Exception
    {
        when(mockResult.getKeyValue()).thenReturn(null);
        generateEvensAndAssertCharactersWrittenThreeTimes();
    }
    
    @Test
    public void shouldSkipLinkIfNoLinkSpecified() throws Exception
    {
        when(mockResult.getLink()).thenReturn(null);
        generateEvensAndAssertCharactersWrittenThreeTimes();
    }
    
    private void generateEvensAndAssertCharactersWrittenThreeTimes() 
            throws SAXException
    {
        component.generateSAXEvents(mockContentHandler);
        verify(mockContentHandler, times(3)).characters((char[]) any(),
                anyInt(), anyInt());
    }
    
}

