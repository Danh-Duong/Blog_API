package com.example.Blog_API.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;

    private Object[] values;

    public SearchCriteria(String key, String operation, Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SearchCriteria(String key, String operation, Object[] values) {
        super();
        this.key = key;
        this.operation = operation;
        this.values = values;
    }
}
