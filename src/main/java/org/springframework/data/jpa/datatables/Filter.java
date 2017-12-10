package org.springframework.data.jpa.datatables;

import com.querydsl.core.types.dsl.PathBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

interface Filter {

    Predicate createPredicate(From<?, ?> from, CriteriaBuilder criteriaBuilder, String attributeName);

    com.querydsl.core.types.Predicate createPredicate(PathBuilder<?> pathBuilder, String attributeName);
}