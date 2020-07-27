package com.pb.stratus.controller.datainterchangeformat;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Base class for DataInterchangeFormats. The primary purpose of this class
 * is to provide the functionality other than sending of the response such
 * as sending cookie. All such common methods should be put int this class.
 */
public abstract class BaseDataInterchangeFormatResponse
        implements DataInterchangeFormatResponse
{
    /**
     * Add the cookies to the response.
     * @param response HttpServletResponse
     * @param cookies Cookie...
     */
    public void addCookieToResponse(HttpServletResponse response,
            Cookie...cookies)
    {
        for(Cookie cookie: cookies)
        {
            response.addCookie(cookie);
        }
    }
}
