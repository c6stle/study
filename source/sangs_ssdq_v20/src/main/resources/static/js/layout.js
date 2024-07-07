$(document).ready(function(){
    lnbInit();
    lnbDepthToggle();            
});

function lnbInit(){
    $('#lnb .menu .depth01 > li').each(function(){
        if($(this).hasClass('on')){
            $(this).find('.depth02').show()
        }
        if($(this).find('.depth02').length > 0){
            $(this).children('a').addClass('closed');
            $(this).children('a').prop('href', 'javascript:;');
        };
    });
}

function lnbDepthToggle(){
    $('#lnb .menu .depth01 > li > a.open, #lnb .menu .depth01 > li > a.closed').click(function(){
        $('#lnb .menu .depth02').slideUp(300);
        
        if($(this).hasClass('open')){
            $(this).closest('li').removeClass('on');
            $(this).removeClass('open').addClass('closed');
            $(this).siblings('.depth02').slideUp(300);
        } else{
            $(this).closest('li').addClass('on');
            $(this).removeClass('closed').addClass('open');
            $(this).siblings('.depth02').slideDown(300);
        }

        $('#lnb .menu .depth01 > li.on').not($(this).closest('li')).removeClass('on');
        $('#lnb .menu .depth01 > li > a.open').not($(this)).removeClass('open').addClass('closed');
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
}

// 팝업닫기
function popClose(id) {
    var id;

    $("#" + id).removeClass('on');
    dimRemove();
}

// dim 옵션 팝업 열기
function popOpenAndDim(id, isDim){
    popOpen(id);
    
    if(isDim == true){
        dimMaker();
    }
}