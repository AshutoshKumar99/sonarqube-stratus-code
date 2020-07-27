package com.pb.stratus.onpremsecurity.authentication.filter;

import com.pb.stratus.onpremsecurity.util.HttpSessionRequestCache;
import com.pb.stratus.security.core.authentication.RequestCache;
import org.apache.log4j.Logger;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/20/14
 * Time: 7:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestCacheFilter extends GenericFilterBean {

    private final static Logger logger = Logger.getLogger(RequestCacheFilter.class);
    private String targetResourceParameterName = "InnerTargetResource";

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
    }

    private String filterProcessesUrl = "/j_spring_security_check";

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String innerTargetResource = request.getParameter(targetResourceParameterName);
        if ((innerTargetResource != null && !"".equals(innerTargetResource)) && innerTargetResource.indexOf(filterProcessesUrl) == -1) {
            requestCache.saveRequest((HttpServletRequest) request, innerTargetResource);
        }
        chain.doFilter(request, response);
    }

    public void setTargetResourceParameterName(String targetResourceParameterName) {
        this.targetResourceParameterName = targetResourceParameterName;
    }

}
