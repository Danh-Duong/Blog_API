package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String username;
    private Date dateOfBitrh;
    private String phone;
    private String address;
    private String image;
    private List<String> roles;
}
