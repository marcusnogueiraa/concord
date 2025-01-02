package com.concord.concordapi.logging.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WebSocketLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketLoggingAspect.class);

    @After("execution(* com.concord.concordapi.websocket.handler.WebSocketHandler.afterConnectionEstablished(..))")
    public void logAfterConnectionEstablished(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof org.springframework.web.socket.WebSocketSession session) {
            String sessionId = session.getId();
            logger.info("WebSocket connection established successfully. Session ID: {}", sessionId);
        }
    }

    @After("execution(* com.concord.concordapi.websocket.handler.WebSocketHandler.afterConnectionClosed(..))")
    public void logAfterConnectionClosed(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 1 && args[0] instanceof org.springframework.web.socket.WebSocketSession session) {
            String sessionId = session.getId();
            org.springframework.web.socket.CloseStatus status = (org.springframework.web.socket.CloseStatus) args[1];
            logger.info("WebSocket connection closed successfully. Session ID: {}, Close Status: {}", sessionId, status);
        }
    }
}
