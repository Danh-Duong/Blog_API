package com.example.Blog_API.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="category")
@Entity
public class Category extends BaseEntity{
    private String name;

    private String code;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Blog> blogs=new ArrayList<>();
}
