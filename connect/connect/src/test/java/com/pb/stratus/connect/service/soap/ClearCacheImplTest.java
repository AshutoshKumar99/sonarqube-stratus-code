package com.pb.stratus.connect.service.soap;

import com.pb.stratus.connect.services.soap.clearcache.ClearCacheServiceFault;
import com.pb.stratus.connect.services.soap.clearcache.ClearCacheServiceImpl;
import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.infrastructure.cache.TenantCacheable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import static com.pb.stratus.security.core.common.Constants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class ClearCacheImplTest  {

    private static final String INVALID_TENANT_NAME = "invalid";
    private static final String VALID_TENANT_NAME = "valid_tenant";
    private static final String VALID_MAP_NAME = "valid_map";

    @Mock
    private WebServiceContext context;
    @Mock
    private ServletContext servletContext;
    @Mock
    private Application application;
    @Mock
    private TenantProfileManager tenantProfileManager;
    @Mock
    private TenantProfile tenantProfile;
    @Mock
    private TenantCacheable tenantCacheable;
    @Mock
    private MessageContext messageContext;

    @InjectMocks
    private ClearCacheServiceImpl clearCacheImpl = new ClearCacheServiceImpl();


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void nullOrEmptyTenantNameTest() {

        //Tenant Name null
        try {
            ClearCacheServiceImpl clearCacheImpl = new ClearCacheServiceImpl();
            clearCacheImpl.clearCache(null, VALID_MAP_NAME);
        } catch (RuntimeException e) {
            assertEquals(EMPTY_TENANT_NAME, e.getMessage());
        }

        //Tenant Name empty
        try {
            clearCacheImpl.clearCache("", VALID_MAP_NAME);
        } catch (RuntimeException e) {
            assertEquals(EMPTY_TENANT_NAME, e.getMessage());
        }
    }

    @Test
    public void nullOrEmptyMapNameTest() {

        //Map Name null
        try {
            ClearCacheServiceImpl clearCacheImpl = new ClearCacheServiceImpl();
            clearCacheImpl.clearCache(VALID_TENANT_NAME, null);
        } catch (RuntimeException e) {
            assertEquals(EMPTY_MAP_NAME, e.getMessage());
        }

        //Map Name empty
        try {
            clearCacheImpl.clearCache(VALID_TENANT_NAME, "");
        } catch (RuntimeException e) {
            assertEquals(EMPTY_MAP_NAME, e.getMessage());
        }
    }

    @Test(expected = ClearCacheServiceFault.class)
    public void invalidTenantName() {
        when(context.getMessageContext()).thenReturn(messageContext);
        when(context.getMessageContext().get(MessageContext.SERVLET_CONTEXT)).thenReturn(servletContext);
        when(servletContext.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME)).thenReturn(application);
        when(application.getTenantProfileManager()).thenReturn(tenantProfileManager);
        when(tenantProfileManager.getProfile(INVALID_TENANT_NAME)).thenReturn(null);

        clearCacheImpl.clearCache(INVALID_TENANT_NAME, VALID_MAP_NAME);
    }

    @Test
    public void clearCacheSuccess() {
        mockDependencies();
        when(tenantCacheable.clear(VALID_MAP_NAME)).thenReturn(true);

        String response = clearCacheImpl.clearCache(VALID_TENANT_NAME, VALID_MAP_NAME);
        assertEquals(CLEAR_CACHE_SUCCESS, response);
    }

    @Test
    public void clearCacheFailure() throws Exception {
        mockDependencies();
        when(tenantCacheable.clear(VALID_MAP_NAME)).thenReturn(false);

        String response = clearCacheImpl.clearCache(VALID_TENANT_NAME, VALID_MAP_NAME);
        assertEquals(CLEAR_CACHE_FAILURE, response);
    }

    private void mockDependencies() {
        when(context.getMessageContext()).thenReturn(messageContext);
        when(context.getMessageContext().get(MessageContext.SERVLET_CONTEXT)).thenReturn(servletContext);
        when(servletContext.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME)).thenReturn(application);
        when(application.getTenantProfileManager()).thenReturn(tenantProfileManager);
        when(tenantProfileManager.getProfile(VALID_TENANT_NAME)).thenReturn(tenantProfile);
        when(tenantProfile.getTenantLegendCache()).thenReturn(tenantCacheable);
    }
}
