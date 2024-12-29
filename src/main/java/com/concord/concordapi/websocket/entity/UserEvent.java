package com.concord.concordapi.websocket.entity;

import java.io.Serializable;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Websocket;
import org.springframework.web.socket.WebSocketSession;


public class UserEvent implements Serializable {
    private String eventType; // Exemplo: USER_CONNECTED, USER_DISCONNECTED
    private String username;
    private String payload;
    private Long serverId;

    // Construtores, getters e setters
    public UserEvent(String eventType, String username, String payload) {
        this.eventType = eventType;
        this.username = username;
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public String getUsername() {
        return username;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "eventType='" + eventType + '\'' +
                ", username='" + username + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}