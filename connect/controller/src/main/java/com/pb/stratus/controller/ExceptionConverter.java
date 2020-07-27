package com.pb.stratus.controller;

/**
 * Converts an arbitrary exception into an another (unchecked) exception.
 * 
 * @author Volker Leidl
 */
public interface ExceptionConverter
{

    /**
     * Converts <code>x</code> into a RuntimeException. This method should not throw any unchecked exceptions itself and
     * must not return a <code>null</code> value.
     * 
     * @param x the exception to convert
     * @return an unchecked exception. Never returns <code>null</code>
     */
    public RuntimeException convert(Exception x);

}
