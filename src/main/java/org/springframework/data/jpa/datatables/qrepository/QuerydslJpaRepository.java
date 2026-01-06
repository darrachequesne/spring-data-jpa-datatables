package org.springframework.data.jpa.datatables.qrepository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;

import java.io.Serializable;

public class QuerydslJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {
  private final EntityPath<T> path;
  private final PathBuilder<T> builder;
  private final Querydsl querydsl;
  private final EntityManager entityManager;

  public QuerydslJpaRepository(
      JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
    this(entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE);
  }

  public QuerydslJpaRepository(
      JpaEntityInformation<T, ID> entityInformation,
      EntityManager entityManager,
      EntityPathResolver resolver) {
    super(entityInformation, entityManager);

    this.path = resolver.createPath(entityInformation.getJavaType());
    this.builder = new PathBuilder<>(path.getType(), path.getMetadata());
    this.querydsl = new Querydsl(entityManager, this.builder);
    this.entityManager = entityManager;
  }

  public Page<T> findAll(Predicate predicate, Pageable pageable) {
    final JPQLQuery<?> countQuery = createCountQuery(predicate);
    JPQLQuery<T> query = querydsl.applyPagination(pageable, createQuery(predicate).select(path));

    return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchCount);
  }

  protected JPQLQuery<?> createCountQuery(@Nullable Predicate... predicate) {
    return doCreateQuery(getQueryHintsForCount(), predicate);
  }

  private AbstractJPAQuery<?, ?> doCreateQuery(QueryHints hints, @Nullable Predicate... predicate) {

    AbstractJPAQuery<?, ?> query = querydsl.createQuery(path);

    if (predicate != null) {
      query = query.where(predicate);
    }

    hints.forEach(query::setHint);

    return query;
  }

  protected AbstractJPAQuery<?, ?> createQuery(Predicate... predicate) {
    AbstractJPAQuery<?, ?> query =
        doCreateQuery(getQueryHints().withFetchGraphs(entityManager), predicate);
    CrudMethodMetadata metadata = getRepositoryMethodMetadata();
    if (metadata == null) {
      return query;
    }

    LockModeType type = metadata.getLockModeType();
    return type == null ? query : query.setLockMode(type);
  }

  public long count(Predicate predicate) {
    return createQuery(predicate).fetchCount();
  }
}
