package com.pb.stratus.security.core.authentication;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy to decide whether a request is made for a static resource, like
 * css, js, images etc.
 */
public class StaticResourceAuthenticationBypassStrategy implements
        AuthenticationBypassStrategy, InitializingBean {

    // injected
    private List<String> urlPatterns;
    private ShouldNotBeControllerCallBypassStrategy
            shouldNotBeControllerCallBypassStrategy;

    private boolean disableControllerCallBypassStrategy = false;

    private final List<String> staticResourceUrls = new ArrayList<String>();
    private final PathMatcher urlMatcher = new AntPathMatcher();

    public void setDisableControllerCallBypassStrategy(boolean disableControllerCallBypassStrategy) {
        this.disableControllerCallBypassStrategy = disableControllerCallBypassStrategy;
    }

    /**
     * Method will return true is request is for a static resource and does not
     * request a controller. If a static resource is accessed via a controller
     * then it return false.
     *
     * @param req
     * @return
     */
    @Override
    public boolean shouldBypassAuthentication(ServletRequest req) {
        HttpServletRequest request = (HttpServletRequest) req;
        String uri = request.getRequestURI();
        for (String staticResourceUrl : staticResourceUrls) {
            if (urlMatcher.match(staticResourceUrl, uri)) {
                // make sure that the static resource is not accessed via controller
                if (!disableControllerCallBypassStrategy) {
                    return shouldNotBeControllerCallBypassStrategy.
                            shouldBypassAuthentication(req);
                } else {
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(urlPatterns);
        if (!disableControllerCallBypassStrategy) {
            Assert.notNull(shouldNotBeControllerCallBypassStrategy);
        }

        for (String urlPattern : urlPatterns) {
            staticResourceUrls.add(urlPattern);
        }
    }

    public List<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(List<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public ShouldNotBeControllerCallBypassStrategy
    getShouldNotBeControllerCallBypassStrategy() {
        return shouldNotBeControllerCallBypassStrategy;
    }

    public void setShouldNotBeControllerCallBypassStrategy(
            ShouldNotBeControllerCallBypassStrategy
                    shouldNotBeControllerCallBypassStrategy) {
        this.shouldNotBeControllerCallBypassStrategy =
                shouldNotBeControllerCallBypassStrategy;
    }

    public List<String> getStaticResourceUrls() {
        return this.staticResourceUrls;
    }
}
