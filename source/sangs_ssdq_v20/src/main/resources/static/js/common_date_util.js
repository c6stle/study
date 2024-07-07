Date.prototype.format = function (f) {
    if (!this.valueOf()) return " ";
    var weekKorName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var weekKorShortName = ["일", "월", "화", "수", "목", "금", "토"];
    var weekEngName = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    var weekEngShortName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    var d = this;
    return f.replace(/(yyyy|yy|MM|dd|KS|KL|ES|EL|HH|hh|mm|ss|a\/p)/gi, function ($1) {
        switch ($1) {
            case "yyyy": return d.getFullYear(); // 년 (4자리)
            case "yy": return (d.getFullYear() % 1000).zf(2); // 년 (2자리)
            case "MM": return (d.getMonth() + 1).zf(2); // 월 (2자리)
            case "dd": return d.getDate().zf(2); // 일 (2자리)
            case "KS": return weekKorShortName[d.getDay()]; // 요일 (짧은 한글)
            case "KL": return weekKorName[d.getDay()]; // 요일 (긴 한글)
            case "ES": return weekEngShortName[d.getDay()]; // 요일 (짧은 영어)
            case "EL": return weekEngName[d.getDay()]; // 요일 (긴 영어)
            case "HH": return d.getHours().zf(2); // 시간 (24시간 기준, 2자리)
            case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2); // 시간 (12시간 기준, 2자리)
            case "mm": return d.getMinutes().zf(2); // 분 (2자리)
            case "ss": return d.getSeconds().zf(2); // 초 (2자리)
            case "a/p": return d.getHours() < 12 ? "오전" : "오후"; // 오전/오후 구분
            default: return $1;
        }
    });
};

String.prototype.string = function (len) { var s = '', i = 0; while (i++ < len) { s += this; } return s; };
String.prototype.zf = function (len) { return "0".string(len - this.length) + this; };
Number.prototype.zf = function (len) { return this.toString().zf(len); };

// format yyyy-MM-dd HH:mm:ss
function getToday(format) {
	var _today = new Date(); 
	return _today.format(format);
}

/*
 * datepicker의 조회조건의 from을 1년전 부터 오늘까지로 셋팅해줌
 */
function fnSet1YearFromTo(fromDt, toDt) {
	var fDt = $(fromDt).val();
	var tDt = $(toDt).val();
	
	
	if($(fromDt).val() == "" &&  $(toDt).val() == "") { 
		let today = new Date();
		let oneYearAgo = new Date();


		//oneYearAgo.setDate(oneYearAgo.getDate()-1); //하루 치 테스트 용
		oneYearAgo.setFullYear(oneYearAgo.getFullYear()-1);
		
		$(fromDt).datepicker().datepicker("setDate", oneYearAgo);
		$(toDt).datepicker().datepicker("setDate", today);
	} else {
		$(fromDt).datepicker().datepicker("setDate", fDt);
		$(toDt).datepicker().datepicker("setDate", tDt);
	}
}

/*
 * 날짜 조회조건 체크
 */
function fnSearcDateChk(startDt, endDt){
	var isChk = true;
	
	
	if(startDt.trim() != ""){
		if(endDt.trim() == ""){
			alert("종료일자를 선택해 주세요.");
			isChk = false;
		}
	}
	
	if(endDt.trim() != ""){
		if(startDt.trim() == ""){
			alert("시작일자를 선택해 주세요.");
			isChk = false;
		}
	}
	
	if(startDt.trim() != "" && endDt.trim() != ""){
		if(startDt.trim() > endDt.trim()){
			alert("시작일자는 종료일자보다 빠를 수 없습니다.");
			isChk = false;
		}
	}
	return isChk;
}