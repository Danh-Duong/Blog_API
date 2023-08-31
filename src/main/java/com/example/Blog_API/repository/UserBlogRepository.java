package com.example.Blog_API.repository;

import com.example.Blog_API.entity.UserBlog;
import com.example.Blog_API.entity.UserBlogKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlogRepository extends JpaRepository<UserBlog, UserBlogKey> {
    UserBlog findByUserIdAndBlogId(Long userId, Long blogId);

//    @Query(value = "SELECT count(*) FROM user_blog WHERE blog_id IN :blogIds AND like_status = 'LIKED'", nativeQuery = true)
//    int countLikeStatusByBlogId(@Param("blogIds") List<Long> blogIds);



}

//@Repository
//public interface UserBlogRepository extends JpaRepository<UserBlog, Long> {
////    UserBlog findByUserAndBlog(User user, Blog blog);
//}