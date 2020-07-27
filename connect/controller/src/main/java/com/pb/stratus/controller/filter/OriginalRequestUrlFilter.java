package com.pb.stratus.controller.filter;

import com.pb.stratus.core.common.Constants;
import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class OriginalRequestUrlFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute(Constants.ORIGINAL_REQUEST_URL, getUrl((HttpServletRequest)request));
        chain.doFilter(request, response);
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private String getUrl(HttpServletRequest req) {
        String reqUrl = req.getRequestURL().toString();
        String queryString = req.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            reqUrl += "?"+queryString;
        }
        return reqUrl;
    }


}
