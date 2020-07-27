package com.pb.stratus.controller.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TileMappingMiDevRepositoryTest
{

    private MiDevRepository repo;

    @Before
    public void setUp()
    {
        this.repo = new TileMappingMiDevRepository("someTenant");
    }

    @Test
    public void shouldPrependTenantNameAndSlashToResourceNames()
    {
        assertEquals("/someTenant/NamedTiles/NamedMaps/someMap", repo.getInternalResourceName(
                "someMap"));
    }

    @Test
    public void shouldPrependSlashIfNoCustomerName()
    {
        this.repo = new TileMappingMiDevRepository(null);
        assertEquals("/NamedTiles/NamedMaps/someResource", repo.getInternalResourceName(
                "someResource"));
    }

}
