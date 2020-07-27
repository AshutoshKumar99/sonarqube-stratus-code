/**
 * 
 */
package com.pb.stratus.security.core.connect.identity.filter;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.core.configuration.TenantConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test the logout filter defined.
 * 
 * @author Saurabh Sharma
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TenantConfiguration.class)
public class LogoutFilterTest {

	/**
	 * Logout filter that will be tested
	 */
	private LogoutFilter loFilter;

	/**
	 * Other mock objects
	 */
	private HttpServletRequest mRequest;
	private HttpServletResponse mResponse;
	private String sTenant = "customerstratustenant1_noida";
	private String m_auth_type = "secured";
	private String logoutURL = "http://www.yahoo.com";
	private ServletContext servletContext;
	private ControllerConfiguration customerConfig;
	private HttpSession mSession;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		/**
		 * Session mocked
		 */
		mSession = mock(HttpSession.class);

		/**
		 * Customer configuration mocked
		 */
		customerConfig = mock(ControllerConfiguration.class);

		/**
		 * The basic logout filter
		 */
		loFilter = new LogoutFilter();

		/**
		 * Request and response
		 */
		mResponse = mock(HttpServletResponse.class);
		mRequest = mock(HttpServletRequest.class);

		/**
		 * Servlet Context
		 */
		servletContext = mock(ServletContext.class);

		/**
		 * Stub 1.
		 */
		when(mRequest.getAttribute(Constants.TENANT_ATTRIBUTE_NAME))
				.thenReturn(sTenant);

		/**
		 * Stub 2.
		 */
		when(mRequest.getSession()).thenReturn(mSession);

		/**
		 * Stub 3.
		 */
		when(mSession.getServletContext()).thenReturn(servletContext);

		/**
		 * Stub 4. Return the mocked context
		 */
		when(mRequest.getSession().getServletContext()).thenReturn(
				servletContext);

        PowerMockito.mockStatic(TenantConfiguration.class);
        when(TenantConfiguration.getTenantConfiguration(mRequest)).thenReturn(customerConfig);
		when(customerConfig.getSloStartUrl()).thenReturn(logoutURL);
		when(customerConfig.getAuthType()).thenReturn(m_auth_type);
	}

	/**
	 * Test method for
	 * {@link com.pb.stratus.security.core.connect.identity.filter.LogoutFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
	 * .
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void testAuthenticatedDoFilter() throws IOException,
			ServletException {
		assertNotNull("Filter should have been successfully created", loFilter);

		FilterChain mFilterChain = mock(FilterChain.class);
		Authentication mockAuthentication = mock(Authentication.class);

		/**
		 * Stubbing
		 */
		when(mockAuthentication.isAuthenticated()).thenReturn(true);

		SecurityContext mockSecurityContext = mock(SecurityContext.class);
		SecurityContextHolder.setContext(mockSecurityContext);

		when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);

		loFilter.doFilter(mRequest, mResponse, mFilterChain);

		/**
		 * Verify the redirection
		 */
		verify(mResponse).sendRedirect(logoutURL);

	}

}
