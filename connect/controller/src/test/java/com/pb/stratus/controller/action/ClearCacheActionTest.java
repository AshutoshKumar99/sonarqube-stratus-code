package com.pb.stratus.controller.action;

import com.pb.stratus.controller.infrastructure.cache.CacheHub;
import com.pb.stratus.controller.infrastructure.cache.CacheType;
import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.core.configuration.Tenant;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: sh003bh
 * Date: 8/23/11
 * Time: 12:29 PM
 */
public class ClearCacheActionTest extends ControllerActionTestBase
{
    private Tenant mockTenant;

    private CacheHub mockCacheHub;

    private Cacheable mockCacheable;

    private ClearCacheAction clearCacheAction;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockTenant = mock(Tenant.class);
        mockCacheable = mock(Cacheable.class);
        when(mockCacheable.get(any(Tenant.class),
                any(String.class))).thenReturn("dummy value");
        mockCacheHub = mock(CacheHub.class);
        when(mockCacheHub.getCacheForTenant(any(Tenant.class),
                any(CacheType.class))).thenReturn
                (mockCacheable);
        clearCacheAction = new ClearCacheAction(mockTenant, mockCacheHub);
    }

    @Test
    public void testMessageWhenParamsNotSupplied() throws IOException,
            ServletException
    {
        clearCacheAction.execute(getRequest(), getResponse());
        String response = getResponse().getContentAsString();
        assertEquals(ClearCacheAction.PARAMS_NOT_PRESENT, response);
    }

    @Test
    public void testMessageWhenMapIsRemovedFromCache() throws IOException,
            ServletException
    {
        getRequest().setParameter("mapName", "map1");
        clearCacheAction.execute(getRequest(), getResponse());
        String response = getResponse().getContentAsString();
        String expectedResponse = String.format(ClearCacheAction.MAP_CLEARED,
                "map1");
        assertEquals(expectedResponse, response);
    }

    @Test
    public void shouldClearAllCacheIfDeleteAllParamPresent() throws IOException,
            ServletException
    {
        getRequest().setParameter("mapName", "map1");
        getRequest().setParameter("deleteAll", "true");
        clearCacheAction.execute(getRequest(), getResponse());
        String response = getResponse().getContentAsString();
        assertEquals(ClearCacheAction.ALL_CACHE_CLEAR, response);
    }

    @Test
    public void shouldNotThrowExceptionIfParametersAreBlank() throws IOException,
            ServletException
    {
        getRequest().setParameter("mapName", "map1");
        getRequest().setParameter("deleteAll", (String)null);
        clearCacheAction.execute(getRequest(), getResponse());
        String response = getResponse().getContentAsString();
        String expectedResponse = String.format(ClearCacheAction.MAP_CLEARED,
                "map1");
        assertEquals(expectedResponse, response);
    }
}
