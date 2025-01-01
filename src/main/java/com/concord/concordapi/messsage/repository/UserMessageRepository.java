package com.concord.concordapi.messsage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.messsage.entity.UserMessage;

@Repository
public interface UserMessageRepository extends MongoRepository<UserMessage, String>{
    
}
