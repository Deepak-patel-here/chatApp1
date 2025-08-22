package com.dollar.ChatApp.controller;

import com.dollar.ChatApp.model.ChatMessage;
import com.dollar.ChatApp.repository.ChatRepository;
import com.dollar.ChatApp.service.UserService;
import com.dollar.ChatApp.util.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private UserService service;

    @Autowired
    private ChatRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        if (service.isUserExist(chatMessage.getSender())) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            service.setUserOnlineStatus(chatMessage.getSender(), true);
            System.out.println("user add successfully!! :" + chatMessage.getSender() + " with session id " + headerAccessor.getSessionId());

            chatMessage.setTimeStamp(LocalDateTime.now());
            if (chatMessage.getContent() == null) {
                chatMessage.setContent("");
            }

            return repository.save(chatMessage);
        }
        return null;
    }


    @MessageMapping("chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(
            @Payload ChatMessage chatMessage
    ) {
        if (service.isUserExist(chatMessage.getSender())) {
            if (chatMessage.getTimeStamp() == null) {
                chatMessage.setTimeStamp(LocalDateTime.now());
            }

            if (chatMessage.getContent() == null) {
                chatMessage.setContent("");
            }

            return repository.save(chatMessage);

        }
        return null;
    }

    //it uses dynamic subscription concept.
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        if (service.isUserExist(chatMessage.getSender()) && service.isUserExist(chatMessage.getReceiver())) {
            if (chatMessage.getTimeStamp() == null) {
                chatMessage.setTimeStamp(LocalDateTime.now());
            }

            if (chatMessage.getContent() == null) {
                chatMessage.setContent("");
            }

            chatMessage.setMessageType(MessageType.PRIVATE_MESSAGE);

            ChatMessage savedMessage = repository.save(chatMessage);
            System.out.println("message saved successfully with id " + savedMessage.getId());

            try {

                String receiverDestination = "/user" + chatMessage.getReceiver() + "/queue/private";
                System.out.println("the receiver destination is " + receiverDestination);
                messagingTemplate.convertAndSend(receiverDestination, savedMessage);

                String senderDestination = "/user" + chatMessage.getSender() + "/queue/private";
                System.out.println("the sender destination is " + senderDestination);
                messagingTemplate.convertAndSend(senderDestination, savedMessage);
            } catch (Exception e) {
                System.out.println("error in sending message : " + e.getLocalizedMessage());
            }


        } else {
            System.out.println("ERROR Sender  : " + chatMessage.getSender() + " or receiver " + chatMessage.getReceiver() + " doesn't exist.");
        }
    }
}
