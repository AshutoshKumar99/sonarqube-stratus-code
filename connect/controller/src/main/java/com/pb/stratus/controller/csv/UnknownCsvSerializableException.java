package com.pb.stratus.controller.csv;

public class UnknownCsvSerializableException extends RuntimeException
{
    public UnknownCsvSerializableException()
    {
        super();
    }
    public UnknownCsvSerializableException(String message)
    {
        super(message);
    }
    public UnknownCsvSerializableException(Throwable throwable)
    {
        super(throwable);
    }
}
