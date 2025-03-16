package com.concord.concordapi.messsage.entity.message;

import com.fasterxml.jackson.databind.JsonNode;

public class MessageFactory {
    public static Message createMessage(MessageType type, Message message) {
        switch (type) {
            case TEXT: return new MessageText(message.getText());
            case IMAGE: return new MessageImage(message.getText(), message.getPath());
            case FILE: return new MessageFile(message.getPath());
            default: throw new IllegalArgumentException("Message type unknown: " + type);
        }
    }
    public static Message createMessage(MessageType type, JsonNode message) {
        switch (type) {
            case TEXT: return new MessageText(message.get("text").asText());
            case IMAGE: return new MessageImage(message.get("text").asText(), message.get("path").asText());
            case FILE: return new MessageFile(message.get("path").asText());
            default: throw new IllegalArgumentException("Message type unknown: " + type);
        }
    }
}