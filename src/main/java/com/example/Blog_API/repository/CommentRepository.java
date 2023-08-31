package com.example.Blog_API.repository;

import com.example.Blog_API.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByBlogId(Long blogId, Pageable pageable);


    @Query(nativeQuery = true, value = "select count(*) from comment where blog_id in (:blogIds)")
    int countCommentByCreateAt(Long[] blogIds);

    @Query(nativeQuery = true, value = "select * from comment where blog_id in (:blogIds)")
    List<Comment> getCommentByCreateAt(Long[] blogIds);

}
