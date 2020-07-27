package com.pb.stratus.core.configuration;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FileSystemConfigReaderTest
{
    ConfigFileTypeResolver mockResolver;

    FileSystemConfigReader configReader;

    private InputStream mockInputStream;

    @Before
    public void setUp() throws Exception
    {
        mockResolver = mock(ConfigFileTypeResolver.class);
        when(mockResolver.resolve(any(String.class),
                any(ConfigFileType.class))).thenReturn(
                "some/resolvedPath");
        configReader = new FileSystemConfigReader(mockResolver, "someBasePath",
                "someCustomerName");
        configReader = spy(configReader);
        mockInputStream = mock(InputStream.class);
        doReturn(mockInputStream).when(configReader).createInputStream(
                any(String.class));
        //XXX we should also test the mocked up factory method, but it's so
        //    simple, it's unlikely to break.
    }

    @Test
    public void shouldResolveFileNameThroughResolver() throws Exception
    {
        configReader.getConfigFile("someFile", ConfigFileType.LOCATOR);
        verify(mockResolver).resolve("someFile", ConfigFileType.LOCATOR);
    }

    @Test
    public void shouldGetCorrectConfigFile() throws Exception
    {
        InputStream actualInputStream = configReader.getConfigFile("someFile",
                ConfigFileType.CATALOG);
        assertEquals(mockInputStream, actualInputStream);
        String expectedPath = "someBasePath" + File.separator
                + "someCustomerName" + File.separator + "some"
                + File.separator + "resolvedPath";
        verify(configReader).createInputStream(expectedPath);
    }

    @Test
    public void shouldGetCorrectConfigFileWithoutType() throws Exception
    {
        InputStream actualInputStream = configReader.getConfigFile(
                "some/path/to/file.asdf");
        assertEquals(mockInputStream, actualInputStream);
        String expectedPath = "someBasePath" + File.separator
                + "someCustomerName" + File.separator + "some"
                + File.separator + "path" + File.separator + "to"
                + File.separator + "file.asdf";
        verify(configReader).createInputStream(expectedPath);
    }

}
