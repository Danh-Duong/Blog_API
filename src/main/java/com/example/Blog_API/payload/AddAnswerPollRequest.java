package com.example.Blog_API.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AddAnswerPollRequest {

    private Long answerPollId;

    @NotBlank
    @NotNull
    @NotEmpty
    private String answer;
}
