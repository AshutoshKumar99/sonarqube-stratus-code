package com.pb.stratus.controller.info;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InfoRowTest
{

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }
 

    @Test
    public void testGetAdditionalFields()
    {
        InfoField field1= new InfoField("c1", "v1");
        InfoField field2= new InfoField("c2", "v2");
        List<InfoField> fields=  new ArrayList<InfoField>(){};
        fields.add(field1);
        fields.add(field2);
        InfoRow row = new InfoRow("title", "key", "description", "link", "image",fields);        
        assertEquals(2,row.getAdditionalFields().size());      
    }

}
