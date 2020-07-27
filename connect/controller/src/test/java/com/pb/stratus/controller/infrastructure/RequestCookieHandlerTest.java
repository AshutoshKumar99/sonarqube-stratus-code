package com.pb.stratus.controller.infrastructure;


import com.pb.stratus.controller.action.ControllerActionTestBase;
import org.junit.Test;

import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestCookieHandlerTest extends ControllerActionTestBase
{
    private RequestCookieHandler requestCookieHandler;

    @Test
    public void testCookieWithValidRequest()
    {
        getRequest().addParameter("cn", "cookie");
        getRequest().addParameter("cv", "value");
        requestCookieHandler = new RequestCookieHandler(getRequest());
        assertTrue(requestCookieHandler.isCookieRequestValid());
    }

    @Test
    public void testCookieNameAndValue()
    {
        getRequest().addParameter("cn", "cookie");
        getRequest().addParameter("cv", "value");
        requestCookieHandler = new RequestCookieHandler(getRequest());
        Cookie cookie = requestCookieHandler.getCookie();
        assertEquals("cookie", cookie.getName());
        assertEquals("value", cookie.getValue());
    }

    @Test
    public void testForDefaultCookieForInvalidRequest()
    {
        getRequest().addParameter("cn", "cookie");
        //omitting cookie value
        requestCookieHandler = new RequestCookieHandler(getRequest());
        assertEquals("null", RequestCookieHandler.DEFAULT_COOKIE.getName());
        assertEquals(null, RequestCookieHandler.DEFAULT_COOKIE.getValue());
    }

}
