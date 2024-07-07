package com.sangs.common.support;

import javax.annotation.PostConstruct;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.sangs.fwk.base.SagnsAbstractBaseDao;


@Repository
public class CommonDao extends SagnsAbstractBaseDao {
	
	@Qualifier("defualtSqlSessionTemplate")
	@Autowired
	public SqlSessionTemplate defualtSqlSessionTemplate;
	 
	
	@PostConstruct
	@Override
	public void setSqlSessionTemplate() {
		setSqlSessionTemplate(defualtSqlSessionTemplate);
	}
	
	public SqlSessionTemplate getSqlSessionTemplate() {
		return super.fwkSqlSession;
	}
	
}
