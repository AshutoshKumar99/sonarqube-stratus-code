
/**
 * This strategy prefixes the authentication failure url with the tenant name. Tenant name
 * is retrieved from session.
 * Created by gu003du on 23-Sep-16.
 */
package com.pb.stratus.onpremsecurity.adminconsole.identity;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class TenantRedirectStrategy implements RedirectStrategy {

    private static final String TENANT_ATTR = "tenant";

    private static final Logger logger = LogManager.getLogger(TenantRedirectStrategy.class);

    private RedirectStrategy redirectStrategy;


    public TenantRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        HttpSession session = request.getSession(false);
        Object tenantAttr = session.getAttribute(TENANT_ATTR);

        if (tenantAttr instanceof String && StringUtils.isNotEmpty((String) tenantAttr)) {
            String redirectUrl = "/" + tenantAttr + url;
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        } else {
            String msg = "Failed to find the tenant name in session.";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }
}
