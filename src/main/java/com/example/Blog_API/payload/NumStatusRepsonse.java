package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NumStatusRepsonse {
    private Long numTotalStatus;
    private Long numLikeStatus;
    private Long numDislikeStatus;
}
