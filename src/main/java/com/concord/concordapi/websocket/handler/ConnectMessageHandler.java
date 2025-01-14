package com.concord.concordapi.websocket.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.auth.exception.IncorrectTokenException;
import com.concord.concordapi.auth.service.JwtTokenService;
import com.concord.concordapi.user.service.UserService;
import com.concord.concordapi.websocket.entity.content.ConnectContent;

@Component
public class ConnectMessageHandler extends EventHandler<ConnectContent>{
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private UserService userService;
    
    @Override
    protected void handle(ConnectContent content, WebSocketSession session) {
        try {
            String token = content.getToken();
            if (!jwtTokenService.isTokenValid(token)) throw new IncorrectTokenException("Incorrect JWT Token.");
            String authenticatedUserEmail = jwtTokenService.getSubjectFromToken(token);
            Long authenticatedUserId = userService.getUserIdByEmail(authenticatedUserEmail);
            session.getAttributes().put("userId", authenticatedUserId);
            sessionService.saveSession(session);
        } catch (Exception e) {
            System.out.println("Erro ao enviar mensagem para o usuário: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
