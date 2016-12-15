package org.springframework.data.jpa.datatables.repository;

import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.ATTRIBUTE_SEPARATOR;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.ESCAPED_ATTRIBUTE_SEPARATOR;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.ESCAPED_NULL;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.ESCAPED_OR_SEPARATOR;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.ESCAPE_CHAR;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.NULL;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.OR_SEPARATOR;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.getLikeFilterValue;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.isBoolean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

class SpecificationFactory {

  public static <T> Specification<T> createSpecification(final DataTablesInput input) {
    return new DataTablesSpecification<T>(input);
  }

  private static class DataTablesSpecification<T> implements Specification<T> {
    private final DataTablesInput input;

    public DataTablesSpecification(DataTablesInput input) {
      this.input = input;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      Predicate predicate = cb.conjunction();
      Expression<Boolean> booleanExpression;
      Expression<String> stringExpression;

      // check for each searchable column whether a filter value exists
      for (Column column : input.getColumns()) {
        String filterValue = column.getSearch().getValue();
        boolean isColumnSearchable = column.getSearchable() && StringUtils.hasText(filterValue);
        if (!isColumnSearchable) {
          continue;
        }

        if (filterValue.contains(OR_SEPARATOR)) {
          // the filter contains multiple values, add a 'WHERE .. IN' clause
          boolean nullable = false;
          List<String> values = new ArrayList<String>();
          for (String value : filterValue.split(ESCAPED_OR_SEPARATOR)) {
            if (NULL.equals(value)) {
              nullable = true;
            } else {
              values.add(ESCAPED_NULL.equals(value) ? NULL : value); // to match a 'NULL' string
            }
          }
          if (values.size() > 0 && isBoolean(values.get(0))) {
            Object[] booleanValues = new Boolean[values.size()];
            for (int i = 0; i < values.size(); i++) {
              booleanValues[i] = Boolean.valueOf(values.get(i));
            }
            booleanExpression = getExpression(root, column.getData(), Boolean.class);
            Predicate in = booleanExpression.in(booleanValues);
            if (nullable) {
              predicate = cb.and(predicate, cb.or(in, booleanExpression.isNull()));
            } else {
              predicate = cb.and(predicate, in);
            }
          } else {
            stringExpression = getExpression(root, column.getData(), String.class);
            Predicate in = stringExpression.in(values);
            if (nullable) {
              predicate = cb.and(predicate, cb.or(in, stringExpression.isNull()));
            } else {
              predicate = cb.and(predicate, in);
            }
          }
          continue;
        }
        // the filter contains only one value, add a 'WHERE .. LIKE' clause
        if (isBoolean(filterValue)) {
          booleanExpression = getExpression(root, column.getData(), Boolean.class);
          predicate = cb.and(predicate, cb.equal(booleanExpression, Boolean.valueOf(filterValue)));
          continue;
        }

        stringExpression = getExpression(root, column.getData(), String.class);
        if (NULL.equals(filterValue)) {
          predicate = cb.and(predicate, stringExpression.isNull());
          continue;
        }

        String likeFilterValue =
            getLikeFilterValue(ESCAPED_NULL.equals(filterValue) ? NULL : filterValue);
        predicate =
            cb.and(predicate, cb.like(cb.lower(stringExpression), likeFilterValue, ESCAPE_CHAR));
      }

      // check whether a global filter value exists
      String globalFilterValue = input.getSearch().getValue();
      if (StringUtils.hasText(globalFilterValue)) {
        Predicate matchOneColumnPredicate = cb.disjunction();
        // add a 'WHERE .. LIKE' clause on each searchable column
        for (Column column : input.getColumns()) {
          if (column.getSearchable()) {
            Expression<String> expression = getExpression(root, column.getData(), String.class);

            matchOneColumnPredicate = cb.or(matchOneColumnPredicate,
                cb.like(cb.lower(expression), getLikeFilterValue(globalFilterValue), ESCAPE_CHAR));
          }
        }
        predicate = cb.and(predicate, matchOneColumnPredicate);
      }
      // findAll method does a count query first, and then query for the actual data. Yet in the
      // count query, adding a JOIN FETCH results in the following error 'query specified join
      // fetching, but the owner of the fetched association was not present in the select list'
      // see https://jira.spring.io/browse/DATAJPA-105
      boolean isCountQuery = query.getResultType() == Long.class;
      if (isCountQuery) {
        return predicate;
      }
      // add JOIN FETCH when necessary
      for (Column column : input.getColumns()) {
        boolean isJoinable =
            column.getSearchable() && column.getData().contains(ATTRIBUTE_SEPARATOR);
        if (!isJoinable) {
          continue;
        }
        String[] values = column.getData().split(ESCAPED_ATTRIBUTE_SEPARATOR);
        PersistentAttributeType type =
            root.getModel().getAttribute(values[0]).getPersistentAttributeType();
        if (type != PersistentAttributeType.ONE_TO_ONE
            && type != PersistentAttributeType.MANY_TO_ONE) {
          continue;
        }
        Fetch<?, ?> fetch = null;
        for (int i = 0; i < values.length - 1; i++) {
          fetch = (fetch == null ? root : fetch).fetch(values[i], JoinType.LEFT);
        }
      }
      return predicate;
    }
  }

  private static <S> Expression<S> getExpression(Root<?> root, String columnData, Class<S> clazz) {
    if (!columnData.contains(ATTRIBUTE_SEPARATOR)) {
      // columnData is like "attribute" so nothing particular to do
      return root.get(columnData).as(clazz);
    }
    // columnData is like "joinedEntity.attribute" so add a join clause
    String[] values = columnData.split(ESCAPED_ATTRIBUTE_SEPARATOR);
    if (root.getModel().getAttribute(values[0])
        .getPersistentAttributeType() == PersistentAttributeType.EMBEDDED) {
      // with @Embedded attribute
      return root.get(values[0]).get(values[1]).as(clazz);
    }
    From<?, ?> from = root;
    for (int i = 0; i < values.length - 1; i++) {
      from = from.join(values[i], JoinType.LEFT);
    }
    return from.get(values[values.length - 1]).as(clazz);
  }

}
