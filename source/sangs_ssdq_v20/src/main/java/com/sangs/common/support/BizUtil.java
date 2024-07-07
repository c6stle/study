package com.sangs.common.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sangs.lib.support.utils.SangsStringUtil;

public class BizUtil {

	public enum DbmsDataTypeGroup {
		NUMERICTYPE("NUMERIC",
				Arrays.asList(
						
						"INT", "INT2", "INT4", "INT8"
						, "BINARY_FLOAT", "TINYINT", "INTEGER", "NUMBER"
						, "FLOAT4", "FLOAT8", "NUMERIC", "BIGINT"
						, "DECIMAL", "BINARY_DOUBLE", "SMALLINT", "REAL"
						, "DOUBLE", "BIT", "SHORT", "MONETARY"
						, "MEDIUMINT"
						
						)
					)
		, CHARACTERTYPE("CHARACTER",
				Arrays.asList(
						
						"VARCHAR", "VARCHAR2", "CHAR", "STRING"
						, "NVARCHAR", "NCHAR", "NVARCHAR2", "VARBINARY"
						
						)
					)

		, DATETYPE("DATE",
				Arrays.asList(
						
						"DATE", "TIMESTAMP", "TIME", "DATETIME"
						, "DATETIME2", "SMALLDATETIME", "YEAR",	"INTERVAL_YEAR"
						, "INTERVAL_DAY", "DATETIME1", "DATETIMEOFFSET"
						, "TIMESTAMP(6)"
						
						)
					)
		
		, LOBTYPE("LARGEOBJECT",
				Arrays.asList(
						"BLOB", "CLOB", "BFILE"
						)
					)
		
		, EMPTY("NULL", Collections.emptyList());

		private String typeNm;
		private List<String> dataTypeList;

		DbmsDataTypeGroup(String typeNm, List<String> dataTypeList) {
			this.typeNm = typeNm;
			this.dataTypeList = dataTypeList;
		}

		public static DbmsDataTypeGroup findByDataType(String dataType) {
			return Arrays.stream(DbmsDataTypeGroup.values())
					.filter(dbmsDataType -> dbmsDataType.hasDataType(dataType))
					.findAny()
					.orElse(EMPTY);
		}

		public boolean hasDataType(String dataType) {
			return dataTypeList.stream().anyMatch(type -> type.equals(dataType));
		}

		public String getTypeNm() {
			return typeNm;
		}
		
		public List<String> getDataTypeList() {
			return dataTypeList;
		}
	}

	
	// DBMS 종류 (db에 들어 있는 명칭CMMN_PRJCT_DBMS.DBMS_NM)
	public static enum DBMS_TYPE_NAME {
		ORACLE
		,CSV
		,MYSQL
		,TIBERO
		,MSSQL
		,CUBRID
		,POSTGRESQL
		,ALTIBASE
		,DB2
		,MONGODB
		,MARIADB
		,VOLTDB
	}
	
	
	public static boolean isEqualDbms(DBMS_TYPE_NAME dbmsTypeName, String strDbmsTypeName) {
		if(SangsStringUtil.isEmpty(strDbmsTypeName))
			return false;
		
		if(dbmsTypeName.toString().equals(strDbmsTypeName.toUpperCase()))
			return true;
		else 
			return false;
	}
	
	
	
	
	
