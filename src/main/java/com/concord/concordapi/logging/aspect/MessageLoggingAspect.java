package com.concord.concordapi.logging.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.concord.concordapi.websocket.entity.content.UserMessageContent;

@Aspect
@Component
public class MessageLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(MessageLoggingAspect.class);

    @AfterReturning("execution(* com.concord.concordapi.messsage.service.UserMessageService.saveUserMessageContent(..)) && args(userMessageContent)")
    public void logAfterSaveUserMessageContent(UserMessageContent userMessageContent) {
        logger.info("Message sent from User ID '{}' to User ID '{}'. Content: '{}'", 
                userMessageContent.getFromUserId(), 
                userMessageContent.getToUserId(), 
                userMessageContent.getMessage());
    }

    @AfterReturning("execution(* com.concord.concordapi.messsage.service.UserMessageService.markAllMessagesAsRead(..)) && args(toUserId, fromUserId)")
    public void logAfterMarkMessagesAsRead(Long toUserId, Long fromUserId) {
        logger.info("All messages from User ID '{}' to User ID '{}' marked as read.", fromUserId, toUserId);
    }

    @AfterReturning("execution(* com.concord.concordapi.messsage.service.UserMessageService.getUnreadChatSummaries(..)) && args(toUserId)")
    public void logAfterGetUnreadChatSummaries(Long toUserId) {
        logger.info("Fetched unread chat summaries for User ID '{}'.", toUserId);
    }

    @AfterReturning("execution(* com.concord.concordapi.messsage.service.UserMessageService.getChatMessages(..)) && args(toUserId, fromUserId, pageable)")
    public void logAfterGetChatMessages(Long toUserId, Long fromUserId, Object pageable) {
        logger.info("Fetched chat messages between User ID '{}' and User ID '{}'.", toUserId, fromUserId);
    }
}
