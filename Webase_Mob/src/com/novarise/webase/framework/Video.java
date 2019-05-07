package com.novarise.webase.framework;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.util.HttpClientUtil;

public class Video {
	
	public static final String username = "admin";  //先登录保存cookie
	public static final String password = "4297f44b13955235245b2497399d7a93";
	
	public static final String loginUrl = "http://ad-kcc.dderp.cn:10080/login";
	
	public List getEasyDssVideoList(String name){
		
		HttpClientUtil hcu = new HttpClientUtil();
		List list=new ArrayList();
		Map params= new HashMap();
		params.put("username", username);
		params.put("password", password);
		hcu.doLoginPost(loginUrl,params);
		params.clear();
		params.put("q", name);
		String jsonVideo=hcu.doPost("http://ad-kcc.dderp.cn:10080/vod/list", params);
		
		 JSONObject jsonObject = JSONObject.parseObject(jsonVideo);
		 String total=jsonObject.get("total").toString();
		 String rows=jsonObject.get("rows").toString();
		 JSONArray jsonArray = JSONArray.parseArray(rows);
		 
		//遍历方式1
		    int size = jsonArray.size();
		    for (int i = 0; i < size; i++) {

		        JSONObject row = jsonArray.getJSONObject(i);
		        System.out.println("name:  " + row.getString("name") + ":" + "  videoUrl:  "
		                + row.getString("videoUrl"));
		    }
		 
		//System.out.println("total="+total+"   jsonVideo="+jsonArray);
		//params.put("q", "3ccef36a08ee31fd178f633929dd66ca");
		//String ss=HttpUtil.sendPost("http://ad-kcc.dderp.cn:10080/vod/list", params);
		
		return list;
		
	}
	
   public JSONObject getVideoObject(String video_id){
		
		HttpClientUtil hcu = new HttpClientUtil();
		List list=new ArrayList();
		Map params= new HashMap();
		params.put("username", username);
		params.put("password", password);
		hcu.doLoginPost(loginUrl,params);
		params.clear();
		params.put("id", video_id);
		String jsonVideo=hcu.doPost("http://ad-kcc.dderp.cn:10080/vod/get", params);
		
		 JSONObject jsonObject = JSONObject.parseObject(jsonVideo);
		
		 System.out.println("jon="+jsonObject);
		//"snapUrl"
		
		return jsonObject;
		
	}
	
	public static void main(String args[])
	{
		
		Video video=new Video();
		video.getVideoObject("Hkgifu0O97");
		/*Map params= new HashMap();
		HttpClientUtil hcu = new HttpClientUtil();
		
		
		/*params.put("username", "admin");
		params.put("password", "4297f44b13955235245b2497399d7a93");
		String login=hcu.doLoginPost("http://ad-kcc.dderp.cn:10080/login",params);
		
		File file=new File("C:\\Users\\Administrator\\Desktop\\video\\QQ视频20181008135203.mp4");
		
		String video_id=hcu.doUploadFilePost("http://ad-kcc.dderp.cn:10080/vod/upload", params,file );
		System.out.println("login="+video_id);
		params.clear();
		params.put("q", "3ccef36a08ee31fd178f633929dd66ca");
		
		String ss=hcu.doPost("http://ad-kcc.dderp.cn:10080/vod/list", params);
		System.out.println("ss="+ss);*/
	}

}
