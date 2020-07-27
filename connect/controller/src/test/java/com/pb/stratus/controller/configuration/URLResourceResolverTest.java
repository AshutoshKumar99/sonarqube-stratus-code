package com.pb.stratus.controller.configuration;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class URLResourceResolverTest
{
    private URLResourceResolver resolver;
    
    private URLBuilder urlBuilder;
    
    private File testFile;
    
    private byte[] mockContent;
    
    private URL mockURL;
    
    private String testFileName = "test.txt";
    
    @Before
    public void setUp() throws Exception
    {   
        testFile = File.createTempFile("test", ".txt");
        mockContent = new byte[] {1, 2, 3};
        ByteArrayInputStream bis = new ByteArrayInputStream(mockContent);
        FileOutputStream fos = new FileOutputStream(testFile);
        IOUtils.copy(bis, fos);
        fos.close();
        mockURL = testFile.toURL();
        urlBuilder = mock(URLBuilder.class);
        when(urlBuilder.buildFullURL(testFileName)).thenReturn(mockURL);
        resolver = new URLResourceResolver(urlBuilder);
    }
    
    @After
    public void tearDown()
    {
        testFile.delete();
    }
    
    @Test
    public void shouldReturnFullURLOfResource()
    {
        URL u = resolver.getResource(testFileName);
        assertEquals(mockURL, u);
    }
    
    @Test
    public void shouldReturnNullUrlIfResourceDoesntExist()
    {
        assertNull(resolver.getResource("/some/non/existent/resource"));
    }

    @Test
    public void shouldStreamContentsOfResource() throws Exception
    {
        InputStream is = resolver.getResourceAsStream(testFileName);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        assertArrayEquals(mockContent, bos.toByteArray());
        is.close();
        verify(urlBuilder).buildFullURL(testFileName);
    }
    
    @Test
    public void shouldReturnLastModifiedDateOfResource()
    {
        assertEquals(testFile.lastModified(), 
                resolver.getLastModified(testFileName));
    }
    
    @Test
    public void shouldReturnNegativeLastModifiedDateIfResourceDoesntExist()
    {
        assertEquals(-1, resolver.getLastModified(
                "/some/non/existent/resource"));
    }
 
    @Test
    public void shouldReturnUrlConnectionToResource() throws Exception
    {
        URLConnection conn = resolver.getResourceConnection(testFileName);
        // assume last modified date is good enough to ensure it's the same
        // resource
        assertEquals(testFile.lastModified(), conn.getLastModified());
    }

}
