// 페이지 이동 이벤트 시 현재 URL 갱신
window.onload = function() {
	var refreshPath = parent.document.getElementById("refreshPath");
	$(refreshPath).val(window.location.pathname + window.location.search);
};
 
function refreshEvent() {
	
	var refreshPath = $("#refreshPath", parent.document).val();
	
 	if(event.keyCode == 116) {
		$("#main_content", parent.document).attr("src", refreshPath);
		return false;
	} else if (event.ctrlKey && (event.keyCode == 78 || event.keyCode == 82)) {
		$("#main_content", parent.document).attr("src", refreshPath);
		return false;
	}
}
document.onkeydown = refreshEvent;
 
// 공통 파일 다운로드 
function fnCommFileDown(fileType, refId) {
	$.ajax({
		url: "/mlms/commdown/checkFile"
		, method : "POST"
		, contentType: 'application/json'
		, data: JSON.stringify({
			fileType: fileType
			, refId: refId
		})
		, success: function(data){
			if(data.resultCd != "OK") {
				alert(data.resultMsg);	
			} else {
				var paramData = { 
					fileType: fileType
					, refId: refId 
     			}

			    $.ajax({
			        url: '/mlms/commdown/download',
			        method: 'POST',
			        xhrFields: {
			            responseType: 'arraybuffer'
			        },
			        data: $.param(paramData) // a=1&b=2&c=3 방식
			    }).done(function(data, textStatus, jqXhr) {
			        if (!data) {
			            return;
			        }
			        try {
				
			            var blob = new Blob([data], { type: jqXhr.getResponseHeader('content-type') });
			            var fileName = getFileName(jqXhr.getResponseHeader('content-disposition'));
			            fileName = decodeURI(fileName);
			 
			            if (window.navigator.msSaveOrOpenBlob) { // IE 10+
			                window.navigator.msSaveOrOpenBlob(blob, fileName);
			            } else { // not IE
			                var link = document.createElement('a');
			                var url = window.URL.createObjectURL(blob);
			                link.href = url;
			                link.target = '_self';
			                if (fileName) link.download = fileName;
			                document.body.append(link);
			                link.click();
			                link.remove();
			                window.URL.revokeObjectURL(url);
			            }
			        } catch (e) {
			            console.error(e)
			        }
			    });
			}
				
		}
	});
}
function fnCommFileDownByUrl(url, fileName, paramObj) {

	 $.ajax({
        url: url,
        method: 'POST',
		contentType : 'application/json',
        xhrFields: {
            responseType: 'arraybuffer'
        },
        data: paramObj 
    }).done(function(data, textStatus, jqXhr) {
        if (!data) {
            return;
        }
        try {
	
            var blob = new Blob([data], { type: jqXhr.getResponseHeader('content-type') });
            //var fileName = getFileName(jqXhr.getResponseHeader('content-disposition'));
            fileName = decodeURI(fileName) + "_" + getToday("yyyyMMddHHmmss");
 
            if (window.navigator.msSaveOrOpenBlob) { // IE 10+
                window.navigator.msSaveOrOpenBlob(blob, fileName);
            } else { // not IE
                var link = document.createElement('a');
                var url = window.URL.createObjectURL(blob);
                link.href = url;
                link.target = '_self';
                if (fileName) link.download = fileName;
                document.body.append(link);
                link.click();
                link.remove();
                window.URL.revokeObjectURL(url);
            }
        } catch (e) {
            console.error(e)
        }
    });
}





