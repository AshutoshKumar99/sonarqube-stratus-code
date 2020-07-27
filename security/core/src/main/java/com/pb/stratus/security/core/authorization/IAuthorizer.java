package com.pb.stratus.security.core.authorization;

import com.pb.stratus.security.core.jaxb.NamedResource;
import com.pb.stratus.security.core.jaxb.Role;
import com.pb.stratus.security.core.jaxb.StratusResource;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: si001jy
 * Date: 3/10/14
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 * The common Interface for all Authorization Services
 */
public interface IAuthorizer
{

   /**
    * Get all the Roles. For On Prem Analyst product and tenant is optional as On Prem is not multi tenanted
    * @param authorizerVO
    * @return
    */
   Set<Role> getAllRoles(AuthorizerVO authorizerVO);

   /**
    * Ideally this should be the only method exposed, we take in all the resources at one go and
    * return the map with the permissions.
    * @param resources
    * @return
    */
   Map<StratusResource, Set<Role>> getAllRolesForStratusResources(Set<StratusResource> resources);

   /**
    * Set all the resource permissions.
    * @param resource
    * @param roles
    * @return
    */
   boolean setRolesForResource(StratusResource resource, Set<Role> roles);


   /**
    * Set the permissions of the resource as defined in the MAP.
    * @param resourceRolesMap
    * @return
    */
    boolean setRolesForResource(Map<StratusResource, Set<Role>> resourceRolesMap);

    boolean hasUnsupportedCustomRoles(AuthorizerVO authorizerVO);

    /**
     * This method will return a map that contains NamedResource Path as a Key
     * and value is again a Map with Key as Role (example Administrator, User) with value as
     * a set of permissions (READ, ALL etc) on that resource for that particular role
     * @param namedResources
     * @return
     */
    Map<String, Map<String, Set<String>>> getResourcesPermissions(
            Set<NamedResource> namedResources) throws AuthorizationException;

    /**
     * get list of roles having read permission on given NamedResources
     */
    Map<NamedResource, Collection<Role>> getAllRolesForNamedResources(
            Collection<NamedResource> resources) throws AuthorizationException;

    /**
     * set read permissions for given roles on given NamedResources.
     */
/*    boolean setAllRolesForNamedResources(Collection<NamedResource> resources,
           Collection<Role> existingRoles) throws AuthorizationException;*/

    Role getAdminRole();
}
