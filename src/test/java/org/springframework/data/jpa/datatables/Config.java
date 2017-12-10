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
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring JavaConfig configuration for general infrastructure.
 */
@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class,
    basePackages = { "org.springframework.data.jpa.datatables.model", "org.springframework.data.jpa.datatables.repository" })
public class Config {

  @Bean
  @Profile({"default", "h2"})
  public DataSource dataSource_H2() throws SQLException {
    return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
  }

  @Bean
  @Profile("mysql")
  public DataSource dataSource_MySQL() throws SQLException {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://127.0.0.1/test");
    dataSource.setUsername("root");
    dataSource.setPassword("");
    return dataSource;
  }

  @Bean
  @Profile("pgsql")
  public DataSource dataSource_PostgreSQL() throws SQLException {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl("jdbc:postgresql://127.0.0.1/test");
    dataSource.setUsername("postgres");
    dataSource.setPassword("");
    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() throws SQLException {
    return new JpaTransactionManager();
  }

  @Bean
  public AbstractEntityManagerFactoryBean entityManagerFactory(DataSource dataSource)
      throws SQLException {

    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setGenerateDdl(true);

    LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
    bean.setJpaVendorAdapter(jpaVendorAdapter);
    bean.setPackagesToScan(Config.class.getPackage().getName());
    bean.setDataSource(dataSource);

    Properties jpaProperties = new Properties();
    jpaProperties.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
    jpaProperties.setProperty(Environment.HBM2DDL_IMPORT_FILES, "init.sql");
    jpaProperties.setProperty(Environment.HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR,
        MultipleLinesSqlCommandExtractor.class.getName());
    bean.setJpaProperties(jpaProperties);

    return bean;
  }

  @Bean
  public SessionFactory sessionFactory(AbstractEntityManagerFactoryBean entityManagerFactory)
      throws SQLException {
    return ((HibernateEntityManagerFactory) entityManagerFactory.getObject()).getSessionFactory();
  }

}
