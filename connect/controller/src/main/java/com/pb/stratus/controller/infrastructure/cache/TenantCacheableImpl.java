package com.pb.stratus.controller.infrastructure.cache;

import com.pb.stratus.controller.KeyNotInCachePresentException;
import com.pb.stratus.core.configuration.Tenant;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.Serializable;

public class TenantCacheableImpl implements TenantCacheable {

    private static final Logger logger = LogManager.getLogger(TenantCacheableImpl.class);

    private Tenant tenant;
    private CacheHub cacheHub;

    public TenantCacheableImpl(Tenant tenant, CacheHub cacheHub) {
        this.tenant = tenant;
        this.cacheHub = cacheHub;
    }

    public boolean clear() {
        try {
            cacheHub.getCacheForTenant(tenant, CacheType.LEGEND_CACHE).clear(tenant);
            logger.info("Tenant (" + tenant.getTenantName() + ") cache cleared");
            return true;
        }
        catch (KeyNotInCachePresentException e) {
            logger.error("Error clearing cache ", e);
        }
        return false;
    }

    public boolean clear(Serializable key) {
        try {
            cacheHub.getCacheForTenant(tenant, CacheType.LEGEND_CACHE).clear(tenant, key);
            logger.info("Tenant (" + tenant.getTenantName() + ") cache cleared");
            return true;
        }
        catch (KeyNotInCachePresentException e) {
            logger.error("Error clearing cache ", e);
        }
        return false;
    }
}
