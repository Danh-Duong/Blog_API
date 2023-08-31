package com.example.Blog_API.controller;

import com.example.Blog_API.entity.Blog;
import com.example.Blog_API.entity.Category;
import com.example.Blog_API.enums.ResponseCode;
import com.example.Blog_API.payload.BlogRequest;
import com.example.Blog_API.payload.StringResponse;
import com.example.Blog_API.payload.UserBlogRequest;
import com.example.Blog_API.repository.BlogRepository;
import com.example.Blog_API.repository.CategoryRepository;
import com.example.Blog_API.service.BlogService;
import com.example.Blog_API.specification.BlogSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    BlogService blogService;

    @GetMapping("/getBlogs")
    public ResponseEntity<?> getBlogs(@RequestParam(required = false) String title,
                                      @RequestParam(required = false) String content,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
                                      @RequestParam(required = false) Integer month,
                                      @RequestParam(required = false) Integer year,
                                      @RequestParam(required = false) String categoryCode,
                                      @RequestParam(required = false, defaultValue = "10") Integer limit) throws ParseException {

//        try{
            Integer actualLimit= (limit != null && limit > 0) ? limit : 10;

            if (dateFrom!=null && dateTo==null)
                throw new RuntimeException("The dateTo is blank");
            if (dateFrom!=null && (dateFrom==dateTo)){
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFrom);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                dateTo = calendar.getTime();
            }
            if (month!=null && year==null)
                year=Calendar.getInstance().get(Calendar.YEAR);
            BlogSpecificationsBuilder builder=new BlogSpecificationsBuilder();
//            System.out.println("title: " + title);
//            if (title==null)
//                return ResponseEntity.ok(blogRepository.findAll(PageRequest.of(0, limit.intValue(), Sort.by("createAt"))).getContent());
//            else {
                if (title!=null)
                    builder.with("title",":", title);
                if (content!=null)
                    builder.with("content",":", content);
                if (categoryCode!=null){
                    Category category= categoryRepository.findByCode(categoryCode);
                    if (category==null)
                        throw new RuntimeException("This categoryId is none-exsit");
                    else
                        builder.with("categoryId",":", category.getId());
                }
                if (month!=null && year!=null){
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
                    String monthR=String.format("%02d",month);
                    builder.with("createAt","<>", new Object[]{simpleDateFormat.parse(year+monthR+"01000000"),simpleDateFormat.parse(year+monthR+"31235959")});
                }
                else if (dateFrom!=null && dateTo!=null){
                    builder.with("createAt","<>",new Object[]{dateFrom, dateTo});
                }
//            }

            Specification<Blog> query=builder.build();
            return ResponseEntity.ok(blogRepository.findAll(query, PageRequest.of(0, limit.intValue(), Sort.by("createAt"))).getContent());
//        }
//        catch (Exception e){
//
//        }
//        return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBlog(@RequestPart(name = "blog", required = false) BlogRequest blog, @RequestPart(name = "file", required = false) MultipartFile file, @RequestHeader(value = "Authorization") String jwt) throws IOException {
        return ResponseEntity.ok(blogService.createBlog(blog, file,jwt));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editBlog(@PathVariable Long id,@RequestPart(name = "blog", required = false) BlogRequest blog, @RequestPart(name = "file", required = false) MultipartFile file,@RequestHeader(value = "Authorization") String jwt) throws IOException {
        return ResponseEntity.ok(blogService.editBlog(blog,file,jwt,id));
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/deleteId/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id, @RequestHeader(value = "Authorization") String jwt) throws IOException {
        blogService.deleteBlogs(new Long[]{id},jwt);
        StringResponse response=new StringResponse();
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        response.setMessage("Delete blog successfully");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBlogs(@RequestBody Long[] ids,@RequestHeader(value = "Authorization") String jwt) throws IOException {
        blogService.deleteBlogs(ids,jwt);
        StringResponse response=new StringResponse();
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        response.setMessage("Delete blogs successfully");
        return ResponseEntity.ok(response);
    }

     // express emotion
    @PostMapping("/addBlogLikeStatus")
    public ResponseEntity<?> addLikeStatus(@RequestBody UserBlogRequest userBlog, @RequestHeader(value = "Authorization") String jwt){
        return ResponseEntity.ok(blogService.addLikeStatus(userBlog, jwt));
    }

    @PutMapping("/updateBlogLikeStatus")
    public ResponseEntity<?> updateLikeStatus(@RequestBody UserBlogRequest userBlog, @RequestHeader(value = "Authorization") String jwt){
        return ResponseEntity.ok(blogService.updateLikeStatus(userBlog, jwt));
    }
}
