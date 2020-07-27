package com.pb.stratus.security.core.resourceauthorization;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: sh003bh
 * Date: 11/16/11
 * Time: 11:29 AM
 */
public class AuthorizationRolesTest {
    @Test
    public void shouldReturnCorrectRole() {
        String expectedRole = "Administrators";
        String actualRole =
                AuthorizationRoles.ADMINISTRATORS.getRoleName();
        assertEquals(expectedRole, actualRole);

        expectedRole = "Users";
        actualRole =
                AuthorizationRoles.USERS.getRoleName();
        assertEquals(expectedRole, actualRole);

        expectedRole = "Public";
        actualRole =
                AuthorizationRoles.PUBLIC.getRoleName();
        assertEquals(expectedRole, actualRole);
    }
}
