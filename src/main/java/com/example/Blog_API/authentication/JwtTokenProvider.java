package com.example.Blog_API.authentication;

import com.example.Blog_API.entity.User;
import com.example.Blog_API.repository.RoleRepository;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final String JWT_SECRET="danh123!@#";
    private final Long JWT_EXPIRATION=604800000L;

    public String generateToken(CustomUserDetails userDetails){
        Date now=new Date();
        Date expired=new Date(now.getTime()+JWT_EXPIRATION);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expired)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String getUsernameFromJwt(String jwt){
        Claims claims=Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(jwt)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken){
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    @Autowired
    RoleRepository roleRepository;

    public boolean isAdmin(User user){
        return (user.getRoles().contains(roleRepository.findByCode("ROLE_ADMIN")));
    }
}
