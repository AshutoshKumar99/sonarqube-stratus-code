package com.pb.stratus.controller;

/**
 * xxxx
 * 
 * @author Kavitha
 */
public class MapNotFoundException extends RuntimeException
{

    private static final long serialVersionUID = 6358846725434283853L;
	public MapNotFoundException()
    {
        super();
    }
    public MapNotFoundException(String message)
    {
        super(message);
    }
    public MapNotFoundException(Throwable throwable)
    {
        super(throwable);
    }

}