package com.pb.stratus.controller.action;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;

/**
 * returns authentication info (current logged in user name) along with sso.start.url and slo.start.url.
 */
public class AuthenticationAction extends DataInterchangeFormatControllerAction {

    private static final String DEFAULT_IDLE_SESSION_TIMEOUT = "idleSessionTimeout";
    private static final Logger LOGGER = LogManager.getLogger(AuthenticationAction.class);

    private ControllerConfiguration config;

    public AuthenticationAction(ControllerConfiguration config) {
        this.config = config;
    }

    //returns public or secure on basis of config.authType's value
    //(access.authenticated.enabled = false, access.public.enabled = false) is not valid case
    //config.authType = both or public returns "secured" otherwise "public" is returned
    public String getAccessMode() {
        String access;
        String authType = config.getAuthType();
        if(authType.equals("both") || authType.equals("public")) {
            access = "public";
        }
        else {
            access = "secured";
        }
        return access;
    }

    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException {
        String requestUrl = request.getParameter("authInfoRequestUrl");
        if (StringUtils.isNotBlank(requestUrl)) {
            request.setAttribute(Constants.ORIGINAL_REQUEST_URL, requestUrl);
        }
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        JSONObject authenticationInfo = new JSONObject();
        JSONObject authType = new JSONObject();
        if (authentication != null && authentication.isAuthenticated()) {
            JSONObject user = new JSONObject();
            user.put("username", authentication.getPrincipal().toString());
            authenticationInfo.put("user", user);
            if (authentication instanceof AnonymousAuthenticationToken) {
                authType.put(Constants.IS_PUBLIC_USER, "true");
            } else {
                authType.put(Constants.IS_PUBLIC_USER, "false");
            }
            authenticationInfo.put("authType", authType);
            Iterator<? extends GrantedAuthority> entries =  authentication.getAuthorities().iterator();
            while (entries.hasNext()) {
               String authorityValue = entries.next().getAuthority();
                if(authorityValue.equals(Constants.SUPER_USER) || authorityValue.equals(Constants.ADMIN) ){
                    authenticationInfo.put("isAdmin", "true");
                }
				if(authorityValue.equals(Constants.SPATIAL_SUB_ADMIN)){
					authenticationInfo.put("isSubAdmin", "true");
				}
            }
        }
        JSONArray properties = new JSONArray();
        int i = 0;
        if (config.getSsoStartUrl() != null) {
            JSONObject property = new JSONObject();
            property.put("name", Constants.SSO_START_URL);
            property.put("value", config.getSsoStartUrl());
            properties.add(i, property);
            i++;
        }
        if (config.getSloStartUrl() != null) {
            JSONObject property = new JSONObject();
            property.put("name", Constants.SLO_START_URL);
            property.put("value", config.getSloStartUrl());
            properties.add(i, property);
            i++;
        }
        authenticationInfo.put("properties", properties);
        String access = getAccessMode();
        authenticationInfo.put("access", access);
        JSONObject authenticationFinal = new JSONObject();
        authenticationFinal.put("authenticationInfo", authenticationInfo);
        return authenticationFinal;
    }
}