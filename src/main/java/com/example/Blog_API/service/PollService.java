package com.example.Blog_API.service;

import com.example.Blog_API.authentication.JwtTokenProvider;
import com.example.Blog_API.entity.*;
import com.example.Blog_API.payload.PollRequest;
import com.example.Blog_API.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PollService {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PollRepository pollRepository;

    @Autowired
    AnswerPollRepository answerPollRepository;
    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserAnswerPollRepository userAnswerPollRepository;

    @Autowired
    RoleRepository roleRepository;


    public void deletePoll(Long blogId,Long pollId,String jwt){
        try{
            String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
            User user=userRepository.findByUsername(username);
            Blog blog=blogRepository.findById(blogId).orElseThrow(() -> new RuntimeException("This blog is none-exsit"));
            Poll poll=pollRepository.findById(pollId).orElseThrow(() -> new RuntimeException("This poll is none-exsit"));
            Boolean isAdmin=user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN"));
            if (poll.getBlogId()!=blogId )
                throw new RuntimeException("This poll is not belong this blog");
            if (!(poll.getUserId()==user.getId() || isAdmin))
                throw new RuntimeException("This action is invalid");
            pollRepository.delete(poll);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public Poll updatePoll(Long blogId, Long pollId,String jwt, PollRequest pollRequest){
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Blog blog=blogRepository.findById(blogId).orElseThrow(() -> new RuntimeException("This blog is none-exsit"));
        Poll poll=pollRepository.findById(pollId).orElseThrow(() -> new RuntimeException("This poll is none-exsit"));
        Boolean isAdmin=user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN"));
        if (poll.getBlogId()!=blogId )
            throw new RuntimeException("This poll is not belong this blog");
        if (!(poll.getUserId()==user.getId() || isAdmin))
            throw new RuntimeException("This action is invalid");

        if (pollRequest.getTitle()!=null)
            poll.setTitle(pollRequest.getTitle());
        if (pollRequest.getAddAnswer()!=null)
            poll.setIsAddAnswer(pollRequest.getAddAnswer());
        pollRepository.save(poll);
        return poll;
    }

}
