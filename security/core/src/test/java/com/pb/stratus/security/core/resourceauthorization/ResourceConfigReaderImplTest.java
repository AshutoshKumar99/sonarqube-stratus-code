package com.pb.stratus.security.core.resourceauthorization;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: sh003bh
 * Date: 11/14/11
 * Time: 11:12 AM
 */
public class ResourceConfigReaderImplTest {

    private ResourceAuthorizationReaderImpl resourceConfigReader;
    File mockFile;
    InputStream mockInputStream;

    @Before
    public void setUp() throws FileNotFoundException {
        resourceConfigReader = new ResourceAuthorizationReaderImpl("someBasePath",
                "someCustomerName");
        resourceConfigReader = spy(resourceConfigReader);
        mockFile = mock(File.class);
        doReturn(mockFile).when(resourceConfigReader).getFile(any(String.class));
        mockInputStream = mock(InputStream.class);
        doReturn(mockInputStream).when(resourceConfigReader).getConfigFile(any(File.class));
    }

    @Test
    public void shouldReturnFileForGivenPath() {

        File actualFile = resourceConfigReader.getFile("somefile");
        assertEquals(mockFile, actualFile);
    }

    @Test
    public void testGetConfigFile() throws FileNotFoundException {
        InputStream actualInputStream =
                resourceConfigReader.getConfigFile(new File("somefile"));
        assertEquals(mockInputStream, actualInputStream);
    }
}
