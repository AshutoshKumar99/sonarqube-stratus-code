package com.pb.stratus.controller.json;

/**
 * An exception that will be thrown by JsonConverters if the conversion
 * of an object into a JSON string fails.
 *  
 * @author Volker Leidl
 */
public class ConversionException extends RuntimeException
{
    
	private static final long serialVersionUID = 355220677072823651L;

	public ConversionException()
    {
        super();
    }

    public ConversionException(Throwable cause)
    {
        super(cause);
    }

}
