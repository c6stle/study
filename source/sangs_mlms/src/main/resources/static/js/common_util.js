

function nvl(str, replaceStr) {
	if(isEmpty(str)) {
		
		if(isEmpty(replaceStr))
			return "";
		else 
			return replaceStr;
	} else {
		return str;
	}		
}


function isEmpty(str) {
	if(!str)
		return true;
	else if(str == undefined)
		return true;
	else if(str == "")
		return true;
	else 
		return false;
}

// 마지막 컴마 remove
function fnRemoveLastComma(str) { 
	if(str != undefined && str != "")
		return str.substring(0, str.lastIndexOf(","));
	else 
		return "";
}


function fnCheckValid(id, itemNm) {
	if(isEmpty($(id).val())) {
		alert(itemNm + "는(은) 필수 입력 항목입니다.");
		$(id).focus();
		return false;
	} else {
		return true;
	}
}


// 금액 천단위 컴마 붙이기
function fnFormatNumber(n) {
	var str = "" + n;
	//format number 1000000 to 1,234,567
	return str.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}


function replaceAll(str, searchStr, replaceStr) {
	return str.split(searchStr).join(replaceStr);
}


// 초단위의 값을 입력 받아서 1h 10h 20m 23s 와 같은 형식의 문자열을 반환한다.  
function fnDateTextBySec(p_sec) {
	
	if(p_sec == 0 || p_sec == "0")
		return "0s";
	
	if(p_sec == undefined || p_sec == "")
		return "";
	
	var v_rtn = "";
	var v_sec = 0;
	var v_min = 0;
	var v_hour = 0;
	var v_day = 0;
	
	if(p_sec < 1) {
		return '1s';
	} else if(p_sec < 60) {
		return p_sec + 's';
	} else {
		v_min = Math.floor(p_sec / 60);
		v_sec = p_sec % 60;
		
		if(v_min < 60) { 
			v_rtn = v_min +  'm ' + v_sec + 's';
		} else {
			v_hour = Math.floor(v_min / 60);
			v_min = v_min % 60;
			
			if(v_hour < 24) {
				v_rtn = v_hour + 'h ' + v_min + 'm '+ v_sec + 's';
			} else {
				v_day = Math.floor(v_hour / 24);
				v_hour = v_hour % 24;
				
				v_rtn = v_day + 'd '+ v_hour + 'h ' + v_min + 'm ' + v_sec + 's';
			}
		}
	}
	return v_rtn;
}



$.datepicker.setDefaults({
   dateFormat: 'yy-mm-dd',
   prevText: '이전 달',
   nextText: '다음 달',
   monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
   monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
   dayNames: ['일', '월', '화', '수', '목', '금', '토'],
   dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
   dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
   showMonthAfterYear: true,
   yearSuffix: '년',
   showOn:"both", 
   buttonImage: "/img/icon/icon_calendar.png"
});

function fnRemoveDateDelim(str) {
	return str.replace(/-/g, "");
}

function fnEngFilterValue(obj) {
	obj.value = obj.value.replace(/[^a-zA-Z*$]/, '');
}

function fnNumberFilterValue(obj) {
	obj.value = obj.value.replace(/[^0-9*$]/, '');
}

/**	
	json 데이터를 sort 한다.
	var columnListByFormWrdList = sortJSON(data.tableColumnList, "dataLength", "asc");
*/
var sortJSON = function(data, key, type) {
	if (type == undefined) {
		type = "asc";
	}
	return data.sort(function(a, b) {
		var x = a[key];
		var y = b[key];
		if (type == "desc") {
			return x > y ? -1 : x < y ? 1 : 0;
		} else if (type == "asc") {
			return x < y ? -1 : x > y ? 1 : 0;
		}
	});
};
