package com.dollar.ChatApp.service;

import com.dollar.ChatApp.model.UserModel;
import com.dollar.ChatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public boolean isUserExist(String sender) {
        return userRepository.existByUserName(sender);
    }

    public void setUserOnlineStatus(String sender, boolean b) {
        UserModel user=userRepository.findByUserName(sender).orElse(null);
        if(user!=null){
            user.setIsOnline(b);
            userRepository.save(user);
        }else System.out.println("user is null");

    }
}
