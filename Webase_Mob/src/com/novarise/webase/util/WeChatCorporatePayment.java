package com.novarise.webase.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;











import javax.servlet.http.HttpServletRequest;

import org.jdom.JDOMException;

import net.sf.json.JSONObject;











import com.novarise.webase.util.GenerateSequenceUtil;
import com.novarise.webase.util.HttpUtil;
import com.novarise.webase.util.MD5Util;
import com.novarise.webase.util.XMLUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;

public class WeChatCorporatePayment {

	public static JSONObject parseDisplayWXEnterPricePay(String proname,
			HttpServletRequest request) {
		// TODO Auto-generated method stub

		
			
			JSONObject json_result = new JSONObject();
			String fee=request.getParameter("fee");
		    
		    
			if(fee ==null || "".equals(fee))
			{
				json_result.put("result", "fail");
				json_result.put("reason", "费用为空");
				return json_result;
			}
			String ip=request.getParameter("ip_address");
			if(ip == null || "".equals(ip)){
				json_result.put("result", "fail");
				json_result.put("reason", "ip地址为空");
				return json_result;
			}

			String openid = request.getParameter("open_id");
			if(openid == null || "".equals(openid)){
				json_result.put("result", "fail");
				json_result.put("reason", "openid地址为空");
				return json_result;
			}
			
			SortedMap<Object, Object> params = new TreeMap<Object, Object>();
			
			
			params.put("mch_appid","wxf85e801e63bb2164");
			params.put("mchid","1355185802");  

			params.put("openid",openid);
			
			String random=java.util.UUID.randomUUID().toString();
			random = random.replace("-", "");
			params.put("nonce_str",random);
			
		   
			
			String out_trade_no="ZZ"+GenerateSequenceUtil.generateSequenceNo();
			params.put("partner_trade_no",out_trade_no);
			
			params.put("check_name","NO_CHECK");
			
			
			params.put("amount",fee); //转账金额分
			params.put("desc","来自【艾来客】的提现，用【艾来客】分享朋友，获取零花钱！");
			
			params.put("spbill_create_ip",ip);
			
			String sign=MD5Util.createSign("UTF-8", params);
			params.put("sign",sign);
			
			String requestXML = HttpUtil.getRequestXml(params);
			//System.out.println("params:"+params);
			//String result =HttpUtil.httpsRequestForCer("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", "POST", requestXML);
			String result;
			Map resultMap = null;
			try {
				result = HttpUtil.httpsRequestForCer("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers","POST",requestXML);
				System.out.println("response:"+result);
				if(result!=null&&!"".equals(result)){
				
					resultMap = XMLUtil.doXMLParse(result);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();json_result.put("result", "fail");
			    json_result.put("reason",e1.toString());
			    return json_result;
			}
			
		
			

			String wx_account = request.getParameter("wx_account");
			
			if ( resultMap.get("return_code").toString().equalsIgnoreCase("SUCCESS") ){
				if(resultMap.get("result_code").toString().equalsIgnoreCase("SUCCESS") )
				{ 
					json_result.put("result", "ok");
					String insert_sql = "insert into OrderBound (bound,bound_date,bound_type,bound_memo,isread,open_id,bound_sts,payment_no) values(-@@fee,,GETDATE(),'提现','小程序提现',0,'@@openid,','已入账','@@payment_no,')";
					insert_sql = insert_sql.replaceAll("@@fee,", String.valueOf(Integer.parseInt(fee)*0.01));
					insert_sql = insert_sql.replaceAll("@@openid,", openid);
					insert_sql = insert_sql.replaceAll("@@payment_no,", resultMap.get("payment_no").toString());
					String upd_sql = "update WeiXinSmart_User set wx_account='@@wx_account,', wx_amount =wx_amount - @@fee, where s_wxopenid='@@openid,'";
					upd_sql = upd_sql.replaceAll("@@fee,", String.valueOf(Integer.parseInt(fee)*0.01));
					upd_sql = upd_sql.replaceAll("@@openid,", openid); 
					upd_sql = upd_sql.replaceAll("@@wx_account,", wx_account); 
					SQLUpdater updater;
					try {
						updater = DSManager.getSQLUpdater();
						updater.executeUpdate(upd_sql); 
						updater.executeUpdate(insert_sql);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					json_result.put("payment_no", resultMap.get("payment_no").toString());
				}
				else{
					json_result.put("result", "fail");
				    json_result.put("reason", resultMap.get("err_code_des").toString());
				}
				    
				
			}
			
			return json_result;
		}

	
	

}
