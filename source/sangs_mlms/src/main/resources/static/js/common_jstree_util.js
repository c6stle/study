$(document).ready(function() {
	
	// 폼 데이터 변경 이벤트 
	$("#menuFrm").on("change", function(e, data) {
		
	   var jstree = $("#menuTree").jstree(true);
	   var nodeId = jstree.get_selected();
	   var nodeNm = $("#menuTree").jstree("get_node", nodeId).text;
	   var data = $("#menuTree").jstree("get_node", nodeId).original;
	   var menuNm = $(e.target).parents("tbody").find("#menuNm").val();
	   
	   if(nodeId.length <= 0){
		   alert("선택된 노드가 없습니다.");
		   return false;
	   }
	   
	   // 메뉴번호가 임시 메뉴번호 작다면 수정상태 
	   if(nodeId < tempMenuSn){
		   data.pmode = 'M';
	   }
	   
	   // 값 세팅
	   data[e.target.id] = e.target.value;
	   
	   // node data key와 from key가 동일한 값 데이터 매핑 
	   $.each(data, function(key, nValue) {
		   $.each($("#menuFrm")[0], function(index, mValue) {
			   if(key == $(mValue).attr("id")){
				   if($(mValue).prop("type").indexOf("select") > -1){
					   $("#"+key).val(nValue);
				   }else{
					   $("input[name="+key+"]").val(nValue);
				   }
			   }
		   });
	   });
	   
	   // menuFrm 변경 메뉴명 노드에 적용
	   nodeEnable("menuTree");
	   
	   if(menuNm != ""){
		   $("#menuTree").jstree("rename_node", nodeId, menuNm + modifyImg);   
	   }else{
		   $("#menuTree").jstree("rename_node", nodeId, nodeNm + modifyImg);   
	   }
	     
	   nodeDisable("menuTree", nodeId);
	});
	
	
	
	// jstreeGrid cell 선택 이벤트 
	$(this).on("select_cell.jstree-grid", function (e, data) {
		
		//console.log("e = ", e);
		//console.log("data = ", data);
	});
	
});

// 신규추가 임시 노드 메뉴순번
var tempMenuSn = 999999;
// 신규추가 임시 노드 생성순번
var tempSortSn = 1;
// 등록 아이콘
var createImg = ' <i class="fas fa-plus-square"></i>';
// 수정 아이콘
var modifyImg = ' <i class="fas fa-edit"></i>';

// 권한 항목 조회
function fnSelectMenuAuthorCodeList(){
	var retObj;
	
	$.ajax({
		url: "/json/authMngAuth/getAuthorIemCodeList"
		, method : "POST"
		, contentType: 'application/json'
		// 동기식 처리 
		, async: false
		, data: JSON.stringify({
			
		})
		, success: function(data){
			retObj = data.list;
		} 
	});
	return retObj;
}


// jstree 셋팅 
var core = {};
	core.check_callback = true;
	core.data = [];
	
	
function fnCoreInit(){
	core = {};
	core.check_callback = true;
	core.data = [];
}
	
// jstree 생성
function fnBindJstree(divId, listObj){
	// core 생성 
	var coreSet = fnCoreSet(listObj, "");

    var jstreeCfg = {
    	'plugins': ["search"]
    	, 'core': coreSet
    	, 'types' : {
    		'default' : {
    			"max_depth" : 3
    		}
    	}
    };
    
	$("#"+divId).jstree(jstreeCfg);
	
	$("#"+divId).bind('loaded.jstree', function(e, data) {
		//console.log("--> jsTree loaded");
		parent.setFrameHeight();
	})

	// jstree 로드 시
	$("#"+divId).bind("loaded.jstree",function(){
		fnJsTreeSelectEvent(divId);
	});
	
}	
	
