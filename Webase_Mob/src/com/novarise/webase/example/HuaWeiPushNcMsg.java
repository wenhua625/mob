/*
 * FileName:  RedirectMsgService.java
 * License:  Copyright 2014-2024 Huawei Tech. Co. Ltd. All Rights Reserved.
 * Description: Sample of Push Notification Message Sender  
 * Modifier:  
 * Modification Date: 2017��7��13��
 * Modification Content: New
 * Modification Version: Push
 */
package com.novarise.webase.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.BConstants;
import com.novarise.webase.xml.XmlUtil;


//Push֪ͨ����ϢDemo
//��ʾ�������е�appId,appSecret,deviceTokens�Լ�appPkgName��Ҫ�û������滻Ϊ��Чֵ
public class HuaWeiPushNcMsg
{
   
  
    private static  String tokenUrl = "https://login.vmall.com/oauth2/token"; //��ȡ��֤Token��URL
    private static  String apiUrl = "https://api.push.hicloud.com/pushsend.do"; //Ӧ�ü���Ϣ�·�API
    private static  String accessToken;//�·�֪ͨ��Ϣ����֤Token
    private static  long tokenExpiredTime;  //accessToken�Ĺ���ʱ��
    
   
    
    //��ȡ�·�֪ͨ��Ϣ����֤Token
    public static  void refreshToken() throws Exception
    {
    	String appId = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.HUAWEIKCCAPPKEY);
    	String appSecret =    XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.HUAWEIKCCAPPSECRET);
        String msgBody = MessageFormat.format(
        		"grant_type=client_credentials&client_secret={0}&client_id={1}", 
        		URLEncoder.encode(appSecret, "UTF-8"), appId);
        String response = httpPost(tokenUrl, msgBody, 5000, 5000);
        JSONObject obj = JSONObject.parseObject(response);
        accessToken = obj.getString("access_token");
        tokenExpiredTime = System.currentTimeMillis() + obj.getLong("expires_in") - 5*60*1000;
    }
    
    //����Push��Ϣ
    public static  String sendPushMessage(String order,String pushToken,String type,String title ,String content) throws IOException
    {
        if (tokenExpiredTime <= System.currentTimeMillis())
        {
            try {
				refreshToken();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }      

        String appId = "";
        String appSecret = "";
    	try {
    		appSecret =    XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.HUAWEIKCCAPPSECRET);
			 appId = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.HUAWEIKCCAPPKEY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        JSONArray deviceTokens = new JSONArray();//Ŀ���豸Token
        deviceTokens.add(pushToken);
        
        JSONObject body = new JSONObject();//��֪ͨ����Ϣ��Ҫ���ñ�������ݣ�͸����Ϣkey��valueΪ�û��Զ���
        body.put("title", title);//��Ϣ����
        body.put("content", content);//��Ϣ������
        
        
        JSONObject jsoncon = new JSONObject();
        jsoncon.put("type",type);
        jsoncon.put("order_code",order);
        
        
        JSONObject param = new JSONObject();
        param.put("appPkgName", "com.example.iDengBao.PlaceOrder");//������Ҫ�򿪵�appPkgName
        param.put("intent", "intent://com.agent/notification?action="+jsoncon+"#Intent;scheme=wonderfullpush;action=android.intent.action.VIEW;launchFlags=0x10000000;end");
        
        JSONObject action = new JSONObject();
        action.put("type", 1);//����3Ϊ��APP��������Ϊ��ο��ӿ��ĵ�����
        action.put("param", param)  ;//��Ϣ�����������
        
        JSONObject msg = new JSONObject();
        msg.put("type", 3);//3: ֪ͨ����Ϣ���첽͸����Ϣ����ݽӿ��ĵ�����
        msg.put("action", action);//��Ϣ�������
        msg.put("body", body);//֪ͨ����Ϣbody����
        
        JSONObject ext = new JSONObject();//��չ��Ϣ����BI��Ϣͳ�ƣ��ض�չʾ�����Ϣ�۵���
        ext.put("biTag", "Trump");//������Ϣ��ǩ��������������ǩ�����ڻ�ִ�����͸�CP���ڼ��ĳ��������Ϣ�ĵ����ʺ�״̬
        ext.put("icon", "http://pic.qiantucdn.com/58pic/12/38/18/13758PIC4GV.jpg");//�Զ���������Ϣ��֪ͨ����ͼ��
        
        JSONObject hps = new JSONObject();//��ΪPUSH��Ϣ�ܽṹ��
        hps.put("msg", msg);
        hps.put("ext", ext);
        
        JSONObject payload = new JSONObject();
        payload.put("hps", hps);
        
        String postBody = MessageFormat.format(
        	"access_token={0}&nsp_svc={1}&nsp_ts={2}&device_token_list={3}&payload={4}",
            URLEncoder.encode(accessToken,"UTF-8"),
            URLEncoder.encode("openpush.message.api.send","UTF-8"),
            URLEncoder.encode(String.valueOf(System.currentTimeMillis() / 1000),"UTF-8"),
            URLEncoder.encode(deviceTokens.toString(),"UTF-8"),
            URLEncoder.encode(payload.toString(),"UTF-8"));
       
        String postUrl = apiUrl + "?nsp_ctx=" + URLEncoder.encode("{\"ver\":\"1\", \"appId\":\"" + appId + "\"}", "UTF-8");
       String result =  httpPost(postUrl, postBody, 5000, 5000);
        net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(result);
        String results= "";
        
        if(json.get("msg").toString().equals("Success")){
        	results = "ok";
        }else{
        	System.out.println("��Ϊ:"+result);
        	results = "no";
        }
        
        return results;
    }
    
    public static String httpPost(String httpUrl, String data, int connectTimeout, int readTimeout) throws IOException
    {
        OutputStream outPut = null;
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        
        try
        {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection)url.openConnection();          
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();
            
            // POST data
            outPut = urlConnection.getOutputStream();
            outPut.write(data.getBytes("UTF-8"));
            outPut.flush();
            
            // read response
            if (urlConnection.getResponseCode() < 400)
            {
                in = urlConnection.getInputStream();
            }
            else
            {
                in = urlConnection.getErrorStream();
            }
            
            List<String> lines = IOUtils.readLines(in, urlConnection.getContentEncoding());
            StringBuffer strBuf = new StringBuffer();
            for (String line : lines)
            {
                strBuf.append(line);
            }
//            System.out.println(strBuf.toString());
            return strBuf.toString();
        }
        finally
        {
            IOUtils.closeQuietly(outPut);
            IOUtils.closeQuietly(in);
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
    }

	

	
}
