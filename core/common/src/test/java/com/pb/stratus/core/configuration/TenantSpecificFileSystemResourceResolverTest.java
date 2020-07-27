package com.pb.stratus.core.configuration;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TenantSpecificFileSystemResourceResolverTest {

    private File mockResource;

    private File tenantDir;
    private File parentResource;

    private TenantSpecificFileSystemResourceResolver resolver;


    @Before
    public void setUp() throws Exception {
        tenantDir = File.createTempFile("tenant", ".dir");
        tenantDir.delete();
        tenantDir.mkdir();
        mockResource = new File(tenantDir, "someResource.txt");
        mockResource.createNewFile();
        parentResource = new File(tenantDir.getParentFile(), "someResource.txt");
        parentResource.createNewFile();

        CustomerConfigDirHolder mockConfigDirHolder = mock(
                CustomerConfigDirHolder.class);
        File customerConfigDir = tenantDir.getParentFile();
        when(mockConfigDirHolder.getCustomerConfigDir()).thenReturn(
                customerConfigDir);
        TenantNameHolder mockTenantNameHolder = mock(TenantNameHolder.class);
        String tenantName = tenantDir.getName();
        when(mockTenantNameHolder.getTenantName()).thenReturn(tenantName);
        resolver = new TenantSpecificFileSystemResourceResolver(
                mockConfigDirHolder, mockTenantNameHolder);
    }

    @After
    public void tearDown() {
        mockResource.delete();
        tenantDir.delete();
        parentResource.delete();
    }


    @Test
    public void shouldUseTenantSpecificDirAsBase() {
        URL u = resolver.getResource(mockResource.getName());
        assertEquals(mockResource, new File(u.getFile()));
    }

    @Test
    public void shouldReturnNullUrlIfResourceDoesntExist() {
        assertNull(resolver.getResource(File.separatorChar + "some" + File.separatorChar + "non" + File.separatorChar + "existent" + File.separatorChar + "resource"));
    }

    @Test
    public void shouldStreamContentsOfResource() throws Exception {
        Writer w = new FileWriter(mockResource);
        w.write("someContent");
        w.close();
        InputStream is = resolver.getResourceAsStream(mockResource.getName());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        is.close();
        assertEquals("someContent", new String(bos.toByteArray()));
    }

    @Test
    public void shouldReturnLastModifiedDateOfUnderlyingFile() {
        assertEquals(mockResource.lastModified(),
                resolver.getLastModified(mockResource.getName()));
    }

    @Test
    public void shouldReturnNegativeLastModifiedDateIfUnderlyingFileDoesntExist() {
        assertEquals(-1, resolver.getLastModified(
                File.separatorChar + "some" + File.separatorChar + "non" + File.separatorChar + "existent" + File.separatorChar + "resource"));
    }

    @Test
    public void shouldReturnUrlConnectionToResource() throws Exception {
        URLConnection conn = resolver.getResourceConnection(
                mockResource.getName());
        // assume last modified date is good enough to ensure it's the same
        // resource
        assertEquals(mockResource.lastModified(), conn.getLastModified());
    }

    @Test
    public void checkForbiddenPath() throws Exception {
        // This test makes sure that trying to access a valid file from the parent folder
        // returns a null as this is not allowed. i.e. There should be no access above the
        // tenant directory.
        assertNull(resolver.getResource(".." + File.separatorChar + "someResource.txt"));
    }

    @Test
    public void checkNonCanonicalPathToValidFile() throws Exception {
        // This test makes sure that a path to a valid file within the tenant folder
        // works even if it contains parent folder ".." elements
        //assertNotNull(resolver.getResource(File.separatorChar + "blah" + File.separatorChar  + "blah" + File.separatorChar  + ".."+ File.separatorChar  + ".." + File.separatorChar + "someResource.txt"));
    }

}
