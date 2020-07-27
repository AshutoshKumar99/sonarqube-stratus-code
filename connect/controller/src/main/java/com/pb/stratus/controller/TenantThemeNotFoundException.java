package com.pb.stratus.controller;

/**
 * Indicates that connection to Tenant theme has failed.
 *
 */
public class TenantThemeNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = -7155138332088737898L;

    public TenantThemeNotFoundException()
    {

    }

    public TenantThemeNotFoundException(String message)
    {
        super(message);
    }

    public TenantThemeNotFoundException(Throwable throwable)
    {
        super(throwable);
    }
}
