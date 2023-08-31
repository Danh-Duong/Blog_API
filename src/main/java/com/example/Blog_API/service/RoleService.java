package com.example.Blog_API.service;

import com.example.Blog_API.entity.Role;
import com.example.Blog_API.entity.User;
import com.example.Blog_API.exception.GlobalException;
import com.example.Blog_API.repository.RoleRepository;
import com.example.Blog_API.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    public Role createRole (Role roleRequest){
        if (roleRepository.findByCode(roleRequest.getCode())!=null)
            throw new GlobalException.DuplicateValueException("This role is already exsit");
        roleRequest.setCode(roleRequest.getName());
        roleRepository.save(roleRequest);
        return roleRequest;
    }

    public User updateUserRole(Long idU, String[] roles){
        User user=userRepository.findById(idU).orElseThrow(() -> new GlobalException.NotFoundException("This user is none-exsit"));
        List<Role> roleList=new ArrayList<>();
        for (String r: roles){
            Role role=roleRepository.findByCode(r);
            if (role==null)
                throw new GlobalException.NotFoundException(String.format("This role with name %s is none-exsit",r));
            roleList.add(role);
        }
        user.setRoles(roleList);
        userRepository.save(user);
        return user;
    }

}
