package com.pb.stratus.controller.configuration;

import com.pb.stratus.controller.UnknownTenantException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Create and Manages a Cache copy of Tenant Profile's.
 */
public class CachingTenantProfileManager implements TenantProfileManager {
    private static final Logger logger = LogManager.getLogger(CachingTenantProfileManager.class);
    private Map<String, TenantProfile> tenantProfileCache = new HashMap<String, TenantProfile>();
    private TenantProfileManager tenantProfileManager;

    public CachingTenantProfileManager(TenantProfileManager tenantProfileManager) {
        this.tenantProfileManager = tenantProfileManager;
    }

    public TenantProfile getProfile(String tenantName) throws UnknownTenantException {

        //critical section! only one thread at a time
        synchronized (tenantProfileCache) {
            TenantProfile tenantProfile = tenantProfileCache.get(tenantName);
            if (tenantProfile == null) {
                tenantProfile = tenantProfileManager.getProfile(tenantName);
                tenantProfileCache.put(tenantName, tenantProfile);
            }
            return tenantProfile;
        }
    }
}
