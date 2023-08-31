package com.example.Blog_API.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Blog_API.authentication.JwtTokenProvider;
import com.example.Blog_API.converter.UserConvert;
import com.example.Blog_API.entity.User;
import com.example.Blog_API.exception.GlobalException;
import com.example.Blog_API.payload.UpdateUserRequest;
import com.example.Blog_API.payload.UserResponse;
import com.example.Blog_API.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class UserServiceMain {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Cloudinary cloudinary;

    @Autowired
    UserConvert userConvert;

    public UserResponse getUser(Long id){
        User user=userRepository.findById(id).orElseThrow(() -> new GlobalException.NotFoundException("This user is none-exsit"));
        return userConvert.userToUserResponse(user);
    }

    public UserResponse updateUserInfo(Long id, UpdateUserRequest updateUserRequest,String jwt) throws ParseException {
        String username= jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User userA=userRepository.findByUsername(username);
        if (userA.getId()!=id)
            throw new RuntimeException("This action is invalid");
        User user=userRepository.findById(id).orElseThrow(()-> new GlobalException.NotFoundException("This user is none-exsit"));
        if (updateUserRequest.getAddress()!=null)
            user.setAddress(updateUserRequest.getAddress());
        if (updateUserRequest.getPhone()!=null)
            user.setPhone(updateUserRequest.getPhone());
        if (updateUserRequest.getDateOfBitrh()!=null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            user.setDateOfBitrh(simpleDateFormat.parse(updateUserRequest.getDateOfBitrh()));
        }
        userRepository.save(user);
        return userConvert.userToUserResponse(user);
    }

    public void updateImgUser(Long userId, String jwt, MultipartFile file) throws IOException {
        String username= jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
        User userA=userRepository.findByUsername(username);
        if (userA.getId()!=userId)
            throw new RuntimeException("This action is invalid");
        User user=userRepository.findById(userId).orElseThrow(()-> new GlobalException.NotFoundException("This user is none-exsit"));
        String oldImgUrl=user.getImage();
        String newImgUrl;
        if (oldImgUrl==null){
            newImgUrl =cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("secure_url").toString();
            user.setImage(newImgUrl);
        }
        else
            cloudinary.uploader().destroy(getNameImage(oldImgUrl), ObjectUtils.emptyMap());

        userRepository.save(user);
    }

    public String getNameImage(String imageUrl){
        int startIndex=imageUrl.lastIndexOf("/")+1;
        int endIndex=imageUrl.lastIndexOf(".");
        return imageUrl.substring(startIndex, endIndex);
    }
}
