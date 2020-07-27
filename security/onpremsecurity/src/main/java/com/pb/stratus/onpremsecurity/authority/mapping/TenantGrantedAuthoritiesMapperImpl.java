package com.pb.stratus.onpremsecurity.authority.mapping;

import com.pb.stratus.security.core.authority.mapping.TenantGrantedAuthoritiesMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/18/14
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class TenantGrantedAuthoritiesMapperImpl implements TenantGrantedAuthoritiesMapper, InitializingBean {
    public final static String DEFAULT_PUBLIC_ROLE = "ROLE_AnalystGuestRole";
    private List<String> ADMIN_ROLES;
    private static final Logger logger = Logger.getLogger(TenantGrantedAuthoritiesMapperImpl.class);
    private String rolePrefix = "ROLE_";
    private String mappedAdminRole = "ROLE_Administrators";

    public String getRoleAnonymous() {
        return roleAnonymous;
    }

    /**
     * CONN-22969 : Ensure a public role if not specified in shared.properties defaults to
     * AnalystGuestRole
     * @param roleAnonymous
     */
    public void setRoleAnonymous(String roleAnonymous) {
        if((roleAnonymous != null) && (roleAnonymous.trim().length()>0))
            this.roleAnonymous = roleAnonymous.trim();
        else
            this.roleAnonymous = DEFAULT_PUBLIC_ROLE;
    }

    private String roleAnonymous;

    @Override
    public Collection<GrantedAuthority> mapAuthorities(Authentication authentication, String tenant) {
        Collection<GrantedAuthority> tenantAuthorities = new HashSet<>();
        if (authentication instanceof AnonymousAuthenticationToken) {

            tenantAuthorities.add(new GrantedAuthorityImpl(roleAnonymous));
            logger.debug("The granted authorities collection for AnonymousAuthenticationToken is added a - " +
                    roleAnonymous);
        }else{
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority instanceof GrantedAuthorityImpl || authority instanceof SimpleGrantedAuthority) {
                    if(ADMIN_ROLES.contains(getRolePrefix() + authority.getAuthority())){
                        tenantAuthorities.add(new SimpleGrantedAuthority(mappedAdminRole));
                    }else {
                        tenantAuthorities.add(new SimpleGrantedAuthority(getRolePrefix() + authority.getAuthority()));
                    }
                }
            }
        }
        return tenantAuthorities;
    }

    public String getRolePrefix() {
        return rolePrefix;
    }
    public void setMappedAdminRole(String mappedAdminRole){
        this.mappedAdminRole = mappedAdminRole;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public void setADMIN_ROLES(List<String> ADMIN_ROLES) {
        this.ADMIN_ROLES = ADMIN_ROLES;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(ADMIN_ROLES,"ADMIN_ROLES list must be set");
        Assert.notNull(mappedAdminRole, "mapped administrator role must ne set");
        Assert.notNull(roleAnonymous, "anonymous role must be set");
    }
}
