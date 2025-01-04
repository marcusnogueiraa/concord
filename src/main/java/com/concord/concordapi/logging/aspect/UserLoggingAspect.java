package com.concord.concordapi.logging.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(UserLoggingAspect.class);

    @AfterReturning("execution(* com.concord.concordapi.user.service.UserService.getById(..)) && args(id)")
    public void logAfterGetById(Long id) {
        logger.info("Successfully fetched user with id: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.user.service.UserService.findUserIdByEmail(..)) && args(email)")
    public void logAfterFindUserId(String email) {
        logger.info("Successfully fetched user ID for username: {}", email);
    }

    @AfterReturning("execution(* com.concord.concordapi.user.service.UserService.update(..)) && args(userPutDto, id)")
    public void logAfterGetAllFriendships(Object userPutDto, Long userId) {
        logger.info("Successfully fetched all friendships for user: {} ID: {}", userPutDto, userId);
    }
}
