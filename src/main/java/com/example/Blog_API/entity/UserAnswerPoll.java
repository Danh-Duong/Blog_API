package com.example.Blog_API.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_answer_poll")
@Entity
public class UserAnswerPoll{
    @EmbeddedId
    UserPollKey id;

    @OneToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @OneToOne
    @MapsId("answerPollId")
    @JoinColumn(name = "answer_poll_id")
    AnswerPoll answerPoll;

}
