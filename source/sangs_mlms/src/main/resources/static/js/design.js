$( document ).ready(function() {

	//레이아웃
	fn_layout();


	// var bodySize = $('body').outerWidth();
	// var HtmlSize = $('html').outerWidth();
	// if(bodySize < 1601 && $('body').hasClass('dashboard')) {
	// 	$('.btn_fold').addClass('toggled');
	// 	$('.left_area').addClass('fold');
	// 	$('#content').addClass('fullsize');
	// }else if(HtmlSize < 740 && $('body').hasClass('dashboard') == false){
	// 	$('.btn_fold').addClass('toggled');
	// 	$('.left_area').addClass('fold');
	// 	$('#content').addClass('fullsize');
	// }
	// var contentHeight = $('.content_area').outerHeight();
	// var bodyHeight = $('body').outerHeight();
	// if(contentHeight > bodyHeight){
	// 	$('.left_area').css('min-height',contentHeight+ 71 +'px');
	// }
	
	// $( window ).resize( function() {
	// 	var bodySize = $('body').outerWidth();
	// 	var HtmlSize = $('html').outerWidth();
		
	// 	if(bodySize < 1601 && $('body').hasClass('dashboard')) {
	// 		$('.btn_fold').addClass('toggled');
	// 		$('.left_area').addClass('fold');
	// 		$('#content').addClass('fullsize');
	// 		$('.lnb_menu ul li').find('a.open').addClass('closed').removeClass('open');
	// 		$('.sub_menu').css('display','none');
	// 	}else if(bodySize > 1601 && $('body').hasClass('dashboard')){
	// 		$('.btn_fold').removeClass('toggled');
	// 		$('.left_area').removeClass('fold');
	// 		$('#content').removeClass('fullsize');
	// 	}
	// 	if(HtmlSize < 740 && $('body').hasClass('dashboard') == false){
	// 		$('.btn_fold').addClass('toggled');
	// 		$('.left_area').addClass('fold');
	// 		$('#content').addClass('fullsize');
	// 		$('.lnb_menu ul li').find('a.open').addClass('closed').removeClass('open');
	// 		$('.sub_menu').css('display','none');
	// 	}else if(HtmlSize > 740 && $('body').hasClass('dashboard') == false){
	// 		$('.btn_fold').removeClass('toggled');
	// 		$('.left_area').removeClass('fold');
	// 		$('#content').removeClass('fullsize');
	// 	}
		
	// });

	
	/* lnb메뉴 
	$('.lnb_menu ul li a').on('click',function(){
			//맨처음 on을 지운다
		// $('.lnb_menu ul li a').parent('li').removeClass('on');

		// if($(this).hasClass('open') == false){
		// 	$('.lnb_menu').find('a.open').next().slideUp('fast');
		// 	$('.lnb_menu').find('a.open').addClass('closed').removeClass('open');
		// }
		// if($(this).siblings('ul.sub_menu').length != 0){
		// 	if($(this).hasClass('closed')){
		// 		$(this).removeClass('closed').addClass('open');
		// 		$(this).next().slideDown('fast');
		// 	}else if($(this).hasClass('closed') == false) {
		// 		$(this).addClass('closed').removeClass('open');
		// 		$(this).next().slideUp('fast');
		// 		$(this).parents().removeClass('on');
		// 	}
		// }
		// $(this).parent().addClass('on');
		
		// $('.lnb_menu ul li').removeClass('on');
		$(this).parent('li').toggleClass('on');
		$(this).next('ul').slideToggle('fast');

		if($(this).parent('li').hasClass('on') == true && $(this).next('ul').css('display') == 'block'){
			//  $(this).parent('li').removeClass('on');
			//  $(this).next('ul').slideToggle('fast');
			return false; 
		}
		
		// else {
		// 	$('.lnb_menu ul li a').next('ul').slideToggle('fast');
		// 	return false; 
		// }
	});
	*/
	tab(); // 20210916 수정

	$('div.selectbox').on("click", function(){
		$(this).parent('.select_search').toggleClass('on');
	});

	$('.select_list_area ul li').on("click", function(){
		var select_txt = $(this).text();
		$('div.selectbox span').text(select_txt);
		$('div.select_search ').removeClass('on');
	});

	$('.tbl_wrap ul.tbl_body li, .view_list ul.tbl_body li').click(function(){/* 20210909 수정 */
		$(this).toggleClass('on');
		$(this).siblings('li').removeClass('on')
	})

	$('.tbl_wrap, .view_list').each(function(){ /* 20210909 수정 */
		var colHeight = $(this).data('height');

		$(this).css('max-height', colHeight);
	});

	/* 20210916 수정 */
	$('.tbl_top .list_toggle_btn').click(function(){
		if($(this).text() == "목록 접기"){
			$(this).text("목록 열기");
			$(this).addClass('on');
		} else{
			$(this).text("목록 접기");
			$(this).removeClass('on');
		}
	});
	/* // 20210916 수정 */

	$('.row').each(function(){
		$(this).children('div').each(function(){
			var colWidth = $(this).data('width');
		
			if(colWidth > 0){
				$(this).css({
					width: colWidth,
					flex: 'none'
				})
			} else{
				$(this).css({
					flex: '1 0'
				})
			}
		})
		
	})
});

