
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


// 디자인된 select box의 이벤트로 변경시 display 되는 text 변경 해준다.  
function fnApplySelectLabel() {
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
}
// 디자인된 select box 보이는 text 를 적용시켜준다 
function fnApplySelectLabelDisp(selectboxId) {
	var select_name = $("#"+selectboxId).children('option:selected').text();
	if($("#"+selectboxId).siblings('label').length == 0) {
		// select box 아래 label 태그가 없으면 생성
		$("#"+selectboxId).closest("div").append("<label>"+select_name+"</label>");
	} else {
		$("#"+selectboxId).siblings('label').text(select_name);
	}
}
// 디자인된 selectbox 의 값을 변경 한다. 
function fnChangeSelectVal(selectboxId, value) {
	if(!value || value == undefined || value == "")
		value = "''";
	$("#"+selectboxId).find("option[value="+value+"]").prop("selected", true);
	// 디자인된 select box 보이는 text 를 적용시켜준다
	fnApplySelectLabelDisp(selectboxId);
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

// 로그아웃
function fnLogoutProc() {
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
