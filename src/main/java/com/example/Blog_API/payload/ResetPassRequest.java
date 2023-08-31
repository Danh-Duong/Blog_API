package com.example.Blog_API.payload;

import lombok.Data;

@Data
public class ResetPassRequest {
    private String token;
    private String newPass;
}
