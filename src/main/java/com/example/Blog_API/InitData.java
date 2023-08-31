package com.example.Blog_API;

import com.example.Blog_API.entity.Role;
import com.example.Blog_API.entity.User;
import com.example.Blog_API.repository.RoleRepository;
import com.example.Blog_API.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class InitData {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final static Role roleAdmin=new Role();

    private final static Role roleUser=new Role();


    @PostConstruct
    public void initData(){
        if (roleRepository.findByCode("ROLE_ADMIN")==null){
            roleAdmin.setCode("ROLE_ADMIN");
            roleAdmin.setName("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
        }

        if (roleRepository.findByCode("ROLE_USER")==null){
            roleUser.setCode("ROLE_USER");
            roleUser.setName("ROLE_USER");
            roleRepository.save(roleUser);
        }

        if (userRepository.findByUsername("duongdanh767@gmail.com")==null){
            User user=new User();
            user.setUsername("duongdanh767@gmail.com");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setPhone("091535271");
            user.setRoles(Arrays.asList(roleAdmin,roleUser));
            userRepository.save(user);
        }
    }
}
