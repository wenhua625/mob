package com.novarise.webase.express;






import net.sf.json.JSONObject;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;






public class HttpUtil {
	
	
    /** 
     * post请求（用于请求json格式的参数） 
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
	 	//参数设置，需要注意的就是里边不能传NULL，要传空字符串
	 	    NameValuePair[] data = {
	 	            new NameValuePair("params", param),
	 	            new NameValuePair("digest",digest),
	 	            new NameValuePair("timestamp",  timestamp),
	 	            new NameValuePair("companyCode", companyCode)
	 	    };
	
	 	    postMethod.setRequestBody(data);
	
	 	    org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
	 	     httpClient.executeMethod(postMethod); // 执行POST方法
	 	    String result = postMethod.getResponseBodyAsString() ;
	
	 	    return result;
	 	} catch (Exception e) {
	 	   System.out.println("请求异常"+e.getMessage());
	 	    return "";
	 	}       

    }  
   
 

  

}
