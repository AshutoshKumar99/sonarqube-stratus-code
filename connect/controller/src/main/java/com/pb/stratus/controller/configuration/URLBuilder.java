package com.pb.stratus.controller.configuration;

import java.io.IOException;
import java.net.URL;

/**
 * Interface to build full URL of resource
 * (Tenant theme, admin console etc. url builders
 * should implement this)
 */
public interface URLBuilder
{
    
    /**
     * This method will generate and return full url for the
     * filename passed.
     * 
     * @param filename
     * @return returns full url path of the resource file.
     * @throws Exception
     */
    public URL buildFullURL(String filename) 
            throws RuntimeException, IOException;
    
}
