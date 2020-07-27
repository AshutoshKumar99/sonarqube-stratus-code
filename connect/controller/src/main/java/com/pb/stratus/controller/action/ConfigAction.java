package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;

/**
 * Action class to get config files from the admin console.
 */
public class ConfigAction extends BaseConfigProviderAction
{
    
    public ConfigAction(ConfigReader configReader, ControllerConfiguration config,
                        AuthorizationUtils authorizationUtils) {
        super(configReader, config, authorizationUtils);
    }
    
    @Override
    protected String getMimeType()
    {
        return "application/xml";
    }

}
