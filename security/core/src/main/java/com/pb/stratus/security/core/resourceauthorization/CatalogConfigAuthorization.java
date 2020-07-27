package com.pb.stratus.security.core.resourceauthorization;

import java.io.FileNotFoundException;


/**
 * User: sh003bh
 * Date: 11/21/11
 * Time: 1:06 PM
 */
public class CatalogConfigAuthorization extends AbstractReadResourceAuthorization {

    private final String PATH = "/catalogconfig";

    public CatalogConfigAuthorization(ResourceAuthorizationReader resourceAuthorizationReader,
                                      ResourceParser resourceParser)
            throws FileNotFoundException, ResourceException {
        super(resourceAuthorizationReader, resourceParser);
    }

    @Override
    protected boolean isIgnoreResourceDir() {
        return true;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.FMN_CONFIG;
    }

    @Override
    protected String getPath() {
        return PATH;
    }
}
