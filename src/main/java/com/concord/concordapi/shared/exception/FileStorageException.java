package com.concord.concordapi.shared.exception;


public class FileStorageException extends RuntimeException{
    public FileStorageException(){
        super();
    }
    public FileStorageException(String message, Exception e){
        super(message);
    }
    public FileStorageException(String message){
        super(message);
    }
}
