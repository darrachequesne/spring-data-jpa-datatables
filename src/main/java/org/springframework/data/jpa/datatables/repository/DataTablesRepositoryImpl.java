package org.springframework.data.jpa.datatables.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.parameter.ColumnParameter;
import org.springframework.data.jpa.datatables.parameter.OrderParameter;
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
		DataTablesOutput<T> output = new DataTablesOutput<T>();
		output.setDraw(input.getDraw());

		try {
			Page<T> data = findAll(new DataTablesSpecification<T>(input),
					getPageable(input));

			output.setData(data.getContent());
			output.setRecordsFiltered(data.getTotalElements());
			output.setRecordsTotal(count());

		} catch (Exception e) {
			output.setError(e.getMessage());
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
		OrderParameter order = input.getOrder().get(0);
		ColumnParameter column = input.getColumns().get(order.getColumn());

		if (column.getOrderable()) {
			String sortColumn = column.getData();
			Direction sortDirection = Direction.fromString(order.getDir());

			return new PageRequest(input.getStart() / input.getLength(),
					input.getLength(), sortDirection, sortColumn);
		} else {
			return new PageRequest(input.getStart() / input.getLength(),
					input.getLength());
		}
	}
}
