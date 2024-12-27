package com.concord.concordapi.shared.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(){
        super();
    }
    public EntityNotFoundException(String message){
        super(message);
    }
}
