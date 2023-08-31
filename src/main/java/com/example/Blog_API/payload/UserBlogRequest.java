package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserBlogRequest {
    @NotNull
    @NotBlank
    private Long blogId;

    @NotNull
    @NotBlank
    private String status;
}