// jstreeGrid 생성
function fnBindJstreeGrid(divId, listObj, authDtlObj, codeObj, userId){
	// core 생성 
	var columnsSet = fnColumnsSet(codeObj);
	var coreSet = fnCoreSet(listObj, codeObj);
	
    var jstreeCfg = {
    	'plugins': ['grid'],
    	'core': coreSet,
    	'grid': {columns: columnsSet}
    };
    
	$("#"+divId).jstree(jstreeCfg);
	
	$("#"+divId).bind('loaded.jstree', function(e, data) {
		console.log("--> jsTreeGrid loaded");
		parent.setFrameHeight();
	})
	
	// jstree 다시 그리기 
	//$("#"+divId).jstree(true).redraw(true);
	
	// jstree 로드 시
	$("#"+divId).bind("loaded_grid.jstree",function(){
		// 초기 로드 시 체크 이벤트 발생
		fnJsTreeCheckEvent(divId, authDtlObj, userId);
	});

	// jstreeGrid 트리구조 외 컬럼요소 가온데 정렬 css 추가 
	$(".jstree-grid-wrapper .jstree-grid-cell:nth-child(n+2):nth-child(-n+100)").css("text-align","center");
	
	
	test(divId);
}


function test(divId){
	
	
	$("#"+divId).jstree(true).settings.core.dblclick_toggle = false;
	
	// 권한 노드 트리 접기 불가   
	$('#'+divId+' i.jstree-ocl').off('click.block');
    
    
	//$('#'+divId).jstree().settings.core.dblclick_toggle = false;
	//$('#'+divId).jstree().settings.core.check_callback = false;
// 	
}

	
function fnBindCore(divId, listObj, codeObj, userId){
	fnCoreInit();
	
	core = fnCoreSet(listObj, codeObj);
	
	$("#"+divId).jstree(true).settings.core = core;
    $("#"+divId).jstree(true).refresh();
    
}

function fnBindCoreGrid(divId, listObj, authDtlObj, codeObj, userId){
	fnCoreInit();
	
	core = fnCoreSet(listObj, codeObj);
	
	$("#"+divId).jstree(true).settings.core = core;
    $("#"+divId).jstree(true).refresh();
    
    fnJsTreeCheckEvent(divId, authDtlObj, userId);
}


function fnStatusIcon(obj){
	var addIcon = "";
	
	if(obj.singlPageYn == 'Y'){
		addIcon += ' <i class="far fa-window-maximize"></i>';
	}
	if(obj.dspyYn == 'N'){
		addIcon += ' <i class="fas fa-eye-slash"></i>';
	}
	
	return obj.menuNm + addIcon;
}
	
// jstree core 데이터 셋
function fnCoreSet(listObj, codeObj){
	
	if(codeObj != ""){
		// top 노드 버튼 추가 
		var tempObj = {};
		$.each(codeObj, function(index, value) {
			var code = value.code.toLowerCase();
			//tempObj[code] = "<button class='btn sm' onclick='javascript:void(0); return false;'>전체</button>";
		});
		// top 노드 버튼 종료
	}
	
	// top 노드 추가
	var topObj = {};
 	topObj.id = 0;
	topObj.parent = '#';
	topObj.text = 'HOME';
	topObj.sort = 0;	
	topObj.state = {'opened' : true};
	topObj.data = tempObj;
	core.data[0] = topObj;
	// top 노드 추가 종료
	
	$.each(listObj, function(index, value) {
		
		var obj = {};
		var dataSet = fnDataSet(value.menuSn, value.treeUpMenuSn, codeObj);
		
		// jstree 적용 정보
		obj.id = value.menuSn;
		obj.parent = value.treeUpMenuSn == '#' ? value.upMenuSn : value.treeUpMenuSn;
		obj.text = fnStatusIcon(value);
		obj.sort = value.sort;
		// 최상위 부모 노드 일 경우 활성화  
		//if(value.treeUpMenuSn == '#'){
		obj.state = {'opened' : true};	
		//}

		if(value.dtlPageYn == 'Y'){
			// 최상위 icon 폴더 / 하위 파일 설정
			obj.icon = 'jstree-icon jstree-file';
		}
		// 노드 URL 주소 설정
		obj.a_attr = {'href' : value.urlAddr};
		obj.data = dataSet;
		
		// menu 기본정보
		obj.menuSn = value.menuSn;
		obj.menuNm = value.menuNm;
		obj.urlAddr = value.urlAddr;
		obj.sysSeCd = value.sysSeCd;
		obj.menuSeCd = value.menuSeCd;
		obj.upMenuSn = value.upMenuSn;
		obj.menuDpSn = value.menuDpSn;
		obj.menuSortSn = value.menuSortSn;
		obj.dspyYn = value.dspyYn;
		obj.dtlPageYn = value.dtlPageYn;
		obj.delYn = value.delYn;
		obj.singlPageYn = value.singlPageYn;
		obj.regUserId = value.regUserId;
		obj.regDt = value.regDt;
		
		core.data[index + 1] = obj;
	});
	
	return core;
}


