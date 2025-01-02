package com.concord.concordapi.logging.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FriendshipLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipLoggingAspect.class);

    @AfterReturning("execution(* com.concord.concordapi.friendship.service.FriendshipService.get(..)) && args(id)")
    public void logAfterGetFriendship(Long id) {
        logger.info("Successfully fetched friendship with ID: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.friendship.service.FriendshipService.getAllFriendships(..)) && args(userId)")
    public void logAfterGetAllFriendships(Long userId) {
        logger.info("Successfully fetched all friendships for user ID: {}", userId);
    }

    @AfterReturning("execution(* com.concord.concordapi.friendship.service.FriendshipService.create(..)) && args(friendshipDTO)")
    public void logAfterCreateFriendship(Object friendshipDTO) {
        logger.info("Successfully created a new friendship with details: {}", friendshipDTO);
    }

    @AfterReturning("execution(* com.concord.concordapi.friendship.service.FriendshipService.delete(..)) && args(id)")
    public void logAfterDeleteFriendship(Long id) {
        logger.info("Successfully deleted friendship with ID: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.friendship.service.FriendshipService.update(..)) && args(id, friendshipDTO)")
    public void logAfterUpdateFriendship(Long id, Object friendshipDTO) {
        logger.info("Successfully updated friendship with ID: {} with details: {}", id, friendshipDTO);
    }

}
