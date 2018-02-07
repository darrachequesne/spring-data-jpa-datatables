package org.springframework.data.jpa.datatables.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.SpecificationBuilder;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public class DataTablesRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
    implements DataTablesRepository<T, ID> {

  DataTablesRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
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
  public <R> DataTablesOutput<R> findAll(DataTablesInput input, Function<T, R> converter) {
    return findAll(input, null, null, converter);
  }

  @Override
  public <R> DataTablesOutput<R> findAll(DataTablesInput input,
      Specification<T> additionalSpecification, Specification<T> preFilteringSpecification,
      Function<T, R> converter) {
    DataTablesOutput<R> output = new DataTablesOutput<>();
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

      SpecificationBuilder<T> specificationBuilder = new SpecificationBuilder<>(input);
      Page<T> data = findAll(
              Specification.where(specificationBuilder.build())
                      .and(additionalSpecification)
                      .and(preFilteringSpecification),
              specificationBuilder.createPageable());

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
