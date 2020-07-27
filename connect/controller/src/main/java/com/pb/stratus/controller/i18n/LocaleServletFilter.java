package com.pb.stratus.controller.i18n;

import org.apache.commons.lang.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

public class LocaleServletFilter implements Filter
{
    
    public static final String LOCALE_ATTRIBUTE_NAME = "com.pb.stratus.locale";
    
    public void init(FilterConfig arg0) throws ServletException
    {
    }

    public void destroy()
    {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest hr = (HttpServletRequest) request;
        Locale locale = getLocale(hr);
        LocaleResolver.setLocale(locale);
        storeLocaleInSessionIfNecessary(hr, locale);
        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            LocaleResolver.setLocale(null);
        }
    }
    
    private Locale getLocale(HttpServletRequest request)
    {
        String localeString = request.getParameter("lang");
        if (StringUtils.isBlank(localeString))
        {
            Locale locale = (Locale) request.getSession().getAttribute(
                    LOCALE_ATTRIBUTE_NAME);
            if (locale == null)
            {
                locale = request.getLocale();
            }
            return locale;
        }
        else
        {
            return parseLocale(localeString);
        }
    }

    private Locale parseLocale(String localeString)
    {
        String[] components = localeString.split("-");
        if (components.length == 1)
        {
            return new Locale(components[0]);
        }
        else if (components.length == 2)
        {
            return new Locale(components[0], components[1]);
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
    
    private void storeLocaleInSessionIfNecessary(HttpServletRequest request, 
            Locale locale)
    {
        if (!StringUtils.isBlank(request.getParameter("lang")))
        {
            request.getSession().setAttribute(LOCALE_ATTRIBUTE_NAME, locale);
        }
    }

}
