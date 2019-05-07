package com.novarise.webase.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;

import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.util.XMLHandler;
import com.novarise.webase.util.XMLUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;
import com.ruirong.certUtil.ProcessMessage;

public class YYZFMonitor extends HttpServlet {
	/**
	 * Constructor of the object.
	 */
	public YYZFMonitor() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
	    InputStream inStream = request.getInputStream();
	    ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int len = 0;
	    while ((len = inStream.read(buffer)) != -1) {
	        outSteam.write(buffer, 0, len);
	    }
	    
	    outSteam.close();
	    inStream.close();
	    String result  = new String(outSteam.toByteArray(),"utf-8");//获取甬易支付调用我们url的返回信息
	    System.out.println("result:"+result);
	    
	  //  String result = "tranData=PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iR0JLIiA%2FPjxCMkNSZXM%2BPHRyYW5TZXJpYWxObz5aRjIwMTkwMzE4MTA2NDA3MzY3NDwvdHJhblNlcmlhbE5vPjxjdXJUeXBlPkNOWTwvY3VyVHlwZT48bWVyY2hhbnRJZD5NMTAwMDAyNjUzPC9tZXJjaGFudElkPjx0cmFuU3RhdD4xPC90cmFuU3RhdD48b3JkZXJObz5YUzIwMTkwMzE4MTcwNzwvb3JkZXJObz48cmVtYXJrPrCstqm79TwvcmVtYXJrPjx0cmFuVGltZT4yMDE5MDMxODE3MzM1MjwvdHJhblRpbWU%2BPG9yZGVyQW10PjE8L29yZGVyQW10PjxzdWJtZXJubz4wMDQ2MTc1MDwvc3VibWVybm8%2BPC9CMkNSZXM%2B&signData=en0QQ9RTLJMEIxQu19tLuhuYmLBJr1B0PZmIyR5vRYE9R8%2BxH%2B1jY%2FqqWiaI7civM%2BmW7QEBQjpI2VTy%2BY0Kqv6l2yWq4in06%2BbiwaGjRQFnxmgzT5lr0o2NlPgAS%2FzR8uz3lVffljMlwDPhHbfr6dLFil2J2FXxZP0vOP7MoGtqBGMl39XwTUyDTfvbSXihrzQLuvNXngUfWj7Lq%2FXW28L52xepcS7aZS7%2BAjeL%2FbWvsFYjve6NxBEXcHXBFdtiCEF%2BmYDpnQALjugrANzOja7MV9Nllg4BrY89yly1r6HqUwU8iz%2BF103ylQvmQsDYf5Si7OfV2bWpRNQnvJYOGQ%3D%3D&interfaceName=PayOrderNotify&version=B2C1.0";
		if( !result.equals("")){
			JSONObject jsonData = new JSONObject();

	    	String [] get_Amount=result.split("&");
			
			for(int i=0;i<get_Amount.length;i++){
	    		String keyvalue[]=get_Amount[i].split("=");
	    		if(keyvalue.length == 2){
	    		     String key=keyvalue[0];
	    		     String value=keyvalue[1];
	    		     jsonData.put(key, value);
	    		}
	    		
	    	}
			System.out.println("jsonData:"+jsonData);
			
			
			
			String interfaceName = jsonData.get("interfaceName").toString();
			String tranDataBase64 = jsonData.get("tranData").toString();
			tranDataBase64 =URLDecoder.decode(tranDataBase64);
			String signData = jsonData.get("signData").toString();
			signData =URLDecoder.decode(signData);
			String version = jsonData.get("version").toString();
			String tranDataGBK = new String(ProcessMessage.Base64Decode(tranDataBase64),"GBK");//通知base64解密用GBK
			String tranDataUTF8 = new String(ProcessMessage.Base64Decode(tranDataBase64),"UTF-8");//通知base64解密用UTF-8
			XMLHandler handler = new XMLHandler();
			net.sf.json.JSONObject result2 = handler.stringToXmlByDom4j(tranDataGBK,"GBK");

		    String agent_code=((String)result2.get("orderNo")).split("A")[1];
		    if( (result2.get("tranStat").toString()).equals("1")){
		   		String sk_amount  = result2.get("orderAmt").toString() ;
				 String bz=(String)result2.get("tranSerialNo")+" "+(String)result2.get("tranTime");
				 float f_amount = Float.parseFloat(sk_amount);
		    	 double j_amount = 0;
		    	 if(f_amount < 134){
					 j_amount = f_amount -1;
				 }else{
					 j_amount = f_amount/1.00755625;
				 }   
	
					
		    	 String sj_amount = new java.text.DecimalFormat("#.00").format(j_amount);  	 
				 String sql="{ call sp_cwsk_mob('@@agent_code,','@@sk_amount,','货款','@@bz,') }";
				 sql=sql.replaceAll("@@agent_code,", agent_code);
				 sql=sql.replaceAll("@@sk_amount,", sj_amount);
				 sql=sql.replaceAll("@@bz,", bz);
				 try {
					SQLUpdater updater = DSManager.getSQLUpdater();
					updater.executeCall(sql);					
							
					response.getWriter().write("success"); 
				 } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				 }
		   	 }
		}
	    
	   
	       
		
		
	
		
		
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
