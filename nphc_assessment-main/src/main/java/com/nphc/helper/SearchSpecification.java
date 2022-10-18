package com.nphc.helper;


import org.springframework.data.jpa.domain.Specification;

import com.nphc.model.Employee;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


public class SearchSpecification implements Specification<Employee> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchCriteria criteria;
	
	


    public SearchCriteria getCriteria() {
		return criteria;
	}




	public void setCriteria(SearchCriteria criteria) {
		this.criteria = criteria;
	}




	public SearchSpecification(SearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}




	@Override
    public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        switch (criteria.getOperation()) {
            case EQUALITY:
                return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESSER_THAN_EQUAL:
                return criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case GREATHER_THAN_EQUAL:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE:
            	 return criteriaBuilder.like(root.get(criteria.getKey()), "%"+criteria.getValue().toString()+"%");
            default:
                return null;
        }
    }
}