// jstree 데이터 셋
function fnDataSet(menuId, upMenuId, codeObj){
	var retDataObj = {};
	//group = group == "#" ? id : group;
	
	if(typeof codeObj != "undefined" && codeObj != ""){
		$.each(codeObj, function(index, value) {
			
			var code = value.code.toLowerCase();
			
			retDataObj[code] = "<input type='checkbox' class='cl_rowCheck' id='author_"+menuId+"_"+code+"_chkbox' name='author_"+menuId+"_"+code+"_chkbox' data-menu-sn='"+menuId+"' data-group='"+upMenuId+"' data-code='"+code+"' onclick='fn_chk(this)'>";
/*			
			if(group != "#"){
				retDataObj[code] = "<input type='checkbox' class='cl_rowCheck' id='author_"+id+"_"+code+"_chkbox' name='author_"+id+"_"+code+"_chkbox' data-group='"+group+"' data-code='"+code+"'>";
			}else{
				retDataObj[code] = "<button class='btn sm' onclick='javascript:void(0); return false;'>전체</button>";
			}
*/				
		});
	}
	
	return retDataObj;
}

// jstree 컬럼 데이터 셋
function fnColumnsSet(codeObj){
	var width = 'auto';
	var retColumnsArr = [];
	
	// 디폴트 세팅
	retColumnsArr.push({width: 400, value : "_DATA_", header :"메뉴 명"});
	
	$.each(codeObj, function(index, value) {
		var retColumnsObj = {};
		var lowVal = value.code.toLowerCase();
		
		// 권한항목이 8개 이상일 경우 컬럼 넓이 측정
		if(codeObj.length >= 8) {
			width = fnTextWidthSet(value.codeNm);
			if(width * 8 > 130){
				width = width * 8;
			}else{
				width = 'auto';
			}
		}
		
		retColumnsObj.width = width;
		retColumnsObj.value = lowVal;
		retColumnsObj.header = value.codeNm;
		
		retColumnsArr.push(retColumnsObj);
 	});	
	
	return retColumnsArr;
}

// 권한 별 항목 체크 이벤트 
function fnJsTreeCheckEvent(divId, authDtlObj, userId){
	var jstreeTable = $("#"+divId).jstree().table;
	
	// treeTable chkbox 목록
	$.each($(jstreeTable).find("input"), function(treeIdx, treeValue) {
		var t_menuSn = $(treeValue).attr("data-menu-sn");
		var t_code = $(treeValue).attr("data-code");
		
		//console.log("t_menuSn = ", t_menuSn, "|| t_code = ", t_code);
		
		// 메뉴 권한 목록
		$.each(authDtlObj, function(a_index, a_value) {
			var a_menuSn = a_value.menuSn;
			var a_menuAuthrtCd = a_value.menuAuthrtCd.toLowerCase();
			
			// 메뉴순번, 권한코드 일치 할 경우 
			if(t_menuSn == a_menuSn && t_code == a_menuAuthrtCd){
				$("input[name="+treeValue.id+"]").prop("checked", true);
				
				console.log("userId = ", userId);
				
				if(a_value.gubun == "AUTH"){
					// 권한 > 사용자 선택 시 권한 체크박스 disabled 처리 
					if(userId == ""){
						$("input[name="+treeValue.id+"]").prop("disabled", false);
					}else{
						$("input[name="+treeValue.id+"]").prop("disabled", true);
					}
				}
				
				if(a_value.gubun == "USER_AUTH"){
					// 권한 > 사용자 선택 시 권한 설정 값 외 사용자 설정값 활성 화 
					$("input[name="+treeValue.id+"]").prop("disabled", false);
				}
			}
		});
	});
}

