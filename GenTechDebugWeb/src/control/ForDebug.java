package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.text.*;

public class ForDebug extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Context ctx ;
	private DataSource ds;
	private Connection conn= null;
	private PreparedStatement prt = null;
	private ResultSet rs =null;
	private ResultSetMetaData rsmt =null;
	private Logger logger = Logger.getLogger(ForDebug.class.getName());
//	private final String tomcatLogDest = "D:/Tomcat Log/logs/";//正式
//	private final String  paseLogDest = "D:/Pase Log/";//正式
	private final String tomcatLogDest = "D:/server/tomcat/logs/";//本機測試
	private final String  paseLogDest = "D:/agentflow/PASE/log/";//本機測試
	
    public ForDebug() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		try {
			ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB");
			conn = ds.getConnection();
			logger.log(Level.INFO,"成功取得連線");
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		try {
			if(rs!=null){rs.close();}
			if(prt!=null){prt.close();}
			if(conn!=null){conn.close();}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String todayStr = sdf.format(new Date());	
		logger.log(Level.INFO,"todayStr:"+todayStr);
		
		String action = request.getParameter("action");
		String sqlComm = request.getParameter("sqlComm");
		


		Integer changeCount;
		String tomcatlog_date ;
		String paselog_date;
		String prefix="";

		if("forDB".equals(action) && sqlComm.trim().length()!=0){	
			try {
				if(conn==null){
					logger.log(Level.INFO,"connection is null try get connection again");
					conn = ds.getConnection();
				}
				sqlComm = new String(sqlComm.getBytes("ISO8859-1"),"utf-8");//因應中文故需重新編碼
				logger.log(Level.INFO,"sqlComm:"+sqlComm);
				
				conn.setAutoCommit(false);
				prt = conn.prepareStatement(sqlComm);
				
				if(prt.execute()){
					rs = prt.getResultSet();
					rsmt = rs.getMetaData();
					int column = rsmt.getColumnCount();
					List<String> tableHead = new ArrayList<String>();
					List<String[]> rowValues = new ArrayList<String[]>();
					
					for(int i =1;i<=column;i++){
						tableHead.add(rsmt.getColumnName(i));
					}
					
					while(rs.next()){
						String[] values = new String[column];
						for(int i=1;i<column;i++){
							values[i-1] = rs.getString(i);
						}
						rowValues.add(values);
					}
					
					request.setAttribute("tableHead", tableHead);
					request.setAttribute("rowValues", rowValues);
					request.setAttribute("sqlComm", sqlComm);
				}else{
					changeCount = prt.getUpdateCount();
					conn.commit();
					request.setAttribute("changeCount",changeCount);
					request.setAttribute("sqlComm", sqlComm);
				}
			} catch (SQLException e) {			
				String erroMessage = e.getErrorCode()+e.getSQLState()+e.getMessage();
				request.setAttribute("erroMessage",erroMessage );
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}

//			request.getRequestDispatcher("/ShowDbResult.jsp").include(request, response);
			request.getRequestDispatcher("/frodebug.jsp").forward(request, response);
		}
		
		
		
		if("forCheckPaseLog".equals(action)){
			cleanFile(request);
			paselog_date = request.getParameter("paselog_date");
			
			if(paselog_date.length()>0){
				prefix = todayStr.equals(paselog_date)?"PASE":"PASE.log_";
				paselog_date = todayStr.equals(paselog_date)?"":paselog_date;
				String fileType = ".log";
				String fileName = prefix+paselog_date+fileType;
				logger.log(Level.INFO,"Pase Log fileName:"+fileName);
				
				File logFile = new File(paseLogDest+fileName);				
				logger.log(Level.INFO,"Pase Log File Directory:"+paseLogDest+fileName);
				
				String fileUri = request.getRequestURL().substring(0,request.getRequestURL().lastIndexOf("/"))+"/logtemp/"+fileName;
				logger.log(Level.INFO,"Pase Log File Uri:"+fileUri);
				
				if(logFile.exists()){
					@SuppressWarnings("deprecation")
					String  destination = request.getRealPath("/logtemp/"+fileName);
					InputStream in = new FileInputStream(logFile);
					OutputStream output = new FileOutputStream(new File(destination));
					dumpFile(in,output);
				}				
				response.getWriter().print(fileUri);
			}
		}
		
		
		if("downloadPaseLog".equals(action)){
			paselog_date =request.getParameter("paselog_date");	
			if(paselog_date.length()>0){
				prefix = todayStr.equals(paselog_date)?"PASE":"PASE.log_";
				paselog_date = todayStr.equals(paselog_date)?"":paselog_date;
				String fileType = ".log";
				String fileName = prefix+paselog_date+fileType;
				logger.log(Level.INFO,"Pase Log fileName:"+fileName);
				
				File logFile = new File(paseLogDest+fileName);
				logger.log(Level.INFO,"Pase Log File Directory:"+logFile);
				
				if(logFile.exists()){
					response.setHeader("Content-Disposition","attachment; filename=\"" + URLEncoder.encode(logFile.getName(), "UTF-8") + "\"");
					response.setContentType("application/octet-stream");
					InputStream in = new FileInputStream(logFile);
					OutputStream output = response.getOutputStream();
					dumpFile(in,output);
				}	
			}
		}
		
		
		if("forCheckTcLog".equals(action)){
			cleanFile(request);
			
			prefix = request.getParameter("prefix");
			tomcatlog_date =request.getParameter("tomcatlog_date");
			
			if(!prefix.equals("prefix類型") && tomcatlog_date.length()>0){
				String fileType = prefix.equals("localhost_access_log.")? ".txt":".log";
				String fileName = prefix+tomcatlog_date+fileType;	
				logger.log(Level.INFO,"Tomcat Log fileName:"+fileName);
				
				File logFile = new File(tomcatLogDest+fileName);
				logger.log(Level.INFO,"Tomcat Log File Directory:"+tomcatLogDest+fileName);
				
				String fileUri = request.getRequestURL().substring(0,request.getRequestURL().lastIndexOf("/"))+"/logtemp/"+fileName;
				logger.log(Level.INFO,"Tomcat Log File Uri:"+fileUri);
				
				if(logFile.exists()){
					@SuppressWarnings("deprecation")
					String  destination = request.getRealPath("/logtemp/"+fileName);
					InputStream in = new FileInputStream(logFile);
					OutputStream output = new FileOutputStream(new File(destination));
					dumpFile(in,output);
				}				
				response.getWriter().print(fileUri);
			}
		}
		
		
		if("downloadTomcatLog".equals(action)){		
			prefix = request.getParameter("prefix");
			tomcatlog_date =request.getParameter("tomcatlog_date");	
			if(!prefix.equals("prefix類型") && tomcatlog_date.length()>0){
				String fileType = prefix.equals("localhost_access_log.")?".txt":".log";
				String fileName = prefix+tomcatlog_date+fileType;
				logger.log(Level.INFO,"Tomcat Log fileName:"+fileName);
				
				File logFile = new File(tomcatLogDest+fileName);
				logger.log(Level.INFO,"Tomcat Log File Directory:"+tomcatLogDest+fileName);

				if(logFile.exists()){
					response.setHeader("Content-Disposition","attachment; filename=\"" + URLEncoder.encode(logFile.getName(), "UTF-8") + "\"");
					response.setContentType("application/octet-stream");
					InputStream in = new FileInputStream(logFile);
					OutputStream output = response.getOutputStream();
					dumpFile(in,output);
				}	
			}
		}	
		
		
		if("froTomcatLog".equals(action)){
			tomcatlog_date =request.getParameter("tomcatlog_date");
			File logFile = null;
			BufferedReader bfread =null;
			FileReader fread=null;
			String[] prefixArry = {"catalina.","host-manager.","localhost.","manager.","localhost_access_log."};
			List<StringBuilder> tcLogList = new ArrayList<StringBuilder>();
			if(tomcatlog_date!=null && tomcatlog_date.trim().length()!=0){
				System.out.println(tomcatlog_date);
				try{
					for(int i =0;i<prefixArry.length;i++){
						String fileType = i==4?".txt":".log";
						String fileName = prefixArry[i]+tomcatlog_date+fileType;
						logFile = new File(tomcatLogDest+fileName);
						if(logFile.exists()){
							fread = new FileReader(logFile);
							bfread = new BufferedReader(fread);
							StringBuilder content = new StringBuilder();
							while(bfread.readLine()!=null){
								content.append(bfread.readLine());
							}
							tcLogList.add(content);
						}
					}
					bfread.close();
					fread.close();
					request.setAttribute("tcLogList",tcLogList );
				}catch(IOException ioe){
					String erroMessage = ioe.getMessage();
					ioe.printStackTrace();
					fread.close();
					bfread.close();
					request.setAttribute("erroMessage",erroMessage );
				}
			}
			request.getRequestDispatcher("/frodebug.jsp").forward(request, response);
		}
	}
	
	private void dumpFile(InputStream in,OutputStream output){
		byte[] b = new byte[2048];
		int len;
		try{
			while((len = in.read(b))>0){
				output.write(b,0,len);
			}
			in.close();
			output.flush();
			output.close();
		}catch(IOException fne){
			try {
				in.close();
				output.flush();
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fne.printStackTrace();
		}
		
	}
	
	private void cleanFile(HttpServletRequest request){
		//先檢查資料夾是否存在
		@SuppressWarnings("deprecation")
		File logtemp = new File(request.getRealPath("/logtemp"));
		if(!logtemp.exists()){
			try {
				if(logtemp.mkdir()){
					logger.log(Level.INFO,"成更建立logtemp資料夾 url:"+logtemp.getPath());
//					System.out.println(logtemp.getPath());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(logtemp.isDirectory()&&logtemp.list().length>0){
			for(int i =0;i<logtemp.list().length;i++){
				logtemp.listFiles()[i].delete();
				logger.log(Level.INFO,"清除logtemp資料夾  file:"+logtemp.listFiles()[i].getName());
			}
		}
	}
}
