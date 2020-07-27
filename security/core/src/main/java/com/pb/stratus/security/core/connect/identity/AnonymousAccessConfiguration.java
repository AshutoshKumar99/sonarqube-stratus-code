package com.pb.stratus.security.core.connect.identity;

/**
 * Author: sh003bh
 * Date: 10/19/11
 * Time: 6:06 PM
 */
public interface AnonymousAccessConfiguration
{

    public String getAnonymousUserName();

    public String getAnonymousPassword();

    public boolean isAnonymousLoginAllowed();

}
