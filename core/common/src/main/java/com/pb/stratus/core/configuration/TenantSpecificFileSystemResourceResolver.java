package com.pb.stratus.core.configuration;

import uk.co.graphdata.utilities.resource.ResourceResolver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TenantSpecificFileSystemResourceResolver implements ResourceResolver
{

    private final CustomerConfigDirHolder configDirHolder;

    private final TenantNameHolder tenantNameHolder;

    public TenantSpecificFileSystemResourceResolver(
            CustomerConfigDirHolder configDirHolder,
            TenantNameHolder tenantNameHolder)
    {
        this.configDirHolder = configDirHolder;
        this.tenantNameHolder = tenantNameHolder;
    }

    public long getLastModified(String path)
    {
        File f = getFile(path);
        if (f == null || !f.exists())
        {
            return -1;
        }
        return f.lastModified();
    }

    public URL getResource(String path)
    {
        URL urlResult = null;

        File f = getFile(path);
        if (f != null && f.exists())
        {
            try
            {
                urlResult = f.toURI().toURL();
            }
            catch (MalformedURLException mfx)
            {
                throw new IllegalArgumentException(mfx);
            }
        }

        return urlResult;
    }

    public InputStream getResourceAsStream(String path)
    {
        InputStream streamResult = null;
        File f = getFile(path);
        try
        {
            if (f != null && f.exists())
            {
                streamResult = new BufferedInputStream(new FileInputStream(f));
            }
        }
        catch (FileNotFoundException fnfx) {}

        return streamResult;
    }

    public URLConnection getResourceConnection(String path) throws IOException
    {
        URL u = getResource(path);
        if (u == null)
        {
            return null;
        }

        return u.openConnection();
    }

    private File getFile(String path)
    {
        // This method returns null if the path requested contains files outside tenant dir
        // It would be better throwing exceptions but we are bound to the ResourceResolver
        // interface and so cannot add the exception throws to the method declarations here.
        File tenantDir = new File(configDirHolder.getCustomerConfigDir(),
                tenantNameHolder.getTenantName());

        File resultFile = null;

        try {
            // Expand relative paths possibly including ".." into fully qualified paths
            File resourceFile = new File(tenantDir, path);
            String resolvedTenantPath = tenantDir.getCanonicalPath() + File.separatorChar;
            String resolvedResourcePath = resourceFile.getCanonicalPath();

            // Ensure that the file requested is under the tenant folder
            if (resolvedResourcePath.indexOf(resolvedTenantPath) == 0) {
                    resultFile = resourceFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultFile;
    }
}
