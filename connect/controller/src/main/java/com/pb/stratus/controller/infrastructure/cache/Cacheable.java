package com.pb.stratus.controller.infrastructure.cache;


import com.pb.stratus.controller.KeyNotInCachePresentException;
import com.pb.stratus.core.configuration.Tenant;

import java.io.Serializable;
import java.util.Set;

/**
 * The top most interface for the caching module. It is not dependent on any
 * particular caching library. The interface includes the tenant as a
 * parameter for all the methods so that caches can be identified per tenant
 * .The interface does not provide any specific way of maintaining the cache
 * as long as keys can be retrieved based on tenant name.
 * Also the  key and the value must be serializable as persistence of cache
 * is a desired feature.
 * The implementations of this interface are currently not concerned about
 * the non existence of the tenant. Such work should be handled before hand.
 * I have done this to make the implementations simpler.
 *
 * TODO:Another desired behaviour would be to support groups for caching,
 * which will give the client more flexibility of having the same key in
 * multiple groups. However since that is dependent on the underlying caching
 * API , i have not included those methods at this point of time.
 *
 * @param <K> The key which should be serializable to support file base cache.
 * @param <V> The value which should be serializable to support file base
 *            cache.
 */
public interface Cacheable<K extends Serializable, V extends Serializable>
{
    /**
     * Put the key value in the cache of the given tenant.
     * @param tenant
     * @param key
     * @param value
     */
    public void put(Tenant tenant, K key, V value);

    /**
     * Get the value for the given key from the cache for the given tenant.
     * In case of non-existent key, null should be returned.
     * @param tenant
     * @param key
     * @return
     */
    public V get(Tenant tenant, K key);

    /**
     * Get all the keys for the given tenant.
     * @param tenant
     * @return
     */
    public Set<K> get(Tenant tenant);

    /**
     * Clear all the cache of the given tenant.
     * @param tenant
     */
    public void clear(Tenant tenant) throws KeyNotInCachePresentException;

    /**
     * Clear the particular key for the given tenant.
     * @param tenant
     * @param key
     */
    public void clear(Tenant tenant, K key) throws KeyNotInCachePresentException;
}
