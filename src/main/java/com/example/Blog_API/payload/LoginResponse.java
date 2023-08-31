package com.example.Blog_API.payload;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String accessToken;
    private String type="Bearer";
    private String username;
    private List<String> roles;

    public LoginResponse(String accessToken, String username, List<String> roles) {
        this.accessToken = accessToken;
        this.username = username;
        this.roles = roles;
    }
}
