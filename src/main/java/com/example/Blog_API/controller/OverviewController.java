package com.example.Blog_API.controller;

import com.example.Blog_API.authentication.JwtTokenProvider;
import com.example.Blog_API.entity.*;
import com.example.Blog_API.payload.NumStatusRepsonse;
import com.example.Blog_API.payload.OverviewResponse;
import com.example.Blog_API.repository.BlogRepository;
import com.example.Blog_API.repository.CommentRepository;
import com.example.Blog_API.repository.UserBlogRepository;
import com.example.Blog_API.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {
    @Autowired
    BlogRepository blogRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    UserBlogRepository userBlogRepository;

    @GetMapping("/getDataMonths")
    // phân tích dữ liệu trong sáu tháng trước đó
    public ResponseEntity<?> getDataMonths(@RequestHeader(name="Authorization") String jwt){
        try{
            String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
            User user=userRepository.findByUsername(username);
            List<OverviewResponse> result=new ArrayList<>();
            int i;
            int month=Calendar.getInstance().get(Calendar.MONTH)+1;
            int year=Calendar.getInstance().get(Calendar.YEAR);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
            for  (i=1;i<=6;i++){
                String monthR=String.format("%02d", month);
                OverviewResponse overviewResponse=new OverviewResponse();
                overviewResponse.setMonth(month);
                overviewResponse.setYear(year);
                Date start=simpleDateFormat.parse(year+monthR+"01000000");
                Date end=simpleDateFormat.parse(year+monthR+"31235959");

                overviewResponse.setNumBlog(blogRepository.getBlogByCreateAtAndUserId(user.getId(),start ,end ).size());

                List<Blog> blogs= blogRepository.getBlogByCreateAtAndUserId(user.getId(), start,end);
                List<Long> blogIds=blogs.stream().map(b -> b.getId()).collect(Collectors.toList());


                int comment=0;
                int subcomment=0;
                for (Blog b:blogs)
                    if (b.getComments()!=null){
                        comment+=b.getComments().size();
                        for (Comment c: b.getComments())
                            if (c.getSubComments()!=null)
                                subcomment+=c.getSubComments().size();
                    }

                NumStatusRepsonse numStatusRepsonse=new NumStatusRepsonse();
                Long numLike=blogRepository.countLikeStatusByBlogId(blogIds) == null ? 0: blogRepository.countLikeStatusByBlogId(blogIds);
                Long numDislike=blogRepository.countDislikeStatusByBlogId(blogIds) == null ? 0:blogRepository.countDislikeStatusByBlogId(blogIds);
                numStatusRepsonse.setNumDislikeStatus(numDislike);
                numStatusRepsonse.setNumLikeStatus(numLike);
                numStatusRepsonse.setNumTotalStatus(numDislike+numLike);

                overviewResponse.setNumStatus(numStatusRepsonse);
                overviewResponse.setNumComment(comment+subcomment);

                month--;
                if (month==0){
                    month=12;
                    year--;
                }

                result.add(overviewResponse);
            }
            return ResponseEntity.ok(result);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


}
