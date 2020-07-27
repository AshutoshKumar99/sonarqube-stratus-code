package com.pb.gazetteer.search;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/6/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException() {
    }

    public InvalidQueryException(String message) {
        super(message);
    }

    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidQueryException(Throwable cause) {
        super(cause);
    }
}
