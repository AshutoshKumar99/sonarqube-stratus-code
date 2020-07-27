package com.pb.stratus.controller;

/**
 * Wraps a {@link java.lang.InterruptedException} in a RuntimeException.
 */
public class ThreadInterruptedException extends RuntimeException
{

    public ThreadInterruptedException(InterruptedException cause)
    {
        super(cause);
    }

}
