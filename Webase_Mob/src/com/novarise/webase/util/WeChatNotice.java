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
	 * ��ȡtoken
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
			System.out.println("�����ȡtokenʧ�ܣ�");
		}
		return access_token;
	}
	 public  boolean sendTemplateMsg(JSONObject json){  
	        boolean flag=false;  
	         
	        return flag;  
	 }
	

	 
	 
	 
	 
	 
	 /**
	  * ���ݵ��̻�ȡ�û���Ч��������
	  *
	  * @param agent_code ���̱���
	  * @return ������
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
			//��String��split�����ָ�õ�����
		    str1 = ss.toString().split(";");
		   /* for(int a=0;a<str1.length;a++){
		    	System.out.println("��+"+a+"+��:" + str1[a]);
		    }*/
			return str1;
	 }
	 
	 /**
	  * ����openid��ȡ�û�fromid 
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
		//	System.out.println("sql ���:"+sql);
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
	  * ����fromid��ȡ�û�openid
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
			//	System.out.println("sql ���:"+sql);
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
			//  System.out.println("sql ���:"+sql);
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
	  * ����fromidɾ�����ݿ��е���һ����¼
	  *
	  * @param 
	  * @return 
	  */
	 public static void delFromId(HttpServletRequest request,String fromId) {
		
		 if(fromId != null || fromId != ""){
			 String sql = "delete from WeiXinSmart_formID where form_id='"+fromId+"'";
			 sql = HtmlFunction.parseVar(sql, request, "");
			 try {
			//	System.out.println("str--sql������Ľ����" + sql);
				SQLUpdater upd = DSManager.getSQLUpdater();
				upd.executeUpdate(sql);
			 } catch (SQLException e) {
				e.printStackTrace();
		   	 }
		 }
		 //ɾ�����ڵ�from_id
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
	  * ���ݵ���code��ȡ�ϰ��ֻ���
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
		//	System.out.println("sql ���:"+sql);
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
	  * http����
	  * 
	  * @param requestUrl url��ַ
	  * @param requestMethod ����ʽ post/get
	  * @param outputStr ����
	  */
	 public static String httpsRequest(String requestUrl, String requestMethod, String outputStr){
	        try {
	            URL url = new URL(requestUrl);
	            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setDoInput(true);
	            conn.setUseCaches(false);
	            // ��������ʽ��GET/POST��
	            conn.setRequestMethod(requestMethod);
	            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
	            // ��outputStr��Ϊnullʱ�������д����
	            if (null != outputStr) {
	                OutputStream outputStream = conn.getOutputStream();
	                // ע������ʽ
	                outputStream.write(outputStr.getBytes("UTF-8"));
	                outputStream.close();
	            }
	            // ����������ȡ��������
	            InputStream inputStream = conn.getInputStream();
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String str = null;
	            StringBuffer buffer = new StringBuffer();
	            while ((str = bufferedReader.readLine()) != null) {
	                buffer.append(str);
	            }
	            // �ͷ���Դ
	            bufferedReader.close();
	            inputStreamReader.close();
	            inputStream.close();
	            inputStream = null;
	            conn.disconnect();
	            return buffer.toString();
	        } catch (ConnectException ce) {
	            System.out.println("���ӳ�ʱ��{}");
	        } catch (Exception e) {
	            System.out.println("https�����쳣��{}");
	        }
	        return null;

	    }
	
}
