package com.example.Blog_API.repository;

import com.example.Blog_API.entity.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    EmailToken findByToken(String token);
}
