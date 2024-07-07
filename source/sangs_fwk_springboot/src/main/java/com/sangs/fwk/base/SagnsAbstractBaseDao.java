package com.sangs.fwk.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.sangs.fwk.common.SangsConstants;
import com.sangs.lib.support.domain.SangsMap;


/**
 * Abstract 공통 DAO<br><br>
 * 공통 DAO 를 사용하기 위해서는 현 Class 를 상속받아서 SqlSessionTemplate 를 셋업시켜줘야 한다. <br> 
 * 자식 class를 만들어 현 class 를 상속후 아래 와 같은 코드로 연결 시킨다. 
 * 
 *<pre>
 *<code>
 * 예시)
 *@Qualifier("defualtSqlSessionTemplate")
 *@Autowired
 *public SqlSessionTemplate defualtSqlSessionTemplate;
 *@PostConstruct
 *public void setSqlSessionTemplate() {
 * 	setSqlSessionTemplate(defualtSqlSessionTemplate);
 *}
 * 
 * </code>
 * </pre>
 * 
 * @author id.yoon
 *
 */
@Repository
public abstract class SagnsAbstractBaseDao {
	
	boolean logFlag = true;
	protected Logger logger = LoggerFactory.getLogger(SagnsAbstractBaseDao.class);
	
	protected SqlSessionTemplate fwkSqlSession;
	
	
	@Value("${fwk.dbms.type:}")
	private String dbmsType;
	
	@PostConstruct
	public abstract void setSqlSessionTemplate(); 
	
	/**
	 * 상속받은 class 에서  mybatis  SqlSessionTemplate 를 셋팅
	 * 
	 *<pre>
	 *<code>
	 * 예시)
	 *@Qualifier("defualtSqlSessionTemplate")
	 *@Autowired
	 *public SqlSessionTemplate defualtSqlSessionTemplate;
	 *@PostConstruct
	 *public void setSqlSessionTemplate() {
	 * 	setSqlSessionTemplate(defualtSqlSessionTemplate);
	 *}
	 * 
	 * </code>
	 * </pre>
	 * 
	 * @param sqlSession  SqlSessionTemplate
	 */
	protected void setSqlSessionTemplate(SqlSessionTemplate sqlSession) {
		this.fwkSqlSession = sqlSession;
	}
	
	public SqlSessionTemplate getSqlSessionTemplate() {
		return this.fwkSqlSession;
	}
	
	
	
	/**
	 * 
	 * 쿼리 수행 로그 출력여부를 setting 한다.<br>
	 *  - default true; 
	 * 
	 * @param logFlag false:로그출력을 하지 않는다.
	 */
	public void setLogFlag(boolean logFlag) {
		this.logFlag = logFlag;
	}
	
