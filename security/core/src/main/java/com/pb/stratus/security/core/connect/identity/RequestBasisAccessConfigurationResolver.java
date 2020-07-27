package com.pb.stratus.security.core.connect.identity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.pb.spectrum.platform.server.common.security.token.TokenManagerService;
import com.pb.spectrum.platform.server.common.security.token.TokenInfo;
import com.pb.stratus.core.common.application.SpringApplicationContextLocator;
import com.pb.stratus.core.configuration.PropertiesFileControllerConfiguration;
import com.pb.stratus.core.configuration.TenantConfiguration;
import com.pb.stratus.security.core.authentication.AuthenticationFailureException;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

import static com.pb.stratus.security.core.connect.identity.Constants.AUTH_TYPE_BOTH;
import static com.pb.stratus.security.core.connect.identity.Constants.AUTH_TYPE_PUBLIC;

/**
 * Author: sh003bh
 * Date: 10/19/11
 * Time: 6:14 PM
 */
public class RequestBasisAccessConfigurationResolver implements
        AuthenticationAccessConfiguration, AnonymousAccessConfiguration {

    @Override
    public String getAnonymousUserName() {
        return TenantConfiguration.getTenantConfiguration((getCurrentRequest())).getAnonymousUserName();
    }

    @Override
    public String getAnonymousPassword() {
        return TenantConfiguration.getTenantConfiguration((getCurrentRequest())).getAnonymousPassword();
    }

    @Override
    public boolean isAnonymousLoginAllowed() {
        String authType = TenantConfiguration.getTenantConfiguration((getCurrentRequest())).getAuthType();
        return AUTH_TYPE_PUBLIC.equalsIgnoreCase(authType) || AUTH_TYPE_BOTH.equalsIgnoreCase(authType);
    }

    @Override
    public boolean onlyAdminAccessAllowed() {
        String authType = TenantConfiguration.getTenantConfiguration((getCurrentRequest())).getAuthType();
        return AUTH_TYPE_PUBLIC.equalsIgnoreCase(authType);
    }

    private HttpServletRequest getCurrentRequest() {
        HttpServletRequest curRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        return curRequest;
    }
}
