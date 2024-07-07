$(document).ready(function(){
    treeAction();
    iconTreeInit();
    toggleList();
    tab();

    if ($('.scroll-tbl-wrap').length > 0){
        $('.scroll-tbl-wrap').each(function(){
            var thisHeight = $(this).data('height');
            var frstTbl = $(this).find('.tbl-wrap').eq(0);
            var scdTbl = $(this).find('.tbl-wrap').eq(1);

            if($('.scroll-tbl-wrap').is('[data-height]')){
                $(this).css('height', thisHeight);
                scdTbl.css({
                    'height': 'calc(100% - '+ frstTbl.height() +'px)',
                    'overflow-x': 'hidden'
                });
                scdTbl.find('table').width(scdTbl.find('table').width() + ($(this).width() - scdTbl.find('table').width()))
            }
        });
    }
});

function treeAction(){
    $('.tree-wrap ul li .btn').click(function(){
        $(this).toggleClass('on');
        $(this).toggleClass('off');
        $(this).closest('p').siblings('ul').toggle();
    });
}

function iconTreeInit(){
    $('.icon-tree').not('.select').find('p').each(function(){
        if($(this).siblings('ul').length > 0){
            $(this).prepend('<button class="btn ic-folder"></button>')
        } else {
            $(this).prepend('<button class="btn ic-file"></button>')
        }
    })
}

function tab(){
    var thisNo = $('.tab-area > .tab-btn-area > .tab-btn.on').index();

    $('.tab-area > .tab-cont-area > .tab-cont').hide();
    $('.tab-area > .tab-cont-area > .tab-cont').eq(thisNo).show();
    
    $('.tab-area > .tab-btn-area > .tab-btn').click(function(){
        $(this).siblings('.tab-btn').removeClass('on');
        $(this).addClass('on');
        
        thisNo = $(this).index();

        $('.tab-area > .tab-cont-area > .tab-cont').hide();
        $('.tab-area > .tab-cont-area > .tab-cont').eq(thisNo).show();
    })
}

function toggleList(){
    $('.toggle-list > ul > li').click(function(){
        $(this).children('ul').toggle();
        $(this).toggleClass('on');
        $('.toggle-list > ul > li').not($(this)).children('ul').hide();
        $('.toggle-list > ul > li').not($(this)).removeClass('on');
    })
}