package com.pb.stratus.controller.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CachingTenantProfileManagerTest {

    @Test
    public void shouldCreateAMapAndReturnTest() {
        TenantProfileManager tenantProfileManager1 = mock(TenantProfileManager.class);
        String tenantName1 = "Tenant1";
        TenantProfile tenantProfile1 = mock(TenantProfile.class);
        when(tenantProfileManager1.getProfile(tenantName1)).thenReturn(tenantProfile1);
        when(tenantProfile1.getTenantName()).thenReturn(tenantName1);
        TenantProfileManager tenantProfileManager2 = mock(TenantProfileManager.class);
        String tenantName2 = "Tenant2";
        TenantProfile tenantProfile2 = mock(TenantProfile.class);
        when(tenantProfileManager2.getProfile(tenantName2)).thenReturn(tenantProfile2);
        when(tenantProfile2.getTenantName()).thenReturn(tenantName2);

        CachingTenantProfileManager cachingTenantProfileManager = new CachingTenantProfileManager(tenantProfileManager1);
        assertEquals(tenantName1, cachingTenantProfileManager.getProfile(tenantName1).getTenantName());
        cachingTenantProfileManager = new CachingTenantProfileManager(tenantProfileManager2);
        assertEquals(tenantName2, cachingTenantProfileManager.getProfile(tenantName2).getTenantName());

    }


}
