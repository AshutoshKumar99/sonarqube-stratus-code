package com.pb.stratus.security.core.resourceauthorization;

import java.io.FileNotFoundException;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 1:25 PM
 * implementation of AbstractReadResourceAuthorization for map configs
 */
public class MapConfigAuthorization extends AbstractReadResourceAuthorization {

    private final String PATH = "/config";

    public MapConfigAuthorization(ResourceAuthorizationReader resourceAuthorizationReader,
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
        return ResourceType.MAP_CONFIG;
    }

    @Override
    protected String getPath() {
        return PATH;
    }
}
