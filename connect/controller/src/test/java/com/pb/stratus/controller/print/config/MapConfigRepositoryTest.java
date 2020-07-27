package com.pb.stratus.controller.print.config;

import com.pb.stratus.controller.wmsprofile.WMSProfileParser;
import com.pb.stratus.core.configuration.ConfigReader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MapConfigRepositoryTest
{
    
    private MapConfigRepository repo;
    
    private ConfigReader mockConfigReader;

    private MapConfigParser mockParser;

    private WMSProfileParser mockWMSProfileParser;

    @Before
    public void setUp()
    {
        mockConfigReader = mock(ConfigReader.class);
        mockParser = mock(MapConfigParser.class);
        mockWMSProfileParser = mock(WMSProfileParser.class);
        repo = new MapConfigRepositoryImpl(mockConfigReader,
                mockParser, mockWMSProfileParser);
    }
    
    @Test
    public void shouldGetMapConfigFromConfigReader() throws Exception
    {
        repo.getMapConfig("someMapConfig");
        verify(mockConfigReader).getConfigFile("/project/someMapConfig.xml");
    }

    @Test
    public void shouldParseMapConfig() throws Exception
    {
        /*InputStream mockInputStream = mock(InputStream.class);
        when(mockConfigReader.getConfigFile(any(String.class))).thenReturn(
                mockInputStream);
        MapConfig mockConfig = mock(MapConfig.class);
        when(mockParser.parseMapProject(mockInputStream)).thenReturn(
                mockConfig);
        MapConfig actualConfig = repo.getMapConfig("someMapConfig");
        assertEquals(mockConfig, actualConfig);*/
    }

}
