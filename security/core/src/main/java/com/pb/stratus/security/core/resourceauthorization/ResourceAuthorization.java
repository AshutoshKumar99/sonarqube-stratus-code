package com.pb.stratus.security.core.resourceauthorization;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 2:01 PM
 * Interface to query for the authorizations. The query can be of two types
 * 1- Get a Set of ResourceAuthorizationConfig for the given Collection of
 * GrantedAuthority
 * 2- Get List of GrantedAuthority against the given resource.
 */
public interface ResourceAuthorization {

    /**
     * This method helps you query get ResourceAuthorizationConfigs for all the
     * resources in the specified resourceDir.
     * If the ResourceDir is not found then a ResourceException will be thrown.
     *
     * @param grantedAuthorities
     * @param resourceDir
     * @return
     */
    public Set<ResourceAuthorizationConfig> getAuthorizationConfigs(
            Collection<GrantedAuthority> grantedAuthorities, String resourceDir)
            throws ResourceException;

    /**
     * Get a List of authorization associated with a given resource. If the
     * resource is not found then a ResourceException must be thrown.
     * resource name should be relative to the base path.
     * where base path is the top level dir for the configs as given in the list below
     * mapconfig  = "/config"
     * fmnconfig = "/catalogcongig"
     * queryconfig = "/querycongig"
     * For example you want to get List<GrantedAuthority> for a  resource in mapconfig
     * then resourceName = "someconfig"
     * If you want to do it for a query the you need to give
     * resourceName = "sometable/someconfig"
     *
     * @param resourceName
     * @return
     * @throws ResourceException
     */
    public List<GrantedAuthority> getAuthorities(String resourceName) throws ResourceException;

    /**
     * The implementation class must override this method to identify itself to be associated
     * wit a resource.
     *
     * @return ResourceType Enum
     */
    public ResourceType getResourceType();

    /**
     * This will set the role that will be considered as administrator(default it is ROLE_Administrators)
     */
    public void setAdministratorRole(String administratorRole);
}
