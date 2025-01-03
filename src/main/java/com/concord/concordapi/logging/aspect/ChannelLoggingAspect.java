package com.concord.concordapi.logging.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ChannelLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ChannelLoggingAspect.class);

    @AfterReturning("execution(* com.concord.concordapi.channel.service.ChannelService.get(..)) && args(id)")
    public void logAfterGetChannel(Long id) {
        logger.info("Successfully fetched channel with ID: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.channel.service.ChannelService.create(..)) && args(channel)")
    public void logAfterCreateChannel(Object channel) {
        logger.info("Successfully created a new channel with details: {}", channel);
    }

    @AfterReturning("execution(* com.concord.concordapi.channel.service.ChannelService.delete(..)) && args(id)")
    public void logAfterDeleteChannel(Long id) {
        logger.info("Successfully deleted channel with ID: {}", id);
    }

    @AfterReturning("execution(* com.concord.concordapi.channel.service.ChannelService.update(..)) && args(id, channel)")
    public void logAfterUpdateChannel(Long id, Object channel) {
        logger.info("Successfully updated channel with ID: {} and details: {}", id, channel);
    }
}
