package com.pb.stratus.security.core.resourceauthorization;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.io.*;
import java.util.*;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * User: sh003bh
 * Date: 11/14/11
 * Time: 2:15 PM
 */
public class AbstractReadResourceAuthorizationTest {
    ResourceAuthorizationReader mockResourceAuthorizationReader;
    ResourceParser mockResourceParser;
    private AbstractReadResourceAuthorizationTestHelper
            abstractReadResourceAuthorizationTestHelper;
    File file1;
    File file2;

    @Before
    public void setUp() throws Exception {
        mockResourceAuthorizationReader = mock(ResourceAuthorizationReader.class);
        mockResourceParser = mock(ResourceParser.class);
        abstractReadResourceAuthorizationTestHelper = new AbstractReadResourceAuthorizationTestHelper(
                mockResourceAuthorizationReader, mockResourceParser);
    }

    @Test
    public void shouldReturnAllConfigNamesWithExtensionStripped()
            throws FileNotFoundException, ResourceException {
        File mockDir = mock(File.class);
        when(mockDir.list(Matchers.<FilenameFilter>any())).
                thenReturn(new String[]{"a.xml", "b.xml"});
        List<String> actualResult =
                abstractReadResourceAuthorizationTestHelper.getListOfAllConfigNames(mockDir);
        assertEquals(Arrays.asList(new String[]{"a", "b"}), actualResult);
    }

    @Test
    public void shouldIgnoreResourceDirIfArgumentNull()
            throws FileNotFoundException, ResourceException {
        // with null argument
        abstractReadResourceAuthorizationTestHelper.getResourceDir(null);
        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        verify(mockResourceAuthorizationReader).getFile(captor.capture());
        assertEquals("/somepath", captor.getValue());
    }

