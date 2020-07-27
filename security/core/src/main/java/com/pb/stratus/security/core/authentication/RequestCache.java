package com.pb.stratus.security.core.authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/20/14
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RequestCache {

    public void saveRequest(HttpServletRequest request, String requestUrl);

    public String getRequest(HttpServletRequest request);
}
