package org.springframework.data.jpa.datatables.qrepository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.PredicateBuilder;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public class QDataTablesRepositoryImpl<T, ID extends Serializable>
    extends QuerydslJpaRepository<T, ID> implements QDataTablesRepository<T, ID> {

  private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER =
      SimpleEntityPathResolver.INSTANCE;

  private final PathBuilder<T> builder;

  QDataTablesRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
      EntityManager entityManager) {
    this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
  }

  public QDataTablesRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
      EntityManager entityManager, EntityPathResolver resolver) {
    super(entityInformation, entityManager);
    EntityPath<T> path = resolver.createPath(entityInformation.getJavaType());
    this.builder = new PathBuilder<>(path.getType(), path.getMetadata());
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input) {
    return findAll(input, null, null, null);
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input, Predicate additionalPredicate) {
    return findAll(input, additionalPredicate, null, null);
  }

  @Override
  public DataTablesOutput<T> findAll(DataTablesInput input, Predicate additionalPredicate,
      Predicate preFilteringPredicate) {
    return findAll(input, additionalPredicate, preFilteringPredicate, null);
  }

  @Override
  public <R> DataTablesOutput<R> findAll(DataTablesInput input, Function<T, R> converter) {
    return findAll(input, null, null, converter);
  }

  @Override
  public <R> DataTablesOutput<R> findAll(DataTablesInput input, Predicate additionalPredicate,
      Predicate preFilteringPredicate, Function<T, R> converter) {
    DataTablesOutput<R> output = new DataTablesOutput<>();
    output.setDraw(input.getDraw());
    if (input.getLength() == 0) {
      return output;
    }

    try {
      long recordsTotal = preFilteringPredicate == null ? count() : count(preFilteringPredicate);
      if (recordsTotal == 0) {
        return output;
      }
      output.setRecordsTotal(recordsTotal);

      PredicateBuilder predicateBuilder = new PredicateBuilder(this.builder, input);
      BooleanBuilder booleanBuilder = new BooleanBuilder()
              .and(predicateBuilder.build())
              .and(additionalPredicate)
              .and(preFilteringPredicate);
      Predicate predicate = booleanBuilder.getValue();
      Page<T> data = predicate != null
              ? findAll(predicate, predicateBuilder.createPageable())
              : findAll(predicateBuilder.createPageable());

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