// jstree 선택 된 node > form 데이터 셋팅
function fnJsTreeSelectEvent(divId){
	
	$("#"+divId).bind('select_node.jstree', function(event, data){
		
		var dataMenuSn = data.node.original.menuSn;   
		// 폼 리셋
		$('#menuFrm')[0].reset();
		
		$.each(data.node.original, function(key, nValue) {
	    	$.each($("#menuFrm")[0], function(index, mValue) {
	    		if(key == $(mValue).attr("id")){
		    		if($(mValue).prop("type").indexOf("select") > -1){
		    			$("#"+key).val(nValue);
		    		}else{
		    			if(data.node.original.menuDpSn != 1){
		    				if(key == "menuSeCd"){
		    					$("#"+key).attr("readonly",true);
		    				}
		    			}else{
		    				if(key == "menuSeCd"){
		    					$("#"+key).attr("readonly",false);
		    				}
	    				}
		    			$("input[name="+key+"]").val(nValue);
		    		}
		    	}
		    });
	    });
	});
}

// 노드 셀렉트
function nodeSelected(divId, nodeId){
	$("#"+divId).jstree('select_node', nodeId);
}

// 추가 노드를 제외한 노드 nodeDisable
function nodeDisable(divId, nodeId) {
	$('#'+divId+' li.jstree-node').each(function() {
		if(this.id != nodeId){
			$('#'+divId).jstree("disable_node", this.id);
		}
	});
	$('#'+divId+' i.jstree-ocl').off('click.block').on('click.block', function() {
		return false;
	});
	$('#'+divId).jstree().settings.core.dblclick_toggle = false;
	$('#'+divId).jstree().settings.core.check_callback = false;
} 

// 전체 노드 nodeEnable
function nodeEnable(divId) {
	$('#'+divId+' li.jstree-node').each(function() {
		$('#'+divId).jstree("enable_node", this.id)
	});
	
	$('#'+divId+' i.jstree-ocl').off('click.block');
	$('#'+divId).jstree().settings.core.dblclick_toggle = true;
	$('#'+divId).jstree().settings.core.check_callback = true;
}

// jstree 선택 된 노트 해제
function fnDeSelectTreeNode(divId){
	$('#'+divId).jstree("deselect_all");
	
	nodeEnable(divId);
}

// jstree 선택 된 노트 삭제
function fnDelectTreeNode(divId){
	var jstree = $('#'+divId).jstree(true);
	var nodeId = jstree.get_selected();
	var thisNode = $("#"+divId).jstree(true).get_node(nodeId).original;
	
	if(typeof thisNode != "undefined"){
		if(thisNode.pmode == "C"){
			// 전체 노드 활성화 
			nodeEnable(divId);
			
			$('#'+divId).jstree().delete_node(nodeId);
		}else{
			alert("신규 생성된 노드만 삭제 가능합니다.");
			return false;
		}
	}else{
		alert("선택된 노드가 없습니다.");
		return false;
	}
	
	$('#menuFrm')[0].reset();
}


