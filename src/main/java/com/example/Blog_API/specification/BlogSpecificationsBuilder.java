package com.example.Blog_API.specification;

import com.example.Blog_API.entity.Blog;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlogSpecificationsBuilder {
    private final List<SearchCriteria> params;

    public BlogSpecificationsBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public BlogSpecificationsBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BlogSpecificationsBuilder with(String key, String operation, Object[] value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<Blog> build() {
        if (params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(BlogSpecification::new)
                .collect(Collectors.toList());
        Specification result = specs.get(0);
        for (int i = 1; i < params.size(); i++)
            result = Specification.where(result).and(specs.get(i));
        return result;
    }

}
