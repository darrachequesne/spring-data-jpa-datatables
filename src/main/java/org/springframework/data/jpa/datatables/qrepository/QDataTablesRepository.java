package org.springframework.data.jpa.datatables.qrepository;

import java.io.Serializable;

import org.springframework.core.convert.converter.Converter;
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

  /**
   * Returns the filtered list for the given {@link DataTablesInput}.
   * 
   * @param input the {@link DataTablesInput} mapped from the Ajax request
   * @param additionalPredicate an additional {@link Predicate} to apply to the query (with an "AND"
   *        clause)
   * @param preFilteringPredicate a pre-filtering {@link Predicate} to apply to the query (with an
   *        "AND" clause)
   * @return a {@link DataTablesOutput}
   */
  DataTablesOutput<T> findAll(DataTablesInput input, Predicate additionalPredicate,
      Predicate preFilteringPredicate);

  /**
   * Returns the filtered list for the given {@link DataTablesInput}.
   *
   * @param input the {@link DataTablesInput} mapped from the Ajax request
   * @param converter the {@link Converter} to apply to the results of the query
   * @return a {@link DataTablesOutput}
   */
  <R> DataTablesOutput<R> findAll(DataTablesInput input, Converter<T, R> converter);

  /**
   * Returns the filtered list for the given {@link DataTablesInput}.
   *
   * @param input the {@link DataTablesInput} mapped from the Ajax request
   * @param additionalPredicate an additional {@link Predicate} to apply to the query (with an "AND"
   *        clause)
   * @param preFilteringPredicate a pre-filtering {@link Predicate} to apply to the query (with an
   *        "AND" clause)
   * @param converter the {@link Converter} to apply to the results of the query
   * @return a {@link DataTablesOutput}
   */
  <R> DataTablesOutput<R> findAll(DataTablesInput input, Predicate additionalPredicate,
      Predicate preFilteringPredicate, Converter<T, R> converter);

}
