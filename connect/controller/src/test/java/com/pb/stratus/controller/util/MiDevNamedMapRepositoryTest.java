package com.pb.stratus.controller.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MiDevNamedMapRepositoryTest
{
    
    private MiDevRepository repo;
    
    @Before
    public void setUp()
    {
        this.repo = new MiDevNamedMapRepository("someTenant");
    }
    
    @Test
    public void shouldPrependTenantNameAndSlashToResourceNames()
    {
        assertEquals("/someTenant/NamedMaps/someMap", repo.getInternalResourceName(
                "someMap"));
    }

    @Test
    public void shouldPrependSlashIfNoCustomerName()
    {
        this.repo = new MiDevNamedMapRepository(null);
        assertEquals("/NamedMaps/someResource", repo.getInternalResourceName(
                "someResource"));
    }

}
