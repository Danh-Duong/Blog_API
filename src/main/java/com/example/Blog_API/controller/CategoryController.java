package com.example.Blog_API.controller;

import com.example.Blog_API.entity.Category;
import com.example.Blog_API.enums.ResponseCode;
import com.example.Blog_API.payload.StringResponse;
import com.example.Blog_API.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/lists")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody Category category){
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateCategory(@RequestBody Category category, @PathVariable Long id){
        category.setId(id);
        return ResponseEntity.ok(categoryService.updateCategory(category));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteCategory( @PathVariable Long id){
        categoryService.deleteCategory(id);
        StringResponse response=new StringResponse();
        response.setMessage("Delete Category success");
        response.setResponseCode(ResponseCode.SUCCESSFUL.getCode());
        response.setResponseStatus(ResponseCode.SUCCESSFUL.name());
        return ResponseEntity.ok(response);
    }
}
