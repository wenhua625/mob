package com.novarise.webase.example;


import com.novarise.webase.BConstants;
import com.novarise.webase.xml.XmlUtil;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Sender;

import net.sf.json.JSONObject;

import org.json.simple.parser.ParseException;



import java.io.IOException;

/**
 * author: Administrator
 * created on: 2017/11/6 10:53
 * description:
 */
public class XiaoMiPushtest {


	 /*   private static String APP_SECRET = "AJHOVx8oKSbsLoAaTceTGQ==";

	    private static String MY_PACKAGE_NAME = "com.example.iDengBao.PlaceOrder";
	   */
	
    public static void sendMessage(String order,String lxfs,String type,String title ,String description) {
    	
//    	String APP_SECRET = "AJHOVx8oKSbsLoAaTceTGQ==";
//    	String MY_PACKAGE_NAME = "com.example.iDengBao.PlaceOrder";
    	 String APP_SECRET = "";
         String MY_PACKAGE_NAME = "";
     	try {
     		APP_SECRET =    XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.XIAOMIAPPSECRET);
     		MY_PACKAGE_NAME = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.XIAOMIMYPACKAGENAME);
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET);
        JSONObject jsoncon = new JSONObject();
        jsoncon.put("type",type);
        jsoncon.put("order_code",order);
        String messagePayload = "This is a message";
        String intent = "intent://com.agent/notification?action="+jsoncon+"#Intent;scheme=wonderfullpush;action=android.intent.action.VIEW;launchFlags=0x10000000;end";
      
      
       
        		
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .passThrough(0)     // ������Ϣ�Ƿ�ͨ��͸���ķ�ʽ��App, 1��ʾ͸����Ϣ, 0��ʾ֪ͨ����Ϣ(Ĭ����֪ͨ����Ϣ)
                .notifyType(-1)      // ����֪ͨ����, type����(-1, 1-ʹ��Ĭ����ʾ����ʾ, 2-ʹ��Ĭ������ʾ, 3-ʹ��Ĭ��led�ƹ���ʾ)
                .extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_ACTIVITY)
                .extra(Constants.EXTRA_PARAM_INTENT_URI, intent)
                .build();
      
        try {            

        	 sender.sendToAlias(message, lxfs, 1);  //�Զ������
          //  sender.send(message, "asasas", 1); //������Ϣ��һ���豸��, regids�������ó���1000��
          //  sender.sendToAlias(message, alias, 3); 
            //sender.broadcastAll(message, 1); //�������豸������Ϣ��retriesΪ����ʧ��֮�����ԵĴ�����
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
  
    
 
}

