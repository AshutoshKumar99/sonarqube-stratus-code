package com.pb.stratus.onpremsecurity.adminconsole.identity;

import com.pb.stratus.core.exception.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created with IntelliJ IDEA.
 * User: al021ch
 * Date: 3/22/14
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalystSsoRedirectHelper implements InitializingBean {

    private static final Logger logger = Logger.getLogger(AnalystSsoRedirectHelper.class);

    private static final String m_tenantName = "@{tenant}";
    private static final String CURRENT_TENANT = "currenttenant";
    private static final String PATH_SEPARATOR = "/";
    private static final String ORIGINAL_URI = "@{originalUriEncoded}";

    private String m_ssoStartUrl;
    private String m_processedSsoStartUrl;
    private String m_adminConsoleExternalUrl;

    @Override
    public void afterPropertiesSet() {
        if (org.apache.commons.lang.StringUtils.isEmpty(m_ssoStartUrl)) {
            throw new ConfigurationException("Sso start url required.");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(m_adminConsoleExternalUrl)) {
            throw new ConfigurationException("admin console external url required.");
        }
        m_processedSsoStartUrl = m_ssoStartUrl;
    }

    public void setSsoStartUrl(String ssoStartUrl) {
        m_ssoStartUrl = ssoStartUrl;
    }


    public void setAdminConsoleExternalUrl(String adminConsoleExternalUrl) {
        m_adminConsoleExternalUrl = adminConsoleExternalUrl;
    }


    public void sendSsoRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder redirectBuilder = new StringBuilder();
        // Admin URL
        String tenantAdminUrl = doPlaceholderReplacement(m_adminConsoleExternalUrl +
                PATH_SEPARATOR + m_tenantName, request);
        String loginPath = doPlaceholderReplacement(m_processedSsoStartUrl, request);

        if (loginPath.startsWith("login/?TargetResource=")) {
            redirectBuilder.append(tenantAdminUrl);
            redirectBuilder.append(PATH_SEPARATOR);
        } else {
            loginPath = loginPath.replace(ORIGINAL_URI,
                    URLEncoder.encode(tenantAdminUrl, StandardCharsets.UTF_8.toString()));
        }
        redirectBuilder.append(loginPath);

        response.sendRedirect(redirectBuilder.toString());
    }

    private String doPlaceholderReplacement(String text, HttpServletRequest request) {
        String replacedText = null;
        logger.debug(" Replace: tenantName Information");
        replacedText = doTenantReplacement(text, request);
        return replacedText;
    }

    /**
     * Create dynamic URL information for replacing the tenant placeholder
     *
     * @param text
     * @param request
     * @return
     */
    private String doTenantReplacement(String text, HttpServletRequest request) {
        String tenant = null;
        if (request != null) {
            tenant = (String) request.getAttribute(CURRENT_TENANT);
            if (tenant != null) {
                text = text.replace(m_tenantName, tenant);
                logger.debug(" Tenant Replaced: " + tenant);
            } else {
                logger.error(" Tenant missing:  URL replacement failed!");
            }
        }

        return text;
    }
}
