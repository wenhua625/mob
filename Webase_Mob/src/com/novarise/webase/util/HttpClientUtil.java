package com.novarise.webase.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
 
public class HttpClientUtil {
	public static final String urlString = "http://....?passport=...&password=...";  //�ȵ�¼����cookie
	public static final String urlString2 = "http://......";
	public String sessionId = "";
	
	public void doGet(String urlStr) throws IOException{
		String key = "";
		String cookieVal = "";
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect(); //���˲�ֻ�ǽ������������tcp���ӣ���δ����http����
		/** 
		 * ����cookie
		 */
		if(!sessionId.equals("")){
			connection.setRequestProperty("Cookie", sessionId);
		}
		
		for(int i=1;(key=connection.getHeaderField(i))!=null;i++){
			cookieVal = connection.getHeaderField(i);
			cookieVal = cookieVal.substring(0,cookieVal.indexOf(";")>-1?cookieVal.indexOf(";"):cookieVal.length()-1);
			sessionId = sessionId + cookieVal + ";";
		}
		
		//ֱ��getInputStream()��������������������ͳ�ȥ
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line=br.readLine()) != null){
			sb.append(line);
			sb.append("\n");
		}
		System.out.println(sb.toString());
		br.close();
		connection.disconnect();
	}
	
	 /**
     * ����POST����
     * 
     * @param url
     *            Ŀ�ĵ�ַ
     * @param parameters
     *            ���������Map���͡�
     * @return Զ����Ӧ���
     */
    public  String doPost(String url, Map<String, String> parameters) {
    	String key = "";
		String cookieVal = "";
        String result = "";// ���صĽ��
        BufferedReader in = null;// ��ȡ��Ӧ������
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// �����������
        String params = "";// ����֮��Ĳ���
        try {
            // �����������
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // ����URL����
            java.net.URL connURL = new java.net.URL(url);
            // ��URL����
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // ����ͨ������
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //System.out.println("sessionId="+sessionId);
            if(!sessionId.equals("")){
            	httpConn.setRequestProperty("Cookie", sessionId);
    		}


           // httpConn.addRequestProperty("Set-Cookie", sessionId);
            
           
            
            // ����POST��ʽ
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            
            
    		
            
            // ��ȡHttpURLConnection�����Ӧ�������
            out = new PrintWriter(httpConn.getOutputStream());
            // �����������
            out.write(params);
          
            
            // flush������Ļ���
            out.flush();
           
           
           
            // ����BufferedReader����������ȡURL����Ӧ�����ñ��뷽ʽ
            in = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(), "UTF-8"));
            String line;
            // ��ȡ���ص�����
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    
    /**
     * ����POST����
     * 
     * @param url
     *            Ŀ�ĵ�ַ
     * @param parameters
     *            ���������Map���͡�
     * @return Զ����Ӧ���
     */
    public  void doUploadFilePost(String url, Map<String, String> parameters,String localFile) {
    	 CloseableHttpClient httpClient = null;
         CloseableHttpResponse response = null;
         try {
             httpClient = HttpClients.createDefault();
             // ��һ����ͨ�������ļ��ϴ������������ַ ��һ��servlet
             HttpPost httpPost = new HttpPost(url);
        
             // ���ļ�ת����������FileBody
             FileBody bin = new FileBody(new File(localFile));
             HttpEntity reqEntity = MultipartEntityBuilder.create()
                     // �൱��<input type="file" name="file"/>
                     .addPart("file", bin)
                     // �൱��<input type="text" name="userName" value=userName>
                     .build();
           //  System.out.println(reqEntity.getContentLength());
             httpPost.setEntity(reqEntity);
             httpPost.setHeader("Cookie", sessionId);
             // �������� �������������Ӧ
             response = httpClient.execute(httpPost);
             System.out.println("The response value of token:" + response.getFirstHeader("token"));
             // ��ȡ��Ӧ����
             HttpEntity resEntity = response.getEntity();
             if (resEntity != null) {
                 // ��ӡ��Ӧ����
                 System.out.println("Response content length: " + resEntity.getContentLength());
                 // ��ӡ��Ӧ����
                 System.out.println(EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
             }
             // ����
             EntityUtils.consume(resEntity);
         }catch (Exception e){
             e.printStackTrace();
         }finally {
             try {
                 if(response != null){
                     response.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
             try {
                 if(httpClient != null){
                     httpClient.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
       
    }
    
    /**
     * ����POST����
     * 
     * @param url
     *            Ŀ�ĵ�ַ
     * @param parameters
     *            ���������Map���͡�
     * @return �㲥ID
     */
    public  String doUploadFilePost(String url, Map<String, String> parameters,File localFile) {
    	String video_id="";
    	CloseableHttpClient httpClient = null;
         CloseableHttpResponse response = null;
         try {
             httpClient = HttpClients.createDefault();
             // ��һ����ͨ�������ļ��ϴ������������ַ ��һ��servlet
             HttpPost httpPost = new HttpPost(url);
        
             // ���ļ�ת����������FileBody
             FileBody bin = new FileBody(localFile);
             HttpEntity reqEntity = MultipartEntityBuilder.create()
                     // �൱��<input type="file" name="file"/>
                     .addPart("file", bin)
                     // �൱��<input type="text" name="userName" value=userName>
                     .build();
           //  System.out.println(reqEntity.getContentLength());
             httpPost.setEntity(reqEntity);
             httpPost.setHeader("Cookie", sessionId);
            // httpPost.setHeader("Content-Type", "video/mp4");
             
             // �������� �������������Ӧ
             response = httpClient.execute(httpPost);
            // System.out.println("The response value of token:" + response.getFirstHeader("token"));
             // ��ȡ��Ӧ����
             HttpEntity resEntity = response.getEntity();
             if (resEntity != null) {
            	  video_id=EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
            	  JSONArray jsonArray = JSONArray.parseArray(video_id);
            	  if(jsonArray.size()>0){
            		  video_id=(String)jsonArray.get(0);
            	  }
            	  //video_id=jsonArray.toJSONString();
                 // ��ӡ��Ӧ����
                // System.out.println("Response content length: " + resEntity.getContentLength());
                 // ��ӡ��Ӧ����
                 //System.out.println(EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
             }
             // ����
             EntityUtils.consume(resEntity);
         }catch (Exception e){
             e.printStackTrace();
         }finally {
             try {
                 if(response != null){
                     response.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
             try {
                 if(httpClient != null){
                     httpClient.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return video_id;
       
    }
    
    
    
    /**
     * ����POST����
     * 
     * @param url
     *            Ŀ�ĵ�ַ
     * @param parameters
     *            ���������Map���͡�
     * @return Զ����Ӧ���
     */
    public  String doLoginPost(String url, Map<String, String> parameters) {
    	String key = "";
		String cookieVal = "";
        String result = "";// ���صĽ��
        BufferedReader in = null;// ��ȡ��Ӧ������
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// �����������
        String params = "";// ����֮��Ĳ���
        try {
            // �����������
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // ����URL����
            java.net.URL connURL = new java.net.URL(url);
            // ��URL����
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // ����ͨ������
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            


           // httpConn.addRequestProperty("Set-Cookie", sessionId);
            
           
            
            // ����POST��ʽ
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            
            
    		
            
            // ��ȡHttpURLConnection�����Ӧ�������
            out = new PrintWriter(httpConn.getOutputStream());
            // �����������
            out.write(params);
          
            
            // flush������Ļ���
            out.flush();
           
            for(int i=1;(key=httpConn.getHeaderField(i))!=null;i++){
    			cookieVal = httpConn.getHeaderField(i);
    			cookieVal = cookieVal.substring(0,cookieVal.indexOf(";")>-1?cookieVal.indexOf(";"):cookieVal.length()-1);
    			
    			sessionId = sessionId + cookieVal + ";";
    		}
           
            // ����BufferedReader����������ȡURL����Ӧ�����ñ��뷽ʽ
            in = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(), "UTF-8"));
            String line;
            // ��ȡ���ص�����
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * �������ͷ��Ϣ��ȡ��session��
     * @param request
     * @param connection
     */
    private static void getCookieToSession(URLConnection connection) {

        Map<String, List<String>> map = connection.getHeaderFields();
        for (String key : map.keySet()) {
            System.out.println(key + "--->" + map.get(key));
        }
      

        List<String> cookie=map.get("Set-Cookie");
        if(cookie!=null){

            String verCode=getCookieBySet("verCode",cookie.get(0));

            String JSESSIONID=getCookieBySet("sid",cookie.get(0));

           // if(verCode!=null){
               // session.setAttribute("verCode",verCode);
          //  }

          //  if(JSESSIONID!=null){
                //session.setAttribute("sid",JSESSIONID);
          //  }
        }

    }
    
    /**
     * ��ȡverCode=b5pcogZaFGikpAc1mQ+G5wOJGBWtXLsHafpf5wlgF5s=; Path=/SSODAO/; HttpOnly
     * ��cookie��Ϣ
     */
    public static String getCookieBySet(String name,String set){

        String regex=name+"=(.*?);";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =pattern.matcher(set);
        if(matcher.find()){
            return matcher.group();
        }
        return null;

    }


	
	public static void main(String[] args) throws IOException {
		HttpClientUtil hcu = new HttpClientUtil();
		//hcu.doGet(urlString);
		//hcu.doGet(urlString2);
	}
}
