package com.sangs.common.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sangs.lib.support.utils.SangsStringUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = "com.sangs.dq.mapper")
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
	
	@Value("${spring.default.datasource.hikari.maximum-pool-size:3}")
	private String maximumPoolSize;
	@Value("${spring.default.datasource.hikari.minimum-idle:3}")
	private String minimumIdle;
	
	
	@Value("${spring.default.datasource.hikari.idle-timeout:}")
	private String idleTimeout;
	@Value("${spring.default.datasource.hikari.connection-timeout:}")
	private String connectionTimeout;
	@Value("${spring.default.datasource.hikari.max-lifetime:}")
	private String maxLifetime;
	
	
	@Value("${app.mariaDB4j.databaseName:}")
	private String databaseName;
	
 
	// remote db case 
	//@ConfigurationProperties(prefix = "spring.default.datasource")\
	@Bean(name = "defaultDataSource")
	@Primary
	public DataSource dataSource() throws Exception {
		
		//return DataSourceBuilder.create().type(HikariDataSource.class).build();
		HikariConfig hc = new HikariConfig();
		hc.setMaximumPoolSize(Integer.parseInt(maximumPoolSize));
		hc.setMinimumIdle(Integer.parseInt(minimumIdle));
		hc.setDriverClassName(driverName);
		hc.setJdbcUrl(jdbcUrl);
		hc.setUsername(userName);
		hc.setPassword(password);
		if(!SangsStringUtil.isEmpty(idleTimeout))
			hc.setIdleTimeout(Integer.parseInt(idleTimeout));
		if(!SangsStringUtil.isEmpty(connectionTimeout))
			hc.setConnectionTimeout(Integer.parseInt(connectionTimeout));
		if(!SangsStringUtil.isEmpty(maxLifetime))
			hc.setMaxLifetime(Integer.parseInt(maxLifetime));
		
		DataSource dataSource = new HikariDataSource(hc);
		
		return dataSource;
	}
 
	
	
 /*
	// inner database(embed db)
	@Bean
	@Primary
    public MariaDB4jSpringService mariaDB4j() {
		MariaDB4jSpringService svc = new MariaDB4jSpringService();
		svc.getConfiguration().addArg("--lower_case_table_names=1");
		//svc.getConfiguration().setSecurityDisabled(false);
		return svc;
    }
	
	@Bean(name = "defaultDataSource")
	@Primary
	@DependsOn("mariaDB4j")
    DataSource dataSource(MariaDB4jSpringService mariaDB4jSpringService) throws ManagedProcessException {
		
		mariaDB4jSpringService.getDB().createDB(databaseName);
		//System.out.println("userName : " + userName);
		return DataSourceBuilder
			.create()
			//.type(HikariDataSource.class)
			.username(userName)
			.password(password)
			//.url(datasourceUrl + "&" + databaseName + "&serverTimezone=UTC")
			.url(jdbcUrl)
			.driverClassName(driverName)
			.build();
    }
  */
	
 
	
	
	
	@Bean(name = "defaultSqlSessionFactory")
	@Primary
	public SqlSessionFactory sqlSessionFactory(@Qualifier("defaultDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		Resource myBatisConfig = new PathMatchingResourcePatternResolver().getResource(configLocation);
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources(mapperLocation));
		sqlSessionFactoryBean.setConfigLocation(myBatisConfig);
		Properties properties = new Properties();
		properties.put("mapUnderscoreToCamelCase", true);
		sqlSessionFactoryBean.setConfigurationProperties(properties);
		return sqlSessionFactoryBean.getObject();
	}

	@Bean(name = "defualtSqlSessionTemplate")
	@Primary
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("defaultSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		sqlSessionFactory.getConfiguration().setMapUnderscoreToCamelCase(true);
		return new SqlSessionTemplate(sqlSessionFactory);
	}

 
}
