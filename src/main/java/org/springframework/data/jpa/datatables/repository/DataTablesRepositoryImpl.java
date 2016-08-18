package org.springframework.data.jpa.datatables.repository;

import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.getPageable;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.core.convert.converter.Converter;
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
    return findAll(input, null, null, null);
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input,
      Specification<T> additionalSpecification) {
    return findAll(input, additionalSpecification, null, null);
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input,
      Specification<T> additionalSpecification, Specification<T> preFilteringSpecification) {
    return findAll(input, additionalSpecification, preFilteringSpecification, null);
  }

  @Override
  public <R> DataTablesOutput<R> findAll(DataTablesInput input, Converter<T, R> converter) {
    return findAll(input, null, null, converter);
  }

  @Override
  public <R> DataTablesOutput<R> findAll(DataTablesInput input,
      Specification<T> additionalSpecification, Specification<T> preFilteringSpecification,
      Converter<T, R> converter) {
    DataTablesOutput<R> output = new DataTablesOutput<R>();
    output.setDraw(input.getDraw());
    if (input.getLength() == 0) {
      return output;
    }

    try {
      long recordsTotal =
          preFilteringSpecification == null ? count() : count(preFilteringSpecification);
      if (recordsTotal == 0) {
        return output;
      }
      output.setRecordsTotal(recordsTotal);

      Specification<T> specification = SpecificationFactory.createSpecification(input);
      Page<T> data = findAll(Specifications.where(specification).and(additionalSpecification)
          .and(preFilteringSpecification), getPageable(input));

      @SuppressWarnings("unchecked")
      List<R> content =
          converter == null ? (List<R>) data.getContent() : data.map(converter).getContent();
      output.setData(content);
      output.setRecordsFiltered(data.getTotalElements());

    } catch (Exception e) {
      output.setError(e.toString());
    }

    return output;
  }

}
