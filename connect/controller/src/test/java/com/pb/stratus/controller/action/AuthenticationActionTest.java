package com.pb.stratus.controller.action;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationActionTest {

    @Test
    public void UnAuthenticatedUserReturnedTest() throws Exception {
        String sloUrl = "sloUrl";
        String ssoUrl = "ssoUrl";
        String authType = "both";
        MockHttpServletRequest request = new MockHttpServletRequest();
        ControllerConfiguration config = mock(ControllerConfiguration.class);
        when(config.getSsoStartUrl()).thenReturn(ssoUrl);
        when(config.getSloStartUrl()).thenReturn(sloUrl);
        when(config.getAuthType()).thenReturn(authType);

        AuthenticationAction action = new AuthenticationAction(config);
        Object actionReturn = action.createObject(request);
        JSONObject json = (JSONObject) actionReturn;
        JSONObject authenticationInfo = (JSONObject) json.get("authenticationInfo");
        JSONArray properties = (JSONArray) authenticationInfo.get("properties");
        JSONObject property = (JSONObject) properties.get(0);
        String access = (String) authenticationInfo.get("access");
        assertEquals(access, "public");
        assertEquals(Constants.SSO_START_URL, property.get("name"));
        assertEquals(ssoUrl, property.get("value"));
        property = (JSONObject) properties.get(1);
        assertEquals(Constants.SLO_START_URL, property.get("name"));
        assertEquals(sloUrl, property.get("value"));
    }

    @Test
    public void AuthenticatedUserReturnedTest() throws Exception {

        String user = "Authenticated User";
        String sloUrl = "sloUrl";
        String ssoUrl = "ssoUrl";
        String authType = "both";

        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.isAuthenticated()).thenReturn(true);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ControllerConfiguration config = mock(ControllerConfiguration.class);
        when(config.getSsoStartUrl()).thenReturn(ssoUrl);
        when(config.getSloStartUrl()).thenReturn(sloUrl);
        when(config.getAuthType()).thenReturn(authType);

        AuthenticationAction action = new AuthenticationAction(config);
        Object actionReturn = action.createObject(request);
        JSONObject json = (JSONObject) actionReturn;
        JSONObject authenticationInfo = (JSONObject) json.get("authenticationInfo");
        JSONObject authenticatedUser = (JSONObject) authenticationInfo.get("user");
        assertEquals(user, authenticatedUser.get("username"));
        JSONArray properties = (JSONArray) authenticationInfo.get("properties");
        JSONObject property = (JSONObject) properties.get(0);
        String access = (String) authenticationInfo.get("access");
        assertEquals(access, "public");
        assertEquals(Constants.SSO_START_URL, property.get("name"));
        assertEquals(ssoUrl, property.get("value"));
        property = (JSONObject) properties.get(1);
        assertEquals(Constants.SLO_START_URL, property.get("name"));
        assertEquals(sloUrl, property.get("value"));
    }

    @Test
    public void getBothInAuthTest() throws Exception{
        String authType = "both";
        ControllerConfiguration config = mock(ControllerConfiguration.class);
        when(config.getAuthType()).thenReturn(authType);

        AuthenticationAction action = new AuthenticationAction(config);
        String access = action.getAccessMode();
        assertEquals(access, "public");
    }

    @Test
    public void getSecuredInAuthTest() throws Exception{
        String authType = "secured";
        ControllerConfiguration config = mock(ControllerConfiguration.class);
        when(config.getAuthType()).thenReturn(authType);

        AuthenticationAction action = new AuthenticationAction(config);
        String access = action.getAccessMode();
        assertEquals(access, "secured");
    }

    @Test
    public void getPublicInAuthTest() throws Exception{
        String authType = "public";
        ControllerConfiguration config = mock(ControllerConfiguration.class);
        when(config.getAuthType()).thenReturn(authType);

        AuthenticationAction action = new AuthenticationAction(config);
        String access = action.getAccessMode();
        assertEquals(access, "public");
    }
}
