package com.pb.stratus.core.configuration;

/**
 * A source of a tenant name.  
 */
public interface TenantNameHolder
{
    
    /**
     * Returns a non-empty textual identifier to be used in the current thread
     * of execution. 
     */
    String getTenantName();

}
