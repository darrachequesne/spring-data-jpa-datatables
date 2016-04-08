package org.springframework.data.jpa.datatables.qrepository;

import java.io.Serializable;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mysema.query.types.Predicate;

/**
 * Convenience interface to allow pulling in {@link PagingAndSortingRepository} and
 * {@link QueryDslPredicateExecutor} functionality in one go.
 * 
 * @author Damien Arrachequesne
 */
@NoRepositoryBean
public interface QDataTablesRepository<T, ID extends Serializable>
    extends PagingAndSortingRepository<T, ID>, QueryDslPredicateExecutor<T> {

  /**
   * Returns the filtered list for the given {@link DataTablesInput}.
   * 
   * @param input the {@link DataTablesInput} mapped from the Ajax request
   * @return a {@link DataTablesOutput}
   */
  DataTablesOutput<T> findAll(DataTablesInput input);

  /**
   * Returns the filtered list for the given {@link DataTablesInput}.
   * 
   * @param input the {@link DataTablesInput} mapped from the Ajax request
   * @param additionalPredicate an additional {@link Predicate} to apply to the query (with an "AND"
   *        clause)
   * @return a {@link DataTablesOutput}
   */
  DataTablesOutput<T> findAll(DataTablesInput input, Predicate additionalPredicate);

}
