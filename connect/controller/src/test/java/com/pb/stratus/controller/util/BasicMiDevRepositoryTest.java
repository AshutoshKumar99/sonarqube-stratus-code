package com.pb.stratus.controller.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicMiDevRepositoryTest
{
    
    private BasicMiDevRepository repo;
    
    @Before
    public void setUp()
    {
        this.repo = new BasicMiDevRepository("someTenant")
        {

            @Override
            public String getInternalResourceName(String externalResourceName) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
    
    @Test
    public void shouldRemoveSlashes()
    {
        assertEquals("someMap", repo.removeSlashes("/some/Map/"));
    }
    
}
