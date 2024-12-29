package com.concord.concordapi.auth.exception;

public class IncorrectCodeException extends RuntimeException {
    public IncorrectCodeException(String message){
        super(message);
    }
}
