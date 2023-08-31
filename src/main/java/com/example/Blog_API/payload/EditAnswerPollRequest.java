package com.example.Blog_API.payload;

import lombok.Data;

import java.util.List;

@Data
public class EditAnswerPollRequest {

    private List<AddAnswerPollRequest> list;
}
