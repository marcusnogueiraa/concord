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

    @AfterReturning("execution(* com.concord.concordapi.user.service.UserService.getByUsername(..)) && args(username)")
    public void logAfterGetByUsername(String username) {
        logger.info("Successfully fetched user with username: {}", username);
    }

    @AfterReturning("execution(* com.concord.concordapi.user.service.UserService.findUserId(..)) && args(username)")
    public void logAfterFindUserId(String username) {
        logger.info("Successfully fetched user ID for username: {}", username);
    }

    @AfterReturning("execution(* com.concord.concordapi.user.service.UserService.changePassword(..))")
    public void logAfterChangePassword() {
        logger.info("Password successfully changed for the authenticated user.");
    }
}
