package com.novarise.webase.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.novarise.webase.BConstants;



public class HttpUtil {
	
	
	
	 //�������Ͷ���
    public static String doPost(String urlstr,Map<String,String> params) {
        
        String res="";
       
        	 HttpPostModel model=submitByPost(urlstr, params);
            int statusCode = model.get_ResonseCode();
            if (statusCode == HttpStatus.SC_OK) {
                res =model.get_ResponseString();
            }
       
        return res;
    }
    
    public static HttpPostModel submitByPost(String urlAddress, Map<String, String> map) {
		
  		HttpPostModel model=new HttpPostModel();
  		StringBuffer params = new StringBuffer();

  		Iterator<Entry<String, String>> it = map.entrySet().iterator();
  		while (it.hasNext()) {
  			Entry<String, String> element = (Entry<String, String>) it.next();
  			params.append(element.getKey());
  			params.append("=");
  			params.append(element.getValue());
  			params.append("&");
  		}

  		if (params.length() > 0) {
  			params.deleteCharAt(params.length() - 1);
  		}
  		
  		HttpURLConnection conn = null;
  		try {
  			URL url = new URL(urlAddress);
  			conn = (HttpURLConnection) url.openConnection();

  			conn.setDoOutput(true);
  			conn.setRequestMethod("POST");
  			conn.setUseCaches(false);
  			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
  			conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
  			conn.setDoInput(true);
  			conn.connect();

  			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
  			out.write(params.toString());
  			out.flush();
  			out.close();
  			model.set_ResponseCode(conn.getResponseCode()); 
  			
  			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
  			
  			model.set_ResponseString(br.readLine());
  			
  			br.close();			
  			
  		} catch (Exception ex) {
  			ex.printStackTrace();
  		} finally {
  			conn.disconnect();
  		}
  		return model;
  	}
    
    
    
    //�������Ͷ���
    public static String doPostForCer(String urlstr,Map<String,String> params) {
        
        String res="";
       
        	 HttpPostModel model=submitByPostForCer(urlstr, params);
            int statusCode = model.get_ResonseCode();
            if (statusCode == HttpStatus.SC_OK) {
                res =model.get_ResponseString();
            }
       
        return res;
    }
    
