package com.concord.concordapi.auth.exception;

public class MaxRetryException extends RuntimeException {
    public MaxRetryException(String message){
        super(message);
    }
}
