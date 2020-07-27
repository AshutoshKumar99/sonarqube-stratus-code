package com.pb.stratus.security.core.resourceauthorization;

import java.io.FileNotFoundException;

/**
 * User: sh003bh
 * Date: 11/14/11
 * Time: 2:14 PM
 */
public class AbstractReadResourceAuthorizationTestHelper extends AbstractReadResourceAuthorization {
    private final String EXTENSION = ".someextension";
    private final String PATH = "/somepath";

    public AbstractReadResourceAuthorizationTestHelper(ResourceAuthorizationReader resourceAuthorizationReader,
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
        return ResourceType.MAP_CONFIG;
    }

    @Override
    public String getResourceExtension() {
        return EXTENSION;
    }

    @Override
    public String getPath() {
        return PATH;
    }
}
