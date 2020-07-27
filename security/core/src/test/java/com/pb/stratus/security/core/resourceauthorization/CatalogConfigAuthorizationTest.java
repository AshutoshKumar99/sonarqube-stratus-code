package com.pb.stratus.security.core.resourceauthorization;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * User: sh003bh
 * Date: 11/21/11
 * Time: 5:21 PM
 */
public class CatalogConfigAuthorizationTest {

    private CatalogConfigAuthorization catalogConfigAuthorization;
    ResourceAuthorizationReader mockResourceAuthorizationReader;
    ResourceParser mockResourceParser;

    @Before
    public void setUp() throws FileNotFoundException, ResourceException {
        mockResourceAuthorizationReader = mock(ResourceAuthorizationReader.class);
        mockResourceParser = mock(ResourceParser.class);
    }

    @Test
    public void testResourceType() throws FileNotFoundException, ResourceException {
        catalogConfigAuthorization =
                new CatalogConfigAuthorization(mockResourceAuthorizationReader,
                        mockResourceParser);
        assertEquals(ResourceType.FMN_CONFIG,
                catalogConfigAuthorization.getResourceType());
    }

    @Test
    public void testPath() throws FileNotFoundException, ResourceException {
        catalogConfigAuthorization =
                new CatalogConfigAuthorization(mockResourceAuthorizationReader,
                        mockResourceParser);
        assertEquals("/catalogconfig",
                catalogConfigAuthorization.getPath());
    }

    @Test
    public void shouldIgnoreIfNestedDirectoryIsSpecified()
            throws FileNotFoundException, ResourceException {
        catalogConfigAuthorization =
                new CatalogConfigAuthorization(mockResourceAuthorizationReader,
                        mockResourceParser);
        assertTrue(catalogConfigAuthorization.isIgnoreResourceDir());

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        catalogConfigAuthorization.getResourceDir("soemdir");
        verify(mockResourceAuthorizationReader).getFile(captor.capture());
        Assert.assertEquals("/catalogconfig", captor.getValue());
    }
}
