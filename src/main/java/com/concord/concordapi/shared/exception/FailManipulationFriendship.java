package com.concord.concordapi.shared.exception;

public class FailManipulationFriendship extends RuntimeException{
    public FailManipulationFriendship(){
        super();
    }
    public FailManipulationFriendship(String message){
        super(message);
    }
}
