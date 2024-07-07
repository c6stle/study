package com.sangs.lib.support.utils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.sangs.lib.support.exception.SangsMessageException;


/**
 * Object 관련 Util
 * 
 * @author id.yoon
 * @since 2022.05.02
 * @version 1.0
 * @see
 *
 * <pre>
 * &lt;&lt; 개정이력(Modification Information) &gt;&gt;
 *   수정일               수정자              수정내용
 *  -------       --------    ---------------------------
 *  2022.05.02    id.yoon     최초 생성
 * </pre>
 */
public class SangsObjectUtil {

	/**
	 * Object to Map 
	 * @param obj Object
	 * @return Map
	 */
	public static Map<String, Object> convertToMap(Object obj) {
        try {
            if (Objects.isNull(obj)) {
                return Collections.emptyMap();
            }
            Map<String, Object> convertMap = new HashMap<>();

            Field[] fields = obj.getClass().getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                convertMap.put(field.getName(), field.get(obj));
            }
            return convertMap;
        } catch (Exception e) {
            throw new SangsMessageException(e);
        }
    }
	
	public static enum LIST_SORT_TYPE {ASC, DESC};
	
	 
	/**
	 * List 안의 Map 형태의 데이터를 Map 의 key 기준으로 정렬 
	 * 
	 * @param list 정렬할 list object
	 * @param sortKey 정렬할 key
	 * @param sortType LIST_SORT_TYPE.ASC or LIST_SORT_TYPE.DESC
	 */
	public static void sortList(List<Map<String, Object>> list, String sortKey, LIST_SORT_TYPE sortType) {
		list.sort(new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				
				Object obj1 = o1.get(sortKey);
				if(obj1 instanceof java.lang.String) {
					
					String o1Val = "";
					String o2Val = "";
					
					if(sortType == LIST_SORT_TYPE.ASC) {
						o1Val = (String)o1.get(sortKey);
						o2Val = (String)o2.get(sortKey);
					} else {
						o1Val = (String)o2.get(sortKey);
						o2Val = (String)o1.get(sortKey);
					}
					
					if(o1Val.length() == o2Val.length()) 
						return o1Val.compareTo(o2Val);
					else
						return o1Val.length() - o2Val.length();
				} else {
					int o1Val = -1;
					int o2Val = -1;
					
					if(sortType == LIST_SORT_TYPE.ASC) {
						o1Val = (Integer)o1.get(sortKey);
						o2Val = (Integer)o2.get(sortKey);
					} else {
						o1Val = (Integer)o2.get(sortKey);
						o2Val = (Integer)o1.get(sortKey);
					}
					
					
					if(o1Val == o2Val) return 0;
					else if(o1Val > o2Val) return 1;
					else return -1;
				}
			}
		});
	}
	
	
	 
}
