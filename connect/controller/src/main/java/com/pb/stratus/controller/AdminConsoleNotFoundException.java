package com.pb.stratus.controller;

/**
 * Indicates that connection to Admin console has failed.
 * @author ka001ye
 *
 */
public class AdminConsoleNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = 439363825629808064L;

	public AdminConsoleNotFoundException()
	{

	}

	public AdminConsoleNotFoundException(String message)
	{
		super(message);
	}

	public AdminConsoleNotFoundException(Throwable throwable)
	{
		super(throwable);
	}
}
