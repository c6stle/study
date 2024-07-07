
 
function fnBindTableValue(tableId, list, pagingInfo, callback) {	
	
	$("#"+tableId).find(".fwk_tr_no_row").hide();
	
	$("#"+tableId).find(".fwk_data_row").remove();
	
	var templeteTr = $("#"+tableId).find(".fwk_templete_row").html();
	//console.log(templeteTr);
	
	var dataKey;
	var htmlTrs = "";
	
	var trAttrs = "";
	try {
		$("#"+tableId).find(".fwk_templete_row").each(function() {
			$.each(this.attributes, function() {
				if (this.specified) {
					var tval = (this.value).replace("fwk_templete_row", "fwk_data_row");
					trAttrs = trAttrs + this.name + "='"+ tval + "'";
				}
			});
		});
	} catch(e) {
		trAttrs = "class='fwk_data_row'";
	}
	
	if(list.length == 0) {
		if($("#"+tableId).find(".fwk_tr_no_row").length == 0) {
			// 	fwk_tr_no_row 가 선언되어 있지 않는경우
			
			var tempTdCnt = 10;
			try {
				tempTdCnt = $("#"+tableId).find(".fwk_templete_row td").length;
			} catch(e){}
			
			var noRowHtml = '<tr class="fwk_tr_no_row"><td colspan="'+tempTdCnt+'" style="text-align: center">조회된데이터가 없습니다.</td></tr>';	
			
			$("#"+tableId).find("tbody").append(noRowHtml);
		} else {
			$("#"+tableId).find(".fwk_tr_no_row").show();
		}	
		
	} else {
		for(var i = 0 ; i < list.length; i++) {
			if(i == 0) 
				dataKey = Object.keys(list[i]);
		
			var data = list[i];
			
			//htmlTrs = htmlTrs + "<tr class='fwk_data_row'>";
			htmlTrs = htmlTrs + "<tr "+trAttrs+">";
			
			var htmlTr = templeteTr;
			
			
			
			// #{FWK_TR_NO_DESC} 로 명시 한곳은 마지막 데이터의 번호 ~ 1 번까지 역순으로 순번을 표시한다.
			if(htmlTr.indexOf('#{FWK_TR_NO_DESC}') >= 0) {	// row 번호
				if(pagingInfo != undefined && pagingInfo != null) {
					htmlTr = fnReplaceAll(htmlTr, '#{FWK_TR_NO_DESC}', fnFormatNumber(Number(pagingInfo.totalCount) - (fnGetTrNo(i, pagingInfo.pageSize, pagingInfo.pageNum))) );
				} else {
					htmlTr = fnReplaceAll(htmlTr, '#{FWK_TR_NO_DESC}' (list.length - i));
				}
			}
			/* 
			if(htmlTr.indexOf('#{FWK_TR_NO_DESC}') >= 0 && pagingInfo != undefined && pagingInfo != null) {	// row 번호 
				htmlTr = fnReplaceAll(htmlTr, '#{FWK_TR_NO_DESC}', fnFormatNumber(Number(pagingInfo.totalCount) - (fnGetTrNo(i, pagingInfo.pageSize, pagingInfo.pageNum))) );
			}
			*/
			
			// #{FWK_TR_NO} 로 명시 한곳은 1 ~ 데이터에 대한 순번을 표시한다. 
			//if(htmlTr.indexOf('#{FWK_TR_NO}') >= 0 && pagingInfo != undefined) {	// row 번호
			if(htmlTr.indexOf('#{FWK_TR_NO}') >= 0 ) {	// row 번호
				if(pagingInfo != undefined && pagingInfo != null)
					htmlTr = fnReplaceAll(htmlTr, '#{FWK_TR_NO}', fnFormatNumber(fnGetTrNo((i+1), pagingInfo.pageSize, pagingInfo.pageNum)));
				else
					htmlTr = fnReplaceAll(htmlTr, '#{FWK_TR_NO}', (i+1));
			}
			
			
			// row (tr) 의 index를 반환 0 부터 ..
			htmlTr = fnReplaceAll(htmlTr, '#{FWK_TR_INDEX}', i);
			
			for(var j = 0 ; j < dataKey.length ; j++) {
				//console.log(dataKey[j]);
				var value = data[dataKey[j]];
				if(value == null )
					value = "";
				htmlTr = fnReplaceAll(htmlTr, '#{'+dataKey[j]+'}', value);
				//console.log(htmlTr);
			}
			
			// 스크립트 펑션 처리 
			for(var j = 0 ; j < dataKey.length ; j++) {
				// 펑션 사용시 
				try {
					if(htmlTr.indexOf('#fn{') >= 0)
						htmlTr = fnApplyCustFn(htmlTr);
				} catch(e) {
					console.log(e);
				}
			}
			
			
			htmlTrs = htmlTrs + htmlTr + "</tr>";
		}
		$("#"+tableId).find("tbody").append(htmlTrs);
	}
	
	setFrameHeight();
	
	if(callback && callback != undefined)
		callback(list);
}


