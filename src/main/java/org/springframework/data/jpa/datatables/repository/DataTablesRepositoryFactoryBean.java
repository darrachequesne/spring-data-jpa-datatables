package org.springframework.data.jpa.datatables.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;
import java.io.Serializable;

/**
 * {@link FactoryBean} creating DataTablesRepositoryFactory instances.
 * 
 * @author Damien Arrachequesne
 */
public class DataTablesRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
    extends JpaRepositoryFactoryBean<R, T, ID> {

  public DataTablesRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
    super(repositoryInterface);
  }

  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
    return new DataTablesRepositoryFactory<T, ID>(entityManager);
  }

  private static class DataTablesRepositoryFactory<T, ID extends Serializable>
      extends JpaRepositoryFactory {
    private final EntityManager entityManager;

    public DataTablesRepositoryFactory(EntityManager entityManager) {
      super(entityManager);
      this.entityManager = entityManager;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
      Class<?> repositoryInterface = metadata.getRepositoryInterface();
      if (DataTablesRepository.class.isAssignableFrom(repositoryInterface)) {
        return DataTablesRepositoryImpl.class;
      } else {
        return super.getRepositoryBaseClass(metadata);
      }
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(
        RepositoryMetadata metadata) {
      RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

      if (DataTablesRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {

        JpaEntityInformation<?, Serializable> entityInformation =
            getEntityInformation(metadata.getDomainType());

        DataTablesRepositoryImpl<?, ?> fragmentImplementation =
            new DataTablesRepositoryImpl<>(entityInformation, entityManager);

        return RepositoryComposition.RepositoryFragments.just(fragmentImplementation)
            .append(fragments);
      }

      return fragments;
    }
  }
}
