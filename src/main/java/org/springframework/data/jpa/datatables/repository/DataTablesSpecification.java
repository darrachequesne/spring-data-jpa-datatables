package org.springframework.data.jpa.datatables.repository;

import java.util.Arrays;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
				if (filterValue.contains(OR_SEPARATOR)) {
					// the filter contains multiple values, add a 'WHERE .. IN'
					// clause
					// Note: "\\" is added to escape special character '+'
					String[] values = filterValue.split("\\" + OR_SEPARATOR);
					predicate = criteriaBuilder.and(
							predicate,
							root.get(column.getData()).as(String.class)
									.in(Arrays.asList(values)));
				} else {
					// the filter contains only one value, add a 'WHERE .. ='
					// clause
					predicate = criteriaBuilder.and(predicate, criteriaBuilder
							.equal(root.get(column.getData()).as(String.class),
									filterValue));
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
					matchOneColumnPredicate = criteriaBuilder.or(
							matchOneColumnPredicate, criteriaBuilder.like(root
									.get(column.getData()).as(String.class),
									"%" + globalFilterValue + "%"));
				}
			}
			predicate = criteriaBuilder.and(predicate, matchOneColumnPredicate);
		}

		return predicate;
	}
}
