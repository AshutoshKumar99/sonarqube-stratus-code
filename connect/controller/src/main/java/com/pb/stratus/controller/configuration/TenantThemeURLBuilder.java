package com.pb.stratus.controller.configuration;

import com.pb.stratus.controller.TenantThemeNotFoundException;
import com.pb.stratus.controller.util.ResourceUtils;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * This is url builder implementation for creating full url of tenant theme
 * resource.
 */
public class TenantThemeURLBuilder implements URLBuilder
{

    private static final Logger logger = LogManager
        .getLogger(TenantThemeURLBuilder.class);

    private ControllerConfiguration config;

    public TenantThemeURLBuilder(ControllerConfiguration config)
    {
        this.config = config;
    }

    public URL buildFullURL(String fileName)
        throws TenantThemeNotFoundException, IOException
    {
        if (!ResourceUtils.isValidFilename(fileName))
        {
            throw new IOException(
                "Invalid filename specified for requested file.");
        }

        URL themeUrl = config.getTenantThemeUrl();
        String tenantThemeBaseURL = themeUrl != null ? themeUrl.toString() : "";
        if (tenantThemeBaseURL.equals(""))
        {
            throw new TenantThemeNotFoundException(
                "no property 'tenantThemeLocation' found in controller.properties");
        }

        logger.debug("tenantThemeLocation=" + tenantThemeBaseURL);

        StringBuilder sb = new StringBuilder();
        sb.append(tenantThemeBaseURL);
        if (!tenantThemeBaseURL.endsWith("/"))
        {
            sb.append("/");
        }

        fileName = URLEncoder.encode(fileName, "UTF-8");
        fileName = decodeForwardStroke(fileName);
        sb.append(fileName);

        logger.debug("Tenant Theme Resource URL = " + sb.toString());

        URL fullUrl = new URL(sb.toString());
        return fullUrl;
    }

    // The character "/" gets encoded to %2F in url encoding process and thus
    // has to be decoded back.
    private String decodeForwardStroke(String fileName)
    {
        return fileName.replaceAll("%2F", "/");
    }

}
