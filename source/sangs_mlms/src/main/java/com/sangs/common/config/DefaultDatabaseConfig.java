package com.sangs.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@EnableTransactionManagement
@Configuration
public class DefaultDatabaseConfig {

	@Value("${mybatis.mapper-locations}")
	private String mapperLocation;

	@Value("${mybatis.config-location}")
	private String configLocation;
	
	
	@Value("${spring.default.datasource.driver-class-name}")
	private String driverName;
	@Value("${spring.default.datasource.jdbc-url}")
	private String jdbcUrl;
	@Value("${spring.default.datasource.username}")
	private String userName;
	@Value("${spring.default.datasource.password}")
	private String password;
	

	
	
	//@ConfigurationProperties(prefix = "spring.default.datasource")\
	@Bean(name = "defaultDataSource")
	public DataSource dataSource() throws Exception {
		
		//return DataSourceBuilder.create().type(HikariDataSource.class).build();
		HikariConfig hc = new HikariConfig();
		hc.setMaximumPoolSize(3);
		hc.setMinimumIdle(3);
		hc.setDriverClassName(driverName);
		hc.setJdbcUrl(jdbcUrl);
		hc.setUsername(userName);
		hc.setPassword(password);
		
		DataSource dataSource = new HikariDataSource(hc);
		
		return dataSource;
	}
	
 
	
	
	
	@Bean(name = "defaultSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("defaultDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		Resource myBatisConfig = new PathMatchingResourcePatternResolver().getResource(configLocation);
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources(mapperLocation));
		sqlSessionFactoryBean.setConfigLocation(myBatisConfig);
		//Properties properties = new Properties();
		//properties.put("mapUnderscoreToCamelCase", true);
		//sqlSessionFactoryBean.setConfigurationProperties(properties);
		return sqlSessionFactoryBean.getObject();
	}

	@Bean(name = "defualtSqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("defaultSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
		return new SqlSessionTemplate(sqlSessionFactory);
	}

 
}
