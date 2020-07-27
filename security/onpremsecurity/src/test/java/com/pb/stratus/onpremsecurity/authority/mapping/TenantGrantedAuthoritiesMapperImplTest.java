package com.pb.stratus.onpremsecurity.authority.mapping;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/18/14
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class TenantGrantedAuthoritiesMapperImplTest {

    private TenantGrantedAuthoritiesMapperImpl target = new TenantGrantedAuthoritiesMapperImpl();

    @Before
    public void setup(){
        List<String> adminroles = new ArrayList<String>();
        adminroles.add("ROLE_superuser");
        adminroles.add("ROLE_admin");

        target.setADMIN_ROLES(adminroles);
    }

    @Test
    public void testAdminRolesMapping(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthorityImpl("superuser"));
        authorities.add(new GrantedAuthorityImpl("admin"));
        Authentication mockAuth = mock(Authentication.class);
        //when(mockAuth.getAuthorities()).thenReturn(authorities);
        Mockito.doReturn(authorities).when(mockAuth).getAuthorities();
        GrantedAuthority expectedAdmin = new GrantedAuthorityImpl("ROLE_Administrators");
        Collection<GrantedAuthority> mappedAuthorities = target.mapAuthorities(mockAuth, "testTenant");
        assertTrue(mappedAuthorities.contains(expectedAdmin));
    }

    @Test
    public void testAnonymousUserRoleMapping(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        target.setRoleAnonymous(TenantGrantedAuthoritiesMapperImpl.DEFAULT_PUBLIC_ROLE);
        authorities.add(new GrantedAuthorityImpl("AnalystGuestRole"));
        AnonymousAuthenticationToken mockAuth = new AnonymousAuthenticationToken("key","test", authorities);
        GrantedAuthority expectedAdmin = new GrantedAuthorityImpl("ROLE_AnalystGuestRole");
        Collection<GrantedAuthority> mappedAuthorities = target.mapAuthorities(mockAuth, "testTenant");
        assertTrue(mappedAuthorities.contains(expectedAdmin));
    }

    @Test
    public void testEmptyAnonymousUserRoleMapping(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        target.setRoleAnonymous(" ");
        authorities.add(new GrantedAuthorityImpl("AnalystGuestRole"));
        AnonymousAuthenticationToken mockAuth = new AnonymousAuthenticationToken("key","test", authorities);
        GrantedAuthority expectedAdmin = new GrantedAuthorityImpl(TenantGrantedAuthoritiesMapperImpl.DEFAULT_PUBLIC_ROLE);
        Collection<GrantedAuthority> mappedAuthorities = target.mapAuthorities(mockAuth, "testTenant");
        assertTrue(mappedAuthorities.contains(expectedAdmin));
    }

    @Test
    public void testNonDefaultAnonymousUserRoleMapping(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        target.setRoleAnonymous("ROLE_AnalystPublicGuestRole");

        authorities.add(new GrantedAuthorityImpl("AnalystPublicGuestRole"));
        AnonymousAuthenticationToken mockAuth = new AnonymousAuthenticationToken("key","test", authorities);
        GrantedAuthority expectedAdmin = new GrantedAuthorityImpl("ROLE_AnalystPublicGuestRole");
        Collection<GrantedAuthority> mappedAuthorities = target.mapAuthorities(mockAuth, "testTenant");
        assertTrue(mappedAuthorities.contains(expectedAdmin));
    }

}
