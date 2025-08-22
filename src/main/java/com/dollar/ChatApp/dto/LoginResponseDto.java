package com.dollar.ChatApp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String token;
    private UserDto userDto;
}
