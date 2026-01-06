package org.springframework.data.jpa.datatables.qrepository;

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
public class QDataTablesRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
    extends JpaRepositoryFactoryBean<R, T, ID> {

  public QDataTablesRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
    super(repositoryInterface);
  }

  @Override
  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
    return new DataTablesRepositoryFactory(entityManager);
  }

  private static class DataTablesRepositoryFactory extends JpaRepositoryFactory {
    private final EntityManager entityManager;

    DataTablesRepositoryFactory(EntityManager entityManager) {
      super(entityManager);
      this.entityManager = entityManager;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
      Class<?> repositoryInterface = metadata.getRepositoryInterface();
      if (QDataTablesRepository.class.isAssignableFrom(repositoryInterface)) {
        return QDataTablesRepositoryImpl.class;
      } else {
        return super.getRepositoryBaseClass(metadata);
      }

    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(
            RepositoryMetadata metadata) {
      RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata);

      if (QDataTablesRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
        JpaEntityInformation<?, Serializable> entityInformation =
                getEntityInformation(metadata.getDomainType());

        QDataTablesRepositoryImpl<?, ?> fragmentImplementation =
                new QDataTablesRepositoryImpl<>(entityInformation, entityManager);

        return RepositoryComposition.RepositoryFragments
                .just(fragmentImplementation)
                .append(fragments);
      }

      return fragments;
    }
  }
}
