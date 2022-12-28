package org.springframework.data.jpa.datatables.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.tree.domain.AbstractSqmFrom;
import org.hibernate.query.sqm.tree.domain.SqmPath;
import org.hibernate.query.sqm.tree.from.SqmAttributeJoin;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.SpecificationBuilder;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.SearchPanes;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class DataTablesRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
    implements DataTablesRepository<T, ID> {
  private final EntityManager entityManager;

  DataTablesRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
      EntityManager entityManager) {

    super(entityInformation, entityManager);
    this.entityManager = entityManager;
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
    log.debug("input: {}", input);

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
      Specification<T> specification = Specification.where(specificationBuilder.build())
              .and(additionalSpecification)
              .and(preFilteringSpecification);
      Page<T> data = findAll(specification, specificationBuilder.createPageable());

      @SuppressWarnings("unchecked")
      List<R> content =
          converter == null ? (List<R>) data.getContent() : data.map(converter).getContent();
      output.setData(content);
      output.setRecordsFiltered(data.getTotalElements());

      if (input.getSearchPanes() != null) {
        SpecificationBuilder<T> specificationSearchPaneBuilder = new SpecificationBuilder<>(input);
        output.setSearchPanes(computeSearchPanes(input, specificationSearchPaneBuilder.buildSearchPane()));
      }
    } catch (Exception e) {
      log.warn("error while fetching records", e);
      output.setError(e.toString());
    }

    log.debug("output: {}", output);
    return output;
  }

  private SearchPanes computeSearchPanes(DataTablesInput input, Specification<T> specification) {
    Map<String, List<SearchPanes.Item>> options = new HashMap<>();

    input.getSearchPanes().forEach((attribute, values) -> {
      CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
      CriteriaQuery<Object[]> query = criteriaBuilder.createQuery(Object[].class);
      Root<T> root = query.from(getDomainClass());
      query.multiselect(root.get(attribute), criteriaBuilder.count(root));
      query.groupBy(root.get(attribute));
      query.where(specification.toPredicate(root, query, criteriaBuilder));

      List<SearchPanes.Item> items = new ArrayList<>();

      this.entityManager.createQuery(query).getResultList().forEach(objects -> {
        String value = String.valueOf(objects[0]);
        long count = (long) objects[1];
        items.add(new SearchPanes.Item(value, value, count, count));
      });

      options.put(attribute, items);
    });

    return new SearchPanes(options);
  }

}
