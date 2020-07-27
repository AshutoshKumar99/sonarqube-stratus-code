package com.pb.stratus.controller.infrastructure.cache;

import com.pb.stratus.core.configuration.Tenant;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.pb.stratus.core.common.Constants.LEGEND_CACHE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: sh003bh
 * Date: 9/15/11
 * Time: 1:06 PM
 */
public class CacheManagerTest
{
    private CacheManager cacheManager;

    @Before
    public void setUp()
    {
        cacheManager = CacheManager.getInstance();
    }

    @Test
    public void testInstanceIsNotNull()
    {
        assertNotNull(cacheManager);
    }

    @Test
    public void checkCacheDirectoryIsCreatedForTenant()
    {
        Tenant tenant = mock(Tenant.class);
        when(tenant.getTenantName()).thenReturn("dummy");
        when(tenant.getTenantPath()).thenReturn(".");
        cacheManager.getCacheForTenant(tenant, CacheType.LEGEND_CACHE);
        File tenantPath  = new File(".");
        File legendRepository = new File(tenantPath, LEGEND_CACHE);
        assertTrue(legendRepository.exists());
        legendRepository.delete();
    }

    @Test(expected=NullPointerException.class)
    public void shouldThrowExceptionIfTenantIsNull() throws Exception
    {
        cacheManager.getCacheForTenant(null, CacheType.LEGEND_CACHE);
    }
}
