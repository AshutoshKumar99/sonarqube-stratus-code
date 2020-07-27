package com.pb.stratus.controller.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MiDevNamedTableRepositoryTest
{
    
    private MiDevRepository repo;
    
    @Before
    public void setUp()
    {
        this.repo = new MiDevNamedTableRepository("someTenant");
    }
    
    @Test
    public void shouldPrependTenantNameAndSlashToResourceNames()
    {
        assertEquals("/someTenant/NamedTables/someMap", repo.getInternalResourceName(
                "someMap"));
    }
    
    @Test
    public void shouldPrependSlashIfNoCustomerName()
    {
        this.repo = new MiDevNamedTableRepository(null);
        assertEquals("/NamedTables/someResource", repo.getInternalResourceName(
                "someResource"));
    }

}
