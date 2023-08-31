package com.example.Blog_API.repository;

import com.example.Blog_API.entity.SubComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Long> {

    public List<SubComment> findByCommentId(Long commentId, Pageable pageable);
}
