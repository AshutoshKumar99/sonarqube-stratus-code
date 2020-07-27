package com.pb.stratus.security.core.util;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.SystemPropertyCustomerConfigDirHolder;
import com.pb.stratus.security.core.authority.mapping.TenantGrantedAuthoritiesMapper;
import com.pb.stratus.security.core.resourceauthorization.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/26/14
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationUtilsImpl implements AuthorizationUtils, InitializingBean {
    private final Logger logger = Logger.getLogger(AuthorizationUtilsImpl.class);

    private String administratorRole;

    private TenantGrantedAuthoritiesMapper authoritiesMapper;

    public void setAuthoritiesMapper(TenantGrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    public void setAdministratorRole(String administratorRole) {
        this.administratorRole = administratorRole;
    }

    public Collection<GrantedAuthority> extractAuthorities(Authentication authentication, String tenantName) {
        return authoritiesMapper.mapAuthorities(authentication, tenantName);
    }

    /**
     * @param request
     * @param type
     * @param resource
     * @return Set<ResourceAuthorizationConfig>
     * @throws com.pb.stratus.security.core.resourceauthorization.ResourceException
     */
    @Override
    public Set<ResourceAuthorizationConfig> getAuthorizeConfigs(
            HttpServletRequest request, ResourceType type, String resource)
            throws ResourceException {
        Authentication authentication = getAuthentication();
        String tenant =
                (String) (request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME));
        Collection<GrantedAuthority> authorities = extractAuthorities(authentication, tenant);

        String customerConfigDirPath = new SystemPropertyCustomerConfigDirHolder().getCustomerConfigDir()
                .getAbsolutePath();
        ResourceAuthorization authorization = getTenantResourceAuthorization(customerConfigDirPath, tenant, type);
        return authorization.getAuthorizationConfigs(authorities, resource);
    }


    /**
     * @param request
     * @param type
     * @return Set<ResourceAuthorizationConfig>
     * @throws ResourceException Overloaded getAuthorizeConfigs(request,type,resource)
     */
    @Override
    public Set<ResourceAuthorizationConfig> getAuthorizeConfigs(
            HttpServletRequest request, ResourceType type)
            throws ResourceException {
        return getAuthorizeConfigs(request, type, null);
    }

    /**
     * Fetches the authentication from the Security Context
     *
     * @return Authentication object
     */
    protected Authentication getAuthentication() {
        Authentication auth = null;
        if (SecurityContextHolder.getContext() != null) {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context.getAuthentication() != null) {
                auth = context.getAuthentication();
            } else {
                logger.debug("No authentication found in security context");
                throw new AuthenticationCredentialsNotFoundException(
                        "No authentication found in security context");
            }
        }
        return auth;
    }

    /**
     * Compares the given config with all the authorized configs
     *
     * @param configName
     * @param configs
     * @return
     */
    @Override
    public boolean configsContains(String configName,
                                   Set<ResourceAuthorizationConfig> configs) {
        for (ResourceAuthorizationConfig config : configs) {
            if (config.getName().equalsIgnoreCase(configName))
                return true;
        }
        return false;
    }

    /**
     * This method returns the alphabetical order of the authorized configs
     *
     * @param configs
     * @return
     */
    @Override
    public String getFirstLexicographicEntry(
            Set<ResourceAuthorizationConfig> configs) {
        if (configs != null && configs.size() > 0) {
            TreeSet<ResourceAuthorizationConfig> sorted =
                    new TreeSet<ResourceAuthorizationConfig>(
                            new ResourceAuthorizationConfigComapartor());
            sorted.addAll(configs);
            return sorted.iterator().next().getName();
        } else
            return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(authoritiesMapper, "authorityMapper must be set");
    }

    /**
     * Comparator to compare the resource authorization configs
     */
    private class ResourceAuthorizationConfigComapartor implements
            Comparator<ResourceAuthorizationConfig> {
        @Override
        public int compare(ResourceAuthorizationConfig o1,
                           ResourceAuthorizationConfig o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    /**
     * This method is used to check whether the requested file is authorized or
     * not. If not then we return false else we return true.
     *
     * @param request
     * @param fileName
     * @param type
     * @return boolean
     */
    @Override
    public boolean isConfigurationAuthorized(HttpServletRequest request,
                                             String fileName, ConfigFileType type) {
        Set<ResourceAuthorizationConfig> configs = null;

        try {
            configs = getAuthorizeConfigs(request, getResourceTypeForConfigType(type));
        } catch (ResourceException e) {
            logger.error(e.getMessage());
            return false;
        }
        // No authorization to user on any catalog configuration.
        if (configs == null || configs.size() == 0) {
            return false;
        }
        if (configsContains(fileName, configs)) {
            return true;
        }
        return false;
    }

    /**
     * This method will return the resourcetype based on the configfiletype
     *
     * @param configType
     * @return
     */
    @Override
    public ResourceType getResourceTypeForConfigType(
            ConfigFileType configType) {
        ResourceType resourceType = null;
        if (configType == null) {
            return null;
        }

        if (ConfigFileType.MAP.equals(configType)) {
            resourceType = ResourceType.MAP_CONFIG;
        } else if (ConfigFileType.CATALOG.equals(configType)) {
            resourceType = ResourceType.FMN_CONFIG;
        }
        return resourceType;
    }

    /**
     * This method will be used to check that user logged in as anonymous user
     * or not.
     *
     * @return
     */
    @Override
    public boolean isAnonymousUser() {
        Authentication auth = getAuthentication();
        return (auth instanceof AnonymousAuthenticationToken);
    }

    @Override
    public Collection<GrantedAuthority> getUserRoles(HttpServletRequest request) {
        Authentication authentication = getAuthentication();
        String tenant =
                (String) (request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME));
        Collection<GrantedAuthority> authorities = extractAuthorities(authentication, tenant);
        return authorities;
    }

    /**
     * Get ResourceAuthorization for the given resource for the tenant.\
     *
     * @param customerConfigDirPath
     * @param tenantName
     * @param type
     * @return ResourceAuthorization interface to query for authorization and resources.
     * @throws ResourceException
     */
    @Override
    public ResourceAuthorization getTenantResourceAuthorization(String customerConfigDirPath, String tenantName,
                                                                ResourceType type) throws ResourceException {

        ResourceAuthorizationFactory resourceAuthorizationFactory = null;
        List<ResourceAuthorization> resourceAuthorizations = new ArrayList<ResourceAuthorization>();
        ResourceAuthorizationReader resourceAuthorizationReader = new ResourceAuthorizationReaderImpl(
                customerConfigDirPath, tenantName);
        ResourceParser resourceParser = new DefaultResourceParserImpl();
        try {
            ResourceAuthorization configAuthorization = new MapConfigAuthorization(resourceAuthorizationReader,
                    resourceParser);
            resourceAuthorizations.add(configAuthorization);

            configAuthorization = new QueryConfigAuthorization(resourceAuthorizationReader, resourceParser);
            resourceAuthorizations.add(configAuthorization);

            configAuthorization = new CatalogConfigAuthorization(resourceAuthorizationReader, resourceParser);
            resourceAuthorizations.add(configAuthorization);
            if (administratorRole != null) {
                for (ResourceAuthorization resourceAuth : resourceAuthorizations)
                    resourceAuth.setAdministratorRole(administratorRole);
            }

        } catch (FileNotFoundException e) {
            throw new ResourceException(e);
        }
        resourceAuthorizationFactory = new ResourceAuthorizationFactory(resourceAuthorizations);
        return resourceAuthorizationFactory.getAuthorizationConfigs(type);
    }

    @Override
    public Map<String, Set<String>> getAuthorizedConfigs(HttpServletRequest request,
                                                         ResourceType type,
                                                         Set<String> resources) throws ResourceException {
        Authentication authentication = getAuthentication();
        String tenant =
                (String) (request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME));
        Collection<GrantedAuthority> authorities = extractAuthorities(authentication, tenant);

        String customerConfigDirPath = new SystemPropertyCustomerConfigDirHolder().getCustomerConfigDir()
                .getAbsolutePath();
        ResourceAuthorization authorization = getTenantResourceAuthorization(customerConfigDirPath, tenant, type);

        Map<String, Set<String>> queries = new HashMap();
        for (String resource : resources) {
            try {
                Set<ResourceAuthorizationConfig> configs = authorization.getAuthorizationConfigs(authorities, resource);
                Set<String> queryConfigSet = new HashSet<>();
                for (ResourceAuthorizationConfig config : configs) {
                    queryConfigSet.add(config.getName());
                }
                queries.put(resource, queryConfigSet);
            } catch (ResourceException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No Query Found for " + resource + " : " + e);
                }
            }
        }
        return queries;
    }

    /**
     * Returns if current user is an admin or not.
     * @return true if current user is admin, false otherwise.
     */
    public boolean isAdminUser() {
        boolean isAdmin = false;
        Authentication authentication = getAuthentication();
        Iterator<? extends GrantedAuthority> entries = authentication.getAuthorities().iterator();
        while (entries.hasNext()) {
            String authorityValue = entries.next().getAuthority();
            if (authorityValue.equals(Constants.SUPER_USER) || authorityValue.equals(Constants.ADMIN)) {
                isAdmin = true;
                break;
            }
        }
        return isAdmin;
    }
}

