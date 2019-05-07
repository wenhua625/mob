package push;


import org.json.JSONArray;
import org.json.JSONObject;

import push.android.AndroidBroadcast;
import push.android.AndroidCustomizedcast;
import push.android.AndroidFilecast;
import push.android.AndroidGroupcast;
import push.android.AndroidUnicast;
import push.ios.IOSBroadcast;
import push.ios.IOSCustomizedcast;
import push.ios.IOSFilecast;
import push.ios.IOSGroupcast;
import push.ios.IOSUnicast;

public class UMengPush {
	private String appkey = null;
	private String appMasterSecret = null;
	private String timestamp = null;
	private PushClient client = new PushClient();
	
	public UMengPush(String key, String secret) {
		try {
			appkey = key;
			appMasterSecret = secret;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	//广播
	public   String sendAndroidBroadcast(String ticker,String title,String text) throws Exception {
		AndroidBroadcast broadcast = new AndroidBroadcast(appkey,appMasterSecret);
		broadcast.setTicker(ticker);
		broadcast.setTitle( title);
		broadcast.setText(text);
		broadcast.goAppAfterOpen();
		broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		broadcast.setProductionMode();
		// Set customized fields
		broadcast.setExtraField("test", "helloworld");
		return client.send(broadcast);
	}
	//单播
	public String sendAndroidUnicast(String ticker,String title,String text,String devicetoken) throws Exception {
		AndroidUnicast unicast = new AndroidUnicast(appkey,appMasterSecret);
		// TODO Set your device token
		unicast.setDeviceToken(devicetoken);
		unicast.setTicker(ticker);
		unicast.setTitle(title);
		unicast.setText(text);
		//unicast.goAppAfterOpen();
		//unicast.goActivityAfterOpen("login.Message_Activity");
		unicast.goCustomAfterOpen(text);
		
		unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		unicast.setProductionMode();
		// Set customized fields
		unicast.setExtraField("test", "helloworld");
		return client.send(unicast);
	}
	
	public void sendAndroidGroupcast() throws Exception {
		AndroidGroupcast groupcast = new AndroidGroupcast(appkey,appMasterSecret);
		/*  TODO
		 *  Construct the filter condition:
		 *  "where": 
		 *	{
    	 *		"and": 
    	 *		[
      	 *			{"tag":"test"},
      	 *			{"tag":"Test"}
    	 *		]
		 *	}
		 */
		JSONObject filterJson = new JSONObject();
		JSONObject whereJson = new JSONObject();
		JSONArray tagArray = new JSONArray();
		JSONObject testTag = new JSONObject();
		JSONObject TestTag = new JSONObject();
		testTag.put("tag", "test");
		TestTag.put("tag", "Test");
		tagArray.put(testTag);
		tagArray.put(TestTag);
		whereJson.put("and", tagArray);
		filterJson.put("where", whereJson);
		System.out.println(filterJson.toString());
		
		groupcast.setFilter(filterJson);
		groupcast.setTicker( "Android groupcast ticker");
		groupcast.setTitle(  "itle");
		groupcast.setText(   "Android groupcast text");
		groupcast.goAppAfterOpen();
		groupcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		groupcast.setProductionMode();
		client.send(groupcast);
	}
	
	public void sendAndroidCustomizedcast() throws Exception {
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(appkey,appMasterSecret);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then 
		// use file_id to send customized notification.
		customizedcast.setAlias("alias", "alias_type");
		customizedcast.setTicker( "Android customizedcast ticker");
		customizedcast.setTitle(  "itle");
		customizedcast.setText(   "Android customizedcast text");
		customizedcast.goAppAfterOpen();
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		customizedcast.setProductionMode();
		client.send(customizedcast);
	}
	
	public void sendAndroidCustomizedcastFile() throws Exception {
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(appkey,appMasterSecret);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then 
		// use file_id to send customized notification.
		String fileId = client.uploadContents(appkey,appMasterSecret,"aa"+"\n"+"bb"+"\n"+"alias");
		customizedcast.setFileId(fileId, "alias_type");
		customizedcast.setTicker( "Android customizedcast ticker");
		customizedcast.setTitle(  "itle");
		customizedcast.setText(   "Android customizedcast text");
		customizedcast.goAppAfterOpen();
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device. 
		// For how to register a test device, please see the developer doc.
		customizedcast.setProductionMode();
		client.send(customizedcast);
	}
	
	public void sendAndroidFilecast() throws Exception {
		AndroidFilecast filecast = new AndroidFilecast(appkey,appMasterSecret);
		// TODO upload your device tokens, and use '\n' to split them if there are multiple tokens 
		String fileId = client.uploadContents(appkey,appMasterSecret,"aa"+"\n"+"bb");
		filecast.setFileId( fileId);
		filecast.setTicker( "Android filecast ticker");
		filecast.setTitle(  "itle");
		filecast.setText(   "Android filecast text");
		filecast.goAppAfterOpen();
		filecast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		client.send(filecast);
	}
	
	public void sendIOSBroadcast(String text) throws Exception {
		IOSBroadcast broadcast = new IOSBroadcast(appkey,appMasterSecret);

		broadcast.setAlert(text);
		broadcast.setBadge(0);
		broadcast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		broadcast.setTestMode();
		// Set customized fields
		broadcast.setCustomizedField("test", "helloworld");
		client.send(broadcast);
	}
	
	public void sendIOSUnicast(String text,String devicetoken) throws Exception {
		IOSUnicast unicast = new IOSUnicast(appkey,appMasterSecret);
		// TODO Set your device token
		unicast.setDeviceToken(devicetoken);
		unicast.setAlert(text);
		unicast.setBadge(0);
		unicast.setSound("default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		unicast.setTestMode();
		// Set customized fields
		unicast.setCustomizedField("test", "helloworld");
		client.send(unicast);
	}
	
	public void sendIOSGroupcast() throws Exception {
		IOSGroupcast groupcast = new IOSGroupcast(appkey,appMasterSecret);
		/*  TODO
		 *  Construct the filter condition:
		 *  "where": 
		 *	{
    	 *		"and": 
    	 *		[
      	 *			{"tag":"iostest"}
    	 *		]
		 *	}
		 */
		JSONObject filterJson = new JSONObject();
		JSONObject whereJson = new JSONObject();
		JSONArray tagArray = new JSONArray();
		JSONObject testTag = new JSONObject();
		testTag.put("tag", "iostest");
		tagArray.put(testTag);
		whereJson.put("and", tagArray);
		filterJson.put("where", whereJson);
		System.out.println(filterJson.toString());
		
		// Set filter condition into rootJson
		groupcast.setFilter(filterJson);
		groupcast.setAlert("IOS 缁勬挱娴嬭瘯");
		groupcast.setBadge( 0);
		groupcast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		groupcast.setTestMode();
		client.send(groupcast);
	}
	
	public void sendIOSCustomizedcast() throws Exception {
		IOSCustomizedcast customizedcast = new IOSCustomizedcast(appkey,appMasterSecret);
		// TODO Set your alias and alias_type here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then 
		// use file_id to send customized notification.
		customizedcast.setAlias("alias", "alias_type");
		customizedcast.setAlert("IOS");
		customizedcast.setBadge( 0);
		customizedcast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		customizedcast.setTestMode();
		client.send(customizedcast);
	}
	
	public void sendIOSFilecast() throws Exception {
		IOSFilecast filecast = new IOSFilecast(appkey,appMasterSecret);
		// TODO upload your device tokens, and use '\n' to split them if there are multiple tokens 
		String fileId = client.uploadContents(appkey,appMasterSecret,"aa"+"\n"+"bb");
		filecast.setFileId( fileId);
		filecast.setAlert("IOS");
		filecast.setBadge( 0);
		filecast.setSound( "default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		filecast.setTestMode();
		client.send(filecast);
	}
	
	public static void main(String[] args) {
		// TODO set your appkey and master secret here
		UMengPush demo = new UMengPush("56975e8467e58e71fc002730", "sa7dqq7b5wvlo6vxcqwnosmugcg8tgpn");
		try {
			demo.sendAndroidBroadcast("测试看看GBK","测试看看GBK","测试看看GBK");
			/* TODO these methods are all available, just fill in some fields and do the test
			 * demo.sendAndroidCustomizedcastFile();
			 * demo.sendAndroidBroadcast();
			 * demo.sendAndroidGroupcast();
			 * demo.sendAndroidCustomizedcast();
			 * demo.sendAndroidFilecast();
			 * 
			 * demo.sendIOSBroadcast();
			 * demo.sendIOSUnicast();
			 * demo.sendIOSGroupcast();
			 * demo.sendIOSCustomizedcast();
			 * demo.sendIOSFilecast();
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

}
