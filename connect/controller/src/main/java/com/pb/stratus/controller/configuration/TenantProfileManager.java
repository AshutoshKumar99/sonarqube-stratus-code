package com.pb.stratus.controller.configuration;

import com.pb.stratus.controller.UnknownTenantException;

/**
 * Manages TenantProfile for all tenants.
 */
public interface TenantProfileManager {

    /**
     * Get the profile associated with the specified Tenant.
     * @param tenantName
     * @return
     * @throws UnknownTenantException if the Tenant doesn't exist
     */
    TenantProfile getProfile(String tenantName) throws UnknownTenantException;
}
