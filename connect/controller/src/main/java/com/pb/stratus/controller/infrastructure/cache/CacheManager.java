package com.pb.stratus.controller.infrastructure.cache;

import com.pb.stratus.controller.infrastructure.cache.legendcache.LegendSerializableCache;
import com.pb.stratus.core.common.Preconditions;
import com.pb.stratus.core.configuration.Tenant;

import java.io.File;

import static com.pb.stratus.core.common.Constants.LEGEND_CACHE;

/**
 * The class acts as a hub for caches of different tenants. For the very
 * first request of a tenant, his cache wont exist, so it would be created at
 * runtime. The instance of the cache will be maintained and will be returned
 * from thereon.
 *
 * Author: Shoubhik Bhattacharya
 */
public final class CacheManager implements CacheHub
{
    /**
     * cannot be initialized.
     */
    private CacheManager()
    {
    }

    private static final CacheManager instance = new CacheManager();

    public static CacheManager getInstance()
    {
        return instance;
    }


    /**
     * Will return an instance of Cacheable for each tenant. If the instance
     * does not already exist it will be created first and then returned.
     * @param tenant
     * @return
     */
    public synchronized Cacheable getCacheForTenant(Tenant tenant,
            CacheType cacheType)
    {
        Preconditions.checkNotNull(tenant, "tenant cannot be null");
        return getCache(tenant, cacheType);
    }

    private  Cacheable getCache(Tenant tenant, CacheType cacheType)
        {
        switch(cacheType)
        {
            case LEGEND_CACHE:
            default:
                createLegendCacheDirIfNotPresent(tenant);
                return LegendSerializableCache.getInstance();
        }
    }

    private void createLegendCacheDirIfNotPresent(Tenant tenant)
    {
        String path = tenant.getTenantPath() + File.separator + LEGEND_CACHE;
        File f = new File(path);
        if (!f.exists())
        {
            f.mkdir();
        }
    }
}
