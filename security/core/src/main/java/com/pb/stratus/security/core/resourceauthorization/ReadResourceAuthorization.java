package com.pb.stratus.security.core.resourceauthorization;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 11:44 AM
 * Interface to read the resource authorization. a resource can be anything like
 * a map config, FMN config or a query config.
 */
public interface ReadResourceAuthorization {

    /**
     * The file name extension of the file being used to maintain the authorization
     * related for the given resource like ".auth".
     * @return
     */
    public String getResourceExtension();

    /**
     * Return a list of ResourceAuthorizationConfig for the given resource. All files
     * with the extension mentioned in getResourceExtension() will be read
     * to get the ResourceAuthorizationConfig
     * @return List<ResourceAuthorizationConfig>
     * @throws FileNotFoundException
     * @throws ResourceException
     */
    public List<ResourceAuthorizationConfig> getResourceList() throws FileNotFoundException, ResourceException;

    /**
     * return the path relative to the base path from where the authorization
     * should be read.
     * @return
     */
    public String getPath();
}
