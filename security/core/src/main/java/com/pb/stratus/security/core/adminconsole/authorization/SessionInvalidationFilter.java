package com.pb.stratus.security.core.adminconsole.authorization;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionInvalidationFilter extends OncePerRequestFilter
{
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
	{
		// don't call chain.doFilter if we redirect
		boolean sentRedirect = false;

		// from the redirect headers
		String requestTenant = (String) request.getAttribute("tenant");
		String originalRequestUri = (String) request.getAttribute("originalRequestUri");

		HttpSession session = request.getSession(false);
		if(session != null && StringUtils.isNotEmpty(requestTenant) && StringUtils.isNotEmpty(originalRequestUri))
		{
			String sessionTenant = (String) session.getAttribute("tenant");
			if(StringUtils.isNotEmpty(sessionTenant) && !sessionTenant.equalsIgnoreCase(requestTenant))
			{
				session.invalidate();

				// sendRedirect treats paths as relative from the server root
				originalRequestUri = getServletContext().getContextPath() + originalRequestUri;
				String queryString = request.getQueryString();
				if(StringUtils.isNotEmpty(queryString))
				{
					originalRequestUri = originalRequestUri + "?" + queryString;
				}
				response.sendRedirect(originalRequestUri);

				// don't continue the filter chain
				sentRedirect = true;
			}
		}

		if(!sentRedirect)
		{
			filterChain.doFilter(request, response);
		}
	}
}
