package com.pb.stratus.security.core.util;

import com.pb.stratus.security.core.authority.mapping.TenantGrantedAuthoritiesMapper;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

import static com.pb.stratus.core.configuration.SystemPropertyCustomerConfigDirHolder.DIR_PROPERTY_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AuthorizationUtilsTest {
    private HttpServletRequest m_request;
    private Authentication authentication;
    private SecurityContext m_context;
    private HttpSession  m_session;
    private ServletContext m_servletContext;
    private AuthorizationUtils authorizationUtils;
    private TenantGrantedAuthoritiesMapper m_authoritiesMapper;

    private static final String PUBLIC_ROLE = "ROLE_Public";
    private static final String USER_ROLE = "Users";
    private static final String ADMIN_ROLE = "Administrators";
    private static final String TENANT_NAME = "tenant";
    private static final String TEST_CONFIG = "testconfig";


    @Before
    public void setUp() throws Exception
    {
        m_request = mock(HttpServletRequest.class);
        m_context = mock(SecurityContext.class);
        m_session = mock(HttpSession.class);
        m_servletContext = mock(ServletContext.class);
        SecurityContextHolder.setContext(m_context);
        authorizationUtils = new AuthorizationUtilsImpl();
        m_authoritiesMapper = mock(TenantGrantedAuthoritiesMapper.class);
        File f = new File("src\\test\\resources\\com\\pb\\stratus\\saassecurity\\common\\resourceauthorization");
        System.setProperty(DIR_PROPERTY_NAME, f.getCanonicalPath());
    }

    @After
    public void tearDown()
    {
        System.getProperties().remove(DIR_PROPERTY_NAME);
    }

    @Test
    public void testIsAnonymous(){
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(createAnonymousToken());
        assertTrue(authorizationUtils.isAnonymousUser());
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetFirstLexicographicConfig(){
        Set<ResourceAuthorizationConfig> configs = new HashSet<>();
        configs.add(new ResourceAuthorizationConfig("abc", Collections.singletonList(new GrantedAuthorityImpl("ROLE_ANYONE"))));
        configs.add(new ResourceAuthorizationConfig("bcd", Collections.singletonList(new GrantedAuthorityImpl("ROLE_ANYONE"))));
        assertEquals("abc", authorizationUtils.getFirstLexicographicEntry(configs));
        configs.add(new ResourceAuthorizationConfig("Abc", Collections.singletonList(new GrantedAuthorityImpl("ROLE_ANYONE"))));
        assertEquals("Abc", authorizationUtils.getFirstLexicographicEntry(configs));
        configs.add(new ResourceAuthorizationConfig("1Abc", Collections.singletonList(new GrantedAuthorityImpl("ROLE_ANYONE"))));
        assertEquals("1Abc", authorizationUtils.getFirstLexicographicEntry(configs));

    }

    private Authentication createAnonymousToken()
    {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new GrantedAuthorityImpl("ROLE_ANONYMOUS"));
        return  new AnonymousAuthenticationToken("stratus", "Guest", authorities);
    }
}
