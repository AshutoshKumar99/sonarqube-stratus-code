package com.pb.stratus.controller.configuration;

import com.pb.stratus.controller.TenantThemeNotFoundException;
import com.pb.stratus.controller.util.MockSupport;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.fail;

public class TenantThemeURLBuilderTest {
    
    private TenantThemeURLBuilder urlBuilder;
    private ControllerConfiguration config;
    
    @Before
    public void setUp()
    {
        MockSupport ms = new MockSupport();
        config  = ms.createMock(ControllerConfiguration.class);
        urlBuilder = new TenantThemeURLBuilder(config);
    }

    @Test
    public void testBuildFullURLValid() throws Exception
    {
        URL u = new URL("http://somehost:8080/tenanttheme");
        expect(config.getTenantThemeUrl()).andReturn(u);
        replay(config);
        String expectedUrl = "http://somehost:8080/tenanttheme/"+"defaultmap.xml";
        String actualUrl = urlBuilder.buildFullURL("defaultmap.xml").toString();
        Assert.assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void testBuildFullURLValidEndingInSlash() throws Exception
    {
        URL u = new URL("http://somehost:8080/tenanttheme/");
        expect(config.getTenantThemeUrl()).andReturn(u);
        replay(config);
        String expectedUrl = "http://somehost:8080/tenanttheme/"+"defaultmap.xml";
        String actualUrl = urlBuilder.buildFullURL("defaultmap.xml").toString();
        Assert.assertEquals(expectedUrl, actualUrl);
    }
    
    @Test
    public void testBuildFullURLInValid() throws Exception
    {
        expect(config.getTenantThemeUrl()).andReturn(null);
        replay(config);
        try
        {
            String actualUrl = urlBuilder.buildFullURL("defaultmap.xml").toString();
            fail("Exception expected");
        }
        catch (TenantThemeNotFoundException e) {
            //expected
        }
    }
}
