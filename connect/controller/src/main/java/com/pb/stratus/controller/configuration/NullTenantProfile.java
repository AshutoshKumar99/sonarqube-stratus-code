package com.pb.stratus.controller.configuration;

import com.pb.stratus.controller.action.ControllerActionFactory;
import com.pb.stratus.controller.infrastructure.cache.TenantCacheable;
import com.pb.stratus.controller.print.config.MapConfigRepositoryImpl;
import com.pb.stratus.controller.service.RESTAnalystProxy;
import com.pb.stratus.controller.service.RESTLocatorProxy;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;

/**
 * A null implementation of TenantProfile. Useful for making clearer the cause of
 * exceptions resulting from a missing tenant parameter.
 * Date: Aug 15, 2011
 * Time: 5:55:50 PM
 */
public class NullTenantProfile implements TenantProfile {
    public ControllerActionFactory getActionFactory() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    public String getTenantName() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    public ControllerConfiguration getConfiguration() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    public MapConfigRepositoryImpl getMapConfigRepository() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    public TenantCacheable getTenantLegendCache() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    @Override
    public ResourceAuthorization getResourceAuthorization(ResourceType resourceType) {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    @Override
    public RESTLocatorProxy getLocateRestProxy() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    @Override
    public RESTAnalystProxy getAnalystRestProxy() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }

    @Override
    public int getSessionTimeout() {
        throw new UnsupportedOperationException("No current tenant profile is available because none was specified!");
    }
}
