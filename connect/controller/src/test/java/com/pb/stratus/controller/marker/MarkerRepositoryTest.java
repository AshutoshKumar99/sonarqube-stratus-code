package com.pb.stratus.controller.marker;

import com.pb.stratus.core.configuration.ConfigReader;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MarkerRepositoryTest
{
    
    private ConfigReader mockConfigReader;
    
    private MarkerRepository repo;

    private InputStream mockInputStream;

    @Before
    public void setUp() throws Exception
    {
        mockConfigReader = mock(ConfigReader.class);
        mockInputStream = mock(InputStream.class);
        when(mockConfigReader.getConfigFile(any(String.class))).thenReturn(
                mockInputStream);
        repo = new MarkerRepository(mockConfigReader);
    }
    
    @Test
    public void shouldReturnInputStreamFromConfigReader() throws Exception
    {
        InputStream is = repo.getMarkerImage("someMarker.png", 
                MarkerType.MARKER);
        assertEquals(mockInputStream, is);
    }

    @Test
    public void shouldRequestMarkerImageFromExpectedPath() throws Exception
    {
        repo.getMarkerImage("someMarker.png", MarkerType.MARKER);
        verify(mockConfigReader).getConfigFile(
                "/theme/images/catalog/marker/someMarker.png");
    }
    
    @Test
    public void shouldRequestIconImageFromCorrectPath() throws Exception
    {
        repo.getMarkerImage("someMarker.png", MarkerType.ICON);
        verify(mockConfigReader).getConfigFile(
                "/theme/images/catalog/markericon/someMarker.png");
    }
    
    @Test
    public void shouldRequestShadowImageFromCorrectPath() throws Exception
    {
        repo.getMarkerImage("someMarker.png", MarkerType.SHADOW);
        verify(mockConfigReader).getConfigFile(
                "/theme/images/catalog/markershadow/someMarker.png");
    }
}
