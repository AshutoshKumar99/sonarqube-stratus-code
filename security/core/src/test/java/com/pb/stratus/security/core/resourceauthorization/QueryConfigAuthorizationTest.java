package com.pb.stratus.security.core.resourceauthorization;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.FileNotFoundException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * User: sh003bh
 * Date: 11/21/11
 * Time: 5:59 PM
 */
public class QueryConfigAuthorizationTest {

    private QueryConfigAuthorization queryConfigAuthorization;
    ResourceAuthorizationReader mockResourceAuthorizationReader;
    ResourceParser mockResourceParser;

    @Before
    public void setUp() throws FileNotFoundException, ResourceException {
        mockResourceAuthorizationReader = mock(ResourceAuthorizationReader.class);
        mockResourceParser = mock(ResourceParser.class);
    }

    @Test
    public void testResourceType() throws FileNotFoundException, ResourceException {
        queryConfigAuthorization =
                new QueryConfigAuthorization(mockResourceAuthorizationReader,
                        mockResourceParser);
        Assert.assertEquals(ResourceType.QUERY_CONFIG,
                queryConfigAuthorization.getResourceType());
    }

    @Test
    public void shouldConstructCorrectPath() throws FileNotFoundException, ResourceException {
        queryConfigAuthorization =
                new QueryConfigAuthorization(mockResourceAuthorizationReader,
                        mockResourceParser);
        assertEquals("/queryconfig",queryConfigAuthorization.getPath());
    }

    @Test
    public void shouldNotIgnoreIfNestedDirectoryIsSpecified()
            throws FileNotFoundException, ResourceException {
        queryConfigAuthorization =
                new QueryConfigAuthorization(mockResourceAuthorizationReader,
                        mockResourceParser);
        assertFalse(queryConfigAuthorization.isIgnoreResourceDir());

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        queryConfigAuthorization.getResourceDir("soemdir");
        verify(mockResourceAuthorizationReader).getFile(captor.capture());
        junit.framework.Assert.assertEquals("/queryconfig/soemdir", captor.getValue());
    }
}