// jstree 노드 생성 
function fnAddTreeNode(btn, divId){
	var jstree = $('#'+divId).jstree(true);
	var nodeId = jstree.get_selected();
	var maxDepth = jstree.settings.types.default.max_depth;
	var selectedNodeLen = $("#"+divId).jstree("get_selected").length;
	var nowDepth = "";
	var thisNode = $("#"+divId).jstree(true).get_node(nodeId).original; 
	var menuSortSn = 99;
	
	var isNodeChk = true;	
	
	if(nodeId.length <= 0){
		alert("생성 할 상위노드를 선택해 주세요.");
		return false;
	}
	
	$.each($('#'+divId).jstree(true).get_json('#', {flat:true}), function(key, value) {
		if(value.text.indexOf("edit") > -1){
			alert("수정 중인 노드가 존재 합니다.");
			// 노드 선택 해제
			fnDeSelectTreeNode(divId);
			
			// 대상 노드 제외 Disable
			nodeDisable(divId, value.id);
			
			// 대상 노드 선택
			nodeSelected(divId, value.id);
			isNodeChk =  false;
		}
		
		if(value.text.indexOf("plus") > -1){
			alert("신규 생성 노드가 존재합니다.");
			// 노드 선택 해제
			fnDeSelectTreeNode(divId);
			
			// 대상 노드 제외 Disable
			nodeDisable(divId, value.id);
			
			// 대상 노드 선택
			nodeSelected(divId, value.id);
			isNodeChk =  false;
		}
	});
	
	if(isNodeChk){
		
		if(thisNode.singlPageYn == 'Y'){
			alert("'단일페이지 여부'를 확인해 주세요.");
			return false;
		}
		
		if(thisNode.dtlPageYn == 'Y'){
			alert("'상세페이지 여부' 'Y'일 경우 하위 메뉴를 생성할 수 없습니다.");
			return false;
		}
		
		fnDeSelectTreeNode(divId);
		
		// 최상위 노드 라면
		if(typeof thisNode != "undefined" && thisNode.id == 0){
			// 임시 메뉴 순번 
			tempMenuSn++;
			
			var nodeData = {
					'id': tempMenuSn
					, 'text' : '상위 메뉴_'+tempSortSn + createImg
					, 'menuSn' : tempMenuSn
					, 'menuDpSn' : 1
					//, 'sysSeCd' : p_sysSeCd
					, 'menuSortSn' : menuSortSn
					, 'upMenuSn' : 0
					, 'dspyYn' : 'Y', 'dtlPageYn' : 'N', 'singlPageYn' : 'N', 'delYn' : 'N', 'pmode' : 'C'
			};
			// 노드 생성
			jstree.create_node("#", nodeData);
			//$('#'+divId).jstree("rename_node", tempMenuSn, '상위 메뉴_'+tempSortSn + createImg);
			
			// 임시 생성 순서 
			tempSortSn++;
			
		// 부모노드 id가 최상위 노드가 아니라면 
		}else if (typeof thisNode != "undefined" && thisNode.id > 0){
			// 노드 길이 체크
			if(!nodeId.length) { return false; }
			
			// 메뉴 깊이 
			nowDepth = thisNode.menuDpSn;
			nodeId = nodeId[0];
	
			if(maxDepth <= nowDepth){
				alert(maxDepth+"Depth 까지 생성이 가능합니다.");
				return false;
			}else{
				// 임시 메뉴 순번 
				tempMenuSn++;
				
				menuSortSn = fnMaxSortSnSet(divId, thisNode);
				
				var nodeData = {
					'id': tempMenuSn
					, 'text' : '하위 메뉴_'+tempSortSn + createImg
					, 'icon': 'jstree-icon jstree-file' 
					, 'menuSn' : tempMenuSn
					, 'menuDpSn' : thisNode.menuDpSn + 1
					, 'sysSeCd' : thisNode.sysSeCd
					, 'menuSeCd' : thisNode.menuSeCd
					, 'menuSortSn' : menuSortSn
					, 'upMenuSn' : nodeId
					, 'dspyYn' : 'Y', 'dtlPageYn' : 'N', 'singlPageYn' : 'N', 'delYn' : 'N', 'pmode' : 'C'
				};
				
				// 노드 생성
				jstree.create_node(nodeId, nodeData);
				//$('#'+divId).jstree("rename_node", tempMenuSn, '하위 메뉴_'+tempSortSn + createImg);
				
				// 임시 생성 순서
				tempSortSn++;
			}    
		}
		
		// 생성 노드 활성화
		$("#"+divId).jstree("open_node", nodeId);
 
		fnJsTreeSelectEvent(divId);
				
		// 생성 노드 선택
		nodeSelected(divId, tempMenuSn);
		
		// disable(divId)
		nodeDisable(divId, tempMenuSn);
	}
}

