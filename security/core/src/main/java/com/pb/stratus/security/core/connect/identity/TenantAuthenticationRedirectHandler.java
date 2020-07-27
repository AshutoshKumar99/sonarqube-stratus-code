package com.pb.stratus.security.core.connect.identity;

import com.pb.stratus.core.configuration.TenantConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class TenantAuthenticationRedirectHandler implements AuthenticationFailureHandler, AuthenticationEntryPoint {
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.sendRedirect(getSsoStartUrl(request));
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendRedirect(getSsoStartUrl(request));
    }

    private static final String getSsoStartUrl(HttpServletRequest request) {
        return TenantConfiguration.getTenantConfiguration(request).getSsoStartUrl();
    }
}
