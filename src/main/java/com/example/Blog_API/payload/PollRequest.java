package com.example.Blog_API.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollRequest {
    @NotNull(message = "title is required")
    @NotBlank(message = "title is required")
    private String title;

    private Boolean addAnswer=Boolean.FALSE;

    private List<String> answerPolls;
}
