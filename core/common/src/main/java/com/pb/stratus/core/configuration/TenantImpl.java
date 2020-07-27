package com.pb.stratus.core.configuration;


import com.pb.stratus.core.common.Preconditions;

import java.io.File;

public class TenantImpl implements Tenant
{
    private CustomerConfigDirHolder configDirHolder;
    private TenantNameHolder tenantNameHolder;

    public TenantImpl(CustomerConfigDirHolder configDirHolder,
            TenantNameHolder tenantNameHolder)
    {
        Preconditions.checkNotNull(configDirHolder, "config-holder cannot be " +
                "null");
        Preconditions.checkNotNull(tenantNameHolder, "tenant name" +
                "-holder cannot be null");
        this.configDirHolder = configDirHolder;
        this.tenantNameHolder = tenantNameHolder;
    }

    public String getTenantName()
    {
        return this.tenantNameHolder.getTenantName();
    }

    public String getTenantPath()
    {
        return this.configDirHolder.getCustomerConfigDir().getAbsolutePath()
                + File.separator + getTenantName();
    }
}
