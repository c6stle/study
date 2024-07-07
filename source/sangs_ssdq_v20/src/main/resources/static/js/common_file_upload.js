 



/* 
	공통 파일 다운로드
	
	<form id="fileUploadForm" name="fileUploadForm">
		<input type="file" name="file"  />	<!--name을 file로 해야 인식함
		<input type="hidden" name="basePathId" value="mls.resource.base_path" />
		<input type="hidden" name="subDir" value="test/" />
	</form>
	
	fnCommSingleFileUpload("fileUploadForm", function(data) {
		if(data.resultCd == "OK") {
			console.log("resultCd : " + data.resultCd);		// 처리 결과 
			console.log("orgFileNm : " + data.orgFileNm);	// 업로드 한 파일명
			console.log("savedFileNm : " + data.savedFileNm);	// 저장된 파일명
			console.log("basePathId : " + data.basePathId);		// application.properties의 경로 id
			console.log("subDir : " + data.subDir);				// basePathId 의 하위 폴더
		} else {
			alert("업로드중 에러가 발생하였습니다.");
		}  

	});	
 */
function fnCommSingleFileUpload(formId, callbackFunc) {
	var form = $("#"+formId)[0];
	
	//console.log(form);
	//@RequestParam(required = true) String basePathId, @RequestParam(required = true) String fileNm)
	var data = new FormData(form);
	
	$.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "/cmmnUpload/upload",
        data: data,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
			callbackFunc(data);
        },
        error: function (e) {
			
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

