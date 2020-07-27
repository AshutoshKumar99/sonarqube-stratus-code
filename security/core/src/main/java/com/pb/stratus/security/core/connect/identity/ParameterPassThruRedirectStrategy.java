package com.pb.stratus.security.core.connect.identity;

import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * To change this template use File | Settings | File Templates.
 */
public class ParameterPassThruRedirectStrategy implements RedirectStrategy {

    private RedirectStrategy redirectStrategy;

    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        UrlBuilder urlBuilder = new UrlBuilder((String)request.getAttribute("tenant"), url);
        urlBuilder.addQueryStringFragment(request.getQueryString());
        redirectStrategy.sendRedirect(request, response, urlBuilder.getUrl());
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

}
