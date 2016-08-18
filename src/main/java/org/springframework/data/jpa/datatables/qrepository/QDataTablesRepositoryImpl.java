package org.springframework.data.jpa.datatables.qrepository;

import static org.springframework.data.jpa.datatables.repository.DataTablesUtils.getPageable;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;

/**
 * Repository implementation
 * 
 * @author Damien Arrachequesne
 */
public class QDataTablesRepositoryImpl<T, ID extends Serializable>
    extends QueryDslJpaRepository<T, ID> implements QDataTablesRepository<T, ID> {

  private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER =
      SimpleEntityPathResolver.INSTANCE;

  private final EntityPath<T> path;
  private final PathBuilder<T> builder;

  public QDataTablesRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
      EntityManager entityManager) {
    this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
  }

  public QDataTablesRepositoryImpl(JpaEntityInformation<T, ID> entityInformation,
      EntityManager entityManager, EntityPathResolver resolver) {
    super(entityInformation, entityManager);
    this.path = resolver.createPath(entityInformation.getJavaType());
    this.builder = new PathBuilder<T>(path.getType(), path.getMetadata());
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
  public <R> DataTablesOutput<R> findAll(DataTablesInput input, Converter<T, R> converter) {
    return findAll(input, null, null, converter);
  }

  @Override
  public <R> DataTablesOutput<R> findAll(DataTablesInput input, Predicate additionalPredicate,
      Predicate preFilteringPredicate, Converter<T, R> converter) {
    DataTablesOutput<R> output = new DataTablesOutput<R>();
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

      Predicate predicate = PredicateFactory.createPredicate(this.builder, input);
      Page<T> data = findAll(new BooleanBuilder().and(predicate).and(additionalPredicate)
          .and(preFilteringPredicate).getValue(), getPageable(input));

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
