package com.example.Blog_API.service;

import com.example.Blog_API.authentication.JwtTokenProvider;
import com.example.Blog_API.entity.Blog;
import com.example.Blog_API.entity.Comment;
import com.example.Blog_API.entity.SubComment;
import com.example.Blog_API.entity.User;
import com.example.Blog_API.exception.GlobalException;
import com.example.Blog_API.payload.CommentRequest;
import com.example.Blog_API.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    SubCommentRepository subCommentRepository;
    public List<Comment> getList(Long idBlog, int limit){
        Blog blog=blogRepository.findById(idBlog).orElseThrow(() -> new RuntimeException("This blog is none-exsit"));
        Pageable pageable= PageRequest.of(0,limit, Sort.by("createAt").descending());
        List<Comment> comments=commentRepository.findByBlogId(blog.getId(), pageable );
        if (comments.size()==0)
            throw new GlobalException.NotFoundException("No comments in this blog");
        return comments;
        }


    public Comment createComment(Long blogId,CommentRequest commentRequest, String jwt){
        if (commentRequest.getComment()==null)
            throw new RuntimeException("This message is required");
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Blog blog=blogRepository.findById(blogId).orElseThrow(()-> new RuntimeException("This blog is none-exsit"));

        Comment comment=new Comment();
        comment.setBlogId(blog.getId());
        comment.setUserId(user.getId());
        comment.setContent(commentRequest.getComment());

        commentRepository.save(comment);
        return comment;

    }

    public Comment updateComment(Long commentID,Long blogId,CommentRequest commentRequest, String jwt){
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Blog blog=blogRepository.findById(blogId).orElseThrow(()-> new RuntimeException("This blog is none-exsit"));
//        List<Comment> oldComment=commentRepository.findByBlogIdAndUserId(blog.getId(),user.getId());

        Comment comment=commentRepository.findById(commentID).orElseThrow(() -> new RuntimeException("This comment is none-exsit"));
        if (comment.getUserId()!=user.getId())
            throw new RuntimeException("This action is invalid");
        comment.setContent(commentRequest.getComment());
        commentRepository.save(comment);
        return comment;
    }

    public void deleteComment(Long commentId, String jwt){
        Comment comment=commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("This comment is none-exsit"));
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Boolean isAdmin=user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN"));
        System.out.println("test: "+ isAdmin);
        if (!(comment.getUserId()==user.getId() || isAdmin))
            throw new RuntimeException("This action is invalid");
        commentRepository.delete(comment);
    }

    public List<SubComment> getListSubCom(Long commentID, int limt){
        Comment comment=commentRepository.findById(commentID).orElseThrow(() -> new RuntimeException("This comment is none-exsit"));
        Pageable pageable=PageRequest.of(0,limt,Sort.by("createAt").descending());
        List<SubComment> list=subCommentRepository.findByCommentId(commentID, pageable);
        return list;
    }

    public SubComment createSubCom(CommentRequest commentRequest, Long commentId, String jwt){
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Comment com=commentRepository.findById(commentId).orElseThrow(() -> new GlobalException.NotFoundException("This comment parents is none-exsit"));
        if (commentRequest.getComment() ==null)
            throw new RuntimeException("This subComment is required");
        SubComment subComment=new SubComment();
        subComment.setUserId(user.getId());
        subComment.setCommentId(commentId);
        subComment.setContent(commentRequest.getComment());
        subCommentRepository.save(subComment);
        return subComment;
    }

    public SubComment updateSubCom(CommentRequest commentRequest, Long subComId, String jwt, Long commentId){
        commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("This comment parent is none-exsit"));
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        SubComment subCom=subCommentRepository.findById(subComId).orElseThrow(() -> new GlobalException.NotFoundException("This sub comment is none-exsit"));
        if (commentRequest.getComment() ==null)
            throw new RuntimeException("This content of subComment is required");
        if (subCom.getUserId()!=user.getId())
            throw new RuntimeException("This action is invalid");
        subCom.setContent(commentRequest.getComment());
        subCommentRepository.save(subCom);
        return subCom;
    }

    public void deleteSubCom(Long subCommentId, String jwt){
        SubComment comment=subCommentRepository.findById(subCommentId).orElseThrow(() -> new RuntimeException("This subComment is none-exsit"));
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Boolean isAdmin=user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN"));
        if (!(comment.getUserId()==user.getId() || isAdmin))
            throw new RuntimeException("This action is invalid");
        subCommentRepository.delete(comment);
    }
}

