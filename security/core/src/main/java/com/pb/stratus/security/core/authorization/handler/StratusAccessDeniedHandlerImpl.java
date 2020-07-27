package com.pb.stratus.security.core.authorization.handler;

import com.pb.stratus.core.configuration.TenantConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author vi001ty
 *
 *  This class handle AccessDeniedException.
 *   Redirect to error page or first(lexicographic) authorize mapconfig if defaultmap is not accessable.   
 */
public class StratusAccessDeniedHandlerImpl implements AccessDeniedHandler 
{
    private String errorPage;
    
    private DefaultMapConfigRedirectHandler mapconfigRedirecthandler;
    private AuthorizationUtils authorizationUtils;

    public void setMapconfigRedirecthandler(
            DefaultMapConfigRedirectHandler mapconfigRedirecthandler)
    {
        this.mapconfigRedirecthandler = mapconfigRedirecthandler;
    }

    public void setAuthorizationUtils(AuthorizationUtils authorizationUtils) {
        this.authorizationUtils = authorizationUtils;
    }

    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException,
            ServletException
    {
        if (!response.isCommitted()) {
            if (errorPage != null) {
                
                String mapcfg = request.getParameter(AuthorizationUtils.REQUEST_PARAM_MAPCFG);
                if(request.getPathInfo() == null && (StringUtils.isBlank(mapcfg))){
                    mapconfigRedirecthandler.handle(request,response);
                }

                if(!response.isCommitted() && authorizationUtils.isAnonymousUser()){
                    response.sendRedirect(getSsoStartUrl(request));
                }
                if (!response.isCommitted()) {
                    // Put exception into request scope (perhaps of use to a view)
                    request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);
    
                    // Set the 403 status code.
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    
                    // forward to error page.
                    RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
                    dispatcher.forward(request, response);
                }
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
            }
        }
        

    }
    
    /**
     * The error page to use. Must begin with a "/" and is interpreted relative to the current context root.
     *
     * @param errorPage the dispatcher path to display
     *
     * @throws IllegalArgumentException if the argument doesn't comply with the above limitations
     */
    public void setErrorPage(String errorPage) {
        if ((errorPage != null) && !errorPage.startsWith("/")) {
            throw new IllegalArgumentException("errorPage must begin with '/'");
        }

        this.errorPage = errorPage;
    }
    
    
    /**
     * To get SSO URL. 
     * @param request
     * @return
     */
    private final String getSsoStartUrl(HttpServletRequest request) {
        return TenantConfiguration.getTenantConfiguration(request).getSsoStartUrl();
    }

}
