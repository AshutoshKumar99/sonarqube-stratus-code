package com.pb.stratus.onpremsecurity.authorization;

import com.g1.dcg.managers.access.AclManagerService;
import com.pb.spectrum.platform.server.config.core.ws.productapi.impl.SecurityProductServiceImpl;
import com.pb.stratus.security.core.authorization.AbstractAuthorizer;
import com.pb.stratus.security.core.authorization.AuthorizationException;
import com.pb.stratus.security.core.authorization.AuthorizerVO;
import com.pb.stratus.security.core.jaxb.NamedResource;
import com.pb.stratus.security.core.jaxb.Role;
import com.pb.stratus.security.core.jaxb.StratusResource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: HA008SA
 * Date: 3/10/14
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizerImpl extends AbstractAuthorizer implements InitializingBean{

    private static String ADMIN="admin";
    public static String ACE_TYPE_NAME= "Location Intelligence.Named Resources";
    //Apart from the admin Role, Analyst will be listing only those roles that start with the String Analyst
    private static String VALID_ANALYST_ROLE_PREFIX="Analyst";
    private static final Logger logger = Logger.getLogger(AuthorizerImpl.class);
    //Default Mask for VIEW only permission
    public static final int GRANT_MASK=1;
    public static final int DENY_MASK=14;
    private Role adminRole;

    public void setAuthorizationHelper(AnalystAuthorizationHelper authorizationHelper) {
        this.authorizationHelper = authorizationHelper;
    }
	
	public void setSecurityProductService(SecurityProductServiceImpl securityProductService) {
        this.securityProductService = securityProductService;
    }

    private AnalystAuthorizationHelper authorizationHelper;
    private AclManagerService aclManagerService;
    private AuthorizerVO _authorizerVO;
	private SecurityProductServiceImpl securityProductService;

    public AuthorizerImpl(AclManagerService aclManagerService) {
        this.aclManagerService = aclManagerService;
    }


    /**
     * Will return only admin Role and those Roles that has a Prefix as "Analyst"
     * @param authorizerVO
     * @return  All Valid Roles for Analyst
     */
    @Override
    public Set<Role> getAllRoles(AuthorizerVO authorizerVO)
    {
        Set<Role> roles = new HashSet<Role>();
		List<String> systemRoles = securityProductService.listRoles();

        for (String roleValue : systemRoles) {
            if((roleValue.equals(ADMIN)) ||
                    (roleValue.startsWith(VALID_ANALYST_ROLE_PREFIX)))
            {
                Role role = new Role();
                role.setName(roleValue);
                role.setAdmin((roleValue.equals(ADMIN)) ? true : false);
                if(role.isAdmin())
                    setAdminRole(role);
                roles.add(role);
            }
        }
        logger.debug("Roles found in the System : "+ roles.size());
        _authorizerVO = authorizerVO;
        _authorizerVO.setAllRolesInSystem(roles);
        return roles;
    }

    /**
     * Will return all Roles that are assigned to this Stratus Resource in the AUTH files
     * @param resources
     * @return
     */
    @Override
    public Map<StratusResource, Set<Role>> getAllRolesForStratusResources(Set<StratusResource> resources) {
        return null;
    }

    /**
     * TODO
     * @param resource
     * @param roles
     * @return
     */
    public boolean setRolesForResource(StratusResource resource, Set<Role> roles) {
        return true;
    }

    @Override
    public boolean setRolesForResource(Map<StratusResource, Set<Role>> resourceRolesMap) {
        return true;
    }

    @Override
    public boolean hasUnsupportedCustomRoles(AuthorizerVO authorizerVO)
    {
        return false;
    }

    /**
     * This method will return a map that contains NamedResource Path as a Key
     * and value is again a Map with Key as Role (example Administrator, User) with value as
     * a set of permissions (READ, ALL etc) on that resource for that particular role
     * @param namedResources
     * @return
     */
    @Override
    public Map<String, Map<String, Set<String>>> getResourcesPermissions(
            Set<NamedResource> namedResources) throws AuthorizationException
    {
        Map<String, Map<String, Set<String>>> resourcesPermission = new TreeMap();
        Set<String> permissions = null;
        TreeMap<String, Set<String>> roleWithPermissions = null;
        Set<Role> roles = null;
        Map<NamedResource, Collection<Role>> permissionData =null;
        synchronized(this){
        permissionData =  getAllRolesForNamedResources(namedResources);
        }
        for(NamedResource resource : namedResources)
        {
            roleWithPermissions = new TreeMap<>();
            roles = (Set<Role>) permissionData.get(resource);

            for(Role role :roles)
            {
                permissions = new HashSet<>();

                //case if role is admin ,then permission is ALL,else READ.

                if(role.isAdmin())
                {
                    permissions.add("ALL");
                }
                else
                {
                    permissions.add("READ");
                }
                 roleWithPermissions.put(role.getName(), permissions);
            }
            resourcesPermission.put(resource.getResourcePath(), (TreeMap<String, Set<String>>)roleWithPermissions.clone());
        }

        return resourcesPermission;
    }

    @Override
    public  Map<NamedResource, Collection<Role>> getAllRolesForNamedResources(
            Collection<NamedResource> resources) throws AuthorizationException {

        Set<Role> allRoles = _authorizerVO.getAllRolesInSystem();
        Set<Role> assignedRoles = null;
        Map<NamedResource, Collection<Role>> namedResourceRolesMap = new HashMap<>();
        for (NamedResource namedResource : resources) {
            assignedRoles = new HashSet();

            for (Role role : allRoles) {
                if (role.isAdmin()) {
                    assignedRoles.add(role);
                    continue;
                }
            }

            namedResourceRolesMap.put(namedResource, assignedRoles);
        }
        return namedResourceRolesMap;
    }

    /*
    *   To get all roles which are affected by permission change
    *   for which Entity Override will be done.
    */
    private Collection<Role> getAllAffectedRoles(
            Collection<NamedResource> resources,
            Collection<Role> existingRoles)
    {
        Collection<Role> affectedRoles = new HashSet<>();
        for(NamedResource resource: resources){
            affectedRoles.addAll(resource.getRoles());
        }
        if(existingRoles != null)
            affectedRoles.addAll(existingRoles);
        Role adminRole = null;
        for(Role role : affectedRoles)
        {
            if(role.isAdmin())
                adminRole = role;
        }
        if(adminRole != null)
            affectedRoles.remove(adminRole);
        return affectedRoles;
    }

    /**
     * Spectrum manages Entity Overrides by a set of GRANT and DENY. I believe just reading GRANT mask should be sufficient for us to know that a Role has View Permission on a resource or not
     * @param grantMask
     * @return true if the grant mask indicates VIEW Permission
     */
    private boolean hasViewPermission(int grantMask)
    {
        switch (grantMask)         {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
            case 13:
            case 15:
            case 17:
            case 19:
            case 21:
            case 23:
            case 25:
            case 27:
            case 29:
            case 31:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(aclManagerService, "aclManagerService must be set");
        Assert.notNull(authorizationHelper, "authorizationHelper must be set");
    }

    @Override
    public Role getAdminRole()
    {
        return adminRole;
    }

    private void setAdminRole(Role role)
    {
        adminRole = role;
    }
}
