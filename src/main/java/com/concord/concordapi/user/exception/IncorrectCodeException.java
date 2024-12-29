package com.concord.concordapi.user.exception;

public class IncorrectCodeException extends RuntimeException {
    public IncorrectCodeException(String message){
        super(message);
    }
}
