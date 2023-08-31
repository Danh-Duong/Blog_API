package com.example.Blog_API.repository;

import com.example.Blog_API.entity.AnswerPoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerPollRepository extends JpaRepository<AnswerPoll, Long> {
    public List<AnswerPoll> findByPollId(Long pollId);

    public AnswerPoll findByAnswerAndPollId(String answer, Long pollId);
}
