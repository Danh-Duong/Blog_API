package com.example.Blog_API.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subComment")
@Entity
public class SubComment extends BaseEntity{
    private String content;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    @JsonIgnore
    private Comment comment;
}