/* 20210916 수정 */
$(window).on('load',function(){
	setGridWidth();
})

function setGridWidth(){
	$('.tbl_wrap ul li .col, .view_list ul li .col').each(function(){
		var colWidth = $(this).data('width');
		var rowWidth = $(this).closest('li').width();

		if(colWidth > 0){
			$(this).css('width', (colWidth/$(this).closest('li').width())*100 + '%'); // 20210826 수정
		} else{
			$(this).css('flex', '1 0');
		}
	});
}
/* // 20210916 수정 */

// 레이아웃
function fn_layout(){
	//메인 즐겨찾기
	$('a.bookmark').on('click',function(){
		$(this).toggleClass('active');
	});

	//셀렉트 박스
	var selectTarget = $('.selectbox select');
	selectTarget.change(function(){
		var select_name = $(this).children('option:selected').text();
		$(this).siblings('label').text(select_name);
	});
	
	//lnb 메뉴 열고닫기
	$('.btn_fold').click(function(){
			/*	수정 2021.10.26
		var bodySize = $('body').outerWidth();
		var HtmlSize = $('html').outerWidth();
		*/
		
		$(this).toggleClass('toggled');
		$('.left_area').toggleClass('fold');
		$('#content').toggleClass('fullsize');
		/*	수정 2021.10.26
		if(bodySize > 1601 && $('body').hasClass('dashboard')){
			$(this).toggleClass('toggled');
			$('.left_area').toggleClass('fold');
			$('#content').toggleClass('fullsize');
		}else if(HtmlSize > 740 && $('body').hasClass('dashboard') == false){
			$(this).toggleClass('toggled');
			$('.left_area').toggleClass('fold');
			$('#content').toggleClass('fullsize');
		}
		*/
		//서브메뉴가 열린상태에서 lnb를 접엇을경우 초기화
		/* 수정 2021.10.26
		// $('.lnb_menu ul li a').parent('li').removeClass('on');
		$('.lnb_menu ul li').find('a.open').addClass('closed').removeClass('open');
		$('.sub_menu').css('display','none');
		*/ 
		
		$(this).toggleClass('fold');

		// $('.lnb_menu ul li a').parent('li').removeClass('on');
		if($(".left_area").hasClass("fold"))		// 왼쪽으로 접혔을때 모든 하위메뉴를 닫는다.
			$('.lnb_menu ul li a').next('ul').slideUp('fast');
	});

};

function listToggle(target){
    var target;

    $('#' + target).find('ul.tbl_body li').not('.on').toggle();
	if($('#' + target).data('height') > $('#' + target).height()){
		$('.tbl_wrap ul').css("width", '');
	} else{
		$('.tbl_wrap ul').css("width", 'auto');
	}
}

/* 20210916 수정 */
function tab(){
    $('.tab_wrap .tab_btn_wrap .tab_btn').click(function(){
        $(this).addClass('on');
        $(this).siblings().removeClass('on');
    
        var tab = $(this).children().attr('data-tab');
        // var tabName = $(this).children().text();

        // $(this).closest('.tab_wrap').find('.tab_select').text(tabName);

        if(tab > 0){
            $(this).closest(".tab_wrap").children('.tab_cont_wrap').children(".tab_content[data-tab='"+ tab + "']").show();
            $(this).closest(".tab_wrap").children('.tab_cont_wrap').children(".tab_content[data-tab='"+ tab + "']").not().siblings('.tab_content').hide();

			setGridWidth();//20211007 수정
			
			// 2021.11.24 수정 
			try {
				if($(this).attr("data-callbackfn") && $(this).attr("data-callbackfn") != undefined && $(this).attr("data-callbackfn") != "") {
					eval($(this).attr("data-callbackfn"));
				}
			} catch(e) {}
        }
    });
}

// dim 생성
function dimMaker() {
    if($('body').find('.dim').length > 0){
        return;
    }
    $('body').append('<div class="dim"></div>');
    bodyHidden();
}

// dim 제거
function dimRemove() {
    $('.dim').remove();
    bodyAuto();
}

// body scroll hidden
function bodyHidden() {
    $('body').css('overflow', 'hidden');
}

// body scroll auto
function bodyAuto() {
    $('body').css('overflow', '')
}

// 팝업열기
function popOpen(id){
    var id;

    $("#" + id).addClass('on');

    if($("#" + id).height() > $(window).height()){
        $("#" + id).children('.pop_content').css({
            'overflow': 'auto',
            'max-height': $(window).height() -  $("#" + id).children('.pop_header').outerHeight() - $("#" + id).children('.pop_footer').outerHeight() - 50
        });
    }
}

// 팝업닫기
function popClose(id) {
    var id;

    $("#" + id).removeClass('on');
}

// dim 옵션 팝업 열기
function popOpenAndDim(id, isDim){
    popOpen(id);
	setGridWidth();
    
    if(isDim == true){
        dimMaker();
    }
}

// dim 옵션 팝업 열기
function popCloseAndDim(id, isDim){
    popClose(id);
    
    if(isDim == true){
        dimRemove();
    }
}

function toggleBtn(target){
    var target;

    $('.' + target).toggle();
}
/* //20210916 수정 */