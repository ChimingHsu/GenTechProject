
function main(){
	$('#sent_btn').click(function(){
		var sqlStr = $('#sqltextarea').val().trim();
		if(sqlStr.charAt(sqlStr.length-1)==";"){
			$('#sqltextarea').val(sqlStr.substring(0,sqlStr.length-1));
		}
		$('#formForDB').submit();
	});

	
	$('#clean_btn').text('篩選');

	$('#realclean_btn').click(function(){
		$('#sqltextarea').val('');
	});
	
	$('#clean_btn').click(function(){
		if($('#clean_btn').text()==='篩選'){
			$('#clean_btn').text('取消');
			$('#clean_btn').attr('class','btn btn-warning');
		}else{
			$('#clean_btn').text('篩選');
			$('#clean_btn').attr('class','btn btn-info');
			//$('.cb_delete').removeAttr('checked');
			//$('.cb_delete').attr('checked', false);
			$('.cb_delete').prop('checked', false);
		}
		$('#th_delete').toggle();
		$('.td_delete').toggle();
	});
	
	$('#btn_real_delete').click(function(){
		var trs = $('tbody:first').children('tr');
		for(var i =0;i<trs.length;i++){
			var cbId = "cb_del_"+i;
			//用attr判斷會錯誤!!
			if($('#'+cbId).prop('checked')){
				var trId = "tr_"+i;
				$('#'+trId).remove();
			}
		}
	});
	
	$('#tomcatlog_check_btn').click(function(){
		var data = {'action':'forCheckTcLog',
				 	'tomcatlog_date':$('#tomcatlog_date').val(),
				 	'prefix':$('#sel_log_type').val()
					};
		
		var callback = function(url){
			if(url===""){
				alert("請選擇日期和prefix");
			}else{
				window.open(url);
			}
		}
		$.post('ForDebug',data,callback);
	});
	

	$('#tomcatlog_download_btn').click(function(){
		$('#hidden_val').val('downloadTomcatLog');
		$('#hidden_val2').val($('#tomcatlog_date').val());
		$('#hidden_val3').val($('#sel_log_type').val());
		$('#formForTomCat').submit();
	});
	
	
	$('#paselog_check_btn').click(function(){
		var data = {'action':'forCheckPaseLog',
				 	'paselog_date':$('#paselog_date').val()
					};
	
		var callback = function(url){
			if(url===""){
				alert("請選擇日期");
			}else{
				window.open(url);
			}
	
		}
		$.post('ForDebug',data,callback);
	});

	
	
	$('#paselog_download_btn').click(function(){
		$('#action_val').val('downloadPaseLog');
		$('#date_cal').val($('#paselog_date').val());
		$('#FormFroPase').submit();
	})

}


window.onload = main;
//window.addEventListener('load', main, false);

