package com.pb.stratus.controller;

public class IllegalRequestException extends RuntimeException
{
    private static final long serialVersionUID = -1683897673355615016L;

    public IllegalRequestException()
    {
        super();
    }

    public IllegalRequestException(String message)
    {
        super(message);
    }

    public IllegalRequestException(Throwable throwable)
    {
        super(throwable);
    }

}