    public static HttpPostModel submitByPostForCer(String urlAddress, Map<String, String> map) {
		
  		HttpPostModel model=new HttpPostModel();
  		StringBuffer params = new StringBuffer();

  		Iterator<Entry<String, String>> it = map.entrySet().iterator();
  		while (it.hasNext()) {
  			Entry<String, String> element = (Entry<String, String>) it.next();
  			params.append(element.getKey());
  			params.append("=");
  			params.append(element.getValue());
  			params.append("&");
  		}

  		if (params.length() > 0) {
  			params.deleteCharAt(params.length() - 1);
  		}
  		
  		HttpsURLConnection conn = null;
  		try {
  		// ����SSLContext���󣬲�ʹ������ָ�������ι�������ʼ��
            TrustManager[] tm = {  new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // ������SSLContext�����еõ�SSLSocketFactory����
            SSLSocketFactory ssf = sslContext.getSocketFactory();
  			URL url = new URL(urlAddress);
  		    conn = (HttpsURLConnection) url.openConnection();
  		    conn.setSSLSocketFactory(ssf);
  			conn.setDoOutput(true);
  			conn.setRequestMethod("POST");
  			conn.setUseCaches(false);
  			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
  			conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
  			conn.setDoInput(true);
  			conn.connect();

  			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
  			out.write(params.toString());
  			out.flush();
  			out.close();
  			model.set_ResponseCode(conn.getResponseCode()); 
  			
  			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
  			
  			model.set_ResponseString(br.readLine());
  			
  			br.close();			
  			
  		} catch (Exception ex) {
  			ex.printStackTrace();
  		} finally {
  			conn.disconnect();
  		}
  		return model;
  	}
    
    
    /**
     * @author ������
     * @date 2014-12-5����2:32:05
     * @Description�����������ת��Ϊxml��ʽ��string
     * @param parameters  �������
     * @return
     */
    public static String getRequestXml(SortedMap<Object,Object> parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)||"sign".equalsIgnoreCase(k)) {
                sb.append("<"+k+">"+"<![CDATA["+v+"]]></"+k+">");
            }else {
                sb.append("<"+k+">"+v+"</"+k+">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    
    /**
     * ����https����
     * @param requestUrl �����ַ
     * @param requestMethod ����ʽ��GET��POST��
     * @param outputStr �ύ�����
     * @return ����΢�ŷ�������Ӧ����Ϣ
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        try {
            // ����SSLContext���󣬲�ʹ������ָ�������ι�������ʼ��
            TrustManager[] tm = {  new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // ������SSLContext�����еõ�SSLSocketFactory����
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // ��������ʽ��GET/POST��
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // ��outputStr��Ϊnullʱ�������д���
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
        	ce.printStackTrace();
            //log.error("���ӳ�ʱ��{}", ce);
        } catch (Exception e) {
        	e.printStackTrace();
            //log.error("https�����쳣��{}", e);
        }
        return null;
    }
    
    /**
     * ����https����
     * @param requestUrl �����ַ
     * @param requestMethod ����ʽ��GET��POST��
     * @param outputStr �ύ�����
     * @return ����΢�ŷ�������Ӧ����Ϣ
     */
    public static String httpsRequestForCer(String requestUrl, String requestMethod, String outputStr) throws Exception {
    	
    	 StringBuffer buffer = new StringBuffer();
    	KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File("D:/iDengCer/cert/apiclient_cert.p12"));
        try {
            keyStore.load(instream, "1355185802".toCharArray());
        } finally {
            instream.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, "1355185802".toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        
       
        try {

            HttpPost httpget = new HttpPost(requestUrl);
            
            httpget.addHeader("content-type", "application/x-www-form-urlencoded");
            httpget.setEntity(new StringEntity(outputStr,"UTF-8"));
            
            //httpclient.getConnectionManager().
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();

               
               // System.out.println(response.getStatusLine());
                if (entity != null) {
                    
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
                    String text;
                   
                    while ((text = bufferedReader.readLine()) != null) {
                    	buffer.append(text);
                    }
                   
                }
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return buffer.toString();
       // return null;
    }
    
    
    //����xml��ʽ����
    public static SortedMap doXmlParse(String result) throws UnsupportedEncodingException, DocumentException{
    	SortedMap map = new TreeMap();
		if(result!=null&&!"".equals(result)){
		String return_code ="";
		String return_msg ="";
		String appid  ="";
		String mch_id ="";
		String nonce_str ="";
		String sign ="";
		String result_code ="";
		String prepay_id ="";
		String trade_type ="";
		String code_url = "";
	SAXReader saxreader = new SAXReader();
	Document document = saxreader.read(new ByteArrayInputStream(result.getBytes("UTF-8")));
	Element root = document.getRootElement(); //��ȡ��ڵ�
	 return_code = root.element("return_code").getText();
	 if("SUCCESS".equals(return_code)){
	 return_msg = root.element("return_msg").getText();
	 appid = root.element("appid").getText();
	 mch_id = root.element("mch_id").getText();
	 nonce_str = root.element("nonce_str").getText();
	 sign = root.element("sign").getText();
	 result_code = root.element("result_code").getText();
	 prepay_id = root.element("prepay_id").getText();
	 trade_type = root.element("trade_type").getText();
	 if (trade_type.equals("NATIVE"))
		 code_url = root.element("code_url").getText();
	 
     map.put("return_code", return_code);
     map.put("return_msg", return_msg);
     map.put("appid", appid);
     map.put("mch_id",mch_id);
     map.put("nonce_str", nonce_str);
     map.put("sign", sign);
     map.put("result_code", result_code);
     map.put("prepay_id", prepay_id);
     map.put("trade_type", trade_type);
     if (trade_type.equals("NATIVE"))
    	  map.put("code_url", code_url);
		 
	  return map;
	 }else{
		 map.put("error", "�������״̬ʧ��");
		 return map;
	 }
		}else{
			map.put("error", "����Ϊ��");
			 return map;
		}
	
	
	
	}
    
    
    
    
  //�����߻�ͨ���ص�XML
    public static SortedMap doGHTQuickXmlParse(String result) throws UnsupportedEncodingException, DocumentException{
    	SortedMap map = new TreeMap();
		if(result!=null&&!"".equals(result)){
			
			String busi_code="";	
			String merchant_no="";
			String terminal_no="";
			String child_merchant_no="";
			String token_id="";
			//String bank_code="ABCQBY";
			String bank_code="SMALL_QBY";
            String resp_code="";
            String  resp_desc="";
            String sign="";
            String order_no="";
            String sign_type="";
            
            
			
		
	SAXReader saxreader = new SAXReader();
	Document document = saxreader.read(new ByteArrayInputStream(result.getBytes("UTF-8")));
	Element root = document.getRootElement(); //��ȡ��ڵ�
	resp_code = root.element("resp_code").getText();
	 if("00".equals(resp_code)){
		 resp_desc = root.element("resp_desc").getText();
		 busi_code = root.element("busi_code").getText();
		 merchant_no = root.element("merchant_no").getText();
		 terminal_no = root.element("terminal_no").getText();
		 
	 sign = root.element("sign").getText();
	 order_no = root.element("order_no").getText();
	 token_id=root.element("token_id").getText();
	 sign_type = root.element("sign_type").getText();
	// bank_code = root.element("bank_code").getText();
	 
	
	
     map.put("resp_code", resp_code);
     map.put("resp_desc", resp_desc);
     map.put("busi_code", busi_code);
     map.put("token_id", token_id);
     
     map.put("merchant_no", merchant_no);
    // String child_no=
     if(root.element("child_merchant_no") == null){
    	 
     }else{
     child_merchant_no= root.element("child_merchant_no").getText();
     map.put("child_merchant_no", child_merchant_no);
     }
     
     map.put("sign", sign);
     map.put("terminal_no", terminal_no);
     map.put("order_no", order_no);
     map.put("sign_type", sign_type);
     map.put("bank_code", bank_code);
    
		 
	  return map;
	 }else{
		 map.put("error", "�������״̬ʧ��");
		 map.put("resp_code", resp_code);
		 return map;
	 }
		}else{
			map.put("error", "����Ϊ��");
			 return map;
		}
	
	
	
	}
    
   
    /** 
    * ��ָ�� URL ����POST���������� 
    *  
    * @param url ��������� URL 
    * @param param ������� 
    * @return ����Զ����Դ����Ӧ��� 
    */  
    public static String sendPost(String url, Map<String, ?> paramMap) {  
          PrintWriter out = null;  
          BufferedReader in = null;  
          String result = "";  
            
          String param = "";  
          /* Iterator<String> it = paramMap.keySet().iterator();  
      
    while(it.hasNext()) {  
        String key = it.next();  
        param += key + "=" + paramMap.get(key) + "&";  
    }  */
    
    
    StringBuffer sb = new StringBuffer();
	Set es = paramMap.entrySet();//���в��봫�εĲ�����accsii��������
	Iterator it = es.iterator();
	while(it.hasNext()) {
		Map.Entry entry = (Map.Entry)it.next();
		String k = (String)entry.getKey();
		String v = (String)entry.getValue();
		try {
			v=java.net.URLEncoder.encode(v,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			sb.append(k + "=" + v + "&");
		
	}
	param=sb.toString();
          try {  
              URL realUrl = new URL(url);  
              // �򿪺�URL֮�������  
              URLConnection conn = realUrl.openConnection();  
              // ����ͨ�õ���������  
              conn.setRequestProperty("accept", "*/*");  
              conn.setRequestProperty("connection", "Keep-Alive");  
              conn.setRequestProperty("Accept-Charset", "utf-8");  
              conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");  
              // ����POST�������������������  
              conn.setDoOutput(true);  
              conn.setDoInput(true);  
              // ��ȡURLConnection�����Ӧ�������  
              out = new PrintWriter(conn.getOutputStream());  
              // �����������  
              out.print(param);  
              // flush������Ļ���  
              out.flush();  
              // ����BufferedReader����������ȡURL����Ӧ  
              in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));  
              String line;  
              while ((line = in.readLine()) != null) {  
                  result += line;  
              }  
          } catch (Exception e) {  
            //log.error(e.getMessage(), e);  
        	  e.printStackTrace();
          }  
          //ʹ��finally�����ر��������������  
          finally{  
              try{  
                  if(out!=null){  
                      out.close();  
                  }  
                  if(in!=null){  
                      in.close();  
                  }  
              }  
              catch(IOException ex){  
                  ex.printStackTrace();  
              }  
          }  
          return result;  
      }  

    /** 
     * post请求（用于请求json格式的参数） 
     * @param url 
     * @param params 
     * @return 
     */  
    public static String doPostJson(String url, String params) throws Exception {  
          
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpPost httpPost = new HttpPost(url);// 创建httpPost     
        httpPost.setHeader("Accept", "application/json");   
        httpPost.setHeader("Content-Type", "application/json");  
        String charSet = "UTF-8";  
        StringEntity entity = new StringEntity(params, charSet);  
        httpPost.setEntity(entity);          
        CloseableHttpResponse response = null;  
          
        try {  
              
            response = httpclient.execute(httpPost);  
            StatusLine status = response.getStatusLine();  
            int state = status.getStatusCode();  
            if (state == HttpStatus.SC_OK) {  
                HttpEntity responseEntity = response.getEntity();  
                String jsonString = EntityUtils.toString(responseEntity);  
                return jsonString;  
            }  
            else{  
                 System.out.println("请求返回:"+state+"("+url+")");  
            }  
        }  
        finally {  
            if (response != null) {  
                try {  
                    response.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return null;  
    }  
    
    
    /**
     * post请求（用于请求json格式的参数）  vivo
     * @param url
     * @param params
     * @param authToken  
     * @return
     * @throws Exception
     */
    public static String doVivoPost(String url, String params,String authToken) throws Exception {  
          
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpPost httpPost = new HttpPost(url);// 创建httpPost     
        httpPost.setHeader("Accept", "application/json");   
        httpPost.setHeader("Content-Type", "application/json");  
        httpPost.setHeader("authToken", authToken);  
        String charSet = "UTF-8";  
        StringEntity entity = new StringEntity(params, charSet);  
        httpPost.setEntity(entity);          
        CloseableHttpResponse response = null;  
          
        try {  
              
            response = httpclient.execute(httpPost);  
            StatusLine status = response.getStatusLine();  
            int state = status.getStatusCode();  
            if (state == HttpStatus.SC_OK) {  
                HttpEntity responseEntity = response.getEntity();  
                String jsonString = EntityUtils.toString(responseEntity);  
                return jsonString;  
            }  
            else{  
                 System.out.println("请求返回:"+state+"("+url+")");  
            }  
        }  
        finally {  
            if (response != null) {  
                try {  
                    response.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return null;  
    }  
    
    
    
    public static void main(String args[])
    {
    	/*Map params= new HashMap();
    	params.put("cardNo","4392250022892805");
    	String s=HttpUtil.doPost("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true", params);
    	Map object= JsonUtil.getMap4Json(s);
    	System.out.println("bb="+object.get("bank"));
    	Map bankObject= JsonUtil.getMap4Json(BConstants.BANK);
    	//System.out.println("bank="+bankObject);
    	System.out.println(bankObject.get(object.get("bank")));*/
    	
    	 String sk_amount ="3678.56";
    	 float f_amount = Float.parseFloat(sk_amount);
    	 double j_amount = f_amount/1.00755625;
    	 
    	 String sj_amount = new java.text.DecimalFormat("#.00").format(j_amount);
    	 
    	 System.out.println("ddd="+sj_amount);
    	
    }
   

}
