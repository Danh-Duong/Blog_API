package com.example.Blog_API.service;

import com.example.Blog_API.entity.Category;
import com.example.Blog_API.exception.GlobalException;
import com.example.Blog_API.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    public Category createCategory(Category categoryRequest){
//        if (categoryRequest.getId()==null) {
            Category categoryFind = categoryRepository.findByName(categoryRequest.getName());
            if (categoryFind != null)
                throw new RuntimeException("This name of Category is exsit");
//        }
//        else{
//            Optional<Category> oldCategory=categoryRepository.findById(categoryRequest.getId());
//            if (!oldCategory.isPresent())
//                throw new RuntimeException("This id doesn't belong any Category");
//
//        }
        categoryRequest.setCode(NameToCode(categoryRequest.getName()));
        categoryRepository.save(categoryRequest);
        return categoryRequest;
    }

    public Category updateCategory(Category categoryRequest){
        Optional<Category> oldCategory=categoryRepository.findById(categoryRequest.getId());
        if (!oldCategory.isPresent())
            throw new RuntimeException("This id doesn't belong any Category");
        oldCategory.get().setCode(NameToCode(categoryRequest.getName()));
        oldCategory.get().setName(categoryRequest.getName());
        categoryRepository.save(oldCategory.get());
        return oldCategory.get();
    }

    public void deleteCategory(Long id){
        Optional<Category> oldCategory=categoryRepository.findById(id);
        if (!oldCategory.isPresent())
            throw new GlobalException.NotFoundException("This id doesn't belong any Category");
        categoryRepository.delete(oldCategory.get());
    }


    public String NameToCode(String value) {
        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
