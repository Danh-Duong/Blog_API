package com.example.Blog_API.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPollKey implements Serializable {
    @Column(name = "answer_poll_id")
    Long answerPollId;

    @Column(name = "user_id")
    Long userId;
}
