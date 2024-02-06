package com.example.sumatra.config;

import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class JooqConfig {

    private static final Logger logger = LoggerFactory.getLogger(JooqConfig.class);


    @Autowired
    private DataSource dataSource;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String defaultSchema;


    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultDSLContext dsl() {
        DefaultDSLContext defaultDSLContext = new DefaultDSLContext(configuration());
        defaultDSLContext.setSchema(defaultSchema);
        return defaultDSLContext;
    }

    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.setSQLDialect(SQLDialect.POSTGRES);
        jooqConfiguration.set(connectionProvider());
        jooqConfiguration.set(new Settings().withRenderSchema(true));
        jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));
        return jooqConfiguration;
    }

    @Bean
    public JooqExceptionTranslator exceptionTransformer() {
        return new JooqExceptionTranslator();
    }
}
