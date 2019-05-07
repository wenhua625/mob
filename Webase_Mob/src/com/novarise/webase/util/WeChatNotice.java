package com.novarise.webase.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

















import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.framework.HtmlFunction;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.SQLUpdater;

public class WeChatNotice {
	/**
	 * 获取token
	 * 
	 * @param url
	 * @param grantType
	 * @param appid
	 * @param secret
	 * @return
	 */
	public static String getAccessToken(String url, String grantType,
			String appid, String secret) {
		String access_token = "";
		String tokenUrl = url + "?grant_type=" + grantType + "&appid=" + appid
				+ "&secret=" + secret;
		Map params = new HashMap();
		Object result = HttpUtil.doPost(tokenUrl, params);
		JSONObject jsons = JSONObject.parseObject(result.toString());
		String expires_in = jsons.getString("expires_in");
		if (expires_in != null && Integer.parseInt(expires_in) == 7200) {
			access_token = jsons.getString("access_token");
		} else {
			System.out.println("出错获取token失败！");
		}
		return access_token;
	}
	 public  boolean sendTemplateMsg(JSONObject json){  
	        boolean flag=false;  
	         
	        return flag;  
	 }
	

	 
	 
	 
	 
	 
	 /**
	  * 根据店铺获取用户有效的推送码
	  *
	  * @param agent_code 店铺编码
	  * @return 推送码
	  */
	 public static String[] getValidFormId(HttpServletRequest request) {
		 String[] str1 = null;
		 String ss="";
		 String sql = "select wx_openid from WeiXinSmart_UserAgent where agent_code='@@agent_code,'";
		 sql = HtmlFunction.parseVar(sql, request, "");
			try {
				SQLQuery query = DSManager.getSQLQuery();
				String[][] str = query.ArrayQuery(sql);
				for(int i = 0; i< str.length ; i++){
					String sqlUrl = "select top(1) form_id from weixinsmart_formId where open_id='"+str[i][0]+"' and add_time > DATEADD(DAY,-7,GETDATE())  order by add_time ";
					sqlUrl = HtmlFunction.parseVar(sqlUrl, request, "");
					String[][] arr = query.ArrayQuery(sqlUrl);
					if(arr.length > 0){
						for(int j=0;j<arr.length;j++){
							ss +=arr[j][0] +";";
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//用String的split方法分割，得到数组
		    str1 = ss.toString().split(";");
		   /* for(int a=0;a<str1.length;a++){
		    	System.out.println("第+"+a+"+个:" + str1[a]);
		    }*/
			return str1;
	 }
	 
	 /**
	  * 根据openid获取用户fromid 
	  *
	  * @param 
	  * @return 
	  */
	 public static String getFromid(HttpServletRequest request) {
		 String  fromId=null;
		 String open_id = request.getParameter("open_id");
		 String sql = "select top(1) form_id from weixinsmart_formId where open_id='"+open_id+"' and add_time > DATEADD(DAY,-7,GETDATE())  order by add_time ";
		 sql = HtmlFunction.parseVar(sql, request, "");
		 try {
			SQLQuery query = DSManager.getSQLQuery();
		//	System.out.println("sql 语句:"+sql);
			String[][] str = query.ArrayQuery(sql);				
			if( str.length > 0 ){
				fromId = str[0][0];
			}
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   	 }
			 
		 
		return fromId;
	 }
	 
	 /**
	  * 根据fromid获取用户openid
	  *
	  * @param 
	  * @return 
	  */
	 public static String getOPenid(HttpServletRequest request,String fromId) {
		 String openid=null;
		 if(fromId != null || fromId != ""){
			 String sql = "select open_id from WeiXinSmart_formID where form_id='"+fromId+"'";
			 sql = HtmlFunction.parseVar(sql, request, "");
			 try {
				SQLQuery query = DSManager.getSQLQuery();
			//	System.out.println("sql 语句:"+sql);
				String[][] str = query.ArrayQuery(sql);				
				if( str.length > 0 ){
					openid=str[0][0];
				}
			 } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		   	 }
		 }
		return openid;
	 }
	 public static String getAgentNameByCode(HttpServletRequest request) {
		 String name="";
		 String sql = "select agent_name from agent_list where agent_code='@@agent_code,'";
		 sql = HtmlFunction.parseVar(sql, request, "");
		 try {
			SQLQuery query = DSManager.getSQLQuery();
			//  System.out.println("sql 语句:"+sql);
			String[][] str = query.ArrayQuery(sql);				
			if( str.length > 0 ){
				name = str[0][0];
			}
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }	  	
		 
		return name;
	 }
	 /**
	  * 根据fromid删除数据库中的这一条记录
	  *
	  * @param 
	  * @return 
	  */
	 public static void delFromId(HttpServletRequest request,String fromId) {
		
		 if(fromId != null || fromId != ""){
			 String sql = "delete from WeiXinSmart_formID where form_id='"+fromId+"'";
			 sql = HtmlFunction.parseVar(sql, request, "");
			 try {
			//	System.out.println("str--sql语句查出的结果：" + sql);
				SQLUpdater upd = DSManager.getSQLUpdater();
				upd.executeUpdate(sql);
			 } catch (SQLException e) {
				e.printStackTrace();
		   	 }
		 }
		 //删除过期的from_id
		 String delSql = "delete from WeiXinSmart_formID  where add_time < DATEADD(day,-7,GETDATE())";
		 delSql = HtmlFunction.parseVar(delSql, request, "");
		 try {
			SQLUpdater upd = DSManager.getSQLUpdater();
			upd.executeUpdate(delSql);
		 } catch (SQLException e) {
			e.printStackTrace();
	   	 }
	 }
	 
	 
	 
	 /**
	  * 根据店铺code获取老板手机号
	  *
	  * @param 
	  * @return 
	  */
	 public static String getTelByAgent_Code(HttpServletRequest request) {
		 String agent_tel = "";
		 String sql = "select agent_tel from agent_list where agent_code='@@agent_code,'";
		 sql = HtmlFunction.parseVar(sql, request, "");
		 try {
			SQLQuery query = DSManager.getSQLQuery();
		//	System.out.println("sql 语句:"+sql);
			String[][] str = query.ArrayQuery(sql);				
			if( str.length > 0 ){
				agent_tel = str[0][0];
			}
		 } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   	 }	 
		 
		return agent_tel;
	 }
	 
	 /**
	  * http请求
	  * 
	  * @param requestUrl url地址
	  * @param requestMethod 请求方式 post/get
	  * @param outputStr 内容
	  */
	 public static String httpsRequest(String requestUrl, String requestMethod, String outputStr){
	        try {
	            URL url = new URL(requestUrl);
	            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            conn.setUseCaches(false);
	            // 设置请求方式（GET/POST）
	            conn.setRequestMethod(requestMethod);
	            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
	            // 当outputStr不为null时向输出流写数据
	            if (null != outputStr) {
	                OutputStream outputStream = conn.getOutputStream();
	                // 注意编码格式
	                outputStream.write(outputStr.getBytes("UTF-8"));
	                outputStream.close();
	            }
	            // 从输入流读取返回内容
	            InputStream inputStream = conn.getInputStream();
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String str = null;
	            StringBuffer buffer = new StringBuffer();
	            while ((str = bufferedReader.readLine()) != null) {
	                buffer.append(str);
	            }
	            // 释放资源
	            bufferedReader.close();
	            inputStreamReader.close();
	            inputStream.close();
	            inputStream = null;
	            conn.disconnect();
	            return buffer.toString();
	        } catch (ConnectException ce) {
	            System.out.println("连接超时：{}");
	        } catch (Exception e) {
	            System.out.println("https请求异常：{}");
	        }
	        return null;

	    }
	
}