function getFileName(contentDisposition) {
    var fileName = contentDisposition
        .split(';')
        .filter(function(ele) {
            return ele.indexOf('filename') > -1
        })
        .map(function(ele) {
            return ele
                .replace(/"/g, '')
                .split('=')[1]
        });
    return fileName[0] ? fileName[0] : null
}


/**
	constants 의 값을 ajax로 가져온다. 
	fnGetMlmsConstantsVal("RESOURCE_DATASET_BASE_PATH", function(data) {
		$("#upl_masterForm").find("input[name=subDir]").val(data.value);
	});
 */
function fnGetMlmsConstantsVal(constantVar, callbackFunc) {
	$.ajax({
		url: "/mlms/constant/load"
		, method : "POST"
		, contentType : 'application/json'
		, data: JSON.stringify({
			constantVar : constantVar
		})
		, success: function(data){
			callbackFunc(data);
		}
	});
}
/**
	constants 의 값을 ajax로 가져온다. 
	fnGetMetaConstantsVal("RESOURCE_DATASET_BASE_PATH", function(data) {
		$("#upl_masterForm").find("input[name=subDir]").val(data.value);
	});
 */
function fnGetMetaConstantsVal(constantVar, callbackFunc) {
	$.ajax({
		url: "/meta/constant/load"
		, method : "POST"
		, contentType : 'application/json'
		, data: JSON.stringify({
			constantVar : constantVar
		})
		, success: function(data){
			callbackFunc(data);
		}
	});
}


// 결과 조회 팝업
function fnOpenCmmnResultPop(lrnExcnSn, testHistSn) { 
	
	if(!lrnExcnSn || lrnExcnSn == undefined) {
		alert("학습수행순번이 필요 합니다. ");
		return ;
	}
	var tempTestHistSn = '';
	if(testHistSn && testHistSn != undefined)
		tempTestHistSn = testHistSn;
		
	
	window.open('/open/mlms/result/result_info_pop?lrnExcnSn='+lrnExcnSn+'&testHistSn='+tempTestHistSn, '_blank', 'width=1400, height=1000, resize=yes');
}





function fnSelectedTableValue(trId, nameVlaue, targetValue){
	var rtnValue = null;
	var selectObj = $(trId).find("input[name="+nameVlaue+"]");
	
	$.each(selectObj, function(index, obj) {
		
		if(obj.value === targetValue){
			var targetTr = $(obj).parent('tr');
			$(trId).find('td').css("background-color", "#fff");
			$(targetTr).find('td').css("background-color", "aliceblue");
			$(trId).attr("data-selectedtr", "N");
			$(targetTr).attr("data-selectedtr", "Y");
			
			rtnValue = obj.value;
		}	
	});
	
	return rtnValue;
}


function fnSelectTrEvent(trId, callback) {
	/*
	$(trId).on("click", function() {
		$(trId).css("background-color", "#fff");
		$(this).css("background-color", "aliceblue");
		
		$(trId).attr("data-selectedtr", "N");
		
		$(this).attr("data-selectedtr", "Y");
		callback($(this));
	});
	*/
	
	// fwk_data_row tr만 클릭 이벤트 적용
	$.each($(trId), function(key, value) {
		if($(value).hasClass('fwk_data_row')){
			$(value).on("click", function() {
				$(trId).find("td").css("background-color", "#fff");
				$(value).find("td").css("background-color", "aliceblue");
				$(trId).attr("data-selectedtr", "N");
				$(value).attr("data-selectedtr", "Y");
				callback($(value));
			});
		}
	});
}

function fnClearSelectTrEvent(trId) { 
	$(trId).attr("data-selectedtr", "N");
	$(trId).find("td").css("background-color", "#fff");
}


// 테이블  row 선택 히든 값 조회
function fnSelectTrHiddenValue(tableId, inputName){
	return $("#"+tableId).find("tr[data-selectedtr=Y]").find("input[name="+inputName+"]").val();
}

// 테이블 전체 row 중 체크 여부 / 선택된 obj 리턴
function fnIsSelectTrChk(tableId){
	var selectdTable = $("#"+tableId).find('tr');
	
	var isChk = false;
	var retObj = {};
	
	$.each(selectdTable, function(index, value) {
		/*
		 	$(this).attr("data-selectedtr", "Y"); 와 같이 data value 값을 변경 할 경우
		  	$(this).data("selectedtr"); 로 현재 값을 가져올 수 없다. 화면상에는 변경 된 상태로 보임 
		  	$(this).attr("data-selectedtr"); 로 값을 가져오도록 한다.
		 */
		if($(value).attr("data-selectedtr") == 'Y'){
			retObj.isChk = true;
			retObj.obj = $(value);
		}
	});	
	
	return retObj;
}

function fnShowFullModal(id) {
	popOpenAndDim(id, true);
	var viewHeight = $(parent.window).innerHeight();
	var viewWidth = $(window).innerWidth();
	$("#"+id).css("height", viewHeight - 160 + "px");
	$("#"+id).css("width", viewWidth - 80 + "px");
	$("#"+id).fnModalCenterPosition();
	$(window).on("resize", function() {
		$("#"+id).fnModalCenterPosition();  
	});
	
}

// modal layer popup open
function fnShowModal(id) {
	
	popOpenAndDim(id, true);
	$("#"+id).draggable({scroll : false, containment : 'window', handle : '.pop_header'	});
	$("#"+id).fnModalCenterPosition();
	$(window).on("resize", function() {
		$("#"+id).fnModalCenterPosition();  
	});
}


jQuery.fn.fnModalCenterPosition = function () {
    this.css("position","absolute");
    //this.css("top", Math.max(0, (($(parent.window).height() - $(this).outerHeight()) / 2) + $(parent.window).scrollTop()) + "px");
	this.css("top", (Math.max(0, (($(parent.window).height() - $(this).outerHeight()) / 2) + $(parent.window).scrollTop()) -25) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) + $(window).scrollLeft()) + "px");
    return this;
}
// modal layer popup close
function fnCloseModal(id) {
	popCloseAndDim(id, true);
	setFrameHeight();
}

