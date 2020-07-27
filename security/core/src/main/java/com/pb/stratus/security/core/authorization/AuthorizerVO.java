package com.pb.stratus.security.core.authorization;

import com.pb.stratus.security.core.jaxb.Role;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 * Date: 3/19/14
 * Time: 6:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizerVO {

    private String tenant;

    private String product;

    private Set<Role> allRolesInSystem;

    private boolean isAuthenticatedAccessEnabled;

    private boolean isPublicAccessEnabled;

    private boolean isCustomRoleSupported;

    public boolean isAuthenticatedAccessEnabled() {
        return isAuthenticatedAccessEnabled;
    }

    public void setAuthenticatedAccessEnabled(boolean authenticatedAccessEnabled) {
        isAuthenticatedAccessEnabled = authenticatedAccessEnabled;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public boolean isPublicAccessEnabled() {
        return isPublicAccessEnabled;
    }

    public void setPublicAccessEnabled(boolean publicAccessEnabled) {
        isPublicAccessEnabled = publicAccessEnabled;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public boolean isCustomRoleSupported() {
        return isCustomRoleSupported;
    }

    public void setCustomRoleSupported(boolean customRoleSupported) {
        isCustomRoleSupported = customRoleSupported;
    }

    public Set<Role> getAllRolesInSystem () {
        return allRolesInSystem;
    }

    public void setAllRolesInSystem (Set<Role> allRolesInSystem) {
        this.allRolesInSystem = allRolesInSystem;
    }
}
