package com.pb.stratus.controller.exception;

/**
 * @author ekiras
 */
public class AnalystCreditsExpiredException extends RuntimeException {

    private static final String MESSAGE = "Credits Expired";

    public AnalystCreditsExpiredException(){
        super(MESSAGE);
    }

    public AnalystCreditsExpiredException(String message){
        super(message);
    }

}
