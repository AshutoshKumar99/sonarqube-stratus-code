package com.pb.stratus.controller;

/**
 * xxxx
 * 
 * @author Kavitha
 */
public class RemoteAccessException extends RuntimeException
{

    private static final long serialVersionUID = 2008425442836381580L;
    
	public RemoteAccessException()
    {
        super();
    }
    public RemoteAccessException(String message)
    {
        super(message);
    }
    public RemoteAccessException(Throwable throwable)
    {
        super(throwable);
    }

}