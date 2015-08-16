package org.springframework.data.jpa.datatables.repository;

import java.util.Arrays;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.parameter.ColumnParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * {@link Specification} converting {@link DataTablesInput} to proper SQL query
 * 
 * @author Damien Arrachequesne
 */
public class DataTablesSpecification<T> implements Specification<T> {

	private final static String OR_SEPARATOR = "+";

	private final static String ATTRIBUTE_SEPARATOR = "__";

	private final DataTablesInput input;

	public DataTablesSpecification(DataTablesInput input) {
		this.input = input;
	}

	/**
	 * Creates a WHERE clause for the given {@link DataTablesInput}.
	 * 
	 * @return a {@link Predicate}, must not be {@literal null}.
	 */
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
			CriteriaBuilder criteriaBuilder) {

		Predicate predicate = criteriaBuilder.conjunction();

		// check for each searchable column whether a filter value exists
		for (ColumnParameter column : input.getColumns()) {
			String filterValue = column.getSearch().getValue();
			if (column.getSearchable() && StringUtils.hasText(filterValue)) {
				Expression<String> expression = getExpression(root,
						column.getData());

				if (filterValue.contains(OR_SEPARATOR)) {
					// the filter contains multiple values, add a 'WHERE .. IN'
					// clause
					// Note: "\\" is added to escape special character '+'
					String[] values = filterValue.split("\\" + OR_SEPARATOR);
					predicate = criteriaBuilder.and(predicate,
							expression.in(Arrays.asList(values)));
				} else {
					// the filter contains only one value, add a 'WHERE .. ='
					// clause
					predicate = criteriaBuilder.and(predicate,
							criteriaBuilder.equal(expression, filterValue));
				}
			}
		}

		// check whether a global filter value exists
		String globalFilterValue = input.getSearch().getValue();
		if (StringUtils.hasText(globalFilterValue)) {
			Predicate matchOneColumnPredicate = criteriaBuilder.disjunction();
			// add a 'WHERE .. LIKE' clause on each searchable column
			for (ColumnParameter column : input.getColumns()) {
				if (column.getSearchable()) {
					Expression<String> expression = getExpression(root,
							column.getData());

					matchOneColumnPredicate = criteriaBuilder.or(
							matchOneColumnPredicate,
							criteriaBuilder.like(expression, "%"
									+ globalFilterValue + "%"));
				}
			}
			predicate = criteriaBuilder.and(predicate, matchOneColumnPredicate);
		}

		return predicate;
	}

	private Expression<String> getExpression(Root<T> root, String columnData) {
		if (columnData.contains(ATTRIBUTE_SEPARATOR)) {
			// columnData is like "joinedEntity__attribute" so add a join clause
			String[] values = columnData.split("\\" + ATTRIBUTE_SEPARATOR);
			return root.join(values[0], JoinType.LEFT).get(values[1])
					.as(String.class);
		} else {
			// columnData is like "attribute" so nothing particular to do
			return root.get(columnData).as(String.class);
		}
	}
}
