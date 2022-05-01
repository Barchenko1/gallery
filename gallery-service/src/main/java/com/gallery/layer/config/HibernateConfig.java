package com.gallery.layer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
public class HibernateConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setHibernateProperties(hibernateProperties());
        factoryBean.setDataSource(getDataSource());
        factoryBean.setPackagesToScan("com.init.gallery.modal");
        return factoryBean;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        return properties;
    }

    @Bean
    public DataSource getDataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(Objects.requireNonNull(env.getProperty("db.driver")));
        ds.setUrl(Objects.requireNonNull(env.getProperty("db.url")));
        ds.setUsername(Objects.requireNonNull(env.getProperty("db.user")));
        ds.setPassword(Objects.requireNonNull(env.getProperty("db.password")));
        return ds;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        return new HibernateTransactionManager(getSessionFactory().getObject());
    }
}