	public void commit() {
		try {
			this.fwkSqlSession.getConnection().commit();
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	public <R extends Map<String, Object>> void setCommonParam(R reqMap) {
		if(reqMap == null)
			reqMap = (R)new HashMap<String, Object>();
		reqMap.put("APP_DBMS_TYPE", dbmsType.toUpperCase());

		reqMap.put("APP_MNGR_SYS_SE_CD", SangsConstants.APP_MNGR_SYS_SE_CD);
		reqMap.put("APP_WWW_SYS_SE_CD", SangsConstants.APP_WWW_SYS_SE_CD);
		
		
		if(!reqMap.containsKey("APP_DBMS_TYPE")) {
			try {
				((SangsMap)reqMap).putOrg("APP_DBMS_TYPE", dbmsType.toUpperCase());
			} catch(Exception e) {
				logger.error("", e);
			}
		}
			
		
		
	}
	
	//public <T extends SangsMap, R extends Map<String, Object>> List<T> selectList(String sqlId, R reqMap) {
	// public <R extends Map<String, Object>> List<T> selectList(String sqlId, R reqMap) {
	
	/**
	 * mybatis를 통해서 목록을 조회
	 * 
	 * @param sqlId mybatis id([namespace].[query id])
	 * @param reqMap Mapt type parameter Map&lt;String, Object&gt;
	 * @return List&lt;SangsMap&gt;
	 */
	public <R extends Map<String, Object>> List<SangsMap> selectList(String sqlId, R reqMap) throws Exception {
		try {
			
			// 공통 파라미터 셋팅
			this.setCommonParam(reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("common Dao selectList paramemter = {} " , reqMap);
			
			List<SangsMap> list = fwkSqlSession.selectList(sqlId, reqMap); 
			
			if(logger.isDebugEnabled() && logFlag) {
				if(list != null) {
					for(int i = 0 ; i < list.size() ; i++) {
						logger.debug("{}", list.get(i));
					}
					//list.forEach((map) -> logger.debug("{}", map.toString()));
				}
			} 
			return list;
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * mybatis를 통해서 단건 조회 
	 * 
	 * @param sqlId mybatis id([namespace].[query id])
	 * @param reqMap Mapt type parameter Map&lt;String, Object&gt;
	 * @return SangsMap
	 */
	public <R extends Map<String, Object>> SangsMap selectOne(String sqlId, R reqMap) throws Exception {
		try {
			
			// 공통 파라미터 셋팅
			this.setCommonParam(reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("common Dao selectOne paramemter = {} " , reqMap);
			
			SangsMap info = fwkSqlSession.selectOne(sqlId, reqMap);
			
			if(logger.isDebugEnabled() && logFlag && info != null) 
				logger.debug("{}", info);
		
			return info;
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * mybatis 를 통해서 데이터 건수를 조회 
	 * 
	 * @param sqlId mybatis id([namespace].[query id])
	 * @param reqMap Mapt type parameter Map&lt;String, Object&gt;
	 * @return 조회된 count
	 */
	public < R extends Map<String, Object>> int selectCount(String sqlId, R reqMap) throws Exception {
		try {
			
			// 공통 파라미터 셋팅
			this.setCommonParam(reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("common Dao selectCount paramemter = {} " , reqMap);
			
			int count = (Integer)fwkSqlSession.selectOne(sqlId, reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("count = {} ", count);
		
			return count;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	/**
	 * mybatis 를 통해서 integer 타입을 조회한다. 
	 * 
	 * @param sqlId mybatis id([namespace].[query id])
	 * @param reqMap Mapt type parameter Map&lt;String, Object&gt;
	 * @return 조회된 count
	 */
	public < R extends Map<String, Object>> int selectInteger(String sqlId, R reqMap) throws Exception {
		try {
			
			// 공통 파라미터 셋팅
			this.setCommonParam(reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("common Dao selectCount paramemter = {} " , reqMap);
			
			int count = (Integer)fwkSqlSession.selectOne(sqlId, reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("integer = {} ", count);
		
			return count;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	

	
	/**
	 * mybatis를 통해서 update 수행  
	 *  
	 * @param sqlId mybatis id([namespace].[query id])
	 * @param reqMap Mapt type parameter Map&lt;String, Object&gt;
	 * @return update row count
	 */
	public <R extends Map<String, Object>> int update(String sqlId, R reqMap) throws Exception {
		try {
			
			// 공통 파라미터 셋팅
			this.setCommonParam(reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("common Dao update paramemter = {} " , reqMap);
			
			int updCnt = fwkSqlSession.update(sqlId, reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("updCnt : {} " , updCnt);
		
			return updCnt;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	/**
	 * mybatis를 통해서 insert 수행 
	 * 
	 * @param sqlId mybatis id([namespace].[query id])
	 * @param reqMap Mapt type parameter Map&lt;String, Object&gt;
	 */
	public <R extends Map<String, Object>> void insert(String sqlId, R reqMap) throws Exception {
		try {
			
			// 공통 파라미터 셋팅
			this.setCommonParam(reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("common Dao insert paramemter = {} " , reqMap);
		
			fwkSqlSession.insert(sqlId, reqMap);
		} catch(Exception e) {
			
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * mybatis를 통해서 delete 수행
	 * 
	 * @param sqlId mybatis id([namespace].[query id])
	 * @param reqMap Mapt type parameter Map&lt;String, Object&gt;
	 * @return delete row count 
	 */
	public <R extends Map<String, Object>> int delete(String sqlId, R reqMap) throws Exception {
		try {
			
			// 공통 파라미터 셋팅
			this.setCommonParam(reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("common Dao delete paramemter = {} " , reqMap);
			
			int updCnt = fwkSqlSession.delete(sqlId, reqMap);
			
			if(logger.isDebugEnabled() && logFlag) 
				logger.debug("updCnt : {} " , updCnt);
		
			return updCnt;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	

	
}
