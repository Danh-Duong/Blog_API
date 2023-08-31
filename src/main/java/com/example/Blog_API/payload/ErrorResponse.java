package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse extends BaseResponse{
    private ErrorDetails errorDetails;
}
