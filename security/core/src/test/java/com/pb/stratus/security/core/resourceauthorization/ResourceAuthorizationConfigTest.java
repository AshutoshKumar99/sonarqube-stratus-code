package com.pb.stratus.security.core.resourceauthorization;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 5:28 PM
 */
public class ResourceAuthorizationConfigTest {
    ResourceAuthorizationConfig resourceAuthorizationConfig;
    @Test
    public void NonNullArgumentsInConstructor()
    {
        resourceAuthorizationConfig = new ResourceAuthorizationConfig("abc",
                getGrantedAuthorities());
        assertEquals("abc", resourceAuthorizationConfig.getName());
        assertEquals(getGrantedAuthorities(),
                resourceAuthorizationConfig.getGrantedAuthorities());
    }

    @Test (expected= NullPointerException.class)
    public void NullArgumentInConstructors()
    {
        resourceAuthorizationConfig = new ResourceAuthorizationConfig(null,
                getGrantedAuthorities());
    }

    @Test
    public void equalsTest()
    {
        resourceAuthorizationConfig = new ResourceAuthorizationConfig("abc",
                getGrantedAuthorities());
        ResourceAuthorizationConfig anotherConfig = new ResourceAuthorizationConfig("abc",
                getGrantedAuthorities());
        assertEquals(resourceAuthorizationConfig, anotherConfig);
        anotherConfig = new ResourceAuthorizationConfig("def",
                getGrantedAuthorities());
        assertFalse(resourceAuthorizationConfig.equals(anotherConfig));
    }

    @Test
    public void testHashCode()
    {
        resourceAuthorizationConfig = new ResourceAuthorizationConfig("abc",
                        getGrantedAuthorities());

        ResourceAuthorizationConfig expectedConfig = new ResourceAuthorizationConfig("abc",
                getGrantedAuthorities());
        assertEquals(resourceAuthorizationConfig.hashCode(), expectedConfig.hashCode());
        expectedConfig = new ResourceAuthorizationConfig("def",
                getGrantedAuthorities());
        assertFalse(resourceAuthorizationConfig.hashCode() == expectedConfig.hashCode());
    }


    private List<GrantedAuthority> getGrantedAuthorities()
    {
         List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Administrators"));
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_User"));
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Public"));
        return grantedAuthorities;
    }
}
