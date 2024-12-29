package com.concord.concordapi.user.exception;

public class MaxRetryException extends RuntimeException {
    public MaxRetryException(String message){
        super(message);
    }
}
