package com.example.Blog_API.controller;

import com.example.Blog_API.entity.User;
import com.example.Blog_API.enums.ResponseCode;
import com.example.Blog_API.payload.ChangePassRequest;
import com.example.Blog_API.payload.StringResponse;
import com.example.Blog_API.payload.UpdateUserRequest;
import com.example.Blog_API.payload.UserResponse;
import com.example.Blog_API.repository.UserRepository;
import com.example.Blog_API.service.AuthenService;
import com.example.Blog_API.service.UserServiceMain;
import com.example.Blog_API.specification.UserSpecificationsBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceMain userServiceMain;

    @Autowired
    AuthenService authenService;


    @GetMapping("/getUsers")
    public ResponseEntity<?> getListUser(@RequestParam(required = false, defaultValue = "5") Integer limit,
                                         @RequestParam(required = false) String username,
                                         @RequestParam(required = false) String phone,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createFrom,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTo){
        Integer actualLimit= (limit != null && limit > 0) ? limit : 5;
        UserSpecificationsBuilder builder=new UserSpecificationsBuilder();
        if (username!=null)
            builder.with("username",":",username);
        if (phone!=null)
            builder.with("phone",":", phone);
        if (createFrom!=null && createTo==null)
            throw new RuntimeException("The dateTo is required");
        if (createFrom!=null && (createTo==createTo)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createFrom);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            createTo = calendar.getTime();
        }
        if (createFrom!=null && createTo!=null)
            builder.with("createAt","<>",new Object[]{createFrom,createTo});
        Specification<User> query=builder.build();
        Page<User> result= userRepository.findAll(query, PageRequest.of(0,limit, Sort.by("createAt").descending()));
        List<User> response=result.stream().collect(Collectors.toList());
        List<UserResponse> userResponses=new ArrayList<>();
        for (User u:response){
            UserResponse userResponse=new UserResponse();
            BeanUtils.copyProperties(u,userResponse);
            userResponse.setRoles(u.getRoles().stream().map(r-> r.getCode()).collect(Collectors.toList()));
            userResponses.add(userResponse);
        }
        return ResponseEntity.ok(userResponses);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        return ResponseEntity.ok(userServiceMain.getUser(id));

    }


    @PostMapping("/changePass")
    public ResponseEntity<StringResponse> changePassword(@RequestBody @Valid ChangePassRequest changePassRequest, @RequestHeader(name = "Authorization") String jwt){
        authenService.changePass(changePassRequest,jwt);
        StringResponse response=new StringResponse();
        response.setMessage("Change password success");
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest, @RequestHeader(name = "Authorization") String jwt) throws ParseException {
        return ResponseEntity.ok(userServiceMain.updateUserInfo(id,updateUserRequest,jwt));
    }

    @PutMapping("/updateImage/{userId}")
    public ResponseEntity<?> updateImgUser(@PathVariable Long userId, @RequestHeader(name = "Authorization") String jwt, @RequestParam MultipartFile file) throws IOException {
        userServiceMain.updateImgUser(userId,jwt,file);
        StringResponse response=new StringResponse();
        response.setMessage("Update image Successfully");
        return ResponseEntity.ok(response);
    }


}
