package com.pb.stratus.security.core.adminconsole.authorization;
import org.junit.Test;
import org.springframework.security.access.SecurityConfig;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: al021ch
 * Date: 3/24/14
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class StratusRoleVoterTest
{

    /**
     * Testing supported roles method
     */
    @Test
    public void testSupportedRoles() throws Exception
    {
        String role1 = "ROLE_Administrators";
        String role2 = "ROLE_Public";
        StratusRoleVoter roleVoterStratus = new StratusRoleVoter();
        ArrayList<String> roles = new ArrayList<String>();
        assertEquals(0, roleVoterStratus.getSupportedRoles().size());
        roles.add(role1);
        roleVoterStratus.setSupportedRoles(roles);
        assertEquals(1, roleVoterStratus.getSupportedRoles().size());
        roles.add(role2);
        ArrayList<String> returnedSupportedRoles = roleVoterStratus.getSupportedRoles();
        assertEquals(2, returnedSupportedRoles.size());
        assertEquals(true,returnedSupportedRoles.contains(role1));
        assertEquals(true,returnedSupportedRoles.contains(role2));
        assertEquals(false,returnedSupportedRoles.contains(role1.toLowerCase()));
    }

    /**
     * Testing supports method for every role
     */
    @Test
    public void testSupports() throws Exception
    {
        String role1 = "ROLE_Administrators";
        String role2 = "ROLE_Public";
        ArrayList<String> roles = new ArrayList<String>();
        roles.add(role1);
        roles.add(role2);
        StratusRoleVoter roleVoterStratus = new StratusRoleVoter();
        roleVoterStratus.setSupportedRoles(roles);
        assertEquals(roleVoterStratus.supports(new SecurityConfig("ROLE_Public")),true);
        assertEquals(roleVoterStratus.supports(new SecurityConfig("ROLE_Administrators")),true);
        assertEquals(roleVoterStratus.supports(new SecurityConfig("ROLE_Administrator")),false);
    }
}
