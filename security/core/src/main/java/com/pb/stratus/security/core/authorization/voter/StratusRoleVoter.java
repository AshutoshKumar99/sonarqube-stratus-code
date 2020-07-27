package com.pb.stratus.security.core.authorization.voter;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.security.core.authority.mapping.TenantGrantedAuthoritiesMapper;
import com.pb.stratus.security.core.connect.identity.RequestBasisAccessConfigurationResolver;
import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.web.FilterInvocation;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author vi001ty
 *         <p/>
 *         This Rolevoter is responsible for initial authorization for connect.
 *         User is authorized user for current tenant and only admin can access as authenticated user for  'public' accesstype.
 */
public class StratusRoleVoter <S>implements AccessDecisionVoter <S> {

    private static final Logger logger = Logger.getLogger(StratusRoleVoter.class);
    private Collection<String> roles = new ArrayList<String>();
    private String publicRole;
    private String adminRole;
    private RequestBasisAccessConfigurationResolver requestBasisAccessConfigurationResolver;
    private TenantGrantedAuthoritiesMapper authoritiesMapper;

    public RequestBasisAccessConfigurationResolver getRequestBasisAccessConfigurationResolver() {
        return requestBasisAccessConfigurationResolver;
    }

    public void setRequestBasisAccessConfigurationResolver(RequestBasisAccessConfigurationResolver requestBasisAccessConfigurationResolver) {
        this.requestBasisAccessConfigurationResolver = requestBasisAccessConfigurationResolver;
    }

    public ArrayList<String> getSupportedRoles() {
        return (ArrayList<String>) roles;
    }

    public void setSupportedRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public void setPublicRole(String publicRole) {
        this.publicRole = publicRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public TenantGrantedAuthoritiesMapper getAuthoritiesMapper() {
        return authoritiesMapper;
    }

    public void setAuthoritiesMapper(TenantGrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    public boolean supports(ConfigAttribute attribute) {
        return (attribute.getAttribute() != null) && roles.contains(attribute.getAttribute().trim());
    }

    public boolean supports(Class<?> clazz) {
        return true;
    }

    public int vote(Authentication authentication, S object, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;
        FilterInvocation filterInvocation = (FilterInvocation) object;
        String tenant = (String) (filterInvocation.getHttpRequest()).getAttribute(Constants.TENANT_ATTRIBUTE_NAME);
        if (attributes.size() == 1 && attributes.contains(new SecurityConfig(publicRole))) {
            result = ACCESS_GRANTED;
        } else {
            Collection<GrantedAuthority> authorities = authoritiesMapper.mapAuthorities(authentication, tenant);
            // Check for authType=public (only admins can login)
            if (requestBasisAccessConfigurationResolver.onlyAdminAccessAllowed() && !(authentication instanceof AnonymousAuthenticationToken)) {
                if (!authorities.contains(new GrantedAuthorityImpl(adminRole))) {
                    return ACCESS_DENIED;
                }
            }
            for (ConfigAttribute attribute : attributes) {
                if (this.supports(attribute)) {
                    if (authorities.contains(new GrantedAuthorityImpl(attribute.getAttribute().trim()))) {
                        return ACCESS_GRANTED;
                    }
                }
            }
        }

        return result;
    }

}
