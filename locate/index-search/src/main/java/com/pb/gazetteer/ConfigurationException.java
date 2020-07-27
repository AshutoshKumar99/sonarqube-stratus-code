package com.pb.gazetteer;

/**
 * An exception that is thrown when the application configuration cannot be 
 * parsed successfully.
 */
public class ConfigurationException extends RuntimeException
{

    private static final long serialVersionUID = 1272697396601104066L;

    public ConfigurationException()
    {
    }

    public ConfigurationException(String message)
    {
        super(message);
    }

    public ConfigurationException(Throwable cause)
    {
        super(cause);
    }

    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
