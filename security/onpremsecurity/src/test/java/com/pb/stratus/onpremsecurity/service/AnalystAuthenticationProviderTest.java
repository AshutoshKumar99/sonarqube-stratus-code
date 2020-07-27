package com.pb.stratus.onpremsecurity.service;

import com.pb.stratus.onpremsecurity.authentication.AnalystAuthenticationProvider;
import com.pb.stratus.onpremsecurity.authentication.AnalystAuthenticatorImpl;
import com.pb.stratus.security.core.authentication.AuthenticationFailureException;
import com.pb.stratus.security.core.authentication.IAuthenticator;
import com.pb.stratus.security.core.jaxb.Role;
import com.pb.stratus.security.core.jaxb.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Indicates a test class can process a specific  {@link
 * org.springframework.security.core.Authentication} implementation.
 *
 * Created with IntelliJ IDEA.
 * User: ma050si
 */

public class AnalystAuthenticationProviderTest {
    private IAuthenticator analystAuthenticator;

    private AnalystAuthenticationProvider analystAuthenticationProvider;
    private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

    /**
     * Intialial set up of variables.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        analystAuthenticator = mock(AnalystAuthenticatorImpl.class);
        analystAuthenticationProvider = new AnalystAuthenticationProvider(analystAuthenticator);
    }

    /**
     * Test to authenticate the valid user with correct username and password and Assert the
     * authentication token with boolean value true.
     * @throws Exception
     */
    @Test
    public void testAuthenticatedUser() throws Exception {
        List<String> mockRoles = createMockRolesResponse();
        String username = "admin", password = "admin";
        User user = new User();
        user.setUserName("admin");
        user.setDisplayName("admin");
        Role role = new Role();
        role.setName("admin");
        user.getRoles().add(role);
        user.setPassword("admin");
        Authentication authenticationToken = null;
        when(analystAuthenticator.login(Mockito.anyString(),Mockito.anyString())).thenReturn(user);
        usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            authenticationToken = analystAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
        } catch (AuthenticationException e) {

        }
        assertTrue(authenticationToken.isAuthenticated());
    }

    /**
     * Test to authenticate the valid/invalid user with incorrect username and password and assert the
     * BadCredentialException.
     * @throws Exception
     */
    @Test(expected = BadCredentialsException.class)
    public void testAuthenticateUserWithIncorrectCredentials() throws Exception {
        //List<String> mockRoles = createMockRolesResponse();

        String username = "incorrect", password = "incorrect";
        when(analystAuthenticator.login(Mockito.anyString(),Mockito.anyString())).thenThrow(new AuthenticationFailureException(IAuthenticator.INVALID_CREDENTIALS));
        usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        analystAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
    }

    @Test(expected = AuthenticationServiceException.class)
    public void testServiceException()  throws  Exception{
        Exception expectedException = new WebServiceException();
        String username = "incorrect", password = "incorrect";
        when(analystAuthenticator.login(Mockito.anyString(),Mockito.anyString())).
                thenThrow(new AuthenticationFailureException(IAuthenticator.SERVICE_ERROR));
        usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        analystAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
    }

    /**
     * Mock an expected mock role object.
     * @return roles - a list of roles.
     */
    private List<String> createMockRolesResponse() {
        List<String> roles = new ArrayList<String>();
        roles.add("role1");
        roles.add("role2");
        return roles;
    }

}
