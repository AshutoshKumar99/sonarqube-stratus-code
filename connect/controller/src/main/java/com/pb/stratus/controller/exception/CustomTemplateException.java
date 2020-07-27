package com.pb.stratus.controller.exception;

/**
 * Created by GU003DU on 11-Feb-18.
 */
public class CustomTemplateException extends RuntimeException {

    public CustomTemplateException(String message) {
        super(message);
    }

    public CustomTemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomTemplateException(Throwable cause) {
        super(cause);
    }
}
