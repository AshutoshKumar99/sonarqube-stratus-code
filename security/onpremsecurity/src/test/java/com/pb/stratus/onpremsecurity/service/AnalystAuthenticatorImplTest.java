package com.pb.stratus.onpremsecurity.service;

import com.pb.spectrum.platform.server.config.core.ws.productapi.impl.SecurityProductServiceImpl;
import com.pb.spectrum.platform.server.config.core.ws.productapi.impl.UserAccount;
import com.pb.stratus.onpremsecurity.authentication.AnalystAuthenticatorImpl;
import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import com.pb.stratus.onpremsecurity.util.JWTAuthorizationHandler;
import com.pb.stratus.security.core.authentication.AuthenticationFailureException;
import com.pb.stratus.security.core.authentication.IAuthenticator;
import com.pb.stratus.security.core.connect.identity.RequestBasisAccessConfigurationResolver;
import com.pb.stratus.security.core.jaxb.User;
import org.apache.cxf.transport.http.HTTPException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.xml.ws.WebServiceException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 */

public class AnalystAuthenticatorImplTest {

    private RequestBasisAccessConfigurationResolver requestBasisAccessConfigurationResolver;
    private SecurityProductServiceImpl securityProductService;
    private AnalystAuthenticatorImpl analystAuthenticator;
    private JWTAuthorizationHandler mockHandler;

    /**
     * Initial set up of AccountManagerService and instantiation of AnalystAuthenticatorImpl.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        requestBasisAccessConfigurationResolver = mock(RequestBasisAccessConfigurationResolver.class);
        analystAuthenticator = new AnalystAuthenticatorImpl();
        securityProductService = mock(SecurityProductServiceImpl.class);
        mockHandler = mock(JWTAuthorizationHandler.class);
        analystAuthenticator.setJwtAuthorizationHandler(mockHandler);
        analystAuthenticator.setSecurityProductService(securityProductService);
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setParameter("username", "pPublic");
        request.setParameter("password", "PPassword1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    /**
     * test login with authentic user details.
     *
     * @throws Exception
     */
    public void testLoginWithCorrectCredential() throws Exception {
        UserAccount mockAccount = createMockAccount();
        String username = "admin", password = "admin";
        SpectrumToken spectrumToken = new SpectrumToken();
        spectrumToken.setSession("123");
        spectrumToken.setAccessToken("abc");
        spectrumToken.setUserId("admin");

        when(mockHandler.getSpectrumToken(
                Mockito.anyBoolean())).thenReturn(spectrumToken);
        User user = analystAuthenticator.login(username, password);
        assertEquals("user", user.getRoles().get(0).getName());
    }

    @Test(expected = AuthenticationFailureException.class)
    public void testLoginWithInCorrectAuthorizationHeader() throws Exception {
        String username = "wronguser", password = "wrongpassword";
        HTTPException httpEx = new HTTPException(401, "Unauthorised", new URL("http://anyUrl"));
        WebServiceException wse = new WebServiceException(httpEx);
        when(mockHandler.getSpectrumToken(
                Mockito.anyBoolean())).thenThrow(wse);
        User user = null;
        try {
            user = analystAuthenticator.login(username, password);
        } catch (AuthenticationFailureException ex) {
            assertEquals(IAuthenticator.INVALID_CREDENTIALS, ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = AuthenticationFailureException.class)
    public void testLoginWithAnyServiceError() throws Exception {
        String username = "wronguser", password = "wrongpassword";
        HTTPException httpEx = new HTTPException(500, "Internal Server Error", new URL("http://anyUrl"));
        WebServiceException wse = new WebServiceException(httpEx);
        when(mockHandler.getSpectrumToken(
                Mockito.anyBoolean())).thenThrow(wse);
        User user = null;
        try {
            user = analystAuthenticator.login(username, password);
        } catch (AuthenticationFailureException ex) {
            assertEquals(IAuthenticator.SERVICE_ERROR, ex.getMessage());
            throw ex;
        }
    }


    /**
     * Mock an expected mock role object.
     *
     * @return roles - a list of roles.
     */
    private UserAccount createMockAccount() {
        UserAccount account = new UserAccount();
        UserAccount.Roles roles = new UserAccount.Roles();
        roles.getRole().add("user");
        account.setRoles(roles);
        return account;
    }

}
