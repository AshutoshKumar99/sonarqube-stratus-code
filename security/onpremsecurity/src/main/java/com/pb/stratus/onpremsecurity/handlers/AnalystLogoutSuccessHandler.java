package com.pb.stratus.onpremsecurity.handlers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/24/14
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalystLogoutSuccessHandler implements LogoutSuccessHandler, InitializingBean {

    private String logoutSuccessPage;
    private String LOCALE_PARAM = "lang";

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirect = request.getParameter("redirect");
        if(redirect != null && redirect.equalsIgnoreCase("false")){
            response.setHeader("logout_status","success");
            PrintWriter out = response.getWriter();
            out.print("success");
        }else {
            response.sendRedirect(createLogoutSuccessUrl(request));
        }
    }

    private String createLogoutSuccessUrl(HttpServletRequest request) {
        String locale = request.getParameter(LOCALE_PARAM);
        if( locale != null && !locale.equals(""))
        {
            return logoutSuccessPage + "?lang="+ locale;
        }
        return logoutSuccessPage;
    }

    public void setLogoutSuccessPage(String logoutSuccessPage) {
        this.logoutSuccessPage = logoutSuccessPage;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(logoutSuccessPage, "Please configure logout success page");
    }
}
