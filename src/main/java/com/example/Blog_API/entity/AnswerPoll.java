package com.example.Blog_API.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "answer_poll")
@Entity
public class AnswerPoll extends BaseEntity{
    private String answer;
    private long percent=0;
    private int numAnswer=0;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    User user;

    @Column(name = "poll_id")
    private Long pollId;

    @ManyToOne
    @JoinColumn(name = "poll_id", insertable = false, updatable = false)
    @JsonIgnore
    Poll poll;

    @Transient
    @OneToOne(mappedBy = "answerPoll",cascade = CascadeType.ALL)
    UserAnswerPoll userAnswerPoll;

}
