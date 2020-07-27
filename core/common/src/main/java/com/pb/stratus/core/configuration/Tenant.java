package com.pb.stratus.core.configuration;

/**
 * Interface to get tenant related info. A tenant will have a name and a
 * place where his tenant related info will be kept. This interface can have
 * more methods as the tenant gets complex in case of security scenario.
 */
public interface Tenant
{
    /**
     * Return the name of the tenant.
     * @return
     */
    public String getTenantName();

    /**
     * Currently the data of the tenant is not distributed. There will be a
     * centralized repository for the tenant config data. This method should
     * return
     * @return
     */
    public String getTenantPath();
}
