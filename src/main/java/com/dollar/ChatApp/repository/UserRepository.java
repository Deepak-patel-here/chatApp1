package com.dollar.ChatApp.repository;

import com.dollar.ChatApp.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserModel,String> {
    public boolean existByUserName(String userName);
    public Optional<UserModel> findByUserName(String userName);
    public List<UserModel> findByIsOnlineTrue();
}
