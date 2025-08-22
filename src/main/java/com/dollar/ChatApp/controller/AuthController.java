package com.dollar.ChatApp.controller;

import com.dollar.ChatApp.dto.LoginRequestDto;
import com.dollar.ChatApp.dto.LoginResponseDto;
import com.dollar.ChatApp.dto.RegisterRequestDto;
import com.dollar.ChatApp.dto.UserDto;
import com.dollar.ChatApp.model.UserModel;
import com.dollar.ChatApp.repository.UserRepository;
import com.dollar.ChatApp.service.AuthService;
import com.dollar.ChatApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository repository;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(authService.registerUser(registerRequestDto));
    }

    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto loginResponseDto = authService.login(requestDto);
        ResponseCookie responseCookie = ResponseCookie.from("JWT", loginResponseDto.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2 * 60 * 60)
                .sameSite("strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResponseDto.getUserDto());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        return authService.logout();
    }

    @GetMapping("/getOnlineUsers")
    public ResponseEntity<Map<String, Object>> getOnlineUsers(){
        return ResponseEntity.ok(authService.getOnlineUser());
    }


    @GetMapping("/getCurrentUser")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){
        if (authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not authorised");
        }

        String userName=authentication.getName();
        UserModel user=repository.findByUserName(userName).orElseThrow(()->new RuntimeException("user not found."));
        return ResponseEntity.ok(convertToUserDto(user));

    }

    public UserDto convertToUserDto(UserModel userModel){
        UserDto user= new UserDto();
        user.setUserName(userModel.getUserName());
        user.setEmail(userModel.getEmail());
        user.setPhone(userModel.getPhone());
        user.setIsOnline(userModel.getIsOnline());
        return user;
    }
}
