package com.example.Blog_API.converter;

import com.example.Blog_API.entity.User;
import com.example.Blog_API.payload.UserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserConvert {
    public UserResponse userToUserResponse(User user){
        UserResponse userResponse= new UserResponse();
        BeanUtils.copyProperties(user,userResponse);
        userResponse.setRoles(user.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toList()));
        return userResponse;
    }
}
