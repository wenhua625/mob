package com.novarise.webase.util;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

import com.easemob.lmc.model.Authentic;
import com.easemob.lmc.model.TalkMsg;
import com.easemob.lmc.service.TalkDataService;
import com.easemob.lmc.service.impl.TalkDataServiceImpl;
import com.easemob.lmc.service.impl.TalkHttpServiceImplApache;
import com.easemob.lmc.tool.JsonTool;



public class JPush {
	
	public static Authentic.Token TEST_TOKEN = new Authentic.Token("YWMt4EPcDvVpEeWmTm2uJUQPcwAAAVT1s8Bmn-wB5wwM9nqr6HgljAvlo79iDX8",1465203701330L);
	
	public static void main(String args[])
	{
		/*String username="13045672325";
    	Authentic.Token TEST_TOKEN = new Authentic.Token("YWMt4EPcDvVpEeWmTm2uJUQPcwAAAVT1s8Bmn-wB5wwM9nqr6HgljAvlo79iDX8",1465203701330L);
    	TalkDataService service = new TalkDataServiceImpl(new TalkHttpServiceImplApache());
    	service.setToken(TEST_TOKEN);
    	try {
			//System.out.println(JsonTool.write(service.userSave(username,username+"+kcc","���ط�")));
			String aa="{\"ddd\":404}";
			System.out.println(aa.indexOf("statusCode"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
    	
    	JPush.pushJGObject_all_regid_alert("18600000004","����һ��3000.00Ԫ�Ķ�����Ҫ֧����","��������");
		//push.pushObject_all_all_alert("��������һ�°�");
		//push.pushObject_all_regid_alert("13165ffa4e084a43baf","����ǰ�����ĵĲ���Ŷ��");
		//161a3797c80496b37cc
		//13065ffa4e084bc337a 121c83f7602bcda7900 13165ffa4e079f0a04a
		
		//push.pushObject_all_regid_alert("191e35f7e070083d561","�ʹ������������Ͻ��򿪿ʹ���������","������");
		//push.pushObject_all_alert("�������ѣ���������ø���̵�ÿһ�������ü��ֻ�ᣬ�����������������������������������ͺã����ʹ������������ĺùܼң�","������");
		
		
	

	}
	
	
	
	public  String pushObject_all_all_alert(String msg) {
		
		
		
		/* PushPayload payload =PushPayload.alertAll(msg);
		 try {
			PushResult result = jpushClient.sendPush(payload);
		} catch (Exception e) {
			System.out.println(e);
			return "0";
		} */
		return "1";
    }
	
	/**
	 * ��������
	 * @param regid
	 * @param msgcontent
	 * @param title
	 * @return
	 */
	public  static String pushJGObject_all_regid_alert(String regid,String msg,String title) {
		
		String sound="default";
//		  String appKey ="4e7af97f2b1bda3ee6251bb2";// "60521845a0e6639a24c8daf6";  
//		 String masterSecret = "68f374c3657f2bffef9ee7e0";//"7e8542cdea7f467b22306755";
		String appKey = "4e7af97f2b1bda3ee6251bb2";
		String masterSecret = "68f374c3657f2bffef9ee7e0";
		

		System.out.println("-------------1----");
		 JPushClient jpushClient = new JPushClient(masterSecret,appKey);
		/*if(msg.indexOf("�Ƽ��ͻ�") !=-1 || msg.indexOf("����ͻ�") !=-1 ||msg.indexOf("����") !=-1)
		{
			sound="kcc3_con.wav";
		}else if(msg.indexOf("������") !=-1)
		{
			sound="kcc_job.wav";
		}else if(msg.indexOf("Ͷ��") !=-1)
		{
			sound="kcc_ts.wav";
		}
		else if (msg.indexOf("��") !=-1 || msg.indexOf("����") !=-1)
		{
			sound="kcc2_con.wav";
		}else if (msg.indexOf("�տ�") !=-1)
		{
			sound="collectMoney.wav";
		}else if(msg.indexOf("�ϰ�") !=-1 || msg.indexOf("�°�")!=-1)
		{
			sound="upanddown.wav";
		}*/
			System.out.println("-----2------------");
		PushPayload payload =  PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                //.setAudience(Audience.registrationId(regid))
                .setAudience(Audience.alias(regid))
                .setNotification(Notification.newBuilder()  
                        .setAlert(msg)  
                       
                        .addPlatformNotification(IosNotification.newBuilder()  
                                .incrBadge(0)
                                .setSound(sound)
                                .addExtra("extra_key", "extra_value").build())
                        .addPlatformNotification(AndroidNotification.newBuilder()  
                                .setTitle(title).build())  
                        .build())
                .setOptions(Options.newBuilder()
                         .setApnsProduction(true)
                         .build())
                .build();
		System.out.println("------3-----------");
		try {
			System.out.println("-----4------------");
			PushResult result = jpushClient.sendPush(payload);
			System.out.println("PushResult_result:"+result);
			System.out.println("---------5--------");
		} catch (Exception e) {
		e.printStackTrace();
			System.out.println(e.toString());
			return "0";
		}
		return "1"; 
    }
	
	public  String pushObject_all_regid_alert(String regid,String msgcontent,String title) {
		TalkDataService service = new TalkDataServiceImpl(new TalkHttpServiceImplApache());
		//�޸����ҵ��Token
		service.setToken(TEST_TOKEN);
		
		TalkMsg msg= new TalkMsg();
		
		
		msg.setTarget_type(1);
		msg.setType(1);
		msg.setFrom("kcc_admin");
		String [] user={regid};
		msg.setTarget(user);
		
		msg.setMsg(msgcontent);
		String msgResut="0";
		try {
			msgResut = JsonTool.write(service.messageSave(msg));
		} catch (Exception e) {
			msgResut="0";
			e.printStackTrace();
		}
		
		if (msgResut.indexOf("statusCode")!=-1)
		{
			msgResut= "1";
		}
		return msgResut;
		
		
		
		
		
		/*String sound="default";
		if(msg.indexOf("�Ƽ��ͻ�") !=-1 || msg.indexOf("����ͻ�") !=-1 ||msg.indexOf("����") !=-1)
		{
			sound="kcc3_con.wav";
		}else if(msg.indexOf("������") !=-1)
		{
			sound="kcc_job.wav";
		}else if(msg.indexOf("Ͷ��") !=-1)
		{
			sound="kcc_ts.wav";
		}
		else if (msg.indexOf("��") !=-1 || msg.indexOf("����") !=-1)
		{
			sound="kcc2_con.wav";
		}else if (msg.indexOf("�տ�") !=-1)
		{
			sound="collectMoney.wav";
		}else if(msg.indexOf("�ϰ�") !=-1 || msg.indexOf("�°�")!=-1)
		{
			sound="upanddown.wav";
		}
		PushPayload payload =  PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.registrationId(regid))
                .setNotification(Notification.newBuilder()  
                        .setAlert(msg)  
                       
                        .addPlatformNotification(IosNotification.newBuilder()  
                                .incrBadge(0)
                                .setSound(sound)
                                .addExtra("extra_key", "extra_value").build())
                        .addPlatformNotification(AndroidNotification.newBuilder()  
                                .setTitle(title).build())  
                        .build())
                .setOptions(Options.newBuilder()
                         .setApnsProduction(true)
                         .build())
                .build();
		try {
			PushResult result = jpushClient.sendPush(payload);
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println(e.toString());
			return "0";
		}
		return "1"; */
    }
	

}
