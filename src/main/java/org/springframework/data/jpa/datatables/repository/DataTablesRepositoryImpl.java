package org.springframework.data.jpa.datatables.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.parameter.ColumnParameter;
import org.springframework.data.jpa.datatables.parameter.OrderParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/**
 * Repository implementation
 * 
 * @author Damien Arrachequesne
 */
public class DataTablesRepositoryImpl<T, ID extends Serializable> extends
		SimpleJpaRepository<T, ID> implements DataTablesRepository<T, ID> {

	public DataTablesRepositoryImpl(
			JpaEntityInformation<T, ?> entityInformation,
			EntityManager entityManager) {

		super(entityInformation, entityManager);
	}

	@Override
	public DataTablesOutput<T> findAll(DataTablesInput input) {
		return findAll(input, null);
	}

	@Override
	public DataTablesOutput<T> findAll(DataTablesInput input,
			Specification<T> additionalSpecification) {
		DataTablesOutput<T> output = new DataTablesOutput<T>();
		output.setDraw(input.getDraw());

		try {
			output.setRecordsTotal(count());

			Page<T> data = findAll(
					Specifications.where(new DataTablesSpecification<T>(input))
							.and(additionalSpecification), getPageable(input));

			output.setData(data.getContent());
			output.setRecordsFiltered(data.getTotalElements());

		} catch (Exception e) {
			output.setError(e.toString());
			output.setRecordsFiltered(0L);
		}

		return output;
	}

	/**
	 * Creates a 'LIMIT .. OFFSET .. ORDER BY ..' clause for the given
	 * {@link DataTablesInput}.
	 * 
	 * @param input
	 *            the {@link DataTablesInput} mapped from the Ajax request
	 * @return a {@link Pageable}, must not be {@literal null}.
	 */
	private Pageable getPageable(DataTablesInput input) {
		List<Order> orders = new ArrayList<Order>();
		for (OrderParameter order : input.getOrder()) {
			ColumnParameter column = input.getColumns().get(order.getColumn());
			if (column.getOrderable()) {
				String sortColumn = column.getData();
				Direction sortDirection = Direction.fromString(order.getDir());
				orders.add(new Order(sortDirection, sortColumn));
			}
		}
		Sort sort = orders.isEmpty() ? null : new Sort(orders);

		return new PageRequest(input.getStart() / input.getLength(),
				input.getLength(), sort);
	}
}
