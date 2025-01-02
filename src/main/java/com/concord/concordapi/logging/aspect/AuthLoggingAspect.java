package com.concord.concordapi.logging.aspect;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.concord.concordapi.auth.dto.CreateUserDto;
import com.concord.concordapi.auth.dto.ForgotPasswordRequest;
import com.concord.concordapi.auth.dto.LoginUserDto;
import com.concord.concordapi.user.entity.User;

@Aspect
@Component
public class AuthLoggingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthLoggingAspect.class);

    @Pointcut("execution(* com.concord.concordapi.auth.service.AuthService.authenticateUser(..))")
    public void authenticateUserMethod() {}

    @Pointcut("execution(* com.concord.concordapi.auth.service.AuthService.registerUser(..))")
    public void registerUserMethod() {}

    @Pointcut("execution(* com.concord.concordapi.auth.service.AuthService.confirmUserRegister(..))")
    public void confirmUserRegisterMethod() {}

    @Pointcut("execution(* com.concord.concordapi.auth.controller.AuthController.sendForgotPassword(..))")
    public void sendForgotPasswordMethod() {}

    @Pointcut("execution(* com.concord.concordapi.auth.service.AuthService.resetPassword(..))")
    public void resetPasswordMethod() {}

    @AfterReturning(pointcut = "authenticateUserMethod()")
    public void logUserAuthentication(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 1 && args[0] instanceof LoginUserDto loginUserDto) {
            logger.info("User '{}' authenticated successfully from IP '{}'.", loginUserDto.username(), args[1]);
        } else {
            logger.warn("Unexpected parameters in authenticateUser method.");
        }
    }
    

    @AfterReturning(pointcut = "registerUserMethod()")
    public void logSendingEmailRegistrationCode(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof CreateUserDto createdUser){
            logger.info("Verification email sent to '{}'.", createdUser.email());
        } else {
            logger.warn("Unexpected parameters in registerUser method.");
        }
    }

    @AfterReturning(pointcut = "confirmUserRegisterMethod()", returning = "createdUser")
    public void logUserRegisterConfirmation(JoinPoint joinPoint, User createdUser){
        Object[] args = joinPoint.getArgs();
        
        if (args.length > 0 && args[0] instanceof String code){
            logger.info("User '{}' verified with code '{}'.", createdUser.getUsername(), code);
        } else {
            logger.warn("Unexpected parameters in confirmUserRegister method.");
        }
    }

    @AfterReturning("sendForgotPasswordMethod()")
    public void logSendForgotPassword(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 2 && args[0] instanceof ForgotPasswordRequest request && args[1] instanceof String clientIp) {
            logger.info("Password reset link sent to '{}', initiated from IP '{}'.", request.email(), clientIp);
        } else {
            logger.warn("Unexpected parameters in sendForgotPassword method.");
        }
    }

    @AfterReturning("resetPasswordMethod()")
    public void logPasswordResetSuccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length >= 3 && args[0] instanceof String token && args[1] instanceof String newPassword && args[2] instanceof String clientIp) {
            logger.info("Password reset successfully for user with token '{}', initiated from IP '{}'.", token, clientIp);
        } else {
            logger.warn("Unexpected parameters in resetPassword method.");
        }
    }
}
