package com.pb.stratus.security.core.resourceauthorization;


import com.pb.stratus.core.common.Preconditions;

import java.util.List;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 2:06 PM
 */
public class ResourceAuthorizationFactory {

    private List<ResourceAuthorization> resourceAuthorizations;

    /**
     * @param resourceAuthorizations
     */
    public ResourceAuthorizationFactory(List<ResourceAuthorization>
                                                resourceAuthorizations) {
        Preconditions.checkNotNull(resourceAuthorizations,
                "resourceAuthorizations cannot be null");
        this.resourceAuthorizations = resourceAuthorizations;
    }

    /**
     * ResourceAuthorization of the particular resource type will be returned.
     *
     * @param resourceType
     * @return ResourceAuthorization
     */
    public ResourceAuthorization getAuthorizationConfigs(ResourceType resourceType) {
        Preconditions.checkNotNull(resourceType, "resourceType cannot be null");
        for (ResourceAuthorization resourceAuthorization
                : resourceAuthorizations) {
            if (resourceType == resourceAuthorization.getResourceType()) {
                return resourceAuthorization;
            }
        }
        return null;
    }
}
