package com.example.Blog_API.repository;

import com.example.Blog_API.entity.UserAnswerPoll;
import com.example.Blog_API.entity.UserPollKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAnswerPollRepository extends JpaRepository<UserAnswerPoll, UserPollKey> {
}
