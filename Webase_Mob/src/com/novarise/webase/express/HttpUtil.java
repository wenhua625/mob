package com.novarise.webase.express;






import net.sf.json.JSONObject;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;






public class HttpUtil {
	
	
    /** 
     * post������������json��ʽ�Ĳ����� 
     * @param url 
     * @param params 
     * @return 
     */  
    public static String doPost(String url, String params) throws Exception {    
    	
	    try { 	    
	 	    PostMethod postMethod = null;
	 	    postMethod = new PostMethod(url) ;
	 	    postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
	           
	 	   JSONObject json =  JSONObject.fromObject(params);
	
	       String timestamp =  (json.get("timestamp")).toString();
	       String param =  (json.get("params")).toString();
	       String digest = (json.get("digest")).toString() ;
	       String companyCode = (json.get("companyCode")).toString();
	 	//�������ã���Ҫע��ľ�����߲��ܴ�NULL��Ҫ�����ַ���
	 	    NameValuePair[] data = {
	 	            new NameValuePair("params", param),
	 	            new NameValuePair("digest",digest),
	 	            new NameValuePair("timestamp",  timestamp),
	 	            new NameValuePair("companyCode", companyCode)
	 	    };
	
	 	    postMethod.setRequestBody(data);
	
	 	    org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
	 	     httpClient.executeMethod(postMethod); // ִ��POST����
	 	    String result = postMethod.getResponseBodyAsString() ;
	
	 	    return result;
	 	} catch (Exception e) {
	 	   System.out.println("�����쳣"+e.getMessage());
	 	    return "";
	 	}       

    }  
   
 

  

}
