<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import ="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<title>Show Database Result </title>
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
</body>
</html>