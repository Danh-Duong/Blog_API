package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogRequest {
    private String title;
    private String content;

    @NotBlank(message = "CategoryId is not blank")
    @NotNull(message = "CategoryId is required")
    private Long categoryId;

    PollRequest pollRequest;
}
