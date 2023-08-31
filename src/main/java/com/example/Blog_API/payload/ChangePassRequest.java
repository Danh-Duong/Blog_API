package com.example.Blog_API.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChangePassRequest {
    @NotNull(message = "Old password is required")
    @NotBlank(message = "Old password is required")
    private String oldPass;

    @NotNull(message = "New password is required")
    @NotBlank(message = "New password is required")
    private String newPass;
}
