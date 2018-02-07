package org.springframework.data.jpa.datatables;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Spring JavaConfig configuration for general infrastructure.
 */
@Slf4j
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
    public SessionFactory entityManagerFactory(DataSource dataSource) throws Exception {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setPackagesToScan(Config.class.getPackage().getName());
        factory.setDataSource(dataSource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public AbstractEntityManagerFactoryBean entityManager(DataSource dataSource)
            throws SQLException {

        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setJpaVendorAdapter(jpaVendorAdapter);
        bean.setPackagesToScan(Config.class.getPackage().getName());
        bean.setDataSource(dataSource);

        return bean;
    }

    @Bean
    public JpaTransactionManager transactionManager(SessionFactory sessionFactory) throws Exception {
        return new JpaTransactionManager(sessionFactory);
    }

}
