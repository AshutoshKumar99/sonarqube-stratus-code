package com.pb.stratus.controller.info;

import com.mapinfo.midev.service.featurecollection.v1.*;
import com.mapinfo.midev.service.featurecollection.v1.Feature;
import com.pb.stratus.controller.i18n.LocaleResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class FeatureProcessorTest
{
    @Before
    public void setUp() throws Exception
    {
        LocaleResolver.setLocale(new Locale("en"));
    }
    
    @After
    public void tearDown() throws Exception
    {
        LocaleResolver.setLocale(null);
    }
    
    @Test
    public void testGetAttributeValueNullValue()
    {
        String value = getValue(new DateValue());
        assertEquals("", value);
    }
    
    @Test
    public void testGetAttributeValueBooleanValue()
    {
        BooleanValue attr = new BooleanValue();
        attr.setValue(Boolean.TRUE);
        
        String value = getValue(attr);
        assertEquals("true", value);
    }
    
    @Test
    public void testGetAttributeValueDateValue() throws Exception
    {
        DateValue attr = new DateValue();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(0);
        XMLGregorianCalendar cal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(gcal);
        
        String dateStr = gcal.getTime().toString();
        attr.setValue(cal);
        
        String value = getValue(attr);
        assertEquals("Jan 1, 1970", value);
    }
     @Test
    public void testGetAttributeValueDateTimeValue() throws Exception
    {
        DateTimeValue attr = new DateTimeValue();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(0);
        XMLGregorianCalendar cal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(gcal);
        attr.setValue(cal);

        String value = getValue(attr);
        assertEquals("Jan 1, 1970 12:00 AM", value);
    }
     @Test
    public void testGetAttributeValueTimeValue() throws Exception
    {
        TimeValue attr = new TimeValue();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(0);
        XMLGregorianCalendar cal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(gcal);
        attr.setValue(cal);

        String value = getValue(attr);
        assertEquals("12:00:00 AM", value);
    }
    
    @Test
    public void testGetAttributeValueBinaryValue() throws Exception
    {
        BinaryValue attr = new BinaryValue();
        attr.setValue(new byte[] {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89});
        
        String value = getValue(attr);
        assertEquals("AQECAwUIDRUiN1k=", value);
    }
    
    private String getValue(AttributeValue value)
    {
        Feature feature = createFeature(value); 
        return FeatureProcessor.getAttributeValue(feature, 0);
        
    }
    
    private Feature createFeature(AttributeValue value)
    {
        Feature feature 
                = new Feature();
        List<AttributeValue> values = feature.getAttributeValue();
        values.add(value);
        return feature;
    }

}
