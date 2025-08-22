package com.dollar.ChatApp.repository;

import com.dollar.ChatApp.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<ChatMessage,String> {
    @Query(value = "{ '$and': [ " +
            "{ 'messageType': 'PRIVATE_MESSAGE' }, " +
            "{ '$or': [ " +
            "   { 'sender': ?0, 'receiver': ?1 }, " +
            "   { 'sender': ?1, 'receiver': ?0 } " +
            "] } ] }",
            sort = "{ 'timeStamp': 1 }")
    List<ChatMessage> findPrivateMessagesBetweenTwoUsers(String user1, String user2);
}
