package com.pb.stratus.core.exception;

/**
 * An exception thrown if the configuration contains an incorrect property.
 *
 * @author ku002va
 */
public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -1298272429771961772L;

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ConfigurationException(String arg0) {
        super(arg0);
    }

    public ConfigurationException(Throwable arg0) {
        super(arg0);
    }

}