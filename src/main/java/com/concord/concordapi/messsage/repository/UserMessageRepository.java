package com.concord.concordapi.messsage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.concord.concordapi.messsage.entity.UserMessage;

@Repository
public interface UserMessageRepository extends MongoRepository<UserMessage, String>{
    @Query("{ 'toUserId': ?0, 'fromUserId': ?1, 'isRead': false }")
    @Update("{ '$set': { 'isRead': true } }")
    void markMessagesAsRead(Long toUserId, Long fromUserId);

    @Query(value = "{ 'toUserId': ?0, 'fromUserId': ?1, 'isRead': false }", sort = "{ 'timestamp': 1 }")
    List<UserMessage> findUnreadMessagesOrdered(Long toUserId, Long fromUserId);

    Page<UserMessage> findByToUserIdAndFromUserIdOrToUserIdAndFromUserId(
        Long toUserId, Long fromUserId, Long fromUserIdReversed, Long toUserIdReversed, Pageable pageable
    );
}