function fnSaveMenuInfo(divId){
	var jstree = $('#'+divId).jstree(true);
	var nodeId = jstree.get_selected();
	var thisNode = $("#"+divId).jstree(true).get_node(nodeId).original; 
	var chkCnt = 0;
	
	var childrenNode = $("#"+divId).jstree().get_node(nodeId).children_d;
	var childrenArr = [];
	$.each(childrenNode, function(index, value) {
		var cObj = {};
		cObj.menuSn = value;
		cObj.sysSeCd = thisNode.sysSeCd;
		cObj.menuSeCd = thisNode.menuSeCd;
		childrenArr.push(cObj);
	});
	
	if(nodeId.length <= 0){
	   alert("선택된 노드가 없습니다.");
	   return false;
	}
	
	$.each($("#"+divId).jstree(true).get_node(nodeId).children, function(key, value) {
		var childrenNode = $("#"+divId).jstree(true).get_node(value).original;
		if(childrenNode.dspyYn != 'N' || childrenNode.delYn == 'Y'){
			chkCnt++;
		}
		
	});
	
	if(!fnCheckValid("#menuSortSn", "메뉴 정렬 순번")) return;
	if(!fnCheckValid("#menuNm", "메뉴 명")) return;
	if(!fnCheckValid("#urlAddr", "메뉴 URL")) return;

	if(confirm("저장 하시겠습니까?")){
		$.ajax({
			url: "/json/authMngMenu/saveMenuExecInfo"
			, method : "POST"
			, contentType: 'application/json'
			, data: JSON.stringify({
				menuInfo : thisNode
				, childrenArr : childrenArr 
			})
			, success: function(data){
				if(data.resultCd == "OK") {
					alert("저장 되었습니다.");
					fnSearchMenuList("Y");
					
					//nodeEnable(divId);
					//if(confirm("저장 되었습니다. 목록을 갱신 하시겠습니까?")){
					//}
				}
			}
			, error : function(data) {
				console.log(data);
			}
		});
	}
}

