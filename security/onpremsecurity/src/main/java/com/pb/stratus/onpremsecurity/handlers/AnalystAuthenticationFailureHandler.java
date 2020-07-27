package com.pb.stratus.onpremsecurity.handlers;

import com.pb.stratus.core.configuration.TenantConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/22/14
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalystAuthenticationFailureHandler implements AuthenticationFailureHandler, InitializingBean{
    private final String BAD_CREDENTIALS_EXCEPTION = "1";
    private final String WEB_SERVICE_EXCEPTION = "2";

    private Map<String, String> errorTypeMap;

    public void setErrorParam(String errorParam) {
        this.errorParam = errorParam;
    }

    private String errorType = "1";
    private String errorParam;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String redirect = request.getParameter("redirect");
        errorType = errorTypeMap.get(exception.getClass().getSimpleName());
        if(redirect != null && redirect.equalsIgnoreCase("false")){
            response.setHeader("login_status","failure");
            response.setHeader("error_code",errorType);
            PrintWriter out = response.getWriter();
            out.print("failure:"+ errorType);
        }else {
            String ssoStartUrl = getSsoStartUrl(request);
            String targetUrl = createAuthenticationFailureUrl(ssoStartUrl);
            response.sendRedirect(targetUrl);
        }
    }

    private String createAuthenticationFailureUrl(String ssoStartUrl) {
        String targetUrl = (ssoStartUrl.indexOf('?') == -1) ?  ssoStartUrl + "?" + errorParam + "=" + errorType : ssoStartUrl + "&" + errorParam + "=" + errorType;
        return targetUrl;
    }

    private static final String getSsoStartUrl(HttpServletRequest request) {
        return TenantConfiguration.getTenantConfiguration(request).getSsoStartUrl();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(errorParam, "Please specify error parameter name");
        Assert.notNull(errorTypeMap, "Please specify error type mapping");

    }

    public void setErrorTypeMap(Map<String, String> errorTypeMap) {
        this.errorTypeMap = errorTypeMap;
    }
}
