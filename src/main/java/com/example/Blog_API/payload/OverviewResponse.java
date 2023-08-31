package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverviewResponse {
    private int month;
    private int year;
    private int numBlog;
    private int numComment;
    private NumStatusRepsonse numStatus;
}
