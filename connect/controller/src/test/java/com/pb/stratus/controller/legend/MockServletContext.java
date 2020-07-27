package com.pb.stratus.controller.legend;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MockServletContext implements ServletContext
{
	
	private Map<String, Object> attrs = new HashMap<>();
	
	public Object getAttribute(String key)
	{
		return attrs.get(key);
	}
	
	public Enumeration getAttributeNames()
	{
		throw new UnsupportedOperationException();
	}

    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

	public ServletContext getContext(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public String getInitParameter(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public Enumeration getInitParameterNames()
	{
		throw new UnsupportedOperationException();
	}

    @Override
    public boolean setInitParameter(String s, String s1) {
        return false;
    }

    public int getMajorVersion()
	{
		throw new UnsupportedOperationException();
	}
	
	public String getMimeType(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public int getMinorVersion()
	{
		throw new UnsupportedOperationException();
	}

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    public RequestDispatcher getNamedDispatcher(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public String getRealPath(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public RequestDispatcher getRequestDispatcher(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public URL getResource(String arg0) throws MalformedURLException
	{
		throw new UnsupportedOperationException();
	}
	
	public InputStream getResourceAsStream(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public Set getResourcePaths(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public String getServerInfo()
	{
		throw new UnsupportedOperationException();
	}
	
	public Servlet getServlet(String arg0) throws ServletException
	{
		throw new UnsupportedOperationException();
	}
	
	public String getServletContextName()
	{
		throw new UnsupportedOperationException();
	}

    @Override
    public ServletRegistration.Dynamic addServlet(String s, String s1) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> tClass) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String s) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, String s1) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> tClass) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String s) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener(String s) {
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
    }

    @Override
    public void addListener(Class<? extends EventListener> aClass) {
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> tClass) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void declareRoles(String... strings) {
    }

    public Enumeration getServletNames()
	{
		throw new UnsupportedOperationException();
	}
	
	public Enumeration getServlets()
	{
		throw new UnsupportedOperationException();
	}
	
	public void log(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public void log(Exception arg0, String arg1)
	{
		throw new UnsupportedOperationException();
	}
	
	public void log(String arg0, Throwable arg1)
	{
		throw new UnsupportedOperationException();
	}
	
	public void removeAttribute(String arg0)
	{
		throw new UnsupportedOperationException();
	}
	
	public void setAttribute(String key, Object value)
	{
		attrs.put(key, value);
	}
	
}
