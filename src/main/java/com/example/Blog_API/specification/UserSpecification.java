package com.example.Blog_API.specification;

import com.example.Blog_API.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class UserSpecification implements Specification<User> {
    private SearchCriteria criteria;

    public UserSpecification(SearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    public SearchCriteria getCriteria() {
        return criteria;
    }


    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (criteria.getOperation().equalsIgnoreCase(">"))
            return criteriaBuilder.greaterThan(root.get(criteria.getKey()),criteria.getValue().toString());
        else if (criteria.getOperation().equalsIgnoreCase("<"))
            return criteriaBuilder.lessThan(root.get(criteria.getKey()),criteria.getValue().toString());
        else if (criteria.getOperation().equalsIgnoreCase(":"))
            if (root.get(criteria.getKey()).getJavaType()==String.class)
                return criteriaBuilder.like(root.<String>get(criteria.getKey()),"%" +criteria.getValue()+"%");
            else
                return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
        else if (criteria.getOperation().equalsIgnoreCase("<>")){
            if (criteria.getValues()[0] instanceof Date){
                Date dateFrom= (Date) criteria.getValues()[0];
                Date dateTo= (Date) criteria.getValues()[1];
                return criteriaBuilder.between(root.get(criteria.getKey()),dateFrom, dateTo);
            }
        }
        return null;
    }
}
