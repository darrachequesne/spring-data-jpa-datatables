package org.springframework.data.jpa.datatables.repository;

import java.io.Serializable;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Convenience interface to allow pulling in {@link PagingAndSortingRepository}
 * and {@link JpaSpecificationExecutor} functionality in one go.
 * 
 * @author Damien Arrachequesne
 */
@NoRepositoryBean
public interface DataTablesRepository<T, ID extends Serializable> extends
		PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T> {

	/**
	 * Returns the filtered list for the given {@link DataTablesInput}.
	 * 
	 * @param input
	 *            the {@link DataTablesInput} mapped from the Ajax request
	 * @return a {@link DataTablesOutput}
	 */
	DataTablesOutput<T> findAll(DataTablesInput input);

	/**
	 * Returns the filtered list for the given {@link DataTablesInput}.
	 * 
	 * @param input
	 *            the {@link DataTablesInput} mapped from the Ajax request
	 * @param additionalSpecification
	 *            an additional {@link Specification} to apply to the query
	 *            (with an "AND" clause)
	 * @return a {@link DataTablesOutput}
	 */
	DataTablesOutput<T> findAll(DataTablesInput input,
			Specification<T> additionalSpecification);

}
