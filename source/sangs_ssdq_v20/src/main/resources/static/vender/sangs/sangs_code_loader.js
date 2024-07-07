


var _codeBinder;
var CODE_SERVICE_URL = "/json/commonCode/getCommonCodeMultiList";
var _commonCodeMapList = new Object();

var CodeBinder = function() {
	this.commCodeIdArr = new Array();
	this.commCodeBindTargetIdArr = new Array();
	this.commCodeBindTargetTypeArr = new Array();
	this.commCodeDefaultValueArr = new Array();
	this.sqlCodeIdArr = new Array();
	this.sqlCodeBindTargetIdArr = new Array();
	this.sqlCodeBindTargetTypeArr = new Array();
	this.sqlCodeDefaultValueArr = new Array();
	this.firstOptionObj = new Object();
	this.loadCallbackFunc = "";
	//this.common_code_map = new Object();

	this.setCommonCode = function(i_commCodeId, i_commCodeBindTargetId, i_commCodeBindTargetType, i_commCodeDefaultValue) {
		this.commCodeIdArr[this.commCodeIdArr.length] = i_commCodeId;
		this.commCodeBindTargetIdArr[this.commCodeBindTargetIdArr.length] = i_commCodeBindTargetId;

		if(i_commCodeBindTargetType == undefined || i_commCodeBindTargetType == '')
			i_commCodeBindTargetType = "select";

		this.commCodeBindTargetTypeArr[this.commCodeBindTargetTypeArr.length] = i_commCodeBindTargetType;

		if(i_commCodeDefaultValue == undefined || i_commCodeDefaultValue == null)
			i_commCodeDefaultValue = "";

		this.commCodeDefaultValueArr[this.commCodeDefaultValueArr.length] = i_commCodeDefaultValue;

		return this;
	};
	this.setSqlCode = function(i_sqlCodeId, i_sqlCodeBindTargetId, i_sqlCodeBindTargetType, i_sqlCodeDefaultValue) {
		this.sqlCodeIdArr[this.sqlCodeIdArr.length] = i_sqlCodeId;
		this.sqlCodeBindTargetIdArr[this.sqlCodeBindTargetIdArr.length] = i_sqlCodeBindTargetId;

		if(i_sqlCodeBindTargetType == undefined || i_sqlCodeBindTargetType == '')
			i_sqlCodeBindTargetType = "select";

		this.sqlCodeBindTargetTypeArr[this.sqlCodeBindTargetTypeArr.length] = i_sqlCodeBindTargetType;

		if(i_sqlCodeDefaultValue == undefined || i_sqlCodeDefaultValue == null)
			i_sqlCodeDefaultValue = "";

		this.sqlCodeDefaultValueArr[this.sqlCodeDefaultValueArr.length] = i_sqlCodeDefaultValue;


		return this;
	};
	this.addFirstOption = function(i_codeId, i_codeValue, i_codeText) {
		this.firstOptionObj[i_codeId] = {code:i_codeValue, codeNm:i_codeText};
		return this;
	};
	this.setLoadCallbackFunc = function(i_loadCallbackFunc) {
		this.loadCallbackFunc = i_loadCallbackFunc;
		return this;
	};
	this.load = function() {
		var paramObj = {};
		paramObj.COMMON_CODE_LIST = this.commCodeIdArr;
		paramObj.SQL_CODE_LIST = this.sqlCodeIdArr;

		//console.log(paramObj);

		//List<String> commonCodeList = (List<String>)paramMap.get("COMMON_CODE_LIST");
		//List<String> sqlCodeList = (List<String>)paramMap.get("SQL_CODE_LIST");

		var l_commCodeIdArr = this.commCodeIdArr;
		var l_commCodeBindTargetIdArr = this.commCodeBindTargetIdArr;
		var l_commCodeBindTargetTypeArr = this.commCodeBindTargetTypeArr;
		var l_sqlCodeIdArr = this.sqlCodeIdArr;
		var l_sqlCodeBindTargetIdArr = this.sqlCodeBindTargetIdArr;
		var l_sqlCodeBindTargetTypeArr = this.sqlCodeBindTargetTypeArr;
		var l_commCodeDefaultValueArr = this.commCodeDefaultValueArr;
		var l_sqlCodeDefaultValueArr = this.sqlCodeDefaultValueArr;
		var l_firstOptionObj = this.firstOptionObj;
		var l_loadCallbackFunc = this.loadCallbackFunc;
//		var l_common_code_map = this.common_code_map;

		$.ajax({
			url: CODE_SERVICE_URL
			, method : "POST"
			, contentType: 'application/json'
			, data: JSON.stringify(paramObj)
			, success: function(data){

				try {
					//console.log(l_commCodeIdArr);
					//console.log(data);

					var tempCommCodeList = data.COMMON_CODE_LIST;
					var tempSqlCodeList = data.SQL_CODE_LIST;
					//console.log(tempSqlCodeList);

					for(var i = 0 ; i < l_commCodeIdArr.length; i++) {
						//console.log("-->" + l_commCodeIdArr[i]);

						var codeGrp = l_commCodeIdArr[i];
						var codeList = tempCommCodeList[codeGrp];
						var targetType = l_commCodeBindTargetTypeArr[i];


						// 첫번째 option 을 만들어줘야 할경우
						if(l_firstOptionObj[codeGrp] && l_firstOptionObj[codeGrp] != undefined) {
							var tempObj = l_firstOptionObj[codeGrp];
							codeList.unshift(tempObj);
						}

//						_commonCodeMapList[codeGrp] = codeList;
//						l_common_code_map[codeGrp] = codeList;
						_commonCodeMapList[codeGrp] = codeList;

						if(targetType == "select") {

							var selectObj = $("#"+l_commCodeBindTargetIdArr[i]);
							//console.log(selectObj.html());
							for(var j = 0 ; j < codeList.length; j++) {
								var item = codeList[j];
								//console.log(item);

								var selectedText = "";
								if(l_commCodeDefaultValueArr[i] == item.code)
									selectedText = "selected";

								var addObj = "<option value='"+item.code + "' "+selectedText+">"+item.codeNm+"</option>";

								selectObj.append(addObj);
							}

						} else if(targetType == "radio") {
							var radioObj = $("#"+l_commCodeBindTargetIdArr[i]);

							for(var j = 0 ; j < codeList.length; j++) {
								var item = codeList[j];
								//console.log(item);
								var selectedText = "";
								if(l_commCodeDefaultValueArr[i] == item.code)
									selectedText = "checked";

								var addObj = "<input type='radio' name='"+codeGrp+"' id='"+codeGrp+"_"+item.code+"' value='"+item.code + "' "+selectedText+" /><label for='"+codeGrp+"_"+item.code+"' class='req'>"+item.codeNm+"</label> ";

								radioObj.append(addObj);
							}
						} else if(targetType == "checkbox") {
							var checkObj = $("#"+l_commCodeBindTargetIdArr[i]);

							for(var j = 0 ; j < codeList.length; j++) {
								var item = codeList[j];
								//console.log(item);
								var addObj = "<input type='checkbox' name='"+codeGrp+"' id='"+codeGrp+"_"+item.code+"' value='"+item.code + "' /><label for='"+codeGrp+"_"+item.code+"' class='req'>"+item.codeNm+"</label> ";
								checkObj.append(addObj);
							}
						}
					}


					for(var i = 0 ; i < l_sqlCodeIdArr.length; i++) {


						var codeGrp = l_sqlCodeIdArr[i];
						var codeList = tempSqlCodeList[codeGrp];
						var targetType = l_sqlCodeBindTargetTypeArr[i];

						// 첫번째 option 을 만들어줘야 할경우
						if(l_firstOptionObj[codeGrp] && l_firstOptionObj[codeGrp] != undefined) {
							var tempObj = l_firstOptionObj[codeGrp];
							codeList.unshift(tempObj);
						}


//						l_common_code_map[codeGrp] = codeList;
						_commonCodeMapList[codeGrp] = codeList;

						if(l_sqlCodeBindTargetIdArr[i] == "")
							continue;


						if(targetType== "select") {

							var selectObj = $("#"+l_sqlCodeBindTargetIdArr[i]);

							//console.log(selectObj.html());
							for(var j = 0 ; j < codeList.length; j++) {
								var item = codeList[j];
								//console.log(item);

								var selectedText = "";
								if(l_sqlCodeDefaultValueArr[i] == item.code)
									selectedText = "selected";


								var extvarText = "";
								if(item.extval && item.extval != undefined && item.extval != "") {	// extval 이 있을때 
									extvarText = " data-extval='"+item.extval+"'";
								}

								var extvarText2 = "";
								if(item.extval2 && item.extval2 != undefined && item.extval2 != "") {	// extval2 이 있을때 
									extvarText2 = " data-extval2='"+item.extval2+"'";
								}

								var addObj = "<option value='"+item.code + "' "+selectedText+extvarText+" "+selectedText+extvarText2+">"+item.codeNm+"</option>";
								selectObj.append(addObj);
							}

						} else if(targetType== "radio") {

							var radioObj = $("#"+l_sqlCodeBindTargetIdArr[i]);

							for(var j = 0 ; j < codeList.length; j++) {
								var item = codeList[j];
								//console.log(item);

								var selectedText = "";
								if(l_sqlCodeDefaultValueArr[i] == item.code)
									selectedText = "checked";

								var addObj = "<input type='radio' name='"+codeGrp+"' id='"+codeGrp+"_"+item.code+"' value='"+item.code + "' "+selectedText+" /><label for='"+codeGrp+"_"+item.code+"' class='req'>"+item.codeNm+"</label> ";

								radioObj.append(addObj);
							}
						} else if(targetType== "checkbox") {
							var checkObj = $("#"+l_sqlCodeBindTargetIdArr[i]);

							for(var j = 0 ; j < codeList.length; j++) {
								var item = codeList[j];
								//console.log(item);
								var addObj = "<input type='checkbox' name='"+codeGrp+"' id='"+codeGrp+"_"+item.code+"' value='"+item.code + "'/><label for='"+codeGrp+"_"+item.code+"' class='req'>"+item.codeNm+"</label> ";
								checkObj.append(addObj);
							}
						} else if(targetType== "ul"){

							var selectObj = $("."+l_sqlCodeBindTargetIdArr[i]);

							for(var j = 0 ; j < codeList.length; j++) {
								var item = codeList[j];

								var extvarText = "";
								if(item.extval && item.extval != undefined && item.extval != "") {	// extval 이 있을때 
									extvarText = " data-extval='"+item.extval+"'";
								}

								var extvarText2 = "";
								if(item.extval2 && item.extval2 != undefined && item.extval2 != "") {	// extval2 이 있을때 
									extvarText2 = " data-extval2='"+item.extval2+"'";
								}

								var code = "";
								if(item.code && item.code != undefined && item.code != "") {	// code 있을때 
									code = " data-code='"+item.code+"'";
								}
								var addObj = "<li class='li_result' data-option-index='"+j+"' value='"+item.codeNm + "' "+selectedText+code+" "+selectedText+extvarText+" "+selectedText+extvarText2+">"+item.codeNm+"</li>";
								selectObj.append(addObj);
							}

						}
					}

					try {
						// selectbox label 적용 
						fnApplySelectLabel();
					} catch(e) {}

					// callback fucntion call 
					if(l_loadCallbackFunc != "") {
						eval(l_loadCallbackFunc);
					}

				} catch(e) {
					console.log(e);
					fnStopMainLoading();
				}
			}
		});
		_codeBinder = this;
		return this;
	};
	/*
	this.getCodeList = function(codeId) {
		return _commonCodeMapList[codeId];
		//return _commonCodeMapList[codeId];
		//return _codeBinder.common_code_map[codeId];
	};
	*/
	this.getCodeMap = function() {
		//console.log(_commonCodeMapList);
		return _commonCodeMapList;
	}

	this.getSelectForLoadedCode = function(codeId, objId, defaultVal) {
		//var codeList = this.getCodeList(codeId);
		var codeList = _commonCodeMapList[codeId];
		var selectObj = $("#"+objId);
		for(var j = 0 ; j < codeList.length; j++) {
			var item = codeList[j];
			var selectedText = "";
			if(defaultVal == item.code)
				selectedText = "selected";
			var addObj = "<option value='"+item.code + "' "+selectedText+">"+item.codeNm+"</option>";
			selectObj.append(addObj);
		}
	};


}