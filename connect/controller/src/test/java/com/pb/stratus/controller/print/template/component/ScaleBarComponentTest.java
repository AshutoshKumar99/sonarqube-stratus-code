package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.print.Distance;
import com.pb.stratus.controller.print.DistanceUnit;
import com.pb.stratus.controller.print.ScaleBar;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.util.SaxAssertUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class ScaleBarComponentTest {
       
      private InOrder order;
      private ContentHandler mockContentHandler;
      private ScaleBarComponent component;
      private Component mockComponent;
    private Locale oldLocale;
   
    @Before
    public void setUp() 
    {
        oldLocale = LocaleResolver.getLocale();
        LocaleResolver.setLocale(new Locale("en"));
        mockContentHandler = mock(ContentHandler.class);
        ScaleBar scaleBar = new ScaleBar(
                new Distance(DistanceUnit.M, 5), 0.05);
        component = new ScaleBarComponent(scaleBar, null);
        mockComponent = mock(Component.class);
        order = inOrder(mockContentHandler, mockComponent);
    }
    
    @After
    public void tearDown()
    {
        LocaleResolver.setLocale(oldLocale);
    }

    @Test
    public void testScaleBarSvgElementIsCorrectOrder() throws Exception
    {    
        component.generateSAXEvents(mockContentHandler);  	
        assertStartElement("instream-foreign-object");
        assertStartElement("svg");
        assertStartElement("g");
        assertStartElement("polyline");
        assertEndElement("polyline");
        assertStartElement("line");
        assertEndElement("line");
        assertStartElement("text");
        assertEndElement("text");
        assertEndElement("g");
        assertEndElement("svg");
        assertEndElement("instream-foreign-object");
    }

    private void assertStartElement(String elementName) throws SAXException
    {
        SaxAssertUtils.assertStartElement(order, mockContentHandler, elementName);
    }
    
    private void assertEndElement(String elementName) throws SAXException
    {
        order.verify(mockContentHandler).endElement((String) any(), 
                eq(elementName), (String) any());
    }    
}