// modal layer outer click close
/*
$(document).mouseup(function (e){
	var olayerPopup = $(".popup.layer");
	if(olayerPopup.has(e.target).length === 0){
		olayerPopup.removeClass('on');
		dimRemove();
	}
});
*/


// 디자인된 select box의 id값 이벤트로 변경시 display 되는 text 변경 해준다.  
function fnApplySelectIdLabel(selectboxId) {
	/* 2022.10.21 select 박스 기본 사용으로 변경
	//셀렉트 박스
	var selectTarget = $('#'+selectboxId+'');
	selectTarget.each(function(){
		var select_name = $(this).find('option:selected').text();
		if($(this).siblings('label').length == 0) {
			// select box 아래 label 태그가 없으면 생성
			$(this).closest("div").append("<label>"+select_name+"</label>");
		} else {
			$(this).siblings('label').text(select_name);
		}
	});
	
	$("#"+selectboxId+"").change(function (){
		fnApplySelectIdLabel(selectboxId);
	});
	*/
}

// 디자인된 select box의 이벤트로 변경시 display 되는 text 변경 해준다.  
function fnApplySelectLabel() {
	/* 2022.10.21 select 박스 기본 사용으로 변경
	//셀렉트 박스
	var selectTarget = $('.selectbox select');
	selectTarget.each(function(){
		var select_name = $(this).children('option:selected').text();
		if($(this).siblings('label').length == 0) {
			// select box 아래 label 태그가 없으면 생성
			$(this).closest("div").append("<label>"+select_name+"</label>");
		} else {
			$(this).siblings('label').text(select_name);
		}
	});
	
	$(".selectbox").change(function (){
		fnApplySelectLabel();
	});
	*/
}
// 디자인된 select box 보이는 text 를 적용시켜준다 
function fnApplySelectLabelDisp(selectboxId) {
	/* 2022.10.21 select 박스 기본 사용으로 변경
	var select_name = $("#"+selectboxId).children('option:selected').text();
	if($("#"+selectboxId).siblings('label').length == 0) {
		// select box 아래 label 태그가 없으면 생성
		$("#"+selectboxId).closest("div").append("<label>"+select_name+"</label>");
	} else {
		$("#"+selectboxId).siblings('label').text(select_name);
	}
	*/
}
// 디자인된 selectbox 의 값을 변경 한다. 
function fnChangeSelectVal(selectboxId, value) {
	/* 2022.10.21 select 박스 기본 사용으로 변경	
	if(!value || value == undefined || value == "")
		value = "''";
	$("#"+selectboxId).find("option[value="+value+"]").prop("selected", true);
	// 디자인된 select box 보이는 text 를 적용시켜준다
	fnApplySelectLabelDisp(selectboxId);
	*/
}



