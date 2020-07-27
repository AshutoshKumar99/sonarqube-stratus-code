package com.pb.stratus.controller.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MiDevNamedLayerRepositoryTest
{
    
    private MiDevRepository repo;
    
    @Before
    public void setUp()
    {
        this.repo = new MiDevNamedLayerRepository("someTenant");
    }
    
    @Test
    public void shouldPrependTenantNameAndSlashToResourceNames()
    {
        assertEquals("/someTenant/NamedLayers/someMap", repo.getInternalResourceName(
                "someMap"));
    }
    
    @Test
    public void shouldPrependSlashIfNoCustomerName()
    {
        this.repo = new MiDevNamedLayerRepository(null);
        assertEquals("/NamedLayers/someResource", repo.getInternalResourceName(
                "someResource"));
    }

}
