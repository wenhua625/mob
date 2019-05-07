package com.novarise.webase.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.util.XMLHandler;
import com.ruirong.certUtil.ProcessMessage;

/**
 * 
 * @author Admininistrator
 *  甬易支付完成之后前端的回调
 */
public class YYZFMonitorURL extends HttpServlet {
	/**
	 * Constructor of the object.
	 */
	public YYZFMonitorURL() {
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
	    
		response.setContentType("text/html;charset=GBK");//解决中文乱码
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
			String interfaceName = jsonData.get("interfaceName").toString();
			String tranDataBase64 = jsonData.get("tranData").toString();
			tranDataBase64 =URLDecoder.decode(tranDataBase64,"UTF-8");
			String signData = jsonData.get("signData").toString();
			signData =URLDecoder.decode(signData,"UTF-8");
			String version = jsonData.get("version").toString();
			String tranDataGBK = new String(ProcessMessage.Base64Decode(tranDataBase64),"GBK");//通知base64解密用GBK
			String tranDataUTF8 = new String(ProcessMessage.Base64Decode(tranDataBase64),"UTF-8");//通知base64解密用UTF-8
			XMLHandler handler = new XMLHandler();
			net.sf.json.JSONObject result2 = handler.stringToXmlByDom4j(tranDataGBK,"GBK");
			
		   // String agent_code=((String)result2.get("orderNo")).split("A")[1];
			String tranStat = result2.get("tranStat").toString();
			String orderAmt  = result2.get("orderAmt").toString() ;
			StringBuilder sb = new StringBuilder();
	         sb = sb.append("<html><head><meta charset=\"utf-8\" />");
	         sb = sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />");
	       //  sb = sb.append("<style>.button {	border:none;	background:none;cursor:pointer;		color:blue;	font-size:54erm;	} </style>");
	         sb = sb.append("<style type=\"text/css\">");
	         sb = sb.append(" *{margin:0; padding:0;} body{margin:0 auto; text-align:center;} #main{width:960px; height:1000px; margin:0 auto;} .pay{height:3rem; } #span1{font-size:2rem;}  #span2{font-size:3rem;}");
	         sb = sb.append(" .btn{border:none;border-radius:0.25rem;height: 3rem;width: auto; margin: 0.8rem 0.25rem; background-color: #78c4ec;font-size:2rem;color:#fff;display: inline-block; vertical-align:top;}");
	         sb = sb.append("</style>");
	         sb = sb.append("</head><body>");
	         sb = sb.append("<div id=\"mian\"> <div class=\"pay\" >&nbsp;</div> 	<div id=\"top\"><span id=\"span1\">￥</span><span id=\"span2\">"+orderAmt+"</span></div>");
		        
	         
			String stat ="";
			if(tranStat.equals("0")){
				stat = "未支付";
			}else if(tranStat.equals("1")){
				stat = "已支付";				
			}else if(tranStat.equals("2")){
				stat = "付款失败";			
			}

			sb = sb.append(" <div class=\"pay\" >"+ stat +" </div>");
			sb = sb.append("<input id=\"tranStat\" name=\"tranStat\" type=\"hidden\" 	value=\"");
			sb = sb.append(tranStat +"\" />");
			
			sb = sb.append("<div id=\"foot\"> <input class=\"btn\" type=\"button\" id=\"submitBtn\" value=\"          返  回          \">    </div>	</div>");
			//sb = sb.append("<script>	document.getElementById(\"submitBtn\").onclick = function(){ 						alert(document.getElementById(\"tranStat\").value);				} 	 </script>");
			sb = sb.append("<script>	 function btnPayResults(document.getElementById(\"tranStat\").value){ 	 			} 	  android.setPayResult(document.getElementById(\"tranStat\").value)  ;     btnPayResults(document.getElementById(\"tranStat\").value);</script>");
			
			sb = sb.append("<script>	 function  btnPay(document.getElementById(\"tranStat\").value){ 							} 	 </script>");
			sb = sb.append("<script>	document.getElementById(\"submitBtn\").onclick = function(){ 		btnPay(document.getElementById(\"tranStat\").value);				android.payResult(document.getElementById(\"tranStat\").value);	} 	 </script>");
			
			
			
			sb = sb.append("</body></html>");
		    response.getWriter().write(sb.toString());
		     
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
