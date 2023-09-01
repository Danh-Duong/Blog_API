package com.example.Blog_API.controller;

import com.example.Blog_API.payload.AddAnswerPollRequest;
import com.example.Blog_API.payload.EditAnswerPollRequest;
import com.example.Blog_API.service.AnswerPollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/answerPoll")
public class AnswerPollController {

    @Autowired
    AnswerPollService answerPollService;

    // liên quan đến câu trả lời
//    @PostMapping("answer/{blogId}/{pollId}/{answerPollId}")
//    public ResponseEntity<?> answerPoll(@PathVariable Long blogId, @PathVariable Long pollId, @PathVariable Long answerPollId, @RequestHeader(name = "Authorization") String jwt){
//        return ResponseEntity.ok(answerPollService.answerPoll(blogId, pollId, answerPollId, jwt));
//    }


    @PutMapping("updateAnswer/{blogId}/{pollId}/{answerPollId}")
    public ResponseEntity<?> updateAnswerPoll(@PathVariable Long blogId, @PathVariable Long pollId, @PathVariable Long answerPollId, @RequestHeader(name = "Authorization") String jwt){
        return ResponseEntity.ok(answerPollService.updateAnswerPoll(blogId, pollId, answerPollId, jwt));
    }



    // liên quan đến các answer trong poll
    @PostMapping("/add/{blogId}/{pollId}")
    public ResponseEntity<?> addAnswerToPoll(@PathVariable Long blogId, @PathVariable Long pollId, @RequestHeader(name = "Authorization") String jwt, @RequestBody AddAnswerPollRequest addAnswerPollRequest){
        return ResponseEntity.ok(answerPollService.addAnswerToPoll(blogId, pollId, jwt,addAnswerPollRequest));
    }

    @PutMapping("/edit/{blogId}/{pollId}")
    public ResponseEntity<?> editAnswerToPoll(@PathVariable Long blogId, @PathVariable Long pollId, @RequestHeader(name = "Authorization") String jwt, @RequestBody EditAnswerPollRequest editAnswerPollRequest){
        return ResponseEntity.ok(answerPollService.editAnswerToPoll(blogId, pollId, jwt,editAnswerPollRequest));
    }

//
    @DeleteMapping("/delete/{blogId}/{pollId}/{answerPollId}")
    public ResponseEntity<?> deleteAnswerToPoll(@PathVariable Long blogId, @PathVariable Long pollId, @RequestHeader(name = "Authorization") String jwt,@PathVariable Long answerPollId ){
        return ResponseEntity.ok(answerPollService.deleteAnswerToPoll(blogId, pollId, jwt, answerPollId));
    }


}