    @Test
    public void shouldIgnoreResourceDirIfIsIgnoreResourceDirIsTrue()
            throws FileNotFoundException, ResourceException {
        abstractReadResourceAuthorizationTestHelper =
                spy(abstractReadResourceAuthorizationTestHelper);
        // with isIgnoreResourceDir as true
        when(abstractReadResourceAuthorizationTestHelper.isIgnoreResourceDir()).thenReturn(true);
        abstractReadResourceAuthorizationTestHelper.getResourceDir("somedir");
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockResourceAuthorizationReader).getFile(captor.capture());
        assertEquals("/somepath", captor.getValue());
    }

    @Test
    public void shouldNotIgnoreResourceIfArgumentNotNullAndIsIgnoreResourceDirIsFalse()
            throws FileNotFoundException, ResourceException {
        abstractReadResourceAuthorizationTestHelper.getResourceDir("somedir");
        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        verify(mockResourceAuthorizationReader).getFile(captor.capture());
        assertEquals("/somepath/somedir", captor.getValue());
    }

    @Test
    public void testGetAuthorizationConfigWithAdminRole()
            throws FileNotFoundException, ResourceException {
        ResourceAuthorizationConfig actual =
                abstractReadResourceAuthorizationTestHelper.
                        getAuthorizationConfigWithAdminRole("someconfig");
        ResourceAuthorizationConfig expected =
                new ResourceAuthorizationConfig("someconfig",
                        Collections.singletonList(
                                new GrantedAuthorityImpl("ROLE_Administrators")));
        assertEquals(expected, actual);
    }

    @Test
    public void shouldAuthorizeIfOneOfTheRequestedRoleIsPresentInActualRoleList()
            throws FileNotFoundException, ResourceException {
        Collection<GrantedAuthority> requestedRoles =
                new ArrayList<GrantedAuthority>();
        requestedRoles.add(new GrantedAuthorityImpl("role1"));
        requestedRoles.add(new GrantedAuthorityImpl("role2"));

        Collection<GrantedAuthority> actualRoles =
                new ArrayList<GrantedAuthority>();
        actualRoles.add(new GrantedAuthorityImpl("role3"));
        actualRoles.add(new GrantedAuthorityImpl("role2"));

        assertTrue(abstractReadResourceAuthorizationTestHelper.isRoleAuthorized(requestedRoles, actualRoles));
    }

    @Test
    public void shouldNotAuthorizeIfOneOfTheRequestedRoleIsNotPresentInActualRoleList()
            throws FileNotFoundException, ResourceException {
        Collection<GrantedAuthority> requestedRoles =
                new ArrayList<GrantedAuthority>();
        requestedRoles.add(new GrantedAuthorityImpl("role1"));
        requestedRoles.add(new GrantedAuthorityImpl("role2"));

        Collection<GrantedAuthority> actualRoles =
                new ArrayList<GrantedAuthority>();
        actualRoles.add(new GrantedAuthorityImpl("role3"));
        actualRoles.add(new GrantedAuthorityImpl("role4"));

        assertFalse(abstractReadResourceAuthorizationTestHelper.isRoleAuthorized(requestedRoles, actualRoles));

    }

    @Test
    public void shouldAddAuthorizationForConfigIfAuthFileNotPresentAndAdminRoleRequested()
            throws FileNotFoundException, ResourceException {
        Set<ResourceAuthorizationConfig> actualSet =
                new HashSet<ResourceAuthorizationConfig>();
        File mockAuthFile = mock(File.class);
        when(mockAuthFile.exists()).thenReturn(false);
        Collection<GrantedAuthority> grantedAuthorities =
                new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("role1"));
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Administrators"));
        abstractReadResourceAuthorizationTestHelper.addResourceAuthorizationConfig(
                actualSet, mockAuthFile, "someconfig", grantedAuthorities);
        List<? extends GrantedAuthority> admin =
                Collections.singletonList(new GrantedAuthorityImpl("ROLE_Administrators"));
        assertTrue(actualSet.contains(new ResourceAuthorizationConfig("someconfig", admin)));
    }

    @Test
    public void shouldNotAddAuthorizationForConfigIfAuthFileNotPresentAndAdminRoleNotRequested()
            throws FileNotFoundException, ResourceException {
        Set<ResourceAuthorizationConfig> actualSet =
                new HashSet<ResourceAuthorizationConfig>();
        File mockAuthFile = mock(File.class);
        when(mockAuthFile.exists()).thenReturn(false);
        Collection<GrantedAuthority> grantedAuthorities =
                new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("role1"));
        abstractReadResourceAuthorizationTestHelper.addResourceAuthorizationConfig(
                actualSet, mockAuthFile, "someconfig", grantedAuthorities);
        List<? extends GrantedAuthority> admin =
                Collections.singletonList(new GrantedAuthorityImpl("ROLE_Administrators"));
        assertFalse(actualSet.contains(new ResourceAuthorizationConfig("someconfig", admin)));
    }

    @Test
    public void shouldAddAuthForConfigIfRequestedRoleIsPresent()
            throws FileNotFoundException, ResourceException {
        when(mockResourceAuthorizationReader.getConfigFile(any(File.class))).thenReturn(null);
        GrantedAuthority ga = new GrantedAuthorityImpl("role1");
        List<GrantedAuthority> authList =
                Collections.singletonList(ga);
        when(mockResourceParser.parse(Matchers.<InputStream>any())).thenReturn(authList);
        Set<ResourceAuthorizationConfig> actualSet =
                new HashSet<ResourceAuthorizationConfig>();
        File mockAuthFile = mock(File.class);
        when(mockAuthFile.exists()).thenReturn(true);
        Collection<GrantedAuthority> grantedAuthorities =
                new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("role1"));
        abstractReadResourceAuthorizationTestHelper.addResourceAuthorizationConfig(
                actualSet, mockAuthFile, "someconfig", grantedAuthorities);

        assertTrue(actualSet.contains(new ResourceAuthorizationConfig("someconfig", authList)));
    }

    @Test
    public void shouldNotAddAuthForConfigIfRequestedRoleIsNotPresent()
            throws FileNotFoundException, ResourceException {
        when(mockResourceAuthorizationReader.getConfigFile(any(File.class))).thenReturn(null);
        GrantedAuthority ga = new GrantedAuthorityImpl("role2");
        List<GrantedAuthority> authList =
                Collections.singletonList(ga);
        when(mockResourceParser.parse(Matchers.<InputStream>any())).thenReturn(authList);
        Set<ResourceAuthorizationConfig> actualSet =
                new HashSet<ResourceAuthorizationConfig>();
        File mockAuthFile = mock(File.class);
        when(mockAuthFile.exists()).thenReturn(true);
        Collection<GrantedAuthority> grantedAuthorities =
                new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("role1"));
        abstractReadResourceAuthorizationTestHelper.addResourceAuthorizationConfig(
                actualSet, mockAuthFile, "someconfig", grantedAuthorities);

        assertFalse(actualSet.contains(new ResourceAuthorizationConfig("someconfig", authList)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionThrownWhenResourceDirPathIsNullAndIsIgnoreResourceDirIsFalse()
            throws FileNotFoundException, ResourceException {
        abstractReadResourceAuthorizationTestHelper.getAuthorizationConfigs(null, null);
    }

    @Test(expected = ResourceException.class)
    public void shouldThrowExceptionIfFileNotExistsOrNotADirectory()
            throws FileNotFoundException, ResourceException {
        AbstractReadResourceAuthorization obj = new
                AbstractReadResourceAuthorization(
                        mockResourceAuthorizationReader, mockResourceParser) {
                    @Override
                    protected boolean isIgnoreResourceDir() {
                        return true;
                    }

                    @Override
                    protected String getPath() {
                        return "somepath";
                    }

                    @Override
                    public ResourceType getResourceType() {
                        return ResourceType.MAP_CONFIG;
                    }
                };
        // file does not exist
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);
        when(mockFile.isDirectory()).thenReturn(false);
        when(mockResourceAuthorizationReader.getFile(any(String.class))).
                thenReturn(mockFile);
        obj.getAuthorizationConfigs(null, "somepath");

        //file exists but not a directory
        mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isDirectory()).thenReturn(false);
        when(mockResourceAuthorizationReader.getFile(any(String.class))).
                thenReturn(mockFile);
        obj.getAuthorizationConfigs(null, "somepath");
    }

    @Test
    public void testGetAuthoritiesForCorrectFileName()
            throws FileNotFoundException, ResourceException {
        abstractReadResourceAuthorizationTestHelper.getAuthorities("somedir/someresource");
        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);
        verify(mockResourceAuthorizationReader).getFile(captor.capture());
        assertEquals("/somepath/somedir/someresource.someextension", captor.getValue());
    }

    @Test
    public void testGetAuthoritiesShouldThrowExceptionIfResourceNotFound()
            throws ResourceException, FileNotFoundException, UnsupportedEncodingException {
        abstractReadResourceAuthorizationTestHelper = new
                AbstractReadResourceAuthorizationTestHelper(
                mockResourceAuthorizationReader, new DefaultResourceParserImpl());
        String roles = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AuthorizationMetadata><AuthorizedRole>"
                + AuthorizationRoles.ADMINISTRATORS.getRoleName() + "</AuthorizedRole></AuthorizationMetadata>";
        InputStream is = new ByteArrayInputStream(roles.getBytes("UTF-8"));
        when(mockResourceAuthorizationReader.getConfigFile(any(File.class))).thenReturn(is);
        List<GrantedAuthority> actualList =
                abstractReadResourceAuthorizationTestHelper.getAuthorities("somedir/someresource");
        List<? extends GrantedAuthority> expectedList = Collections.singletonList(new GrantedAuthorityImpl("ROLE_Administrators"));
        assertEquals(expectedList, actualList);
    }
}
