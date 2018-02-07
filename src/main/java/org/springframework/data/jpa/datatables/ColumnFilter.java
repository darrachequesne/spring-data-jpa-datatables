package org.springframework.data.jpa.datatables;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Filter which parses the input value to create one of the following predicates:
 * <ul>
 * <li>WHERE ... LIKE ..., see {@link GlobalFilter}</li>
 * <li>WHERE ... IN ... when the input contains multiple values separated by "+"</li>
 * <li>WHERE ... IS NULL when the input is equals to "NULL"</li>
 * <li>WHERE ... IN ... OR ... IS NULL</li>
 * </ul>
 */
class ColumnFilter extends GlobalFilter {
    private final Set<String> values;
    private final Set<Boolean> booleanValues;
    private boolean addNullCase;
    private boolean isBooleanComparison;

    ColumnFilter(String filterValue) {
        super(filterValue);

        isBooleanComparison = true;
        Set<String> values = new HashSet<>();
        for (String value : filterValue.split("\\+")) {
            if ("NULL".equals(value)) {
                addNullCase = true;
            } else {
                isBooleanComparison &= isBoolean(value);
                values.add(nullOrTrimmedValue(value));
            }
        }
        this.values = unmodifiableSet(values);

        Set<Boolean> booleanValues = new HashSet<>();
        if (isBooleanComparison) {
            for (String value : values) {
                booleanValues.add(Boolean.valueOf(value));
            }
        }
        this.booleanValues = unmodifiableSet(booleanValues);
    }

    private boolean isBoolean(String filterValue) {
        return "TRUE".equalsIgnoreCase(filterValue) || "FALSE".equalsIgnoreCase(filterValue);
    }

    @Override
    public Predicate createPredicate(PathBuilder<?> pathBuilder, String attributeName) {
        StringOperation path = Expressions.stringOperation(Ops.STRING_CAST, pathBuilder.get(attributeName));
        BooleanPath booleanPath = pathBuilder.getBoolean(attributeName);

        if (values.isEmpty()) {
            return addNullCase ? path.isNull() : null;
        } else if (isBasicFilter()) {
            return super.createPredicate(pathBuilder, attributeName);
        }

        BooleanExpression predicate = isBooleanComparison ? booleanPath.in(booleanValues) : path.in(values);
        if (addNullCase) predicate = predicate.or(path.isNull());
        return predicate;
    }

    @Override
    public javax.persistence.criteria.Predicate createPredicate(From<?, ?> from, CriteriaBuilder criteriaBuilder, String attributeName) {
        Expression<?> expression = from.get(attributeName);

        if (values.isEmpty()) {
            return addNullCase ? expression.isNull() : criteriaBuilder.conjunction();
        } else if (isBasicFilter()) {
            return super.createPredicate(from, criteriaBuilder, attributeName);
        }

        javax.persistence.criteria.Predicate predicate;
        if (isBooleanComparison) {
            predicate = expression.in(booleanValues);
        } else {
            predicate = expression.as(String.class).in(values);
        }
        if (addNullCase) predicate = criteriaBuilder.or(predicate, expression.isNull());

        return predicate;
    }

    private boolean isBasicFilter() {
        return values.size() == 1 && !addNullCase && !isBooleanComparison;
    }
}