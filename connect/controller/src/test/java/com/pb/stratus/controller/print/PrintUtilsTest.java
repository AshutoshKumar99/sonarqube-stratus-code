package com.pb.stratus.controller.print;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PrintUtilsTest
{

    @Before
    public void setUp() throws Exception
    {}

    @After
    public void tearDown() throws Exception
    {}

    @Test
    public void testProcessMapName()
    {
        String mapName = "/customer/baseMap";
        String actual = PrintUtils.processMapName(mapName);
        assertEquals("baseMap", actual);
    }

    @Test
    public void testProcessMapNameNoCustomer()
    {
        String mapName = "/baseMap";
        String actual = PrintUtils.processMapName(mapName);
        assertEquals("baseMap", actual);
    }
    
    @Test
    public void testProcessMapNameNoLeadingSlash()
    {
        String mapName = "baseMap";
        String actual = PrintUtils.processMapName(mapName);
        assertEquals(mapName, actual);
    }
    
    @Test
    public void testProcessMapNameNull()
    {
        String mapName = null;
        String actual = PrintUtils.processMapName(mapName);
        assertEquals(mapName, actual);
    }
    
    @Test
    public void testProcessMapNameEmpty()
    {
        String mapName = "";
        String actual = PrintUtils.processMapName(mapName);
        assertEquals(mapName, actual);
    }

    @Test
    public void testConstructCurrentRequestBaseUrl() throws IOException
    {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setServerName("testHost");
        mockRequest.setScheme("https");
        mockRequest.setServerPort(8765);
        String actual = PrintUtils.constructCurrentRequestBaseUrl(mockRequest);
        assertEquals("https://testHost:8765", actual);
}
}
