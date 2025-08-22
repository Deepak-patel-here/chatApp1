package com.dollar.ChatApp.listener;

import com.dollar.ChatApp.model.ChatMessage;
import com.dollar.ChatApp.service.UserService;
import com.dollar.ChatApp.util.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;



@Component
public class WebSocketListener {

    private static final Logger logger= LoggerFactory.getLogger(WebSocketListener.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebsocketConnectListener(SessionConnectedEvent event){
        logger.info("connected to websocket");
    }

    @EventListener
    public void handleWebsocketDisConnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor=StompHeaderAccessor.wrap(event.getMessage());
        String userName=headerAccessor.getSessionAttributes().get("userName").toString();
        userService.setUserOnlineStatus(userName,false);
        System.out.println ("disconnected to websocket");

        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMessageType(MessageType.LEAVE);
        chatMessage.setSender(userName);
        messageTemplate.convertAndSend("/topic/public",chatMessage);

    }
}
