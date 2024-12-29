package com.concord.concordapi.shared.exception;

public class SMTPServerException extends RuntimeException {
    public SMTPServerException(String message){
        super(message);
    }
}
