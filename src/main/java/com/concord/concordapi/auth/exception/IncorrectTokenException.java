package com.concord.concordapi.auth.exception;

public class IncorrectTokenException extends RuntimeException {
    public IncorrectTokenException(String message){
        super(message);
    }
}
