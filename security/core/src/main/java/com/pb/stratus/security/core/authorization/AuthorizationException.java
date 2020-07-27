package com.pb.stratus.security.core.authorization;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/28/14
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizationException extends Exception {
    /**
     * Creates a new AuthorizationException.
     */
    public AuthorizationException() {
        super();
    }

    /**
     * Constructs a new AuthorizationException.
     *
     * @param message the reason for the exception
     */
    public AuthorizationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthorizationException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public AuthorizationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new AuthorizationException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
