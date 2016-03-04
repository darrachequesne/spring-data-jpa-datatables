package org.springframework.data.jpa.datatables.qrepository;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

/**
 * {@link FactoryBean} creating DataTablesRepositoryFactory instances.
 * 
 * @author Damien Arrachequesne
 */
public class QDataTablesRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, ID> {

	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new DataTablesRepositoryFactory<T, ID>(entityManager);
	}

	private static class DataTablesRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

		private final EntityManager entityManager;

		public DataTablesRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
			this.entityManager = entityManager;
		}

		@SuppressWarnings({ "unchecked" })
		protected Object getTargetRepository(RepositoryMetadata metadata) {
			JpaEntityInformation<T, ID> entityInformation = (JpaEntityInformation<T, ID>) getEntityInformation(
					metadata.getDomainType());
			Class<?> repositoryInterface = metadata.getRepositoryInterface();
			return QDataTablesRepository.class.isAssignableFrom(repositoryInterface)
					? new QDataTablesRepositoryImpl<T, ID>(entityInformation, entityManager)
					: super.getTargetRepository(metadata);
		}

		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return QDataTablesRepositoryImpl.class;
		}
	}
}
