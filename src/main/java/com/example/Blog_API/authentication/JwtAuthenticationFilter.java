package com.example.Blog_API.authentication;

import com.example.Blog_API.service.UserService;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt=getJwtFromHeader(request);
        if (Strings.hasText(jwt) && jwtTokenProvider.validateToken(jwt)){
            String username=jwtTokenProvider.getUsernameFromJwt(jwt);
            UserDetails userDetails=userService.loadUserByUsername(username);
            if (userDetails!=null){
                UsernamePasswordAuthenticationToken authentication =new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request,response);
    }

    private String getJwtFromHeader(HttpServletRequest request){
        String token =request.getHeader("Authorization");
        if (Strings.hasText(token) && token.startsWith("Bearer "))
            return token.substring(7);
        return null;
    }

}
