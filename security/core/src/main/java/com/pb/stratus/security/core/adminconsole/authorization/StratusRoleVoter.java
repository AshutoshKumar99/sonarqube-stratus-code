package com.pb.stratus.security.core.adminconsole.authorization;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.security.core.authority.mapping.TenantGrantedAuthoritiesMapper;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.web.FilterInvocation;

import java.util.ArrayList;
import java.util.Collection;

public class StratusRoleVoter <S> implements AccessDecisionVoter<S> {

    private Collection<String> roles = new ArrayList<String>();
    private String publicRole;
    private String rolePrefix = "ROLE_";

    private TenantGrantedAuthoritiesMapper authoritiesMapper;

    public void setAuthoritiesMapper(TenantGrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
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

            for (ConfigAttribute attribute : attributes) {
                if (this.supports(attribute)) {
                    result = ACCESS_GRANTED;

                    if (!authorities.contains(new GrantedAuthorityImpl(attribute.getAttribute().trim()))) {
                        return ACCESS_DENIED;
                    }
                }
            }
        }

        return result;
    }

}
