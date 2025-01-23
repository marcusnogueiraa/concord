package com.concord.concordapi.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.websocket.entity.ClientMessage;
import com.concord.concordapi.websocket.entity.EventType;
import com.concord.concordapi.websocket.entity.content.FriendRequestContent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificationService {
    
    @Autowired    
    private SessionService sessionService;

    @Autowired    
    private ObjectMapper objectMapper = new ObjectMapper();

    public Boolean sendFriendRequestToUser(Long userId, FriendshipDto friendshipDto) throws Exception {
        WebSocketSession session = sessionService.getSession(userId);
        if (session != null && session.isOpen()) {
            ClientMessage<FriendRequestContent> friendshipMessage = getfriendshipMessage(friendshipDto);
            serializeAndSendMessage(session, friendshipMessage);
            return true;
        } else {
            System.out.println("Sessão não encontrada ou já fechada para o usuário: " + userId);
            return false;
        }
    }

    public Boolean sendMessageToUser(Long userId, ClientMessage<?> messageContent) throws Exception {
        WebSocketSession session = sessionService.getSession(userId);
        if (session != null && session.isOpen()) {
            serializeAndSendMessage(session, messageContent);
            return true;
        } else {
            System.out.println("Sessão não encontrada ou já fechada para o usuário: " + userId);
            return false;
        }
    }

    private void serializeAndSendMessage(WebSocketSession toSession, ClientMessage<?> clientMessage) throws Exception{
        String jsonMessage = objectMapper.writeValueAsString(clientMessage);
        toSession.sendMessage(new TextMessage(jsonMessage));
    }

    private ClientMessage<FriendRequestContent> getfriendshipMessage(FriendshipDto friendshipDto){
        return ClientMessage.<FriendRequestContent>builder()
                    .eventType(EventType.FRIEND_REQUEST)
                    .content(new FriendRequestContent(friendshipDto)).build();
    }
}
