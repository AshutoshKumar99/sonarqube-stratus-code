package com.pb.stratus.connect.services.soap.clearcache;

import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.infrastructure.cache.TenantCacheable;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import static com.pb.stratus.security.core.common.Constants.*;

@javax.jws.WebService(serviceName = "ClearCacheService", portName = "ClearCacheServicePort",
        targetNamespace = "http://www.pb.com/connect/services/soap/clearcacheservice/v1",
        endpointInterface = "com.pb.stratus.connect.services.soap.clearcache.ClearCacheService")
public class ClearCacheServiceImpl implements ClearCacheService {
    /**
     * Conn 15817 - Adding some log information.
     */
    private final static Logger logger = LogManager.getLogger(ClearCacheServiceImpl.class);

    @Resource
    private WebServiceContext context;

    public String clearCache(String tenantName, String mapName) throws ClearCacheServiceFault {

        tenantName = StringUtils.trimToEmpty(tenantName);
        mapName = StringUtils.trimToEmpty(mapName);

        if (StringUtils.isEmpty(tenantName)) {
            ClearCacheServiceFault fault = new ClearCacheServiceFault(EMPTY_TENANT_NAME);
            logger.error(fault);
            throw fault;
        }

        if (StringUtils.isEmpty(mapName)) {
            ClearCacheServiceFault fault = new ClearCacheServiceFault(EMPTY_MAP_NAME);
            logger.error(fault);
            throw fault;
        }

        ServletContext servletContext =
                (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        Application application = (Application) servletContext.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME);
        TenantProfileManager tenantProfileManager = application.getTenantProfileManager();
        TenantProfile tenantProfile = tenantProfileManager.getProfile(tenantName);

        //  No need to catch a Runtime Exception instead check for null.   The current implementation don't throw any
        // exception anyway.
        if (tenantProfile == null) {
            ClearCacheServiceFault fault = new ClearCacheServiceFault(INVALID_TENANT_NAME);
            logger.error(fault);
            throw fault;
        }

        TenantCacheable tenantCacheable = tenantProfile.getTenantLegendCache();
        boolean status = false;
        if (mapName.equals("*")) {
            status = tenantCacheable.clear();
        } else {
            status = tenantCacheable.clear(mapName);
        }

        if (status) {
            logger.info(CLEAR_CACHE_SUCCESS);
            return CLEAR_CACHE_SUCCESS;
        } else {
            logger.info(CLEAR_CACHE_FAILURE);
            return CLEAR_CACHE_FAILURE;
        }
    }
}
