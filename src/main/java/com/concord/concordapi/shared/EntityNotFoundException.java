package com.concord.concordapi.shared;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(){
        super();
    }
    public EntityNotFoundException(Long id){
        super("Entity " + id + " not found");
    }
}
