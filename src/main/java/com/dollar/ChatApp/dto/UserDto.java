package com.dollar.ChatApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
@Data
public class UserDto {
    private String id;

    @NotBlank(message = "Username cannot be blank")
    @Indexed(unique = true)
    private String userName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Phone cannot be blank")
    @Indexed(unique = true)
    private String phone;


    @NotNull(message = "isOnline cannot be null")
    private Boolean isOnline;
}
