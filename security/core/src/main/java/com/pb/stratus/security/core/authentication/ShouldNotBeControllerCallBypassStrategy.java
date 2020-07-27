package com.pb.stratus.security.core.authentication;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Strategy class to check if the request is a call to the controller
 */
public class ShouldNotBeControllerCallBypassStrategy implements
        AuthenticationBypassStrategy, InitializingBean {

    // injected
    private List<String> controllerUrls;

    private final PathMatcher urlMatcher = new AntPathMatcher();
    private final List<String> restrictedUrls = new ArrayList<String>();

    /**
     * Will return true if the request is not a call to the controller otherwise
     * will return false.
     *
     * @param req
     * @return boolean true if the request is not for controller | false if
     * the request is made for controller
     */
    @Override
    public boolean shouldBypassAuthentication(ServletRequest req) {
        HttpServletRequest request = (HttpServletRequest) req;
        String uri = request.getRequestURI();
        for (Object restrictedUrl : restrictedUrls) {
            if (urlMatcher.match((String) restrictedUrl, uri) && !uri.contains("getinfo"))
                return false;
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(controllerUrls);
        for (Iterator<String> i = controllerUrls.iterator(); i.hasNext(); ) {
            restrictedUrls.add(i.next());
        }
    }

    public List<String> getControllerUrls() {
        return controllerUrls;
    }

    public void setControllerUrls(List<String> controllerUrls) {
        this.controllerUrls = controllerUrls;
    }

    public List<String> getRestrictedUrls() {
        return this.restrictedUrls;
    }
}