/**
 */
function fnBindTableValueForSingle(tableId, info) {	
	
	var templeteTbody = $("#"+tableId).find(".fwk_databind_single_tbody").html();
	var dataKey = Object.keys(info);
	
	for(var j = 0 ; j < dataKey.length ; j++) {
		var value = info[dataKey[j]];
		if(value == null )
			value = "";
			
		templeteTbody = fnReplaceAll(templeteTbody, '#{'+dataKey[j]+'}', value);
		
	}
	// 스크립트 펑션 처리 
	for(var j = 0 ; j < dataKey.length ; j++) {
		// 펑션 사용시 
		try {
			if(templeteTbody.indexOf('#fn{') >= 0)
				templeteTbody = fnApplyCustFn(templeteTbody);
		} catch(e) {
			console.log(e);
		}
	}
	var templeteTbody = $("#"+tableId).find(".fwk_databind_single_tbody").html(templeteTbody);
	 
}




function fnClearBindData(tableId, pagingId) {
	$("#"+tableId).find(".fwk_data_row").remove();
	
	var tempTdCnt = 10;
	try {
		tempTdCnt = $("#"+tableId).find(".fwk_templete_row td").length;
	} catch(e){}
	
	if(!$("#"+tableId).find("tr").hasClass("fwk_tr_no_row")) {
		var noRowHtml = '<tr class="fwk_tr_no_row"><td colspan="'+tempTdCnt+'" style="text-align: center">조회된데이터가 없습니다.</td></tr>';
		$("#"+tableId).find("tbody").append(noRowHtml);
	}
	
	if(pagingId) {
		$("#"+pagingId).html("");
	}
	
}

// 문자열 펑션 호출하여 적용 시키기
var applyCustFnLoopLimit = 100;
function fnApplyCustFn(htmlTr) {
	var c_currentLoopCnt = 0;
	
	var c_htmlTr = htmlTr;
	
	while(true) {
		c_currentLoopCnt++;
		
		if(applyCustFnLoopLimit < c_currentLoopCnt)
			break;
			
		if(c_htmlTr.indexOf('#fn{') >= 0) {
			c_htmlTr = fnApplyCustFnProc(c_htmlTr);	
		} else {
			break;
		}
	}
	return c_htmlTr;
}

function fnApplyCustFnProc(htmlTr) {
	//#fn{fnFormatNumber(2282)}
	
	var startIdx = htmlTr.indexOf("#fn{");
	var preHtml = htmlTr.substring(0, startIdx);
	//console.log("|"+preHtml+"|");
	var temp_postHtml = htmlTr.substring(startIdx + 4, htmlTr.length);
	var fnStr = temp_postHtml.substring(0, temp_postHtml.indexOf("}"));
	var postHtml = temp_postHtml.substring(temp_postHtml.indexOf("}") + 1, temp_postHtml.length);
	//console.log("|"+fnStr+"|");
	//console.log("|"+postHtml+"|");
	//console.log(fnStr);
	var rtnVal = eval(fnStr);
	//console.log("|"+rtnVal+"|");
	return preHtml + rtnVal + postHtml;
}


 
	
function fnReplaceAll(str, searchStr, replaceStr) {
   return str.split(searchStr).join(replaceStr);
}








