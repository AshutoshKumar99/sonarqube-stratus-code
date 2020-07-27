package com.pb.stratus.controller.configuration;

import com.pb.stratus.controller.action.ControllerActionFactory;
import com.pb.stratus.controller.infrastructure.cache.TenantCacheable;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.service.RESTAnalystProxy;
import com.pb.stratus.controller.service.RESTLocatorProxy;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;

/**
 * Provides access to Tenant specific settings and services.
 */
public interface TenantProfile {

    /**
     * Get ControllerActionFactory instance configured for this tenant.
     */
    ControllerActionFactory getActionFactory();

    /**
     * Get the name of Tenant described by this profile.
     * @return the Tenant Name.
     */
    String getTenantName();

    /**
     * Get ControllerConfiguration instance configured for this tenant.
     * @return ControllerConfiguration.
     */
    ControllerConfiguration getConfiguration();

    /**
     * Get MapConfigRepository instance configured for this tenant.
     * @return MapConfigRepository.
     */
    MapConfigRepository getMapConfigRepository();

    /**
     * Get LegendCache instance configured for this tenant.
     * @return TenantCacheable.
     */
    TenantCacheable getTenantLegendCache();

    /**
     * Get ResourceAuthorization for the given resource for the tenant.
     * @param resourceType Enum
     * @return ResourceAuthorization interface to query for authorization and resources.
     */
    ResourceAuthorization getResourceAuthorization(ResourceType
               resourceType);

    /**
     * Get locaterestproxy for rest calls.
     * @return RESTLocatorProxy.
     */
    RESTLocatorProxy getLocateRestProxy();

    RESTAnalystProxy getAnalystRestProxy();

     int getSessionTimeout();
}
