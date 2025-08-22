package com.dollar.ChatApp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Data
public class UserModel {

    @Id
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

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotNull(message = "isOnline cannot be null")
    private Boolean isOnline;
}
