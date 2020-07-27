package com.pb.stratus.core.exception;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 2/10/14
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParseException extends RuntimeException {
    private static final long serialVersionUID = 4206584673665956187L;

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(String message) {
        super(message);
    }
}
