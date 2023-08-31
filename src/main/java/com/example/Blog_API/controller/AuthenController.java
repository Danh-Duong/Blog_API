package com.example.Blog_API.controller;

import com.example.Blog_API.entity.EmailToken;
import com.example.Blog_API.entity.User;
import com.example.Blog_API.enums.ResponseCode;
import com.example.Blog_API.payload.*;
import com.example.Blog_API.service.AuthenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenController {

    @Autowired
    AuthenService authenService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest){
        LoginResponse response=authenService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody User userRequest){
        authenService.signUp(userRequest);
        StringResponse response=new StringResponse();
        response.setMessage("Sign-up success");
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        return ResponseEntity.ok(response);
    }

    // chú ý đường dẫn: đây là đường dẫn cũ
    @GetMapping("/LoginGoogle/LoginGoogleHandler")
    public ResponseEntity<?> loginWithGoogle(@RequestParam String code){
        LoginResponse loginResponse=authenService.loginWithGoogle(code);
        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/forgetPass")
    private ResponseEntity<?> forgetPass(@RequestBody @Valid ForgetPassRequest forgetPassRequest){
        EmailToken emailToken=authenService.forgetPass(forgetPassRequest.getEmail());
        return ResponseEntity.ok(emailToken);
    }

    @PostMapping("/resetPass")
    private ResponseEntity<?> resetPass(@RequestBody ResetPassRequest resetPassRequest){
        authenService.resetPass(resetPassRequest);
        StringResponse response=new StringResponse();
        response.setMessage("Change password success");
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        return ResponseEntity.ok(response);
    }
}
