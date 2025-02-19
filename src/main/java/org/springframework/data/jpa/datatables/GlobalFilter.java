package org.springframework.data.jpa.datatables;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringOperation;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.query.criteria.JpaExpression;

/**
 * Filter which creates a basic "WHERE ... LIKE ..." clause
 */
class GlobalFilter implements Filter {
    private final String escapedRawValue;

    GlobalFilter(String filterValue) {
        escapedRawValue = escapeValue(filterValue);
    }

    String nullOrTrimmedValue(String value) {
        return "\\NULL".equals(value) ? "NULL" : value.trim();
    }

    private String escapeValue(String filterValue) {
        return "%" + nullOrTrimmedValue(filterValue).toLowerCase()
                .replaceAll("~", "~~")
                .replaceAll("%", "~%")
                .replaceAll("_", "~_") + "%";
    }

    @Override
    public Predicate createPredicate(From<?, ?> from, CriteriaBuilder criteriaBuilder, String attributeName) {
        Expression<?> expression = from.get(attributeName);
        return criteriaBuilder.like(criteriaBuilder.lower(castAsStringIfNeeded(expression)), escapedRawValue, '~');
    }

    @SuppressWarnings("unchecked")
    private Expression<String> castAsStringIfNeeded(Expression<?> expression) {
        if (expression.getJavaType() == String.class) {
            return (Expression<String>) expression;
        } else {
            return ((JpaExpression<?>) expression).cast(String.class);
        }
    }

    @Override
    public com.querydsl.core.types.Predicate createPredicate(PathBuilder<?> pathBuilder, String attributeName) {
        StringOperation path = Expressions.stringOperation(Ops.STRING_CAST, pathBuilder.get(attributeName));
        return path.lower().like(escapedRawValue, '~');
    }
}