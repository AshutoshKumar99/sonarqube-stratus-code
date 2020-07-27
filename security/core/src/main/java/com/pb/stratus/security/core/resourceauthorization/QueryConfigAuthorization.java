package com.pb.stratus.security.core.resourceauthorization;

import java.io.FileNotFoundException;

/**
 * User: sh003bh
 * Date: 11/21/11
 * Time: 12:42 PM
 */
public class QueryConfigAuthorization extends AbstractReadResourceAuthorization {

    private String path = "/queryconfig" ;

    public QueryConfigAuthorization(ResourceAuthorizationReader resourceAuthorizationReader,
           ResourceParser resourceParser)
            throws FileNotFoundException, ResourceException {
        super(resourceAuthorizationReader, resourceParser);
    }

    @Override
    protected boolean isIgnoreResourceDir() {
        return false;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.QUERY_CONFIG;
    }

    @Override
    protected String getPath() {
        return path;
    }
}
