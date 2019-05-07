package com.novarise.webase.util;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

import net.sf.json.JSONObject;

public class LikeQrCode {
	public static JSONObject parseProductAqr(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		JSONObject json_result = new JSONObject();
		String agent_code=request.getParameter("agent_code");
		String openid= request.getParameter("openid");
		if(agent_code == null){
			json_result.put("result", "fail");
			json_result.put("msg", "门店编码为空");
			return json_result;
		}
		if(openid == null){
			json_result.put("result", "fail");
			json_result.put("msg", "openid为空");
			return json_result;
		}
		String url = null;
		url = ("https://ad-kcc.dderp.cn/mob/salsa/product_photo/openid/"+agent_code+ "_" + openid +".png");
	       
        String fileUrl = ("D:/Tomcat7.0/ad-kcc/mob/salsa/product_photo/openid/"+agent_code+ "_" + openid +".png");
        File file=new File(fileUrl);    
        if(!file.exists())    
        {   
       	 	String access_token = SmartAqrCode.getAccessToken(
				"https://api.weixin.qq.com/cgi-bin/token", "client_credential",
					"wxf85e801e63bb2164", "9287b9eb47c7f15a112f2c82f15c80c2");			
       	 	String smartpath = "pages/index/index?agent_code=" + agent_code+"&openid="+openid;
			InputStream is = SmartAqrCode.createwxaqrcode(
					"https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode",
					access_token, smartpath, "430");
			
			String qrcodeUrl = SmartAqrCode.saveToImgByInputStream(is, "salsa/product_photo/openid/",agent_code+ "_" + openid +".png");
		
			String ext =SmartAqrCode.getFileExt(qrcodeUrl);
			if(ext == "jpg")
				SmartAqrCode.cutImage(qrcodeUrl, qrcodeUrl, 20, 20, 430, 430);
			else if(ext == "png")
				SmartAqrCode.cutPNG(qrcodeUrl, qrcodeUrl, 20, 20, 430, 430);
			
			String imagePath="https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqoIXjSX9Nd1zIjicf7HrWiawRy50yp6saJ1PGotTHfb7LcQWYVwMsnBZff0DCq9228BDbv4f7Q3DDA/132";
            LogoConfig logoConfig = new LogoConfig(); //LogoConfig中设置Logo的属性
	        
			SmartAqrCode.addLogo_QRCode(new File(qrcodeUrl), new File(imagePath), logoConfig,qrcodeUrl);
        }   
        json_result.put("result", "ok");  
		json_result.put("msg", url);
		return json_result;
	}
	
	

	public static JSONObject parseProductAq(HttpServletRequest request, HttpServletResponse response){

		JSONObject json_result = new JSONObject();
		String agent_code=request.getParameter("agent_code");
		String openid= request.getParameter("openid");
		if(agent_code == null){
			json_result.put("result", "fail");
			json_result.put("msg", "门店编码为空");
			return json_result;
		}
		if(openid == null){
			json_result.put("result", "fail");
			json_result.put("msg", "openid为空");
			return json_result;
		}
		String select_sql="select kcc_id from WeiXinSmart_User  where s_wxopenid ='"+openid+"'";
		//String agent_code="31000030";
		try {
			SQLQuery query = DSManager.getSQLQuery();
			String data[][]=query.ArrayQuery(select_sql);
			if(data.length > 0){
				String fileUrl = null;
		        fileUrl = ("http://ad-kcc.dderp.cn/mob/salsa/product_photo/openid/"+agent_code + "_" + openid +"_smartaqr.png");
		     //   String urlPath ="E:\\Webase\\Webase_Mob\\WebRoot\\salsa\\product_photo\\openid\\"+agent_code+ "_" + openid+"_smartaqr.png";
		        String urlPath="D:\\Tomcat7.0\\ad-kcc\\mob\\salsa\\product_photo\\openid\\"+agent_code+ "_" + openid+"_smartaqr.png";
		        File file=new File(urlPath);    
		        if(!file.exists())    
		        {   
		        	String access_token = SmartAqrCode.getAccessToken(
						"https://api.weixin.qq.com/cgi-bin/token", "client_credential",
						"wxf85e801e63bb2164", "9287b9eb47c7f15a112f2c82f15c80c2");
		        	 URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+access_token);
		             HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		             httpURLConnection.setRequestMethod("POST");// 提交模式
		             
		             // 发送POST请求必须设置如下两行
		             httpURLConnection.setDoOutput(true);
		             httpURLConnection.setDoInput(true);
		             // 获取URLConnection对象对应的输出流
		             PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
		             // 发送请求参数
		             JSONObject paramJson = new JSONObject();
		             paramJson.put("scene", "a-"+agent_code+"'c-"+data[0][0]);
		             paramJson.put("page", "pages/index/index");
		             paramJson.put("width", 430);
		             paramJson.put("auto_color", true);
		             printWriter.write(paramJson.toString());
		           //图片保存位置
		              // flush输出流的缓冲
		             printWriter.flush();
		            //开始获取数据
		             BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
		             OutputStream os = new FileOutputStream(new File(urlPath));
		             int len;
		             byte[] arr = new byte[1024];
		             while ((len = bis.read(arr)) != -1)
		             {
		                 os.write(arr, 0, len);
		                 os.flush();
		             }
		             os.close();
		             
		        }   
		        json_result.put("result", "ok");  
				json_result.put("msg", fileUrl);
				return json_result;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json_result.put("result", "fail");
			json_result.put("msg", "执行sql出错"+e.toString());
			return json_result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json_result.put("result", "fail");
			json_result.put("msg", "执行出错"+e.toString());
			return json_result;
		}
		
		

		return json_result;
	}
	
	
	public static void main(String[] args) {
//		JSONObject json_result =   LikeQrCode.parseProductAq("oAYH949mHI5sAqq4lt0d1XXMu924");
//		
//		System.out.println("JSONObject json_result: "+ json_result);
	}
	
}
