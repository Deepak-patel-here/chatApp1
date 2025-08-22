package com.dollar.ChatApp.model;

import com.dollar.ChatApp.util.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;


@Document("chat_message")
@Data
public class ChatMessage {
    @Id
    private String id;
    private String content;
    private String sender;
    private String receiver;
    @NotNull(message = "time stamp should not be null.")
    private LocalDateTime timeStamp;
    private String color;
    private MessageType messageType;
}