// 권한 별 항목 처리 
function fnSaveAuthor(divId){
	
	var authrtCd = "";
	var selectUserId = "";
	var authorTable = $("#tb_author_table").find(".fwk_data_row");
	var userTable = $("#tb_auth_user_table").find(".fwk_data_row");
	var jstreeTable = $("#"+divId).jstree().table;
	
	var authArr = [];
	var userAuthArr = [];
	// 권한 테이블 선택 된 row value 셋팅
	$.each(authorTable, function(index, value) {
		if($(value).data("selectedtr") == 'Y'){
			 authrtCd = $(value).find("input").val();
		}	
	});
	
	// 권한 사용자 테이블 선택 된 row value 셋팅
	$.each(userTable, function(index, value) {
		if($(value).data("selectedtr") == 'Y'){
			 selectUserId = $(value).find("input").val();
		}	
	});
	
	//console.log("selectUserId = ", selectUserId);
	
	// 메뉴 권한 항목 셋팅
	$.each($(jstreeTable).find("input"), function(index, value) {
		var menuId = value.name.replace(/[^0-9]/g,'');
		var authObj = {};
		var userAuthObj = {};
		
		if(selectUserId == ""){
			// 권한그룹 선택 > 저장 시
			//console.log("사용자 선택 X");
			
			if($(value).is(":checked")){
				authObj.authrtCd = authrtCd;
				authObj.menuSn = menuId;
				//authObj.regUserId = $("#frm").find("#regUserId").val();
				authObj.menuAuthrtCd = $(value).data("code").toUpperCase();
				
				authArr.push(authObj);
			}
			
		}else{
			// 권한그룹 선택 > 권한 그룹 사용자 선택 > 저장 시
			//console.log("사용자 선택 O");
			
			if(!$(value).is(":disabled") && $(value).is(":checked")){
				userAuthObj.authrtCd = authrtCd;
				userAuthObj.menuSn = menuId;
				userAuthObj.userId = selectUserId;
				//userAuthObj.regUserId = $("#frm").find("#regUserId").val();
				userAuthObj.menuAuthrtCd = $(value).data("code").toUpperCase();
				
				userAuthArr.push(userAuthObj);
			}
		}
	});
	
	//console.log("authArr = ", authArr);
	//console.log("userAuthArr = ", userAuthArr);
	
	if(confirm("저장 하시겠습니까?")){
		
		$.ajax({
			url: "/json/authMngAuth/saveAuthorIemExecInfo"
			, method : "POST"
			, contentType: 'application/json'
			, data: JSON.stringify({
				authrtCd : authrtCd
				, userId : selectUserId
				, authList: authArr
				, userAuthList : userAuthArr 
			})
			, success: function(data){
				if(data.resultCd == "OK") {
					alert("저장 되었습니다.");
				}
			}
			, error : function(data) {
				console.log(data);
			}
		});
	}
}


var fn_chk = function(_this){
	var selectNodeId = $(_this).attr("data-menu-sn");
	var selectCode = $(_this).attr("data-code");
	var childrenNode = $("#menuAuthorTree").jstree().get_node(selectNodeId).children_d;
	
	var totalCnt = childrenNode.length;
	var chkCnt = 0;
	for(var i=0; i <= childrenNode.length; i++){
		var sNode = $("#menuAuthorTree").jstree("get_node", childrenNode[i]);
		
		$.each(sNode.data, function(key, value) {
			if(key == selectCode){
				var selectName = $(value).attr("name");
				
				if($("input[name="+selectName+"]").is(":checked")){
					chkCnt++;
				}
			}	
		});
	}
	
	// 하위 노드 개수 == 체크된 노드 개수 
	if(totalCnt == chkCnt){
		fn_checked(_this, false);
	}else{
		fn_checked(_this, true);
	}
	
}

var fn_checked = function(_this, isChecked){
	var selectNodeId = $(_this).attr("data-menu-sn");
	var selectCode = $(_this).attr("data-code");
	var childrenNode = $("#menuAuthorTree").jstree().get_node(selectNodeId).children_d;
	
	for(var i=0; i <= childrenNode.length; i++){
		var sNode = $("#menuAuthorTree").jstree("get_node", childrenNode[i]);
		
		$.each(sNode.data, function(key, value) {
			if(key == selectCode){
				var selectName = $(value).attr("name");
				
				if(isChecked){
					$("input[name="+selectName+"]").prop("checked", true);
				}else{
					$("input[name="+selectName+"]").prop("checked", false);
				}
			}	
		});
	}
}



// 메뉴 max 정렬 순번 셋 
function fnMaxSortSnSet(divId, obj){
	var sortObj = {};
	
	$.each($("#"+divId).jstree(true).get_node(obj).children, function(key, value) {
		sortObj = Number($("#"+divId).jstree(true).get_node(value).original.menuSortSn);
	});

	return Math.max(sortObj) + 1;
}

// 텍스트 길이 측정
function fnTextWidthSet(str){
	$("#spanStrWidth").text(str);
	var retValue = $("#spanStrWidth").outerWidth();
	return retValue;
}