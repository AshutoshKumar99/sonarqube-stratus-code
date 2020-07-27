package com.pb.stratus.controller.print;

public class RenderException extends RuntimeException
{

    private static final long serialVersionUID = 2150486205242323784L;

    public RenderException()
    {
    }

    public RenderException(String message)
    {
        super(message);
    }

    public RenderException(Throwable cause)
    {
        super(cause);
    }

    public RenderException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
