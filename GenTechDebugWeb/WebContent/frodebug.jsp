<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import ="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Gentech</title>
 <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
 <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
 <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
 <script src="fordebug.js"></script>
 <style>
 	.well{
 		overflow-y:scroll;
 		overflow-x:scroll;
 	}
 </style>
 
 
</head>
<%	
	List<String> tableHead =(List<String>) request.getAttribute("tableHead"); 
	List<String[]> rowValues =(List<String[]>) request.getAttribute("rowValues");
	List<StringBuilder> tcLogList =(List<StringBuilder>) request.getAttribute("tcLogList");
	Integer changeCount =(Integer) request.getAttribute("changeCount");
	String erroMessage =(String) request.getAttribute("erroMessage");
	String sqlComm = (String) request.getAttribute("sqlComm");
%>
<body>
<div class="container">
  <h2>Gentech Tool Page</h2>
    <% if(erroMessage!=null && erroMessage.length()!=0){ %>
	  <div class="alert alert-danger alert-dismissable">
	    <a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
	    <strong>錯誤!</strong><br><p><%=erroMessage %></p>
	  </div>
  	<%} %>
  	
  	<% if(changeCount!=null && erroMessage==null){ %>
	  <div class="alert alert-success alert-dismissable">
	    <a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
	    <strong>成功!</strong><br><p>修改<%=changeCount %>筆資料</p>
	  </div>
  	<%} %>
  	
  	<% if(sqlComm!=null && erroMessage==null){ %>
	  <div class="alert alert-info alert-dismissable">
	    <a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>
	    <strong>SQL:</strong><br><p><%=sqlComm %></p>
	  </div>
  	<%} %>
  
  <ul class="nav nav-tabs">
    <li class="active"><a data-toggle="tab" href="#sql_commd">下SQL指令</a></li>
    <li><a data-toggle="tab" href="#pase_log">查看PASE LOG</a></li>
    <li><a data-toggle="tab" href="#tomcat_log">查看TOMCAT LOG</a></li>
    <li><a data-toggle="tab" href="#menu3">其他</a></li>
  </ul>

  <div class="tab-content">
    <div id="sql_commd" class="tab-pane fade in active">
    	<div class="container">
	      <h3>此連線為EFLOW正式區</h3>
	      <form action="ForDebug" method ="post" id ="formForDB">
			<div class="form-group">
				<label for="sqltextarea">輸入SQL後按下送出:</label>
				<textarea rows="10" cols="50" id="sqltextarea" name="sqlComm" class="form-control">
				</textarea>
			</div>
			<input type="hidden" name = "action" value="forDB">
			<button type="button" class="btn btn-primary" id="sent_btn">
				送出
			</button>
			<button type="button" class="btn btn-danger" id="realclean_btn">
				清除
			</button>
			<button type="button" class="btn btn-info" id="clean_btn"<%if(tableHead==null){ %> style="display:none"<%} %>>
				篩選
			</button>
		  </form>
        </div>
