package com.sangs.mlms.controller;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sangs.common.base.ControllerBase;
import com.sangs.fwk.annotation.SangsController;
import com.sangs.lib.support.utils.SangsStringUtil;
import com.sangs.mlms.common.MlmsConstant;


/**
 * MlmsConstant 의 변수의 값을 반환한다.
 * 
 * @author id.yoon
 *
 */
@SangsController("/mlms/constant")
public class MlmsConstantLoadController extends ControllerBase  {

	
	@ResponseBody
	@PostMapping("/load")
	public Map<String, Object> load(@RequestBody Map<String, Object> paramMap) throws Exception {
		
		String constantVar = SangsStringUtil.checkRequiredParamStr(paramMap, "constantVar", "constantVar");
		
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		String rtnVal = "";
		Class<?> cls = MlmsConstant.class;
		Field[] fields = cls.getDeclaredFields();
		for(Field field : fields) {
			logger.debug(field.getName() + "=" + field.get(null));
			if(constantVar.equals(field.getName())) {
				//System.out.println(String.valueOf(field.get(null)));
				rtnVal = String.valueOf(field.get(null));
				break;
			}
		}
		logger.debug("--return value : " + rtnVal);
		rtnMap.put("value", rtnVal);
		return rtnMap;
	}
	
	
}
