package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.websocket.service.SessionService;

public class EventHandler<T> {
    @Autowired
    protected SessionService sessionService; 

    protected void handle(T content, WebSocketSession session){}
}