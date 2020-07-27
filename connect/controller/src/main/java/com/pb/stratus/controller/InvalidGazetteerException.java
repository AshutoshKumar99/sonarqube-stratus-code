package com.pb.stratus.controller;

public class InvalidGazetteerException extends RuntimeException
{
	/**
	 * Class to throw a Runtime Exception when an invalid Gazetteer Name has been retrieved.
	 */
	private static final long serialVersionUID = -4964879514365099393L;

	public InvalidGazetteerException()
    {
    	super();
    }
    
    public InvalidGazetteerException(String message)
    {
    	super(message);
    }
    
    public InvalidGazetteerException(String message, Throwable throwable)
    {
    	super(message, throwable);
    }
}
