package com.novarise.webase.util;


import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;


import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.czb.gateway.api.APITools;


/**
 * 微信小程序信息获取
 * 
 * @author zhy
 */
public class WXAppletUserInfo {
	private static Logger log = Logger.getLogger(WXAppletUserInfo.class);

	private static String       weixinSmartAppId="wxf85e801e63bb2164";
	private static String		weixiSmartAppSecret="9287b9eb47c7f15a112f2c82f15c80c2";
	private static String		weixinAuthUrl="https://api.weixin.qq.com/sns/jscode2session";
	
	/**
	 * 获取微信小程序 session_key 和 openid
	 * 
	 * @author zhy
	 * @param code
	 *            调用微信登陆返回的Code
	 * @return
	 */
	public static JSONObject getSessionKeyOropenid(HttpServletRequest request) {
		// 微信端登录code值
		String wxCode = APITools.nvl(request.getParameter("code"));
		String agent_code = APITools.nvl(request.getParameter("agent_code"));
		// ResourceBundle resource = ResourceBundle.getBundle("weixin");
		// //读取属性文件
		String requestUrl = APITools.nvl(weixinAuthUrl); // resource.getString("url");
												// //请求地址
												// https://api.weixin.qq.com/sns/jscode2session
		// String requestUrl="https://api.weixin.qq.com/sns/jscode2session";
		Map<String, String> requestUrlParam = new HashMap<String, String>();
		requestUrlParam.put("appid",
				APITools.nvl(weixinSmartAppId)); // 开发者设置中的appId
		requestUrlParam.put("secret",
				APITools.nvl(weixiSmartAppSecret)); // 开发者设置中的appSecret
		requestUrlParam.put("js_code", wxCode); // 小程序调用wx.login返回的code
		requestUrlParam.put("grant_type", "authorization_code"); // 默认参数
       // System.out.println("reqeustUrl:"+requestUrl);
		// 发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session
		// 接口获取openid用户唯一标识
        
		JSONObject jsonObject = JSON.parseObject(HttpUtil.sendPost(requestUrl,
				requestUrlParam));
		//System.out.println("-------微信授权------------" + jsonObject);
		jsonObject.put("agent_code", agent_code);
		String openid = jsonObject.getString("openid");
		
		
		return jsonObject;
	}
	
	
	
	
	public static void main(String args[]){
		
		String encryptedData="bPtOi21FrUe8Y75MPSxZf/BUOiJvBdRG22bKbWpZFC31uN1 nt9UPbrGZtuwpP62135UDHfg0WMuQLnYQd0CgXqDbabmgjGYsAOal1pRMXJzI3OiIVS0uA28DUxrtR88YTqfBTh2G1l6imB1pfiznRlY3Dh1 sLkYEfiq7H2eA4UFNa1yPgZeVIvj/llHR83smp7w5yVEw6joyeEoqi 9oUIf40xZ9HiX5Nw9DdEs32vh0bK8Vlmi709fYtMqSKqPt1o8EqqVIV0qB7QB7TJzjSCCGs4YGo6zinrn7ZRHGstkdDePUQ40nGleYqD9r4cQL4skjo6ku5 lnAT3PTZBftnsAuT6YRKcWNqNFL6mecG1A Za0OGJ1Q c0NNXlZMjm/YHChtVuiND/W7yxXKHYvSrAu4bpFTltdj/g5niJbTP0 yhWT6Bvw5XdIPDQ7GxAhKid6sikRVmgPgaFl6W2j6QYxaE93K/nOnpAHgC1Q=";
		String sessionKey="l0Cw3RSNcsgtauDBLXuanQ==";
	    String iv="zxbjNf0bAVO1VVSN2FjfHw==";
	    
	  
		try {
		   //String sss= AesCbcUtil.decrypt(encryptedData,sessionKey,iv,"utf-8");
			//WXAppletUserInfo.getUserInfo(encryptedData,sessionKey,iv);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
