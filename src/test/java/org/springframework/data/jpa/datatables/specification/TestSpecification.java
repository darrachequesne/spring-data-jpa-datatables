package org.springframework.data.jpa.datatables.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class TestSpecification<T> implements Specification<T> {

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder) {

		return criteriaBuilder.equal(root.get("visible").as(boolean.class),
				true);
	}
}
