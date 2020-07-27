package com.pb.stratus.security.core.resourceauthorization;

/**
 * User: sh003bh
 * Date: 11/16/11
 * Time: 11:19 AM
 * Enum to keep a mapping of the roles defined in the spring file and the
 * authorization role in the auth files.
 */
public enum AuthorizationRoles {

    ADMINISTRATORS("Administrators"),
    USER("User"),
    USERS("Users"),
    ADMINISTRATOR("Administrator"),
    PUBLIC("Public");

    private String roleName;

    AuthorizationRoles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
