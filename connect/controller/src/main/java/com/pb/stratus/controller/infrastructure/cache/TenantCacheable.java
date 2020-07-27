package com.pb.stratus.controller.infrastructure.cache;

import java.io.Serializable;

/**
 * This particular interface will clear cache for a tenant or for a specific map
 */
public interface TenantCacheable {

    /**
     * Clear all the cache of the tenant.
     */
    boolean clear();

    /**
     * Clear the particular key for the tenant.
     *
     * @param key
     */
    boolean clear(Serializable key);
}
