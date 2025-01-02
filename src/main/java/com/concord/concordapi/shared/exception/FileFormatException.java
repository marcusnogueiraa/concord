package com.concord.concordapi.shared.exception;

public class FileFormatException extends RuntimeException{
    public FileFormatException(){
        super();
    }
    public FileFormatException(String message){
        super(message);
    }
}
