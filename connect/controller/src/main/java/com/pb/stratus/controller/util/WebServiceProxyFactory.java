package com.pb.stratus.controller.util;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * The WebServiceProxyFactory class is used get a single, unique
 * WebServiceProxy object for a specified configuration.
 *
 * @author co003ki
 */
public class WebServiceProxyFactory
{
    
    private static ServletContext applicationScope;
    
    public static void init(ServletContext context){
        applicationScope = context;
    }
    
    private static Map<ControllerConfiguration,WebServiceProxy> proxies = new HashMap<ControllerConfiguration,WebServiceProxy>();

    /**
     * Get a WebServiceProxy for the given configuration object.
     *
     * @param configuration a configuration object containing server 
     * configuration values.
     * @return an instance of WebServiceProxy that is shared between all
     * requests presenting the same configuration.
     */
    public static synchronized WebServiceProxy getProxy(
            ControllerConfiguration configuration)
    {
        Contract.pre(configuration != null, "Configuration required");
        WebServiceProxy proxy = proxies.get(configuration);

        if (proxy == null)
        {
            proxy = new WebServiceProxy(configuration,applicationScope);
            proxies.put(configuration, proxy);
        }

        return proxy;
    }
}
