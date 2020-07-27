package com.pb.stratus.core.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 1/27/14
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TenantNameHolderImpl implements TenantNameHolder {

    private String tenantName;

    public TenantNameHolderImpl(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantName() {
        return tenantName;
    }
}
