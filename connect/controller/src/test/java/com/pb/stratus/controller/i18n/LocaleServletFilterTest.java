package com.pb.stratus.controller.i18n;

import com.pb.stratus.controller.util.MockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Locale;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocaleServletFilterTest
{
    private LocaleServletFilter filter;
    
    private MockHttpServletRequest request;
    
    private Locale requestLocale;
    
    private MockSupport mockSupport;
    
    @Before
    public void setUp()
    {
        filter = new LocaleServletFilter();
        request = new MockHttpServletRequest();
        requestLocale = new Locale("da", "DK");
        mockSupport = new MockSupport();
    }
    
    @Test
    public void testDoFilterLocaleFromParameter() throws Exception
    {
        request.addParameter("lang", "de");
        callDoFilterAndAssertLocale(new Locale("de"));
    }
    
    @Test
    public void testDoFilterStoreLocaleInSession() throws Exception
    {
        request.addParameter("lang", "de");
        Locale expectedLocale = new Locale("de");
        callDoFilterAndAssertLocale(expectedLocale);
        assertEquals(expectedLocale, request.getSession().getAttribute(
                LocaleServletFilter.LOCALE_ATTRIBUTE_NAME));
    }
    
    @Test
    public void testDoFilterSessionLocaleTakesPreference() throws Exception
    {
        Locale expectedLocale = new Locale("de", "AT");
        request.getSession().setAttribute(
                LocaleServletFilter.LOCALE_ATTRIBUTE_NAME, expectedLocale);
        request.addPreferredLocale(new Locale("fr", "FR"));
        callDoFilterAndAssertLocale(expectedLocale);
    }
    
    @Test
    public void testDoFilterRequestParameterTakesPreference() throws Exception
    {
        request.addParameter("lang", "de-de");
        request.getSession().setAttribute(
                LocaleServletFilter.LOCALE_ATTRIBUTE_NAME, 
                new Locale("de", "AT"));
        Locale expectedLocale = new Locale("de", "DE");
        callDoFilterAndAssertLocale(expectedLocale);
    }
    
    @Test
    public void testDoFilterLocaleWithCountryCode() throws Exception
    {
        request.addParameter("lang", "de-de");
        Locale expectedLocale = new Locale("de", "DE");
        callDoFilterAndAssertLocale(expectedLocale);
    }
    
    @Test
    public void testDoFilterNoLocaleParameter() throws Exception
    {
        request.addPreferredLocale(requestLocale);
        callDoFilterAndAssertLocale(requestLocale);
    }
    
    private void callDoFilterAndAssertLocale(Locale expectedLocale) 
            throws Exception
    {
        filter.doFilter(request, null, createMockFilterChain(expectedLocale));
        mockSupport.verifyAllMocks();
    }
    
    @Test
    public void testDoFilterLocaleReset() throws Exception
    {
        filter.doFilter(request, null, createMockFilterChain(null));
        assertNull(LocaleResolver.getLocale());
    }
    
    private FilterChain createMockFilterChain(final Locale expectedLocale) 
            throws Exception
    {
        FilterChain chain = mockSupport.createMock(FilterChain.class);
        chain.doFilter((ServletRequest) anyObject(), 
                (ServletResponse) anyObject());
        if (expectedLocale != null)
        {
            IAnswer<Object> answer = new IAnswer<Object>()
            {
                public Object answer()
                {
                    assertEquals(expectedLocale, LocaleResolver.getLocale());
                    return null;
                }
            };
            expectLastCall().andAnswer(answer);
        }
        replay(chain);
        return chain;
    }

}
