package com.pb.stratus.controller;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: sh003bh
 * Date: 8/22/11
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeyNotInCachePresentException extends Exception
{
    private Serializable key;
    private String cacheName;

    public KeyNotInCachePresentException(Serializable key, String cacheName)
    {
        this.key = key;
        this.cacheName = cacheName;
    }

    public String getMessage()
    {
        StringBuilder sb =  new StringBuilder();
        sb.append("The key ");
        sb.append(key);
        sb.append(" for tenant ");
        sb.append(this.cacheName);
        sb.append(" was not found");
        return sb.toString();
    }
}
