package com.pb.stratus.security.core.util;

import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 3/12/14
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AuthorizationUtils {

    String PREFIX = "ROLE_";
    String REQUEST_PARAM_MAPCFG = "mapcfg";
    String DEFAULT_MAPCFG = "defaultmap";


    Set<ResourceAuthorizationConfig> getAuthorizeConfigs(
            HttpServletRequest request, ResourceType type, String resource)
            throws ResourceException;

    Set<ResourceAuthorizationConfig> getAuthorizeConfigs(
            HttpServletRequest request, ResourceType type)
            throws ResourceException;

    /*
    * is given ResourceAuthorizationConfig collection contains given config.
    * @param configName given config name
    * @param configs collection of ResourceAuthorisationConfig.
    */
    boolean configsContains(String configName,
                            Set<ResourceAuthorizationConfig> configs);

    /*
    *  get first Lexicographic entry among given configs
    * @param configs collection of ResourceAuthorisationConfig.
    */
    String getFirstLexicographicEntry(
            Set<ResourceAuthorizationConfig> configs);

    /*
    * is current User authorised to access given configuration(MAPCONFIG, QUERYCONFIG or FMNCONFIG)
    * @param request HttpServletRequest
    * @param fileName config file name
    * @param type Config Type
    */
    boolean isConfigurationAuthorized(HttpServletRequest request,
                                      String fileName, ConfigFileType type);

    ResourceType getResourceTypeForConfigType(
            ConfigFileType configType);

    /*
    * is current user is an anonymous user.
    */
    boolean isAnonymousUser();

    Collection<GrantedAuthority> getUserRoles(HttpServletRequest request);

    ResourceAuthorization getTenantResourceAuthorization(String customerConfigDirPath, String tenantName,
                                                         ResourceType type) throws ResourceException;

    /**
     * Gets authorized configs.
     *
     * @param request
     * @param type
     * @param resource
     * @return
     * @throws ResourceException
     */
    Map<String, Set<String>> getAuthorizedConfigs(
            HttpServletRequest request, ResourceType type, Set<String> resource)
            throws ResourceException;

    boolean isAdminUser();
}
