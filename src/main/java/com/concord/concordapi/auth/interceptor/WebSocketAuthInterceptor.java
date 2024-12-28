package com.concord.concordapi.auth.interceptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.concord.concordapi.auth.service.JwtTokenService;


@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtTokenService jwtTokenService;
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        String token = request.getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);  // Remove "Bearer " do token

            String username = jwtTokenService.getSubjectFromToken(jwtToken);
          
            attributes.put("username", username);
      
        }

        return true;  // Permite o handshake continuar
       
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'afterHandshake'");
    }
}
