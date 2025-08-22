package com.dollar.ChatApp.dto;

import lombok.*;


@Data
public class RegisterRequestDto {
    private String userName;
    private String email;
    private String password;
    private String phone;
}
