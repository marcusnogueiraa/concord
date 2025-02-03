package com.concord.concordapi.shared.exception;

public class FileNotFoundException extends RuntimeException{
    public FileNotFoundException(){
        super();
    }
    public FileNotFoundException(String message){
        super(message);
    }
}
