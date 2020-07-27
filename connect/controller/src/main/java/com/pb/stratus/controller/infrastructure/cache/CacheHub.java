package com.pb.stratus.controller.infrastructure.cache;

import com.pb.stratus.core.configuration.Tenant;

/**
 * Basic interface for getting the Cacheable for a particular tenant. What
 * the implementation must make sure is that tenant caching should be
 * discoverable at runtime, this is a requirement for multi-tenancy.
 */
public interface CacheHub
{
    public  Cacheable getCacheForTenant(Tenant tenant, CacheType cacheType);
}
