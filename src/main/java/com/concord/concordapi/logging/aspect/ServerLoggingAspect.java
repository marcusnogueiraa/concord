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

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.create(..)) && args(serverRequestBodyDTO)")
    public void logAfterCreateServer(Object serverRequestBodyDTO) {
        logger.info("Successfully created a new server with details: {}", serverRequestBodyDTO);
    }

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.deleteById(..)) && args(id)")
    public void logAfterDeleteServerById(Long id) {
        logger.info("Successfully deleted server with ID: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.updateById(..)) && args(id, serverPutBodyDTO)")
    public void logAfterUpdateServerById(Long id, Object serverPutBodyDTO) {
        logger.info("Successfully updated server with ID: {} and details: {}", id, serverPutBodyDTO);
    }

    @AfterReturning("execution(* com.concord.concordapi.server.service.ServerService.subscribeUser(..)) && args(username, serverId)")
    public void logAfterSubscribeUser(String username, Long serverId) {
        logger.info("User '{}' successfully subscribed to server with ID: {}", username, serverId);
    }
}
