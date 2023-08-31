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
@Table(name = "blog")
@Entity
public class Blog extends BaseEntity{
    private String title;
    private String content;
    private String imageUrl;

    @Column(name = "userId")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    @JsonIgnore
    private User user;


    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
    private List<Comment> comments=new ArrayList<>();

    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    @JsonIgnore
    private Category category;

    private Boolean isDeleted=Boolean.FALSE;

    private int numUserLiked=0;
    private int numUserDisliked=0;

    @OneToOne(cascade = CascadeType.ALL)
    private Poll poll;
}
