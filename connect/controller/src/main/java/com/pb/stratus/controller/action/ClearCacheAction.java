package com.pb.stratus.controller.action;

import com.pb.stratus.controller.KeyNotInCachePresentException;
import com.pb.stratus.controller.infrastructure.cache.CacheHub;
import com.pb.stratus.controller.infrastructure.cache.CacheType;
import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.core.configuration.Tenant;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Action class to clean the repository.
 * 
 * @author 
 */
public class ClearCacheAction extends BaseControllerAction
{
    
    private static Logger logger = LogManager.getLogger(BaseControllerAction
            .class.getName());

    private Tenant tenant;

    private CacheHub cacheHub;

    private static final String MAP_NAME_PARAM = "mapName";

    private static final String DELETE_ALL_PARAM = "deleteAll";

    public static final String PARAMS_NOT_PRESENT =
            "mapName or deleteAll parameter is required";

    public static final String MAP_CLEARED =
            "Map %s has been removed from cache";

    public static final String MAP_NOT_PRESENT =
            "Map %s is not present in cache.";

    public static final String ALL_CACHE_CLEAR = "All cache cleared";

    public static final String ALL_CACHE_NOT_CLEAR =
            "All cache could not be cleared.";

    public ClearCacheAction(Tenant tenant, CacheHub cacheHub)
    {
        this.tenant = tenant;
        this.cacheHub = cacheHub;
    }
    
    public void execute(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        String mapName = request.getParameter(MAP_NAME_PARAM);
        String deleteAll = request.getParameter(DELETE_ALL_PARAM);

        try{
            if(StringUtils.isBlank(mapName) && StringUtils.isBlank(deleteAll))
            {
                writer.write(PARAMS_NOT_PRESENT);
                writer.flush();
            }
            else if(!StringUtils.isBlank(deleteAll) && !deleteAll.equalsIgnoreCase
                    ("false"))
            {
                clearEntireCache(writer);
            }
            else
            {
                removeMapFromCache(mapName, writer);
            }
        }
        finally
        {
            IOUtils.closeQuietly(writer);

        }

    }

    private void clearEntireCache(PrintWriter writer)
    {
        String status = null;
        try
        {
            getTenantCache().clear(this.tenant);
            status = ALL_CACHE_CLEAR;
        } catch (KeyNotInCachePresentException e)
        {
            status = ALL_CACHE_NOT_CLEAR;
        }
        writer.write(status);
        writer.flush();
    }

    private void removeMapFromCache(String mapName, PrintWriter writer)
    {
        String status = null;
        try
        {
            if(isKeyPresentInCache(mapName))
            {
                getTenantCache().clear(this.tenant, mapName);

                status = String.format(MAP_CLEARED, mapName);
            }
        else
        {

                status = String.format(MAP_NOT_PRESENT, mapName);
                logger.debug(status);
        }
        } catch (KeyNotInCachePresentException e)
        {
            status = e.getMessage();
        }
        writer.write(status);
        writer.flush();
    }

    /**
     * The reason for making htis check is that JCS does  not throw any
     * exceptions for removing keys that do not exists,
     * which probably makes sense.
     * @return
     */
    private boolean isKeyPresentInCache(String mapName)
    {
        Object value = getTenantCache().get(this.tenant, mapName);
        return value != null;
    }

    private Cacheable getTenantCache()
    {
        return this.cacheHub.getCacheForTenant(this.tenant,
                CacheType.LEGEND_CACHE);
}
}
