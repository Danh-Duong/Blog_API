package com.example.Blog_API.entity;//package com.example.Blog.entity;
//

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
//
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_blog")
@Entity
public class UserBlog {
    @EmbeddedId
    UserBlogKey id;

    @OneToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @OneToOne
    @MapsId("blogId")
    @JoinColumn(name = "blog_id")
    Blog blog;

    String likeStatus;
}

//public class UserBlog {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "user_id")
//    private Long userId;
//
//    @OneToOne
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private User user;
//
//    @Column(name = "blog_id")
//    private Long blogId;
//
//    @OneToOne
//    @JoinColumn(name = "blog_id", insertable = false, updatable = false)
//    private Blog blog;
//
//    private String likeStatus;
//}