// 데이터셋 정보 조회 공통 팝업 open dst : L 학습데이터셋, S: 원천 데이터셋
function fnViewDatasetPop(dst, itemSn) {
	window.open('/view/dataset/viewDatasetInfoPop?dst=' + dst + '&itemSn=' + itemSn, '_blank', 'width=1400, height=1000, resize=yes');
}

function fnGetUseYnNm(str) {
	if(str == undefined || str == "")
		return "";
	if("Y" == str.toUpperCase())
		return "사용";
	else if("N" == str.toUpperCase())
		return "미사용";
	else 
		return "";
}

function fnGetNullChk(str) {
	if(str == undefined || str == ""){
		return " - ";
	} else {
		return str;
	}	
}

// 로그아웃
function fnLogoutProc() {
	if(!confirm("로그아웃 하시겠습니까?"))
		return;
	location.href = "/login/logoutProc";	
}

//접속중인 프로젝트 정보 변경시 로그아웃
function fnLogout() {
	alert("프로젝트 정보가 변경되었습니다 재로그인이 필요합니다.");
	location.href = "/login/logoutProc";	
}

// 표준세트 체크
function fnChkStdSet(stdSetSn){
	
	if (stdSetSn =="") {
		alert("해당 기능을 사용하기 위해서는 표준세트 등록이 필요합니다.");
		return false;
	} else {
		return true;
	}
}


function fnOpenStdDataSearchPop() {
	window.open('/open/meta/stddicary/std_item_search_pop','_stdDataSearchPop', 'width=1400, height=700, resize=yes');
}

function fnOpenDbmsSchemaSearchPop() {
	window.open('/open/dq/analysis/analysis_dbms_schema_item_search_pop','_dbmsSchemaSearchPop', 'width=1600, height=700, resize=yes');
}

//div > ul > li scroll 제어
function fnMultiCodeListScrollControl(div_id, ul_class, i , index){
	
	var maxHeight = parseInt($("#"+div_id+""+i+"").find("."+ul_class+"").css("maxHeight"), 10);
	var visible_top = $("#"+div_id+""+i+"").find("."+ul_class+"").scrollTop();
	var visible_bottom = maxHeight + visible_top;
	var high_top = $("#"+div_id+""+i+"").find("[data-option-index =\""+index+"\"]").position().top + visible_top;
	var high_bottom = high_top + $("#"+div_id+""+i+"").find("[data-option-index =\""+index+"\"]").outerHeight();
	
	//console.log("maxHeight : ",maxHeight , " visible_top : ",visible_top, " visible_bottom: ",visible_bottom, " high_top: ", high_top, " high_bottom : ",high_bottom) ;
	$("#"+div_id+""+i+"").find("li").removeClass("on");
	$("#"+div_id+""+i+"").find("[data-option-index =\""+index+"\"]").addClass("on");
	
	if (high_bottom >= visible_bottom) {
		return $("#"+div_id+""+i+"").find("."+ul_class+"").scrollTop((high_bottom - maxHeight) > 0 ? high_bottom - maxHeight : 0);
	} else if (high_top < visible_top) {
		return $("#"+div_id+""+i+"").find("."+ul_class+"").scrollTop(high_top);
	}
	
	
}






