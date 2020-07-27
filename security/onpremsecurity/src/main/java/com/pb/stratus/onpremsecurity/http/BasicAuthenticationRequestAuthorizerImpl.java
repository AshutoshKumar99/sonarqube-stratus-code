package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.util.Base64;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 * Date: 4/3/14
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicAuthenticationRequestAuthorizerImpl implements RequestAuthorizer {
    private static Log log = LogFactory.getLog(BasicAuthenticationRequestAuthorizerImpl.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_SCHEME = "Basic";


    public String getAnonymousPassword() {
        return anonymousPassword;
    }

    public String getAnonymousUsername() {
        return anonymousUsername;
    }

    private String anonymousUsername;
    private String anonymousPassword;

    public void setAnonymousPassword(String anonymousPassword) {
        this.anonymousPassword = anonymousPassword;
    }

    public void setAnonymousUsername(String anonymousUsername) {
        this.anonymousUsername = anonymousUsername;
    }

    @Override
    public boolean isAuthorized(ClientHttpRequest request) {
        String authValue = request.getHeaders().getFirst(AUTH_HEADER);
        boolean authorized = (authValue != null) && (authValue.startsWith(AUTH_SCHEME + " "));

        if (log.isDebugEnabled()) {
            String authmsg = authorized ? "Request is authorized." : "Request is not authorized.";
            log.debug("isAuthorized(): " + authmsg + "  Authorization header value: " + authValue);
        }

        return authorized;
    }

    @Override
    public boolean authorize(ClientHttpRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        boolean debugLogging = log.isDebugEnabled();
        Authentication authentication = (securityContext != null) ?
                securityContext.getAuthentication() : null;

        if (authentication instanceof AnonymousAuthenticationToken) {
            if (anonymousUsername != null && anonymousPassword != null) {
                log.debug("Adding authorization header with anonymous credentials.");
                addBasicAuthenticationHeader(request, anonymousUsername, anonymousPassword);
            }
            return true;
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
            addBasicAuthenticationHeader(request, auth.getPrincipal().toString(), auth.getCredentials().toString());
            return true;
        } else if (authentication == null) {
            // When a user logs in first time.
            HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String username = servletRequest.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) != null ?
                    servletRequest.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) :
                    (String) servletRequest.getAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
            String password = servletRequest.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY) != null ?
                    servletRequest.getParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY) :
                    (String) servletRequest.getAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY);
            addBasicAuthenticationHeader(request, username, password);
        }

        if (debugLogging) {
            log.debug("authorize(): cannot authorize the request.");
        }
        return false;
    }

    private void addBasicAuthenticationHeader(ClientHttpRequest request, String username, String password) {
        if (username != null && password != null) {
            String usernamePasswordPair = username + ":" + password;
            String headerValue = AUTH_SCHEME + " " + Base64.encode(usernamePasswordPair.getBytes());
            request.getHeaders().set(AUTH_HEADER, headerValue);
        } else {
            // throw exception when user name and password not found in incoming request.
            throw new RuntimeException("You are not authorized, user name & password not found in request.");
        }
    }

}
