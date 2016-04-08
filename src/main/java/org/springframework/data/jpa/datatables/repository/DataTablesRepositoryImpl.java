package org.springframework.data.jpa.datatables.repository;

import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.getPageable;
import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.getSpecification;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/**
 * Repository implementation
 * 
 * @author Damien Arrachequesne
 */
public class DataTablesRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
    implements DataTablesRepository<T, ID> {

  public DataTablesRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
      EntityManager entityManager) {

    super(entityInformation, entityManager);
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input) {
    return findAll(input, null, null);
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input,
      Specification<T> additionalSpecification) {
    return findAll(input, additionalSpecification, null);
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input,
      Specification<T> additionalSpecification, Specification<T> preFilteringSpecification) {
    DataTablesOutput<T> output = new DataTablesOutput<T>();
    output.setDraw(input.getDraw());

    try {
      output.setRecordsTotal(
          preFilteringSpecification == null ? count() : count(preFilteringSpecification));

      Page<T> data = findAll(Specifications.where(getSpecification(getDomainClass(), input))
          .and(additionalSpecification).and(preFilteringSpecification), getPageable(input));

      output.setData(data.getContent());
      output.setRecordsFiltered(data.getTotalElements());

    } catch (Exception e) {
      output.setError(e.toString());
      output.setRecordsFiltered(0L);
    }

    return output;
  }

}