function isSessionConChk(isConnectedYn) {
	if(isConnectedYn == undefined || isConnectedYn == ""){
		var isConnectedYn = parent.$("#topgv_isConnectedYn").val();
	}
	
	
	if(isConnectedYn == "N"){
		//alert("진단대상 데이터베이스 설정이 안되어 있습니다");
		alert('진단대상 데이터베이스 설정이 안되어 있습니다.\n\n 설정페이지로 이동합니다.');
		
		
		var isMainFrame = true;
		try {
			isMainFrame = parent.fnIsMainFrame();	// 메인프렝임이 없는 상태에는 exception 으로 빠지고 sub에 있는 spinner를 실행한다.
		} catch(e) {
			isMainFrame = false;
		}
		
		if(isMainFrame) {	
			//location.href = "/open/cmmn/project/project_list";
			parent.fnClickMenu("/open/cmmn/project/project_list");
		} else {	// 팝업 창일때
			opener.fnClickMenu("/open/cmmn/project/project_list");
			window.close(); 
		}
		//location.href = "/mngr/basicInfo/dgnssDbmsList?up=collapse0&sn=6";
		return false;
		
	}else {
		return true;
	}
}

function isCsvFileCheck(dbmsNm){
	
	if(dbmsNm == undefined || dbmsNm == ""){
		var dbmsNm = parent.$("#topgv_dbmsNm").val();
	}
	
	if(dbmsNm == "CSV") {
		alert("CSV 파일은 해당 기능을 지원하지 않습니다.\n\n 설정페이지로 이동합니다.");
		parent.fnClickMenu('/open/cmmn/project/project_list');
		//location.href = "/open/cmmn/project/project_list";
		/*location.href = "/mngr/basicInfo/dgnssDbmsList?up=collapse0&sn=6";*/
		return false;
	}

}
function isMongoDbFileCheck(){
	
	if(parent.$("#topgv_dbmsNm").val() == "mongoDB") {
		alert("mongoDB 파일은 해당 기능을 지원하지 않습니다.\n\n 설정페이지로 이동합니다.");
		parent.fnClickMenu('/open/cmmn/project/project_list');
		return false;
	}
		
}
 


function fnGetMyStdSetSn() {
	
	var stdSetSn = parent.$("#topgv_stdSetSn").val();
	
	if(stdSetSn == undefined || stdSetSn == "") {
		stdSetSn = parent.opener.parent.$("#topgv_stdSetSn").val();
	}
	return stdSetSn;
	
}

function fnGetMyIsConnectedYn() {
	
	var	isConnectedYn = parent.opener.parent.$("#topgv_isConnectedYn").val();
	return isConnectedYn;
	
}

function fnGetMyDbmsNm() {
	var dbmsNm = parent.$("#topgv_dbmsNm").val();
	
	if(dbmsNm == undefined || dbmsNm == "") {
		dbmsNm = parent.opener.parent.$("#topgv_dbmsNm").val();
	}
	
	return dbmsNm;
	

}

function fnGetMyDbmsDatabaseNm() {
	
	var dbmsDatabaseNm = parent.$("#topgv_dbmsDatabaseNm").val();
	
	if(dbmsDatabaseNm == undefined || dbmsDatabaseNm == "") {
		dbmsDatabaseNm = parent.opener.parent.$("#topgv_dbmsDatabaseNm").val();
	}
	
	return dbmsDatabaseNm;

}

//DBMS 미등록시 DBMS선택 팝업
function isDbmsChk(){
	var dbmsNm = parent.$("#topgv_dbmsNm").val();
	if(dbmsNm == undefined || dbmsNm == ""){
		if (!confirm("프로젝트의 DBMS 종류 설정하시겠습니까?")) {
			return false;
		}
		fnShowModal("regdbmsInfo_modal");
	} else {
		return true;
	}
}

// 검색조건의 _ 를 \_로 변경 
function fnUnderbarChgVar(str) {
	try {
		var rtnStr = "";
		for(var i = 0 ; i < str.length ; i++) {
			if (str.charAt(i) == "_") {
				rtnStr += "\\_";
			} else {
				rtnStr += str.charAt(i)
			}
		}
		return rtnStr;
		
	} catch(e) {
		return str;
	}
}
