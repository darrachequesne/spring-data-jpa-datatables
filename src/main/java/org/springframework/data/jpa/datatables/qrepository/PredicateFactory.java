package org.springframework.data.jpa.datatables.qrepository;

import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.ESCAPED_OR_SEPARATOR;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.OR_SEPARATOR;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.isBoolean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.parameter.ColumnParameter;
import org.springframework.util.StringUtils;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.path.PathBuilder;

class PredicateFactory {

  private final static char ESCAPE_CHAR = '~';

  public static Predicate createPredicate(PathBuilder<?> entity, DataTablesInput input) {
    BooleanBuilder predicate = new BooleanBuilder();

    // check for each searchable column whether a filter value exists
    for (ColumnParameter column : input.getColumns()) {
      String filterValue = column.getSearch().getValue();
      boolean isColumnSearchable = column.getSearchable() && StringUtils.hasText(filterValue);
      if (!isColumnSearchable) {
        continue;
      }
      if (filterValue.contains(OR_SEPARATOR)) {
        // the filter contains multiple values, add a 'WHERE .. IN' clause
        String[] values = filterValue.split(ESCAPED_OR_SEPARATOR);
        if (values.length > 0 && isBoolean(values[0])) {
          List<Boolean> booleanValues = new ArrayList<Boolean>();
          for (int i = 0; i < values.length; i++) {
            booleanValues.add(Boolean.valueOf(values[i]));
          }
          predicate = predicate.and(entity.getBoolean(column.getData()).in(booleanValues));
        } else {
          predicate.and(getStringExpression(entity, column.getData()).in(values));
        }
      } else {
        // the filter contains only one value, add a 'WHERE .. LIKE' clause
        if (isBoolean(filterValue)) {
          predicate =
              predicate.and(entity.getBoolean(column.getData()).eq(Boolean.valueOf(filterValue)));
        } else {
          predicate = predicate.and(getStringExpression(entity, column.getData()).lower()
              .like(getLikeFilterValue(filterValue), ESCAPE_CHAR));
        }
      }
    }

    // check whether a global filter value exists
    String globalFilterValue = input.getSearch().getValue();
    if (StringUtils.hasText(globalFilterValue)) {
      BooleanBuilder matchOneColumnPredicate = new BooleanBuilder();
      // add a 'WHERE .. LIKE' clause on each searchable column
      for (ColumnParameter column : input.getColumns()) {
        if (column.getSearchable()) {
          matchOneColumnPredicate =
              matchOneColumnPredicate.or(getStringExpression(entity, column.getData()).lower()
                  .like(getLikeFilterValue(globalFilterValue), ESCAPE_CHAR));
        }
      }
      predicate = predicate.and(matchOneColumnPredicate);
    }
    return predicate;
  }

  private static StringExpression getStringExpression(PathBuilder<?> entity, String columnData) {
    return Expressions.stringOperation(Ops.STRING_CAST, entity.get(columnData));
  }

  private static String getLikeFilterValue(String filterValue) {
    return "%" + filterValue.toLowerCase().replaceAll("~", "~~").replaceAll("%", "~%")
        .replaceAll("_", "~_") + "%";
  }

}
