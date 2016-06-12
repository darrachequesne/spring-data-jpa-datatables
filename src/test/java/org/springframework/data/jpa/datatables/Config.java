package org.springframework.data.jpa.datatables;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring JavaConfig configuration for general infrastructure.
 * 
 * @author Damien Arrachequesne
 */
@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
public class Config {

  @Bean
  public DataSource dataSource() throws SQLException {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
  }

  @Bean
  public PlatformTransactionManager transactionManager() throws SQLException {
    return new JpaTransactionManager();
  }

  @Bean
  public AbstractEntityManagerFactoryBean entityManagerFactory() throws SQLException {

    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setDatabase(Database.H2);
    jpaVendorAdapter.setGenerateDdl(true);

    LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
    bean.setJpaVendorAdapter(jpaVendorAdapter);
    bean.setPackagesToScan(Config.class.getPackage().getName());
    bean.setDataSource(dataSource());

    Properties jpaProperties = new Properties();
    jpaProperties.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
    jpaProperties.setProperty(Environment.HBM2DDL_IMPORT_FILES, "init.sql");
    jpaProperties.setProperty(Environment.HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR,
        MultipleLinesSqlCommandExtractor.class.getName());
    bean.setJpaProperties(jpaProperties);

    return bean;
  }

  @Bean
  public SessionFactory sessionFactory() throws SQLException {
    return ((HibernateEntityManagerFactory) entityManagerFactory().getObject()).getSessionFactory();
  }

}
