package com.pb.stratus.security.core.resourceauthorization;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 12:48 PM
 */
public class ResourceException extends Exception {
    /**
     * Create a new SAXException.
     */
    public ResourceException()
    {
	super();
	this.exception = null;
    }


    /**
     * Create a new SAXException.
     *
     * @param message The error or warning message.
     */
    public ResourceException(String message) {
	super(message);
	this.exception = null;
    }


    /**
     * Create a new ResourceException wrapping an existing exception.
     *
     * <p>The existing exception will be embedded in the new
     * one, and its message will become the default message for
     * the SAXException.</p>
     *
     * @param e The exception to be wrapped in a SAXException.
     */
    public ResourceException(Exception e)
    {
	super();
	this.exception = e;
    }


    /**
     * Create a new ResourceException from an existing exception.
     *
     * <p>The existing exception will be embedded in the new
     * one, but the new exception will have its own message.</p>
     *
     * @param message The detail message.
     * @param e The exception to be wrapped in a SAXException.
     */
    public ResourceException(String message, Exception e)
    {
	super(message);
	this.exception = e;
    }


    /**
     * Return a detail message for this exception.
     *
     * <p>If there is an embedded exception, and if the SAXException
     * has no detail message of its own, this method will return
     * the detail message from the embedded exception.</p>
     *
     * @return The error or warning message.
     */
    public String getMessage ()
    {
	String message = super.getMessage();

	if (message == null && exception != null) {
	    return exception.getMessage();
	} else {
	    return message;
	}
    }


    /**
     * Return the embedded exception, if any.
     *
     * @return The embedded exception, or null if there is none.
     */
    public Exception getException ()
    {
	return exception;
    }

    /**
    * Return the cause of the exception
    *
    * @return Return the cause of the exception
    */
    public Throwable getCause() {
        return exception;
    }

    /**
     * Override toString to pick up any embedded exception.
     *
     * @return A string representation of this exception.
     */
    public String toString ()
    {
	if (exception != null) {
	    return exception.toString();
	} else {
	    return super.toString();
	}
    }

    //////////////////////////////////////////////////////////////////////
    // Internal state.
    //////////////////////////////////////////////////////////////////////


    /**
     * @serial The embedded exception if tunnelling, or null.
     */
    private Exception exception;
}
