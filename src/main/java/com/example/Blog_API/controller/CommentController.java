package com.example.Blog_API.controller;

import com.example.Blog_API.enums.ResponseCode;
import com.example.Blog_API.payload.CommentRequest;
import com.example.Blog_API.payload.StringResponse;
import com.example.Blog_API.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("getComments/{idBlog}")
    public ResponseEntity<?> getListComment(@PathVariable Long idBlog, @RequestParam(required = false, defaultValue = "10") int limit){
        return ResponseEntity.ok(commentService.getList(idBlog,limit));
    }

    @PostMapping("/createComment/{blogID}")
    public ResponseEntity<?> createComment(@PathVariable Long blogID,@RequestBody CommentRequest commentRequest,@RequestHeader(value = "Authorization") String jwt){
        return ResponseEntity.ok(commentService.createComment(blogID,commentRequest,jwt));
    }

    @PutMapping("/updateComment/{blogID}/{commentID}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentID, @PathVariable Long blogID,@RequestBody CommentRequest commentRequest,@RequestHeader(value = "Authorization") String jwt){
        return ResponseEntity.ok(commentService.updateComment(commentID,blogID,commentRequest,jwt));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @DeleteMapping("deleteComment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @RequestHeader(value = "Authorization") String jwt){
        commentService.deleteComment(commentId,jwt);
        StringResponse response=new StringResponse();
        response.setMessage("Delete Comment Successfully");
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        return ResponseEntity.ok(response);
    }

    // SUBCOMMNENTS

    @GetMapping("/getListSubCom/{commentId}")
    public ResponseEntity<?> getListSubCom(@PathVariable Long commentId,@RequestParam(name = "limit", required = false, defaultValue = "5") int limit){
        return ResponseEntity.ok(commentService.getListSubCom(commentId,limit));
    }

    @PostMapping("/{commentId}/create")
    public ResponseEntity<?> createSubCom(@PathVariable Long commentId, @RequestHeader(name = "Authorization") String jwt, @RequestBody CommentRequest commentRequest)
    {
        return ResponseEntity.ok(commentService.createSubCom(commentRequest,commentId,jwt));
    }

    @PutMapping("{commentId}/update/{subComId}")
    public ResponseEntity<?> updateSubCom(@PathVariable Long commentId ,@RequestBody CommentRequest com,@PathVariable Long subComId, @RequestHeader(name = "Authorization") String jwt){
        return ResponseEntity.ok(commentService.updateSubCom(com,subComId,jwt,commentId));
    }

    @DeleteMapping("{commentId}/delete/{subComId}")
    public ResponseEntity<?> deleteSubCom(@PathVariable Long commentId, @PathVariable Long subComId, @RequestHeader(name = "Authorization") String jwt){
        commentService.deleteSubCom(commentId, jwt);
        StringResponse response=new StringResponse();
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        response.setMessage("Delete subComment successfully");
        return ResponseEntity.ok(response);
    }
}
