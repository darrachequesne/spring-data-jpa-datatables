package org.springframework.data.jpa.datatables.qrepository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.datatables.repository.DataTablesContributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

import java.io.Serializable;

/**
 * {@link FactoryBean} creating DataTablesRepositoryFactory instances.
 * 
 * @author Damien Arrachequesne
 * @deprecated please use {@link org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean} instead
 */
public class QDataTablesRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
    extends JpaRepositoryFactoryBean<R, T, ID> {

  public QDataTablesRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
    super(repositoryInterface);
    setRepositoryFragmentsContributor(
            getRepositoryFragmentsContributor().andThen(DataTablesContributor.INSTANCE));
  }
}
