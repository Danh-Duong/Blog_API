package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NotNull
public class UpdateUserRequest {
    private String dateOfBitrh;
    private String phone;
    private String address;
}
