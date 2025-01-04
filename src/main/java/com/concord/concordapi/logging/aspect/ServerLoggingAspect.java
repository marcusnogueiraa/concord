package com.concord.concordapi.logging.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServerLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServerLoggingAspect.class);

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.getById(..)) && args(id)")
    public void logAfterGetServerById(Long id) {
        logger.info("Successfully fetched server with ID: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.create(..)) && args(server)")
    public void logAfterCreateServer(Object server) {
        logger.info("Successfully created a new server with details: {}", server);
    }

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.deleteById(..)) && args(id)")
    public void logAfterDeleteServerById(Long id) {
        logger.info("Successfully deleted server with ID: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.updateById(..)) && args(id, server)")
    public void logAfterUpdateServerById(Long id, Object server) {
        logger.info("Successfully updated server with ID: {} and details: {}", id, server);
    }

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.subscribeUser(..)) && args(userId, serverId)")
    public void logAfterSubscribeUser(Long userId, Long serverId) {
        logger.info("User '{}' successfully subscribed to server with ID: {}", userId, serverId);
    }
}
