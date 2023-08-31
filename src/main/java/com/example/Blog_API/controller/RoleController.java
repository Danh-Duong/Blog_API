package com.example.Blog_API.controller;

import com.example.Blog_API.entity.Role;
import com.example.Blog_API.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createRole(@RequestBody Role role){
        Role result=roleService.createRole(role);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id,@RequestBody String[] roles){
        return ResponseEntity.ok(roleService.updateUserRole(id,roles));
    }
}
