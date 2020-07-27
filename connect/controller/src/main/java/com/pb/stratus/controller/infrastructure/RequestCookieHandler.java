package com.pb.stratus.controller.infrastructure;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Default class for handling cookies requested from the client side. If
 * there is a specific requirement where the client need a cookie with a
 * given  name and given value then this class should be used.
 * In such circumstances cookie-name should be sent as "cn" request parameter
 * and cookie-value should be sent as "cv" as a request parameter.
 */
public class RequestCookieHandler
{
    // parameter name for the cookie in the HttpServletRequest
    private static final String COOKIE_NAME = "cn";
    // parameter value for cookie in HttpServletRequest
    private static final String COOKIE_VALUE = "cv";
    // Default cookie which will be returned in case of invalid request.
    // making it public for testing.
    public static final Cookie DEFAULT_COOKIE = new Cookie("null" , null);
    private String cookieValue;
    private String cookieName;

    /**
     * Constructor which takes HttpServletRequest and checks if a request for
     * cookie has been made.
     * @param request HttpServletRequest
     */
    public RequestCookieHandler(HttpServletRequest request)
    {
        cookieValue = request.getParameter(COOKIE_VALUE);
        cookieName =  request.getParameter(COOKIE_NAME);
    }

    /**
     * Preventive method to check if a valid request for a cookie has been
     * made from the client. A valid request is one in which both the
     * parameters "cn" and "cv" are not null and blank.
     * @return true if the both "cn" and "cv" are present and are both not
     * bull and not blank.
     */
    public boolean isCookieRequestValid()
    {
        return !StringUtils.isBlank(cookieValue) &&
                !StringUtils.isBlank(cookieName);
    }

    /**
     * This method will return a cookie with the requested name and value. If
     * the request is invalid then a default cookie with both name and value
     * set to null will bve returned.
     * @return Cookie the requested cookie.
     */
    public Cookie getCookie()
    {
        if(isCookieRequestValid())
        {
            Cookie cookie =  new Cookie(cookieName, cookieValue);
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/");
            return cookie;
        }
        return  DEFAULT_COOKIE;
    }
}