function fnSetPaging(pagingId, pagingInfo, strSearchNm) {
	//console.log(pagingInfo);
	if(pagingInfo.totalCount == 0) {
		
		$("#"+pagingId).css("visibility", "hidden");				 
					
	} else {
		
		$paginDivObj = $("#"+pagingId);
		$ulOjb = $("<ul></ul>")
		
		//console.log(pagingInfo);
		
		
		if(!strSearchNm || strSearchNm == undefined)
			strSearchNm = "fnSearch";
			
		$ulOjb.append('<li style="float:left; font-size:13px">총 '+fnFormatNumber(pagingInfo.totalCount)+'건</li>');
		$ulOjb.append('<li><a href="#" class="pprev" title="처음페이지로 이동" onclick="'+strSearchNm+'(1);return false;'+'">처음페이지로 이동</a></li>');
		$ulOjb.append('<li><a href="#" class="prev" title="이전페이지로 이동" onclick="'+strSearchNm+'('+pagingInfo.prevGroupPageNum+');return false;'+'">이전페이지로 이동</a></li>');
		//$liFirst = $('<li class="first hidden"><a href="#" onclick="'+strSearchNm+'(1);return false;'+'">&lt;&lt;</a></li>');
		//$prevGrp = $('<li class="previous hidden"><a href="#" onclick="'+strSearchNm+'('+pagingInfo.prevGroupPageNum+');return false;'+'">&lt;</a></li>');
		
		for(var i = 0 ; i < pagingInfo.list.length; i++) {
			
			var selectedStr = "";
			if(pagingInfo.list[i] == pagingInfo.pageNum)
				selectedStr = ' class="on"'; 
			
			$ulOjb.append('<li'+selectedStr+'><a href="#" onclick="'+strSearchNm+'('+pagingInfo.list[i]+');return false;'+'">'+pagingInfo.list[i]+'</a></li>');
		}
		
		if(pagingInfo.lastGroupStart == pagingInfo.nextGroupPageNum && pagingInfo.lastGroupStart == pagingInfo.startPageNum) {	// 페이지 그룹이 한개 일때 다음 페이지 그룹으로 넘어갈 것이 없는경우
			$ulOjb.append('<li><a href="#" class="next" title="다음페이지로 이동" onclick="return false;">다음페이지로 이동</a></li>');
			$ulOjb.append('<li><a href="#" class="nnext" title="마지막 페이지로 이동" onclick="return false;">마지막 페이지로 이동</a></li>');
		} else {
			$ulOjb.append('<li><a href="#" class="next" title="다음페이지로 이동" onclick="'+strSearchNm+'('+pagingInfo.nextGroupPageNum+');return false;'+'">다음페이지로 이동</a></li>');
			$ulOjb.append('<li><a href="#" class="nnext" title="마지막 페이지로 이동" onclick="'+strSearchNm+'('+pagingInfo.lastGroupStart+');return false;'+'">마지막 페이지로 이동</a></li>');
		}
		$paginDivObj.html('<ul>' + $ulOjb.html() + '</ul>');
		$("#"+pagingId).css("visibility", "visible");
	}
	
}

function fnGetTrNo(rowIndex, pageSize, pageNum) {
	return ((pageNum - 1) * pageSize) + rowIndex;
}


function fnBindCodeListObj(codeId, objType, selectVal, objName) {
	
	if(!objName || objName == undefined)
		objName = codeId;
		
	var rtnObjHtml = "";
	
	if(objType == "select") 
		rtnObjHtml += "<select class='form-control' name='"+objName+"'>";
	

	if(_codeBinder && _codeBinder != undefined) {
		//console.log(_codeBinder.common_code_map);
		
		var codeList = _codeBinder.common_code_map[codeId];
		
		if(objType == "text") {
			for(var i = 0 ; i < codeList.length ; i++ ) {
				var item = codeList[i];
				if(selectVal == item.code)
					rtnObjHtml += item.codeNm;
			}
				
		} else if(objType == "select") {
			for(var i = 0 ; i < codeList.length ; i++ ) {
				var item = codeList[i];
				//console.log(item);
				var selectedText = "";
				if(selectVal == item.code)
					selectedText = "selected";
					
				rtnObjHtml+= "<option value='"+item.code + "' "+selectedText+">"+item.codeNm+"</option>";
			}
		}
	}
	
	if(objType == "select") 
		rtnObjHtml+="</select>";
	return rtnObjHtml;
	
	
}


// 금액 천단위 컴마 붙이기
function fnFormatNumber(n) {
	var str = "" + n;
	//format number 1000000 to 1,234,567
	return str.replace(/\D/g, "").replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

