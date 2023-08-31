package com.example.Blog_API.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Blog_API.authentication.JwtTokenProvider;
import com.example.Blog_API.entity.*;
import com.example.Blog_API.enums.LikeStatus;
import com.example.Blog_API.payload.BlogRequest;
import com.example.Blog_API.payload.UserBlogRequest;
import com.example.Blog_API.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    Cloudinary cloudinary;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    UserBlogRepository userBlogRepository;

    @Autowired
    AnswerPollRepository answerPollRepository;

    @Autowired
    PollRepository pollRepository;

    public Blog createBlog(BlogRequest blogRequest, MultipartFile file, String jwt) throws IOException {
        if (blogRepository.existsByTitle(blogRequest.getTitle()))
            throw new RuntimeException("This title is exsit");
        if (!(isImageFile(file) && maxSize(file,5))){
            // throw Exception trong này
        }
        Blog newBlog=new Blog();


        String image=cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("secure_url").toString();
        newBlog.setImageUrl(image);
        newBlog.setCategoryId(blogRequest.getCategoryId());
        newBlog.setTitle(blogRequest.getTitle());
        newBlog.setContent(blogRequest.getContent());
        newBlog.setUserId(userRepository.findByUsername(jwtTokenProvider.getUsernameFromJwt(jwt.substring(7))).getId());

        Blog blog=blogRepository.save(newBlog);

        // lưu poll

        if (blogRequest.getPollRequest()!=null) {
            Poll poll = new Poll();
            poll.setTitle(blogRequest.getPollRequest().getTitle());
            if (blogRequest.getPollRequest().getAnswerPolls() == null || blogRequest.getPollRequest().getAnswerPolls().size() == 0)
                throw new RuntimeException("This answer of poll is required");
            List<AnswerPoll> answerPolls = new ArrayList<>();

            poll.setBlogId(blog.getId());
            poll.setUserId(blog.getUserId());
            poll.setIsAddAnswer(blogRequest.getPollRequest().getAddAnswer());
            pollRepository.save(poll);
            for (String a : blogRequest.getPollRequest().getAnswerPolls()) {
                AnswerPoll answerPoll = new AnswerPoll();
                answerPoll.setAnswer(a);
                answerPoll.setPollId(poll.getId());
                answerPoll.setUserId(blog.getUserId());
                answerPolls.add(answerPoll);
                answerPollRepository.save(answerPoll);
            }
            poll.setAnswerPolls(answerPolls); // để hiển thị
            blog.setPoll(poll); // để hiển thị
        }

        return newBlog;
    }

    public Blog editBlog(BlogRequest blogRequest, MultipartFile file, String jwt, Long idBlog) throws IOException {

            String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
            User user=userRepository.findByUsername(username);
            Blog oldBlog=blogRepository.findById(idBlog).orElseThrow(() -> new RuntimeException("This Blog is non-exsit"));
            if (user.getId()!=oldBlog.getUserId())
                throw new RuntimeException("This action is invalid");
            if (!(isImageFile(file) && maxSize(file,5))){
                // throw Exception ở đây
            }
            if (file!=null && file.getSize()!=0){
                String oldImageUrl= oldBlog.getImageUrl();
                String image=cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("secure_url").toString();
                oldBlog.setImageUrl(image);
                if (oldImageUrl!=null)
                    cloudinary.uploader().destroy(getNameImage(oldImageUrl), ObjectUtils.emptyMap());
            }
            if (blogRequest.getTitle()!=null)
                oldBlog.setTitle(blogRequest.getTitle());
            if (blogRequest.getContent()!=null)
                oldBlog.setContent(blogRequest.getContent());
            blogRepository.save(oldBlog);
            return oldBlog;
        }


    @Transactional
    public void deleteBlogs(Long[] ids,String jwt) throws IOException {

            User user=userRepository.findByUsername(jwtTokenProvider.getUsernameFromJwt(jwt.substring(7)));
            for (Long id:ids){
                Blog blog=blogRepository.findById(id).orElseThrow(() ->new RuntimeException(String.format("This blog with ID: %s is none-exsit",id)));
                System.out.println();
//                if (blog==null)
//                    throw new RuntimeException("p");
                String oldImageUrl=blog.getImageUrl();
                // không phải người tạo và không có quyền admin
//            Boolean isAdmin=user.getRoles().stream().filter(e -> e.getCode().toString().equalsIgnoreCase("ROLE_ADMIN")).collect(Collectors.toList()).size()>0?Boolean.TRUE:Boolean.FALSE;
//                Boolean isAdmin=user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN"));
//                System.out.println("admin: " + isAdmin);
                if (user.getId()!= blog.getUserId())
                    throw new RuntimeException("This action is invalid");
                blogRepository.deleteById(id);
                if (oldImageUrl!=null)
                    cloudinary.uploader().destroy(getNameImage(oldImageUrl), ObjectUtils.emptyMap());
            }
        }

    public Blog addLikeStatus(UserBlogRequest userBlogRequest, String jwt){
        try{
            Blog blog=blogRepository.findById(userBlogRequest.getBlogId()).orElseThrow(()-> new RuntimeException("This blogId is none-exsit"));
            String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
            // set 3 giá trị cho userBlog
            UserBlog userBlog=new UserBlog();
            UserBlogKey userBlogKey=new UserBlogKey();
            userBlogKey.setBlogId(blog.getId());
            userBlogKey.setUserId(userRepository.findByUsername(username).getId());
            userBlog.setId(userBlogKey);
            userBlog.setLikeStatus(userBlogRequest.getStatus().toString());
            userBlog.setUser(userRepository.findByUsername(username));
            userBlog.setBlog(blog);
            userBlogRepository.save(userBlog);
            // update lại số lượng like cho blog
            if (userBlog.getLikeStatus().toString().equalsIgnoreCase("LIKED"))
                blog.setNumUserLiked(blog.getNumUserLiked()+1);
            else if (userBlog.getLikeStatus().toString().equalsIgnoreCase("DISLIKE"))
                blog.setNumUserDisliked(blog.getNumUserDisliked()+1);
            blogRepository.save(blog);
            return blog;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Blog updateLikeStatus(UserBlogRequest userBlogRequest, String jwt){
        String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User user=userRepository.findByUsername(username);
        Blog blog=blogRepository.findById(userBlogRequest.getBlogId()).orElseThrow(() -> new RuntimeException("This blogId is none-exsit"));
        UserBlog userBlog=userBlogRepository.findByUserIdAndBlogId(user.getId(), blog.getId());
        if (userBlog.getUser()!=user)
            throw new RuntimeException("This action is invalid");
        if (userBlog==null)
            return addLikeStatus(userBlogRequest,jwt);
        else{
            System.out.println("userBlog: " + userBlog.getLikeStatus());
            System.out.println("userBlogRequest: " + userBlogRequest.getStatus());
            System.out.println("test 1: " +(userBlog.getLikeStatus().equalsIgnoreCase(LikeStatus.LIKED.name()) && userBlogRequest.getStatus().equalsIgnoreCase(LikeStatus.DISLIKE.name())));
            if (userBlog.getLikeStatus().equalsIgnoreCase(userBlogRequest.getStatus()))
                throw  new RuntimeException("This action is duplicated");
            // nếu cái cũ là LIKED và cái mới là NONE

            if (userBlog.getLikeStatus().equalsIgnoreCase(LikeStatus.LIKED.name()) && userBlogRequest.getStatus().equalsIgnoreCase(LikeStatus.NONE.name()))
                blog.setNumUserLiked(blog.getNumUserLiked()-1);
            // nếu cái cũ là DISLIKED và cái mới là NONE
            else if (userBlog.getLikeStatus().equalsIgnoreCase(LikeStatus.DISLIKE.name()) && userBlogRequest.getStatus().equalsIgnoreCase(LikeStatus.NONE.name()))
                blog.setNumUserDisliked(blog.getNumUserDisliked()-1);
            // nếu cái cũ là DISLIKED và cái mới là LIKE
            else if (userBlog.getLikeStatus().equalsIgnoreCase(LikeStatus.DISLIKE.name()) && userBlogRequest.getStatus().equalsIgnoreCase(LikeStatus.LIKED.name())){
                blog.setNumUserDisliked(blog.getNumUserDisliked()-1);
                blog.setNumUserLiked(blog.getNumUserLiked()+1);
            }
            // nếu cái cũ là LIKED và cái mới là DISLIKED
            else if (userBlog.getLikeStatus().equalsIgnoreCase(LikeStatus.LIKED.name()) && userBlogRequest.getStatus().equalsIgnoreCase(LikeStatus.DISLIKE.name())){
                blog.setNumUserDisliked(blog.getNumUserDisliked()+1);
                blog.setNumUserLiked(blog.getNumUserLiked()-1);
            }
            blogRepository.save(blog);
            userBlog.setLikeStatus(userBlogRequest.getStatus());
            userBlogRepository.save(userBlog);
            return blog;
        }
    }



    public boolean notEmpty(MultipartFile[] file) {
        if (!(file == null || file.length == 0)) return true;
        else throw new RuntimeException("File không được bỏ trống");
    }

    public boolean isSingleFile(MultipartFile[] file) {
        if (!(file.length > 1)) return true;
        else throw new RuntimeException("File tối đa là 1");
    }

    public boolean isImageFile(MultipartFile file) {
        // getContentType: trả về kiểu dữ liệu của file dạng (image/) như: "image/jpeg", "image/png",...
        if (file.getContentType() != null && file.getContentType().startsWith("image/")) return true;
        else throw new RuntimeException("File tải lên phải là ảnh");
    }

    public boolean isEmtyFile(MultipartFile file){
        if (file==null || file.getSize()==0)
            throw new RuntimeException("File is emty");
        return true;
    }

    public boolean maxSize(MultipartFile file, double maxSize) {
        // file.getSize(): trả về đơn vị là byte
        // chia 1024 đầu là để đổi sang kilobyte
        // chia cho 1024 * 1024 để đổi sang Mb (megabyte)
        double sizeFile = (double) file.getSize() / (1024 * 1024);
        if (sizeFile < maxSize) return true;
        else throw new RuntimeException(String.format("File tải lên phải dưới %s Mb", maxSize));
    }

    public String getNameImage(String imageUrl){
        int startIndex=imageUrl.lastIndexOf("/")+1;
        int endIndex=imageUrl.lastIndexOf(".");
        return imageUrl.substring(startIndex, endIndex);
    }

}
