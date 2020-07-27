package com.pb.stratus.controller.print;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Utility class for print functionality
 *
 */
public class PrintUtils 
{
    /**
     * Remove the leading "/" and customer name 
     * from the given map name
     * @param mapName
     * @return
     */
    public static String processMapName(String mapName)
    {
        if(StringUtils.isEmpty(mapName))
        {
            return mapName;
        }
        return mapName.startsWith("/") ? mapName
                .substring(mapName.lastIndexOf("/") + 1) : mapName;
    }
    
    /**
     * Constructs a base part of the request Url
     * @param request
     * @return base request url
     */
    public static String constructCurrentRequestBaseUrl(
            HttpServletRequest request) throws IOException
    {
        try
        {
            String protocol = request.getScheme();
            String host = request.getServerName();
            int port = request.getServerPort();
            return protocol + "://" + host + ":" + port;
        }
        catch(Exception e)
        {
            throw new IOException(e.getMessage().toString());
        }
    }
}
