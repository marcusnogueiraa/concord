package com.concord.concordapi.auth.exception;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
