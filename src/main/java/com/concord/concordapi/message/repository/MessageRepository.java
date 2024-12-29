package com.concord.concordapi.message.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.concord.concordapi.message.entity.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChannel(String channel);
}
