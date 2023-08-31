package com.example.Blog_API.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "poll")
@Entity
public class Poll extends BaseEntity{
    private String title;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<AnswerPoll> answerPolls=new ArrayList<>();

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    User user;

    Boolean isAddAnswer=Boolean.FALSE;

    @Column(name = "blog_id")
    private Long blogId;

    @OneToOne
    @JoinColumn(name = "blog_id", insertable = false, updatable = false)
    @JsonIgnore
    private Blog blog;

    private int numAnswerPoll=0;

}
