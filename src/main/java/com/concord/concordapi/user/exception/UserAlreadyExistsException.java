package com.concord.concordapi.user.exception;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
