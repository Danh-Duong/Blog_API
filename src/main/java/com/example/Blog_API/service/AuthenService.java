package com.example.Blog_API.service;

import com.example.Blog_API.authentication.CustomUserDetails;
import com.example.Blog_API.authentication.JwtTokenProvider;
import com.example.Blog_API.entity.EmailToken;
import com.example.Blog_API.entity.Role;
import com.example.Blog_API.entity.User;
import com.example.Blog_API.payload.*;
import com.example.Blog_API.repository.EmailTokenRepository;
import com.example.Blog_API.repository.RoleRepository;
import com.example.Blog_API.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AuthenService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EmailTokenRepository emailTokenRepository;

    @Autowired
    MyEmailService emailService;

    public LoginResponse loginUser(LoginRequest loginRequest) throws BadCredentialsException {
        Authentication auth;
        try{
        auth=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );}
        catch (BadCredentialsException e){
            throw new RuntimeException("This account is invalid");
        }

        String jwt=jwtTokenProvider.generateToken((CustomUserDetails) auth.getPrincipal());
        String username=((CustomUserDetails) auth.getPrincipal()).getUsername();
        List<String> roles= auth.getAuthorities().stream().map(role -> role.toString()).collect(Collectors.toList());

        LoginResponse response=new LoginResponse(jwt,username,roles);
        return response;
    }

    public void signUp(User userRequest){
        try {
            User user=userRepository.findByUsername(userRequest.getUsername());
            if (user!=null)
                throw new Exception("Username is already exsit");
            if (userRequest.getPassword()==null)
                throw new Exception("Password is required!");
            userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            Role roleUser=roleRepository.findByCode("ROLE_USER");
            userRequest.setRoles(Arrays.asList(roleUser));
            userRepository.save(userRequest);
//            return ResponseEntity.ok("Sign up success");
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public LoginResponse loginWithGoogle(String code){
        // lưu ý: code ở đâu là Refresh Token
        MultiValueMap<String, String> body=new LinkedMultiValueMap<>();
        body.add("code",code);
        body.add("client_id", Constant.GOOGLE_CLIENT_ID);
        body.add("client_secret", Constant.GOOGLE_CLIENT_SECRET);
        body.add("redirect_uri", Constant.GOOGLE_REDIRECT_URI);
        body.add("grant_type", "authorization_code");

        // lấy access token từ refresh token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<ResponseTokenGoogleOAuth> response = restTemplate().postForEntity(
                Constant.GOOGLE_LINK_GET_TOKEN,
                request,
                ResponseTokenGoogleOAuth.class
        );

        ResponseTokenGoogleOAuth responseTokenGitHubOAuth=response.getBody();
        if (responseTokenGitHubOAuth!=null) {
            String accessToken = responseTokenGitHubOAuth.getAccess_token();
            String idToken = responseTokenGitHubOAuth.getId_token();

            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(idToken);
            HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);
            ResponseEntity<UserGoogleResponse> userInfoResponse = restTemplate().exchange(
                    Constant.GOOGLE_LINK_GET_USER_INFO + accessToken,
                    HttpMethod.GET,
                    userInfoRequest,
                    UserGoogleResponse.class
            );

            UserGoogleResponse userGoogleResponse = userInfoResponse.getBody();

            User user = userRepository.findByUsername(userGoogleResponse.getName());
            if (user == null) {
                user = new User();
                user.setUsername(userGoogleResponse.getName());
                Role roleUser = roleRepository.findByCode("ROLE_USER");
                user.setRoles(Arrays.asList(roleUser));
                user.setPassword(passwordEncoder.encode("user123"));
                userRepository.save(user);
            }

            String jwt = jwtTokenProvider.generateToken(new CustomUserDetails(user));
            String username = user.getUsername();
            List<String> roles = user.getRoles().stream().map(role -> role.toString()).collect(Collectors.toList());

            LoginResponse loginResponse = new LoginResponse(jwt, username, roles);
            return loginResponse;
        }
        return null;
    }

    public void changePass(ChangePassRequest changePassRequest, String jwt){
        try{
            String username=jwtTokenProvider.getUsernameFromJwt(jwt.substring(7));
            User user=userRepository.findByUsername(username);
            if (user==null)
                throw new Exception("Got unexpected error");

            if (!passwordEncoder.matches(changePassRequest.getOldPass(),user.getPassword()))
                throw new Exception("Old password is incorrect");
            user.setPassword(passwordEncoder.encode(changePassRequest.getNewPass()));
            userRepository.save(user);
        }

        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public EmailToken forgetPass(String email){
        Long EMAIL_TOKEN_EXPIRED= Long.valueOf(1000*60*3);
        try{
            User user=userRepository.findByUsername(email);
            if (user==null)
                throw new Exception("This email is non-exsit");
            EmailToken emailToken=new EmailToken();
            emailToken.setToken(generateRandomString(5));
            Date now=new Date();
            emailToken.setExpiredTime(new Date(now.getTime() + EMAIL_TOKEN_EXPIRED));
            emailToken.setUserId(user.getId());
            emailTokenRepository.save(emailToken);

            // gửi email token
            emailService.sendEmail(email,"Xác nhận","Mã code để ResetPass là: " + emailToken.getToken());
            return emailToken;
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void resetPass(ResetPassRequest resetPassRequest){
            EmailToken emailToken=emailTokenRepository.findByToken(resetPassRequest.getToken());
            if (emailToken==null)
                throw new RuntimeException("This token email is non-exsit");
            if (!validDateToken(resetPassRequest.getToken()))
                throw new RuntimeException("This token is expired");
            User user=emailToken.getUser();
            user.setPassword(passwordEncoder.encode(resetPassRequest.getNewPass()));
            userRepository.save(user);
            emailTokenRepository.delete(emailToken);
    }

    public boolean validDateToken(String token){
        EmailToken emailToken=emailTokenRepository.findByToken(token);
        if (emailToken.getExpiredTime().after(new Date()))
            return true;
        return false;
    }

    public String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    @Bean
    private RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
