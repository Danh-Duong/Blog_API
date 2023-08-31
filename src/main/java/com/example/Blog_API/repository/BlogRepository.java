package com.example.Blog_API.repository;

import com.example.Blog_API.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

    Boolean existsByTitle(String title);

    @Query(nativeQuery = true, value = "select count(*) from blog where create_at>=?2 and create_at <=?3 and user_id=?1")
    int countBlogByCreateAtAndUserId(Long userId, Date startDate, Date endDate);

    @Query(nativeQuery = true, value = "select * from blog where user_id=?1 and create_at>=?2 and create_at <=?3")
    List<Blog> getBlogByCreateAtAndUserId(Long userId, Date startDate, Date endDate);


    @Query(value = "SELECT sum(num_user_liked) FROM blog where id IN (:blogIds)", nativeQuery = true)
    Long countLikeStatusByBlogId(@Param("blogIds") List<Long> blogIds);
    //
    @Query(value = "SELECT sum(num_user_disliked) FROM blog where id in (:blogIds)", nativeQuery = true)
    Long countDislikeStatusByBlogId(@Param("blogIds") List<Long> blogIds);
}
