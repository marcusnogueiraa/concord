package com.concord.concordapi.logging.aspect;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthLoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthLoggingAspect.class);

    @Pointcut("execution(* com.concord.concordapi.auth.service.AuthService.authenticateUser(..))")
    public void authenticateUserMethod() {}

    @AfterReturning(pointcut = "authenticateUserMethod()", returning = "result")
    public void logUserAuthentication(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof String username) {
            logger.info("User '{}' authenticated successfully.", username);
        } else {
            logger.warn("Unexpected parameters in authenticateUser method.");
        }
    }
}