	/**
	 * 데이터 타입과 길이로 VARCHAR(10) 와 같이 표현 
	 * @param dataType
	 * @param dataLength
	 * @return
	 */
	public static String getDataTypeLengthTxt(String dbmsNm, String dataType, String dataLength, String dataPrecision, String dataScale) {
	
		if(SangsStringUtil.isEmpty(dbmsNm))
			return "";
		
		String dataTypeLength = "";
		String upperDataType = dataType.toUpperCase();
		
		if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.TIBERO, dbmsNm) || BizUtil.isEqualDbms(DBMS_TYPE_NAME.ORACLE, dbmsNm)) {
			if("NUMBER".equals(upperDataType) || "FLOAT".equals(upperDataType)) {
				if(!SangsStringUtil.isEmpty(dataPrecision) && (SangsStringUtil.isEmpty(dataScale) || "0".equals(dataScale) ) )
					dataTypeLength = dataType + "("+dataPrecision+")";
				else if(!SangsStringUtil.isEmpty(dataPrecision) && !SangsStringUtil.isEmpty(dataScale)) 
					dataTypeLength = dataType + "("+dataPrecision+","+dataScale+")";
				else
					dataTypeLength = dataType;
			} else if("DATE".equals(upperDataType) || (upperDataType).indexOf("LOB") >= 0 || "LONG".equals(upperDataType)) {
				dataTypeLength = dataType;
			} else if((upperDataType).indexOf("TIMESTAMP") >= 0 ) {
				dataTypeLength = "TIMESTAMP";
			} 
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.MYSQL, dbmsNm) || BizUtil.isEqualDbms(DBMS_TYPE_NAME.MARIADB, dbmsNm)) {
			if("DECIMAL".equals(upperDataType) || "FLOAT".equals(upperDataType) || "DOUBLE".equals(upperDataType)) {
				//System.out.println("---->" + dataPrecision);
				if( ("DECIMAL".equals(upperDataType) && "10".equals(dataPrecision) && "0".equals(dataScale))
					|| ("FLOAT".equals(upperDataType) && "12".equals(dataPrecision) && SangsStringUtil.isEmpty(dataScale))
					|| ("DOUBLE".equals(upperDataType) && "22".equals(dataPrecision) && SangsStringUtil.isEmpty(dataScale))) {
					// 생성시 사이즈 안넣었을때  
					dataTypeLength = dataType;
				} else {
					if(!SangsStringUtil.isEmpty(dataPrecision) && (SangsStringUtil.isEmpty(dataScale) || "0".equals(dataScale) ) )
						dataTypeLength = dataType + "("+dataPrecision+")";
					else if(!SangsStringUtil.isEmpty(dataPrecision) && !SangsStringUtil.isEmpty(dataScale)) 
						dataTypeLength = dataType + "("+dataPrecision+","+dataScale+")";
					else
						dataTypeLength = dataType;
				}
			} else if("DATE".equals(upperDataType) || "DATETIME".equals(upperDataType) || "TIME".equals(upperDataType)
					|| (upperDataType).indexOf("LOB") >= 0 || (upperDataType).indexOf("TEXT") >= 0 
					|| "INT".equals(upperDataType)  || "INTEGER".equals(upperDataType)
					) {
				dataTypeLength = dataType;
			} else if((upperDataType).indexOf("TIMESTAMP") >= 0 ) {
				dataTypeLength = "TIMESTAMP";
			} 
			
		} else if(BizUtil.isEqualDbms(DBMS_TYPE_NAME.CUBRID, dbmsNm)) {
			
			if("NUMERIC".equals(upperDataType)) {
				System.out.println("====dataLength:" +dataLength +"|" + dataPrecision +"|" + dataScale);
				
				
				if(dataLength.equals("15") && dataScale.equals("0") ) {// 생성시 사이즈 안넣었을때  
					dataTypeLength = dataType;
				} else {
					if(!SangsStringUtil.isEmpty(dataPrecision) && (SangsStringUtil.isEmpty(dataScale) || "0".equals(dataScale) ) )
						dataTypeLength = dataType + "("+dataPrecision+")";
					else if(!SangsStringUtil.isEmpty(dataPrecision) && !SangsStringUtil.isEmpty(dataScale)) 
						dataTypeLength = dataType + "("+dataPrecision+","+dataScale+")";
					else
						dataTypeLength = dataType;
				}
			} else if("DATE".equals(upperDataType) || "DATETIME".equals(upperDataType) || "TIME".equals(upperDataType)
					|| (upperDataType).indexOf("LOB") >= 0 || (upperDataType).indexOf("TEXT") >= 0 
					|| "INT".equals(upperDataType)  || "INTEGER".equals(upperDataType)
					|| "BIGINT".equals(upperDataType) || "SMALLINT".equals(upperDataType)
					|| "DECIMAL".equals(upperDataType) || "FLOAT".equals(upperDataType) || "DOUBLE".equals(upperDataType)
					) {
				dataTypeLength = dataType;
			} else if((upperDataType).indexOf("TIMESTAMP") >= 0 ) {
				dataTypeLength = "TIMESTAMP";
			} 
		}
		
		// default
		if(SangsStringUtil.isEmpty(dataTypeLength))
			dataTypeLength = dataType + "(" + dataLength + ")";
		
		dataTypeLength = dataTypeLength.toUpperCase();
		
		return dataTypeLength;
	}
	
	public static String getRequestURI() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return req.getRequestURI();
	}
	
	
}
