package org.springframework.data.jpa.datatables;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.datatables.qrepository.QDataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = QDataTablesRepositoryFactoryBean.class,
    basePackages = "org.springframework.data.jpa.datatables.qrepository")
public class QConfig {

}
