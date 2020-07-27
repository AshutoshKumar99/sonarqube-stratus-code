package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ConfigActionTest extends ControllerActionTestBase
{
    
    private ConfigAction action;
    private ControllerConfiguration config;
    private AuthorizationUtils authorizationUtils;

    @Before
    public void setUpAction() throws Exception
    {
        ConfigReader mockConfigReader = mock(ConfigReader.class);
        config = mock(ControllerConfiguration.class);
        authorizationUtils = mock(AuthorizationUtils.class);
        action = new ConfigAction(mockConfigReader, config, authorizationUtils);
    }
    
    @Test
    public void shouldBeABaseConfigProviderAction()
    {
        assertTrue(action instanceof BaseConfigProviderAction);
    }
    
    @Test
    public void shouldSetMimeTypeToApplicationXml()
    {
        assertEquals("application/xml", action.getMimeType());
    }

    
}
