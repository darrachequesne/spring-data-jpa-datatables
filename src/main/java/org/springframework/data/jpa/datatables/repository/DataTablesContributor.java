package org.springframework.data.jpa.datatables.repository;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.datatables.qrepository.QDataTablesRepository;
import org.springframework.data.jpa.datatables.qrepository.QDataTablesRepositoryImpl;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFragmentsContributor;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.repository.core.support.RepositoryFragmentsContributor;

import static org.springframework.data.querydsl.QuerydslUtils.QUERY_DSL_PRESENT;

/**
 * JPA-specific {@link RepositoryFragmentsContributor} contributing DataTables fragments if a
 * repository implements {@link DataTablesRepository} or {@link QDataTablesRepository}.
 *
 * @see org.springframework.data.jpa.repository.support.QuerydslContributor
 */
public enum DataTablesContributor implements JpaRepositoryFragmentsContributor {
  INSTANCE;

  @Override
  public RepositoryComposition.RepositoryFragments contribute(
      RepositoryMetadata metadata,
      JpaEntityInformation<?, ?> entityInformation,
      EntityManager entityManager,
      EntityPathResolver resolver) {

    if (isDataTablesRepository(metadata)) {
      DataTablesRepositoryImpl<?, ?> executor =
          new DataTablesRepositoryImpl<>(entityInformation, entityManager);

      return RepositoryComposition.RepositoryFragments.of(
          RepositoryFragment.implemented(DataTablesRepositoryImpl.class, executor));
    }

    if (isQDataTablesRepository(metadata)) {
      QDataTablesRepositoryImpl<?, ?> executor =
          new QDataTablesRepositoryImpl<>(
              entityInformation, entityManager, SimpleEntityPathResolver.INSTANCE);

      return RepositoryComposition.RepositoryFragments.of(
          RepositoryFragment.implemented(QDataTablesRepositoryImpl.class, executor));
    }

    return RepositoryComposition.RepositoryFragments.empty();
  }

  @Override
  public RepositoryComposition.RepositoryFragments describe(RepositoryMetadata metadata) {

    if (isDataTablesRepository(metadata)) {
      return RepositoryComposition.RepositoryFragments.of(
          RepositoryFragment.structural(
              DataTablesRepositoryImpl.class, DataTablesRepositoryImpl.class));
    }

    if (isQDataTablesRepository(metadata)) {
      return RepositoryComposition.RepositoryFragments.of(
          RepositoryFragment.structural(
              QDataTablesRepositoryImpl.class, QDataTablesRepositoryImpl.class));
    }

    return RepositoryComposition.RepositoryFragments.empty();
  }

  private static boolean isDataTablesRepository(RepositoryMetadata metadata) {
    return DataTablesRepository.class.isAssignableFrom(metadata.getRepositoryInterface());
  }

  private static boolean isQDataTablesRepository(RepositoryMetadata metadata) {
    return QUERY_DSL_PRESENT
        && QDataTablesRepository.class.isAssignableFrom(metadata.getRepositoryInterface());
  }
}
