package com.dollar.ChatApp.service;

import com.dollar.ChatApp.dto.LoginRequestDto;
import com.dollar.ChatApp.dto.LoginResponseDto;
import com.dollar.ChatApp.dto.RegisterRequestDto;
import com.dollar.ChatApp.dto.UserDto;
import com.dollar.ChatApp.jwt.JwtService;
import com.dollar.ChatApp.model.UserModel;
import com.dollar.ChatApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    public UserDto registerUser(RegisterRequestDto registerRequestDto) {
        if(repository.findByUserName(registerRequestDto.getUserName()).isPresent()){
            throw new RuntimeException("User already exist");
        }

        UserModel user =new UserModel();
        user.setUserName(registerRequestDto.getUserName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPhone(registerRequestDto.getPhone());
        user.setPassword(encoder.encode(registerRequestDto.getPassword()));
        user.setIsOnline(true);

        UserModel savedUser=repository.save(user);
        return convertToUserDto(savedUser);

    }

    public LoginResponseDto login(LoginRequestDto requestDto) {

        UserModel user=repository
                 .findByUserName(requestDto.getUserName()).orElseThrow(()->new RuntimeException("user not exist"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUserName(),requestDto.getPassword()));
        String jwtToken=jwtService.generateToken(user);
        return LoginResponseDto
                .builder()
                .token(jwtToken)
                .userDto(convertToUserDto(user))
                .build();
    }

    public Map<String,Object> getOnlineUser(){
        List<UserModel> userList=repository.findByIsOnlineTrue();
        Map<String,Object> onlineUser=userList.stream().collect(Collectors.toMap(UserModel::getUserName, userModel -> userModel.getUserName()));
        return onlineUser;
    }

    public UserDto convertToUserDto(UserModel userModel){
        UserDto user= new UserDto();
        user.setUserName(userModel.getUserName());
        user.setEmail(userModel.getEmail());
        user.setPhone(userModel.getPhone());
        user.setIsOnline(userModel.getIsOnline());
        return user;
    }

    public ResponseEntity<String> logout() {
        ResponseCookie responseCookie= ResponseCookie.from("JWT","")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body("logged out successfully");
    }
}
