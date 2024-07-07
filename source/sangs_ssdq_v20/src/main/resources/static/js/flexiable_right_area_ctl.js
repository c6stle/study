var __flexiable_width_wide_flag = false;
var __flexiable_window_size = 1600;

function fnInitFlexiableRightArea() {
	/*
	.flexiableRightArear {display: none !important}
	body{min-width:1150px}
	#main_body_tage {min-width:1450px !important}
	*/
	__flexiable_width_wide_flag = fnChkFlexiableWide();
	
	fnFlexiableRightAreaCtl(true);
	/*
	$(parent.window).on("resize", function() {
		fnFlexiableRightAreaCtl();
	});
	*/
	parent.window.onresize = function(event){
		fnFlexiableRightAreaCtl(false);
	}
}

function fnChkFlexiableWide() {
	if(window.outerWidth < __flexiable_window_size) {
		//__flexiable_width_wide_flag = false;
		return false;
	} else {
		return true;
		//__flexiable_width_wide_flag = true;
	}
}
function fnChkChangeFlexiableWide() {
	var temp = fnChkFlexiableWide();
	if(temp == __flexiable_width_wide_flag) {
		return false;
	} else {
		__flexiable_width_wide_flag = temp;
		return true;
	}
}


function fnFlexiableRightAreaCtl(isInit) {
	// for frame (child page)
	console.log(window.outerWidth);
	
	
	if(fnChkChangeFlexiableWide() || isInit) {	// 바뀌었을때 
	
		console.log("바뀜" + fnChkChangeFlexiableWide());
		console.log($(".flexiableRightArea").attr("style"));
			
		if(__flexiable_width_wide_flag) {
			$(".flexiableRightArea").removeClass("flexiableRightArea_layer");
			$(".flexiableRightArea").addClass("flexiableRightArea_wide");
			
		} else {
			$(".flexiableRightArea").removeClass("flexiableRightArea_wide");
			$(".flexiableRightArea").addClass("flexiableRightArea_layer");
		}
		
	}  
	 
}	

function fnHideFlexiableRightArea() {
	$(".flexiableRightArea").css("display", "flex");
}

function fnShowFlexiableRightArea() {
	
	//$(".flexiableRightArear").attr("disply","flex");
	//$(".flexiableRightArea").addClass("aaaaaa");
	
	
	alert($(".flexiableRightArear").css("display"));
	
	//display: flex !important;
	
	
	///
	var dataWidth = $(".flexiableRightArea").attr("data-width");
	if(dataWidth.indexOf("%") == -1)
		dataWidth = dataWidth + "px";
	
	$(".flexiableRightArea").find(".view_wrap").css("min-height", "none");
	
	//$(".flexiableRightArea").css("height", (Number( ($(".flexiableRightArea").find(".table_area").css("height")).replace("px", "") ) + 120) + "px" );
	
	alert($(".flexiableRightArea").find(".view_wrap").css("height"));
	//$(".flexiableRightArea").css("height", $(".flexiableRightArea").find(".view_wrap").css("height"));
	
	$(".flexiableRightArea").css("height", "600px");
	
	setFrameHeight();
	
	//$(".left").css("height", $(".flexiableRightArea").find(".table_area").css("height"));
	
	$(".flexiableRightArear").animate( {
		width: dataWidth
     } , 500);

}

