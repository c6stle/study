package com.sangs.authmng.support;

import javax.annotation.PostConstruct;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.sangs.fwk.base.SagnsAbstractBaseDao;

/**
 * 
 * @Method Name : AuthMngCommonDao
 * @date : 2021. 9. 30
 * @author : ow.park
 * @history :
 * ----------------------------------------------------------------------------------
 * 변경일                        작성자                              변경내역
 * -------------- -------------- ----------------------------------------------------
 * 2021. 9. 30      ow.park              최초작성
 * ----------------------------------------------------------------------------------
 */
@Repository
public class AuthMngCommonDao extends SagnsAbstractBaseDao {
	
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
