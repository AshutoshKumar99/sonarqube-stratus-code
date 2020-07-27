package com.pb.stratus.controller.exception;


/**
 * Exception for Catalog Config
 * 
 */
public class CatalogConfigException extends RuntimeException
{
    private static final long serialVersionUID = -3090076300597595323L;
    
    private String message;
    private String messageKey;
    private String value;

    
    public CatalogConfigException(String message)
    {
        this.message = message;

    }

    public CatalogConfigException(String message , Throwable cause)
    {
        super(message ,cause);
    }

    public CatalogConfigException(String message, String key)
    {
        this.message = message;
        this.messageKey = key;
    }

    public CatalogConfigException(String message, String key, String object)
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
