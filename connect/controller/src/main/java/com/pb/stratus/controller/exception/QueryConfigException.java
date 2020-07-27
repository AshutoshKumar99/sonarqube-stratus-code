package com.pb.stratus.controller.exception;

/**
 * Exception for Attribute Query Module
 * 
 * @author ar009sh
 */

public class QueryConfigException extends RuntimeException
{
    private static final long serialVersionUID = -3090076300597595886L;
    
    private String message;
    private String messageKey;
    private String value;

    
    
    public QueryConfigException(String message)
    {
        this.message = message;

    }

    public QueryConfigException(String message, String key)
    {
        this.message = message;
        this.messageKey = key;
    }

    public QueryConfigException(String message, String key, String object)
    {
        this.message = message;
        this.messageKey = key;
        this.value = object;

    }

    public String getMessage()
    {
        return this.message;
    }

    public String getKey()
    {
        return messageKey;
    }

    public String getValue()
    {
        return value;
    }

}
