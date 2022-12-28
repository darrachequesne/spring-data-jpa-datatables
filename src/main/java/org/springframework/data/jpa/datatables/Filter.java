package org.springframework.data.jpa.datatables;

import com.querydsl.core.types.dsl.PathBuilder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

interface Filter {

    Predicate createPredicate(From<?, ?> from, CriteriaBuilder criteriaBuilder, String attributeName);

    com.querydsl.core.types.Predicate createPredicate(PathBuilder<?> pathBuilder, String attributeName);
}