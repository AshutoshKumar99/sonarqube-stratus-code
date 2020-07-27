package com.pb.stratus.security.core.resourceauthorization;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * User: sh003bh
 * Date: 11/21/11
 * Time: 4:52 PM
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest
public class MapConfigAuthorizationTest {

    private MapConfigAuthorization mapConfigAuthorization;
    ResourceAuthorizationReader mockResourceAuthorizationReader;
    ResourceParser mockResourceParser;

    @Before
    public void setUp() throws FileNotFoundException, ResourceException {
        mockResourceAuthorizationReader = mock(ResourceAuthorizationReader.class);
        mockResourceParser = mock(ResourceParser.class);
    }

    @Test
    public void testResourceType() throws FileNotFoundException, ResourceException {
        mapConfigAuthorization =
                new MapConfigAuthorization(mockResourceAuthorizationReader, mockResourceParser);
        assertEquals(ResourceType.MAP_CONFIG,
                mapConfigAuthorization.getResourceType());
    }

    @Test
    public void testPath() throws FileNotFoundException, ResourceException {
        mapConfigAuthorization =
                new MapConfigAuthorization(mockResourceAuthorizationReader, mockResourceParser);
        assertEquals("/config",
                mapConfigAuthorization.getPath());
    }

    @Test
    public void shouldIgnoreIfNestedDirectoryIsSpecified()
            throws FileNotFoundException, ResourceException {
        mapConfigAuthorization =
                new MapConfigAuthorization(mockResourceAuthorizationReader,
                        mockResourceParser);
        assertTrue(mapConfigAuthorization.isIgnoreResourceDir());

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        mapConfigAuthorization.getResourceDir("soemdir");
        verify(mockResourceAuthorizationReader).getFile(captor.capture());
        Assert.assertEquals("/config", captor.getValue());
    }

}
