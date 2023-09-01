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

    public Poll answerPoll(Long blogId,Long pollId,Long answerPollId, String jwt){
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

        return poll;
    }

    public Poll updateAnswerPoll(Long blogId,Long pollId,Long answerPollId, String jwt){
        User user=userRepository.findByUsername(jwtTokenProvider.getUsernameFromJwt(jwt.substring(7)));
        UserAnswerPoll userAnswerPoll=userAnswerPollRepository.findByUserIdAndAnswerPollId(user.getId(),answerPollId);
        UserAnswerPoll userAnswerPollOld=null;

        boolean isExsit=false;
        for (AnswerPoll answerPoll: pollRepository.findById(pollId).get().getAnswerPolls()){
            if (userAnswerPollRepository.findByUserIdAndAnswerPollId(user.getId(),answerPoll.getId())!=null){
                isExsit=true;
                userAnswerPollOld=userAnswerPollRepository.findByUserIdAndAnswerPollId(user.getId(),answerPoll.getId());
                break;
            }
        }

        // nếu không có thì tạo mới
        if (userAnswerPoll==null && isExsit==false)
            return answerPoll(blogId,pollId,answerPollId,jwt);

        else {
            System.out.println("ok");
            Blog blog=blogRepository.findById(blogId).orElseThrow(()-> new GlobalException.NotFoundException("This blog is none-exsit"));
            Poll poll=pollRepository.findById(pollId).orElseThrow(() -> new GlobalException.NotFoundException("This poll is none-exsit"));
            if (poll.getBlogId()!=blogId)
                throw new RuntimeException("This poll is none-exsit in blog");

            // nếu có rồi thì hủy cái cũ
            if (userAnswerPollOld.getAnswerPoll().getId()==answerPollId){
                AnswerPoll answerPollOld=userAnswerPoll.getAnswerPoll();
                answerPollOld.setNumAnswer(answerPollOld.getNumAnswer()-1);
                answerPollOld.setPercent((long) (answerPollOld.getNumAnswer()*100.0/poll.getNumAnswerPoll()));
                answerPollRepository.save(answerPollOld);
                userAnswerPollRepository.delete(userAnswerPoll);
                poll.setNumAnswerPoll(poll.getNumAnswerPoll()-1);
                pollRepository.save(poll);
            }

            else{
                AnswerPoll answerPollOld,answerPollNew;
                answerPollOld=userAnswerPollOld.getAnswerPoll();
                answerPollNew=answerPollRepository.findById(answerPollId).get();
                // lưu lại thông tin
                userAnswerPollRepository.delete(userAnswerPollOld);

                UserAnswerPoll userAnswerPollSave=new UserAnswerPoll();
                UserPollKey userPollKey=new UserPollKey();

                userPollKey.setAnswerPollId(answerPollId);
                userPollKey.setUserId(user.getId());
                userAnswerPollSave.setId(userPollKey);
                userAnswerPollSave.setAnswerPoll(answerPollRepository.findById(answerPollId).orElseThrow(()-> new RuntimeException("")));
                userAnswerPollSave.setUser(user);

                userAnswerPollRepository.save(userAnswerPollSave);
                answerPollOld.setNumAnswer(answerPollOld.getNumAnswer()-1);
                answerPollOld.setPercent((long) (answerPollOld.getNumAnswer()*100.0/poll.getNumAnswerPoll()));

                answerPollNew.setNumAnswer(answerPollNew.getNumAnswer()+1);
                answerPollNew.setPercent((long) (answerPollNew.getNumAnswer()*100.0/poll.getNumAnswerPoll()));

                answerPollRepository.save(answerPollNew);
                answerPollRepository.save(answerPollOld);
            }
            return poll;
        }
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
