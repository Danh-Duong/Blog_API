package com.example.Blog_API.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull(message = "Username is not null")
    @Email
    @NotBlank(message = "Username is not blank")
    private String username;

    @NotBlank(message = "Password is not blank")
    @NotNull(message = "Password is not null")
    private String password;
}
