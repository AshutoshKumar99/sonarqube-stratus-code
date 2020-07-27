package com.pb.stratus.security.core.resourceauthorization;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 5:57 PM
 */
public class ResourceAuthorizationFactoryTest {
    private List<ResourceAuthorization> resourceAuthorizationList;
    private ResourceAuthorizationFactory resourceAuthorizationFactory;
    private MapConfigAuthorization mockMapConfigAuthorization;

    @Before
    public void setUp()
    {
        resourceAuthorizationList = new
                ArrayList<ResourceAuthorization>();
        mockMapConfigAuthorization = mock(MapConfigAuthorization.class);
        when(mockMapConfigAuthorization.getResourceType()).thenReturn(ResourceType.MAP_CONFIG);
        resourceAuthorizationList.add(mockMapConfigAuthorization);
    }

    @Test(expected= NullPointerException.class)
    public void testConstructorForNullArgument()
    {
        resourceAuthorizationFactory = new ResourceAuthorizationFactory(null);
    }

    @Test
    public void testGetAuthorizationConfigs()
    {
        resourceAuthorizationFactory = new
                ResourceAuthorizationFactory(resourceAuthorizationList);
        ResourceAuthorization actual =
                resourceAuthorizationFactory.getAuthorizationConfigs(ResourceType.MAP_CONFIG);
        assertEquals(mockMapConfigAuthorization, actual);
    }
}
