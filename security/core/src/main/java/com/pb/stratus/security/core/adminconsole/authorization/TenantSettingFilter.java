package com.pb.stratus.security.core.adminconsole.authorization;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TenantSettingFilter extends GenericFilterBean {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String servletPath = ((HttpServletRequest) request).getServletPath();
        if (servletPath != "") {
            String[] servletPathArray = servletPath.split("/");
            if (servletPathArray.length > 1) {
                request.setAttribute("currenttenant", servletPathArray[1]);
            }
        }
        chain.doFilter(request, response);
    }
}

