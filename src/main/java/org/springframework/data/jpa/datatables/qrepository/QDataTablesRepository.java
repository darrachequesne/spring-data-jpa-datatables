package org.springframework.data.jpa.datatables.qrepository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.function.Function;

/**
 *
 * @author Damien Arrachequesne
 */
@NoRepositoryBean
public interface QDataTablesRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

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
   * @param converter the {@link Function} to apply to the results of the query
   * @return a {@link DataTablesOutput}
   */
  <R> DataTablesOutput<R> findAll(DataTablesInput input, Function<T, R> converter);

  /**
   * Returns the filtered list for the given {@link DataTablesInput}.
   *
   * @param input the {@link DataTablesInput} mapped from the Ajax request
   * @param additionalPredicate an additional {@link Predicate} to apply to the query (with an "AND"
   *        clause)
   * @param preFilteringPredicate a pre-filtering {@link Predicate} to apply to the query (with an
   *        "AND" clause)
   * @param converter the {@link Function} to apply to the results of the query
   * @return a {@link DataTablesOutput}
   */
  <R> DataTablesOutput<R> findAll(DataTablesInput input, Predicate additionalPredicate,
      Predicate preFilteringPredicate, Function<T, R> converter);

}
