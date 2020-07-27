package com.pb.stratus.controller.action;

import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import com.pb.stratus.security.core.resourceauthorization.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AuthorizedMap configuration test class
 * 
 * @author sa021sh
 * 
 */
public class GetAuthorizedMapConfigActionTest {

	/**
	 * Object that shall be used to test the intended functionality.
	 */
	private GetAuthorizedMapConfigAction gamAction;

	private String sTenant = "customerstratustenant1_noida";
	private String ROLE_PUBLIC = "ROLE_Public_customerstratustenant1_noida";

	private HttpServletRequest mRequest;
	private HttpSession mSession = null;

	private TenantProfile tenantProfile = null;
	private TenantProfileManager mTPManager = null;

	private Application mApplication = null;
	private ServletContext servletContext = null;

	private SecurityContext mockSecurityContext;
	private Authentication mockAuthentication;

	private Collection<GrantedAuthority> authorities = null;
	private GrantedAuthorityImpl grantedAuthority = null;
	private ResourceAuthorization resourceAuthorization = null;
	private ResourceAuthorizationConfig resourceAuthorizationConfig = null;
	private Set<ResourceAuthorizationConfig> resourceAuthorizationConfigSet = null;
    private AuthorizationUtils mAuthorizationUtils;

	@Before
	public void setUp() throws Exception {

		mSession = mock(HttpSession.class);
		mRequest = mock(HttpServletRequest.class);
		servletContext = mock(ServletContext.class);
		mApplication = mock(Application.class);
		mTPManager = mock(TenantProfileManager.class);
		tenantProfile = mock(TenantProfile.class);
		mockAuthentication = mock(Authentication.class);
		mockSecurityContext = mock(SecurityContext.class);
        mAuthorizationUtils = mock(AuthorizationUtils.class);
		resourceAuthorization = mock(MapConfigAuthorization.class);

		/**
		 * Stub 1.
		 */
		when(mRequest.getAttribute(Constants.TENANT_ATTRIBUTE_NAME))
				.thenReturn(sTenant);
		/**
		 * Stub 2.
		 */
		when(mRequest.getSession(any(Boolean.class))).thenReturn(mSession);

		/**
		 * Stub 4. Return the mocked context
		 */
		when(mRequest.getSession(any(Boolean.class)).getServletContext()).thenReturn(
				servletContext);

		/**
		 * Stub 5. Mocked application
		 */
		when(
				servletContext
						.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME))
				.thenReturn(mApplication);

		/**
		 * Stub 6.
		 */
		when(mApplication.getTenantProfileManager()).thenReturn(mTPManager);

		/**
		 * Stub 7. Tenant profile stubbed
		 */
		when(mTPManager.getProfile(sTenant)).thenReturn(tenantProfile);
		
		when(tenantProfile.getResourceAuthorization(ResourceType.MAP_CONFIG)).thenReturn(resourceAuthorization);

		/**
		 * Function specific code
		 */
		resourceAuthorizationConfig = new ResourceAuthorizationConfig(
				"defaultmap", getGrantedAuthorities());

		grantedAuthority = new GrantedAuthorityImpl(ROLE_PUBLIC);

		authorities = new HashSet<GrantedAuthority>();
		authorities.add(grantedAuthority);

		/**
		 * Initializing the object that shall be tested
		 */
		gamAction = new GetAuthorizedMapConfigAction(mAuthorizationUtils);

		resourceAuthorizationConfigSet = new HashSet<ResourceAuthorizationConfig>();
		resourceAuthorizationConfigSet.add(resourceAuthorizationConfig);

		/**
		 * Set the security context
		 */
		SecurityContextHolder.setContext(mockSecurityContext);

		/**
		 * Stub 8.
		 */
		when(mockSecurityContext.getAuthentication()).thenReturn(
				mockAuthentication);

		/**
		 * Stub 9.
		 */
		//when(mockAuthentication.getAuthorities()).thenReturn(authorities);
        Mockito.doReturn(authorities).when(mockAuthentication).getAuthorities();

		/**
		 * Stub 11.
		 */
//		when(resourceAuthorization.getAuthorizationConfigs(authorities))
//				.thenReturn(resourceAuthorizationConfigSet);

	}

	@Test
	public void testCreateObject() throws ServletException, IOException, ResourceException {

		assertNotNull("AuthorzedMap configuration should be created!",
				gamAction);

        when(mAuthorizationUtils.getAuthorizeConfigs(mRequest, ResourceType.MAP_CONFIG))
                .thenReturn(new HashSet());

		Object obj = gamAction.createObject(mRequest);
		assert (obj != null);

	}

	/**
	 * Return a list of GrantedAuthority
	 * 
	 * @return grantedAuthorities
	 */
	private List<GrantedAuthority> getGrantedAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Administrators"));
		grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_User"));
		grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Public"));
		return grantedAuthorities;
	}
}
