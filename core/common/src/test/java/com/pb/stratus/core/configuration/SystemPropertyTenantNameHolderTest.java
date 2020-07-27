package com.pb.stratus.core.configuration;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.exception.ConfigurationException;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SystemPropertyTenantNameHolderTest
{
    
    @After
    public void tearDown()
    {
        System.getProperties().remove(Constants.TENANT_NAME);
    }
    
    @Test
    public void shouldGetTenantNameFromSystemProperty()
    {
        System.setProperty(Constants.TENANT_NAME, "someTenantName");
        SystemPropertyTenantNameHolder tenantNameHolder
                = new SystemPropertyTenantNameHolder();
        assertEquals("someTenantName", tenantNameHolder.getTenantName());
    }
    
    @Test
    public void shouldThrowExceptionIfSystemPropertyNotSet()
    {
        try
        {
            new SystemPropertyTenantNameHolder();
            fail("No ConfigurationException thrown");
        }
        catch (ConfigurationException cx)
        {
            // expected
        }
    }
    
}
