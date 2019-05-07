package com.novarise.webase.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;





public class SmsSender {
//	������
	static SmsSender smwps=null;
	//�ڲ�����
	private static URL url;
	private static HttpURLConnection url_con;
	private static String sms_url;
	private static String sms_user;
	private static String sms_pwd;
	private static String sms_type;
	private static String sms_signName;
	
	
//	���ݿ��
	//static SQLQuery query=null;
	//static SQLUpdater updater =null; 
	
	
//�������ʵ����ֻ����һ������ʵ��
	
	public static SmsSender newInstance(String sms_url,String sms_user,String sms_pwd,String sms_type) {
		if(smwps==null){
			smwps=new SmsSender();
			smwps.sms_url=sms_url;
			smwps.sms_user=sms_user;
			smwps.sms_pwd=sms_pwd;
			smwps.sms_type=sms_type;
			
		}
		return smwps;
	}
	/**
	 * ���ŷ��ͣ�����������ݺͺ��룬
	 * @param nr ����
	 * @param hm ���룬���������,�ָ�
	 * @return
	 * @throws Exception
	 */
	public static String send_url( String nr,String hm) throws Exception {
        StringBuilder tempStr;
        //�����ڶ�
        //String param="zh="+sms_user+"&mm="+sms_pwd+"&dxlbid="+sms_type;
        //param+= "&nr="+nr+"&hm="+hm;
        //������Ѷ
        //String param="usr="+sms_user+"&pwd="+sms_pwd+"&extdsrcid="+sms_type;
        //param+= "&sms="+nr+"&mobile="+hm;
        //�Ϻ���ͨ
       /* nr = new String(Hex.encodeHex(nr.getBytes("GBK")));
        String command="MULTI_MT_REQUEST";//"MULTI_MT_REQUEST";
        String param="spid="+sms_user+"&sppassword="+sms_pwd+"&dc=15"+"&command="+command;
        param+= "&sm="+nr+"&das="+hm;*/
        //��������ͨ
       // nr = java.net.URLEncoder.encode(nr); //new String(Hex.encodeHex(nr.getBytes("GBK")));
        nr = sms_signName+nr;
        try {
        	nr =java.net.URLEncoder.encode(nr,"utf-8");
		} catch (UnsupportedEncodingException e) {
			//value="";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //String command="MULTI_MT_REQUEST";//"MULTI_MT_REQUEST";
        //String param="username="+sms_user+"&password="+sms_pwd+"&dc=15";
        //param+= "&content="+nr+"&mobile="+hm;
        
        //����http���󣬲�����http��Ӧ
        String sms_url1=sms_url;
        //String sms_url1=sms_url;
       // System.out.println(sms_url1);
        Map params=new HashMap();
        params.put("username", sms_user);
        params.put("password", sms_pwd);
        params.put("dc", "15");
        params.put("content", nr);
        params.put("mobile", hm);
        
        //String resStr = doPostRequest(sms_url1,params);
        String resStr = doPost(sms_url1,params);
        
        //������Ӧ�ַ���
        //�Ϻ���ͨ
        /*HashMap<String,String> pp = parseResStr(resStr);
        
        String rst_sts=pp.get("mterrcode");
        
        return rst_sts;*/
        return resStr;
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
	
	public static String send_batch_url( String nr,String hm) throws Exception {
        StringBuilder tempStr;
        //�����ڶ�
        //String param="zh="+sms_user+"&mm="+sms_pwd+"&dxlbid="+sms_type;
        //param+= "&nr="+nr+"&hm="+hm;
        //������Ѷ
        //String param="usr="+sms_user+"&pwd="+sms_pwd+"&extdsrcid="+sms_type;
        //param+= "&sms="+nr+"&mobile="+hm;
        //�Ϻ���ͨ
        nr = new String(Hex.encodeHex(nr.getBytes("GBK")));
        String command="BATCH_MT_REQUEST";//"MULTI_MT_REQUEST";
        String param="spid="+sms_user+"&sppassword="+sms_pwd+"&dc=15"+"&command="+command;
        param+="&bmttype=1"  + "&sm=" + nr + "&url=" + hm + "&priority=1";
        //param+= "&sm="+nr+"&das="+hm;
        
        //����http���󣬲�����http��Ӧ
        String sms_url1=sms_url+"?"+param;
        System.out.println(sms_url1);
        String resStr = doGetRequest(sms_url1);
        
        //������Ӧ�ַ���
        //�Ϻ���ͨ
        HashMap<String,String> pp = parseResStr(resStr);
        
        String rst_sts=pp.get("mterrcode");
        
        return rst_sts;
    }
	
	 /**
     * �� �������� ������Ӧ�ַ���������һ��HashMap��
     * @param resStr
     * @return
     */
    public static HashMap parseResStr(String resStr) {
        HashMap pp = new HashMap();
        try {
            String[] ps = resStr.split("&");
            for (int i = 0; i < ps.length; i++) {
                int ix = ps[i].indexOf("=");
                if (ix != -1) {
                    pp.put(ps[i].substring(0, ix), ps[i].substring(ix + 1));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return pp;
    }
    
    /**
     * ����http GET���󣬲�����http��Ӧ�ַ���
     * 
     * @param urlstr ����������url�ַ���
     * @return
     */
    public static String doGetRequest(String urlstr) {
    	//System.out.print(urlstr);
        String res = null;
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);

        HttpMethod httpmethod = new GetMethod(urlstr);
        try {
            int statusCode = client.executeMethod(httpmethod);
            if (statusCode == HttpStatus.SC_OK) {
                res = httpmethod.getResponseBodyAsString();
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpmethod.releaseConnection();
        }
        return res;
    }

    /**
     * ����http POST���󣬲�����http��Ӧ�ַ���
     * 
     * @param urlstr ����������url�ַ���
     * @return
     */
    public static String doPostRequest(String urlstr,Map<String,String> params) {
        String res = null;
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);
        
      
        HttpMethod httpmethod =  new PostMethod(urlstr);
       
        //����Http Post���� 
        if (params != null) { 
                HttpMethodParams p = new HttpMethodParams(); 
                for (Map.Entry<String, String> entry : params.entrySet()) { 
                        p.setParameter(entry.getKey(), entry.getValue()); 
                } 
                httpmethod.setParams(p); 
        } 
        
        try {
            int statusCode = client.executeMethod(httpmethod);
            if (statusCode == HttpStatus.SC_OK) {
                res = httpmethod.getResponseBodyAsString();
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpmethod.releaseConnection();
        }
        return res;
    }
    
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
    
   
    
	public  static void main(String args[]){
		SmsSender sms= SmsSender.newInstance("http://116.213.72.20/SMSHttpService/send.aspx","guodsy","guoding","34");
		String url_result="";
		try {
			url_result=sms.send_url("������ʯ�͡��𾴵Ŀͻ���,5.7����.�Ĵ��н�Ҷ����.5.6.����156XXXXXXXX.1-20.�ۺ�","18616989375");
			
			//url_result=sms.send_url("http://www.dxqf.name/smsComputer/smsComputersend.asp", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(url_result);
	}
}
