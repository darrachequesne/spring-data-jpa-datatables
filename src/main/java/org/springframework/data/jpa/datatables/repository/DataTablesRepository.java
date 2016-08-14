package org.springframework.data.jpa.datatables.repository;

import java.io.Serializable;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Convenience interface to allow pulling in {@link PagingAndSortingRepository} and
 * {@link JpaSpecificationExecutor} functionality in one go.
 * 
 * @author Damien Arrachequesne
 */
@NoRepositoryBean
public interface DataTablesRepository<T, ID extends Serializable>
    extends PagingAndSortingRepository<T, ID>, JpaSpecificationExecutor<T> {

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
   * @param additionalSpecification an additional {@link Specification} to apply to the query (with
   *        an "AND" clause)
   * @return a {@link DataTablesOutput}
   */
  DataTablesOutput<T> findAll(DataTablesInput input, Specification<T> additionalSpecification);

  /**
   * Returns the filtered list for the given {@link DataTablesInput}.
   * 
   * @param input the {@link DataTablesInput} mapped from the Ajax request
   * @param additionalSpecification an additional {@link Specification} to apply to the query (with
   *        an "AND" clause)
   * @param preFilteringSpecification a pre-filtering {@link Specification} to apply to the query
   *        (with an "AND" clause)
   * @return a {@link DataTablesOutput}
   */
  DataTablesOutput<T> findAll(DataTablesInput input, Specification<T> additionalSpecification,
      Specification<T> preFilteringSpecification);

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
   * @param additionalSpecification an additional {@link Specification} to apply to the query (with
   *        an "AND" clause)
   * @param preFilteringSpecification a pre-filtering {@link Specification} to apply to the query
   *        (with an "AND" clause)
   * @param converter the {@link Converter} to apply to the results of the query
   * @return a {@link DataTablesOutput}
   */
  <R> DataTablesOutput<R> findAll(DataTablesInput input, Specification<T> additionalSpecification,
      Specification<T> preFilteringSpecification, Converter<T, R> converter);

}
