package com.concord.concordapi.websocket.entity;

import java.io.Serializable;



public class ClientMessage implements Serializable {
    private String type; // Exemplo: USER_CONNECTED, USER_DISCONNECTED
    private String to;
    private String username;
    private String message;
    

    // Construtores, getters e setters
    public ClientMessage(String type, String to, String message) {
        this.type = type;
        this.to = to;
        this.message = message;
    }
    public ClientMessage(){}

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
    public String getTo(){
        return to;
    }

    public void setUsername(String username){
        this.username = username;
    }

    @Override
    public String toString() {
        return "ClientMessage{" +
                "type='" + type + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}