<%--         <% if(sqlComm!=null){%> --%>
<!--         <div class="container"> -->
<!--         	<div class="well"> -->
<%--         		<jsp:include page="ShowDbResult.jsp"/> --%>
<!--         	</div> -->
<!--         </div> -->
<%--         <%} %> --%>
        <div class="container">
        	<% if(sqlComm!=null){%>
       		<div class="well">
        	<table id="result_table" class="table table-striped">
        		<thead >
        			<tr>
        				<th id="th_delete" style="display:none"><button id = "btn_real_delete">刪除</button></th>
						<%if(tableHead!=null){
							for(int i =0;i<tableHead.size();i++){ 
								String th = tableHead.get(i);  %>
								<th> <%=th%> </th>
							<%}%>
						<% } %>
					<tr>
				</thead>
				<tbody>
				<%if(rowValues!=null){
					for(int i =0;i<rowValues.size();i++){
						String[] row = rowValues.get(i);  %>
						<tr id ="tr_<%=i %>"> 
							<td class="td_delete" id="td_del_<%=i %>" style="display:none"><input class="cb_delete" type="checkbox"  id="cb_del_<%=i %>"></td>
							<%for(int j=0;j<row.length;j++){%>
								<td><%=row[j]%>></td>
							<%} %>
						</tr>
					<%}%>
				<% } %>
				</tbody>
			</table>
			</div>
			<%} %>
        </div>
    </div>
    
    <div id="pase_log" class="tab-pane fade">
      <h3>抓取位於PASE資料夾中的檔案</h3><p>註:FireFox不支援html5中的date</p>
            <div class="container">
		      	<form action="ForDebug" method ="post" id="FormFroPase">
		      		<div class="form-group row">
			      		<div class="col-xs-3 ">
			      			<label for="tomcatlog_date">請選擇日期</label>
			      			<input type ="date" class="form-control" id="paselog_date" name="tomcatlog_date">
			      		</div>
		      		</div>
		      		<input type="hidden" name = "action" value="" id="action_val">
		      		<input type="hidden" name = "paselog_date" value="" id="date_cal">
					<button type="button" class="btn btn-primary" id="paselog_check_btn">
						查看
					</button>
					<button type="button" class="btn btn-success" id="paselog_download_btn">
						下載
					</button>
		      	</form>
     		</div>
    </div>
    
    <div id="tomcat_log" class="tab-pane fade">
      <h3>抓取位於Tomcat的logs資料夾中的檔案</h3><p>FireFox不支援html5中的date</p>
      <div class="container">
      	<form action="ForDebug" method ="post" id="formForTomCat">
      		<div class="form-group row">
	      		<div class="col-xs-3 ">
	      			<label for="tomcatlog_date">請選擇日期</label>
	      			<input type ="date" class="form-control" id="tomcatlog_date" name="tomcatlog_date">
	      		</div>
	      		<div class="col-xs-3 ">
	      			<label for="sel_log_type">選擇log的prefix</label>
	      			<select class="form-control" id="sel_log_type" >
	      				<option>prefix類型</option>
					    <option>catalina.</option>
					    <option>localhost.</option>
					    <option>manager.</option>
					    <option>host-manager.</option>
					    <option>localhost_access_log.</option>
					  </select>
	      		</div>
      		</div>
      		<input type="hidden" name = "action" value="froTomcatLog" id="hidden_val">
      		<input type="hidden" name = "tomcatlog_date" value="" id="hidden_val2">
      		<input type="hidden" name = "prefix" value="" id="hidden_val3">
<!--       		<button type="submit" class="btn btn-primary" id="tomcatlog_sent_btn"> -->
<!-- 				查看 -->
<!-- 			</button> -->
			<button type="button" class="btn btn-primary" id="tomcatlog_check_btn">
				查看
			</button>
			<button type="button" class="btn btn-success" id="tomcatlog_download_btn">
				下載
			</button>
      	</form >
      </div>
   <%if(tcLogList!=null && tcLogList.size()>0){ %>
      <div class="container">
      	<div class="panel-group" id="accordion">
		    <div class="panel panel-default">
		      <div class="panel-heading">
		        <h4 class="panel-title">
		          <a data-toggle="collapse" data-parent="#accordion" href="#collapse1">catalina</a>
		        </h4>
		      </div>
		      <div id="collapse1" class="panel-collapse collapse in">
		        <div class="panel-body"><%=tcLogList.get(0).toString() %></div>
		      </div>
		    </div>
		    
		    <div class="panel panel-default">
		      <div class="panel-heading">
		        <h4 class="panel-title">
		          <a data-toggle="collapse" data-parent="#accordion" href="#collapse2">host-manager</a>
		        </h4>
		      </div>
		      <div id="collapse2" class="panel-collapse collapse">
		        <div class="panel-body"><%=tcLogList.get(1).toString() %></div>
		      </div>
		    </div>
		    
		    <div class="panel panel-default">
		      <div class="panel-heading">
		        <h4 class="panel-title">
		          <a data-toggle="collapse" data-parent="#accordion" href="#collapse3">localhost</a>
		        </h4>
		      </div>
		      <div id="collapse3" class="panel-collapse collapse">
		        <div class="panel-body"><%=tcLogList.get(2).toString() %></div>
		      </div>
		    </div>
		    
		    <div class="panel panel-default">
		      <div class="panel-heading">
		        <h4 class="panel-title">
		          <a data-toggle="collapse" data-parent="#accordion" href="#collapse4">manager</a>
		        </h4>
		      </div>
		      <div id="collapse4" class="panel-collapse collapse">
		        <div class="panel-body"><%=tcLogList.get(3).toString() %></div>
		      </div>
		    </div>
		    
		    <div class="panel panel-default">
		    <div class="panel-heading">
		        <h4 class="panel-title">
		          <a data-toggle="collapse" data-parent="#accordion" href="#collapse5">localhost_access_log</a>
		        </h4>
		      </div>
		      <div id="collapse5" class="panel-collapse collapse">
		        <div class="panel-body"><%=tcLogList.get(4).toString() %></div>
		      </div>
		    </div>
		   </div> 
  		</div> 
  	<%} %>
  	<div class="container" id="show_log_content">
  	</div>
  	
  		
    </div>
    <div id="menu3" class="tab-pane fade">
      <h3>其他</h3>
      
      <p>待建中</p>
    </div>
  </div>
</div>

</body>
</html>