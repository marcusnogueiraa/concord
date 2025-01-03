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

    @AfterReturning("execution(* com.concord.concordapi.user.service.UserService.changePassword(..))")
    public void logAfterChangePassword() {
        logger.info("Password successfully changed for the authenticated user.");
    }
    @AfterReturning("execution(* com.concord.concordapi.user.service.getAllFriendships(..)) && args(userId)")
    public void logAfterGetAllFriendships(Long userId) {
        logger.info("Successfully fetched all friendships for user ID: {}", userId);
    }
}
