package com.pb.stratus.controller.csv;


import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class BaseCsvConverterTest
{

    BaseCsvConverter baseCsvConverter;

    @Before
    public void setUp()
    {
        baseCsvConverter = getBaseCsvConverter();

    }

    @Test
    public void testNullFieldSerialization()
    {
        String expectedResult = "";
        baseCsvConverter.addField(null, ',');
        String actualResult = baseCsvConverter.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testNormalFieldSerialization()
    {
        String dummyField = "hello23";
        String expectedResult = "hello23";
        baseCsvConverter.addField(dummyField, ',');
        String actualResult = baseCsvConverter.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFieldContainingDoubleQuote()
    {
        String dummyField = "hello\"2\"3";
        String expectedResult = "\"hello\"\"2\"\"3\"";
        baseCsvConverter.addField(dummyField, ',');
        String actualResult = baseCsvConverter.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testFieldContainingSeparatorCharacter()
    {
        String dummyField = "hello,123";
        String expectedResult = "\"hello,123\"";
        baseCsvConverter.addField(dummyField, ',');
        String actualResult = baseCsvConverter.toString();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSerializationOfRow()
    {
        List<String> dummyFields = new ArrayList<String>();
        dummyFields.add("field1");
        dummyFields.add("field2");
        dummyFields.add("field3");
        dummyFields.add("field4");
        baseCsvConverter.addRow(dummyFields, ',', "\n");
        String actualResult = baseCsvConverter.toString();
        String expectedResult= "field1,field2,field3,field4\n";
        assertEquals(expectedResult, actualResult);
    }

    private BaseCsvConverter getBaseCsvConverter()
    {
        return new BaseCsvConverter(){

             public String getCsv(char separator, String lineBreak)
             {
                 return sb.toString();
             }
        };
    }

}
