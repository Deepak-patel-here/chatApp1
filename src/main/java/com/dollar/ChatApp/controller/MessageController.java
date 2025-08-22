package com.dollar.ChatApp.controller;

import com.dollar.ChatApp.model.ChatMessage;
import com.dollar.ChatApp.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private ChatRepository repository;

    public ResponseEntity<List<ChatMessage>> getAllMessages(
            @RequestParam String user1,
            @RequestParam String user2
    ){
        List<ChatMessage> messages=repository.findPrivateMessagesBetweenTwoUsers(user1,user2);
        return ResponseEntity.ok(messages);

    }
}
