package com.pb.stratus.security.core.resourceauthorization;


import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 4:55 PM
 */
public class DefaultResourceParserImplTest {

    private InputStream is;
    private DefaultResourceParserImpl defaultResourceParser;

    @Before
    public void setUp() throws Exception {
        is = DefaultResourceParserImplTest.class.getResourceAsStream("mapconfig.auth");
    }

    @Test
    public void testParsing() throws ResourceException {
        defaultResourceParser = new DefaultResourceParserImpl();
        List<GrantedAuthority> actualGrantedAuthorities =
                defaultResourceParser.parse(is);
        assertEquals(getExpectedGrantedAuthority(), actualGrantedAuthorities);
    }

    @Test
    public void shouldDoRoleConversionForSpring() throws UnsupportedEncodingException, ResourceException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <AuthorizationMetadata> " +
                "<AuthorizedRole>" + AuthorizationRoles.ADMINISTRATORS.getRoleName() + "</AuthorizedRole>  </AuthorizationMetadata>";
        is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        defaultResourceParser = new DefaultResourceParserImpl();
        List<GrantedAuthority> actualGrantedAuthorities =
                defaultResourceParser.parse(is);
        List<GrantedAuthority> expectedGrantedAuthorities =
                new ArrayList<GrantedAuthority>();
        expectedGrantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Administrators"));
        assertEquals(expectedGrantedAuthorities, actualGrantedAuthorities);
    }

    private List<GrantedAuthority> getExpectedGrantedAuthority() {
        List<GrantedAuthority> expectedGrantedAuthorities = new ArrayList<GrantedAuthority>();
        expectedGrantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Administrators"));
        expectedGrantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Users"));
        expectedGrantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Public"));
        return expectedGrantedAuthorities;
    }
}
