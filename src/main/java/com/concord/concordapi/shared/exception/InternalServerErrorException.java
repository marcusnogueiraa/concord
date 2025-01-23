package com.concord.concordapi.shared.exception;

public class InternalServerErrorException extends RuntimeException{
    public InternalServerErrorException(){
        super();
    }
    public InternalServerErrorException(String message, Exception e){
        super(message);
    }
    public InternalServerErrorException(String message){
        super(message);
    }
}