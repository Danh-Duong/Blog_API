package com.example.Blog_API.service;

import com.example.Blog_API.authentication.JwtTokenProvider;
import com.example.Blog_API.entity.*;
import com.example.Blog_API.exception.GlobalException;
import com.example.Blog_API.payload.AddAnswerPollRequest;
import com.example.Blog_API.payload.EditAnswerPollRequest;
import com.example.Blog_API.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerPollService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    PollRepository pollRepository;

    @Autowired
    AnswerPollRepository answerPollRepository;

    @Autowired
    UserAnswerPollRepository userAnswerPollRepository;

    @Autowired
    RoleRepository roleRepository;

    public Blog answerPoll(Long blogId,Long pollId,Long answerPollId, String jwt){
        User user=userRepository.findByUsername(jwtTokenProvider.getUsernameFromJwt(jwt.substring(7)));
        Blog blog=blogRepository.findById(blogId).orElseThrow(()-> new GlobalException.NotFoundException("This blog is none-exsit"));
        Poll poll=pollRepository.findById(pollId).orElseThrow(() -> new GlobalException.NotFoundException("This poll is none-exsit"));
        if (poll.getBlogId()!=blogId)
            throw new RuntimeException("This poll is none-exsit in blog");
        UserAnswerPoll userAnswerPoll=new UserAnswerPoll();
        UserPollKey userPollKey=new UserPollKey();

        userPollKey.setAnswerPollId(answerPollId);
        userPollKey.setUserId(user.getId());
        userAnswerPoll.setId(userPollKey);
        userAnswerPoll.setAnswerPoll(answerPollRepository.findById(answerPollId).orElseThrow(()-> new RuntimeException("")));
        userAnswerPoll.setUser(user);

        userAnswerPollRepository.save(userAnswerPoll);

        poll.setNumAnswerPoll(poll.getNumAnswerPoll()+1);

        AnswerPoll answerPoll=answerPollRepository.findById(answerPollId).get();
        answerPoll.setNumAnswer(answerPoll.getNumAnswer()+1);
        answerPoll.setPercent((answerPoll.getNumAnswer()/poll.getNumAnswerPoll())*100);
        pollRepository.save(poll);
        answerPollRepository.save(answerPoll);

        return blog;

    }

    public Poll addAnswerToPoll(Long blogId, Long pollId, String jwt, AddAnswerPollRequest request){
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Blog blog=blogRepository.findById(blogId).orElseThrow(() -> new RuntimeException("This blog is none-exsit"));
        Poll poll=pollRepository.findById(pollId).orElseThrow(() -> new RuntimeException("This poll is none-exsit"));
        if (poll.getBlogId()!=blogId )
            throw new RuntimeException("This poll is not belong this blog");
        if (poll.getIsAddAnswer()==Boolean.FALSE)
            throw new RuntimeException("This poll can't be added any answer");
        if (answerPollRepository.findByAnswerAndPollId(request.getAnswer(), pollId)!=null)
            throw new RuntimeException("This answer Poll " + request.getAnswer() + " is exsit");
        else {
            AnswerPoll answerPoll=new AnswerPoll();
            answerPoll.setAnswer(request.getAnswer());
            answerPoll.setUserId(user.getId());
            answerPoll.setPollId(pollId);
            answerPollRepository.save(answerPoll);
        }
        return poll;

    }


    public Poll editAnswerToPoll( Long blogId,  Long pollId, String jwt, EditAnswerPollRequest request){
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Blog blog=blogRepository.findById(blogId).orElseThrow(() -> new RuntimeException("This blog is none-exsit"));
        Poll poll=pollRepository.findById(pollId).orElseThrow(() -> new RuntimeException("This poll is none-exsit"));
        if (poll.getBlogId()!=blogId )
            throw new RuntimeException("This poll is not belong this blog");

        Boolean isAdmin=user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN"));
        if (!(poll.getUserId()==user.getId() || isAdmin))
            throw new RuntimeException("This action is invalid");

        for (AddAnswerPollRequest a:request.getList()) {
            AnswerPoll answerPoll= answerPollRepository.findById(a.getAnswerPollId()).orElseThrow(() -> new RuntimeException("This answer poll is none-exsit"));
            if (answerPollRepository.findById(a.getAnswerPollId()).get().getAnswer().equalsIgnoreCase(a.getAnswer()))
                throw new RuntimeException("This answer Poll name '" + a.getAnswer() + "' is exsit");
            else {
                answerPoll.setAnswer(a.getAnswer());
                answerPoll.setNumAnswer(0);
                answerPoll.setPercent(0);
                answerPollRepository.save(answerPoll);
            }
        }

        return poll;

    }

    public Poll deleteAnswerToPoll( Long blogId,  Long pollId, String jwt, Long answerPollId){
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Blog blog=blogRepository.findById(blogId).orElseThrow(() -> new RuntimeException("This blog is none-exsit"));
        Poll poll=pollRepository.findById(pollId).orElseThrow(() -> new RuntimeException("This poll is none-exsit"));
        if (poll.getBlogId()!=blogId )
            throw new RuntimeException("This poll is not belong this blog");
        answerPollRepository.findById(answerPollId).orElseThrow(() -> new RuntimeException("This answer of poll is none-exsit"));
        Boolean isAdmin=user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN"));
        if (!(poll.getUserId()==user.getId() || isAdmin))
            throw new RuntimeException("This action is invalid");

        answerPollRepository.delete(answerPollRepository.findById(answerPollId).get());

        return poll;

    }
}
