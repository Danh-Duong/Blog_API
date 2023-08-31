package com.example.Blog_API.controller;

import com.example.Blog_API.enums.ResponseCode;
import com.example.Blog_API.payload.PollRequest;
import com.example.Blog_API.payload.StringResponse;
import com.example.Blog_API.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poll")
public class PollController {

    @Autowired
    PollService pollService;


    // xóa poll bởi quản trị viên hoặc người tạo blog
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @DeleteMapping("delete/{blogId}/{pollId}")
    public ResponseEntity<?> deletePoll(@PathVariable Long blogId,@PathVariable Long pollId, @RequestHeader(name = "Authorization") String jwt){
        pollService.deletePoll(blogId, pollId, jwt);
        StringResponse response=new StringResponse("Delete poll successfully");
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        return ResponseEntity.ok(response);
    }

    // chỉnh sửa poll bởi quản trị viên hoặc người tạo blog
    @PutMapping("/update/{blogId}/{pollId}")
    public ResponseEntity<?> updatePoll(@PathVariable Long blogId,@PathVariable Long pollId, @RequestHeader(name = "Authorization") String jwt, @RequestBody PollRequest pollRequest){
        return ResponseEntity.ok(pollService.updatePoll(blogId,pollId,jwt,pollRequest));
    }



}
