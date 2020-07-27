package com.pb.stratus.security.core.authentication;

/**
 * Created with IntelliJ IDEA.
 * User: si001jy
 * Date: 2/24/14
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthenticationFailureException extends Exception{
    /**
     * Creates a new AuthenticationFailureException.
     */
    public AuthenticationFailureException() {
        super();
    }

    /**
     * Constructs a new AuthenticationFailureException.
     *
     * @param message the reason for the exception
     */
    public AuthenticationFailureException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationFailureException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public AuthenticationFailureException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new AuthenticationFailureException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public AuthenticationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
