package com.example.Blog_API.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
@Entity
public class Comment extends BaseEntity{

    private String content;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @Column(name = "blog_id")
    private Long blogId;

    @ManyToOne
    @JoinColumn(name = "blog_id", insertable = false, updatable = false)
    @JsonIgnore
    private Blog blog;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<SubComment> subComments=new ArrayList<>();

}
