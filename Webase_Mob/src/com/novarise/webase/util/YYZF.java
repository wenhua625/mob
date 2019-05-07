package com.novarise.webase.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

import com.ruirong.certUtil.ProcessMessage;

public class YYZF {
	private static final long serialVersionUID = 1L;
    
	/*
	 * 私钥证书路径
	 */
	private static String PFX_PATH = "D:/cer/yoyiTestNew.pfx";

	/*
	 * 公钥证书路径
	 */
	private static String CERT_PATH = "D:/cer/yoyiTestNew.cer";
	/*
	 * 私钥密码
	 */
	private static String PASSWORD = "1q2w3e4r";
	
	public String getBanksForPayStr(String remark){
		String sourceData ="<?xml version=\"1.0\" encoding=\"GBK\" ?><B2CReq><remark>"+remark+"</remark></B2CReq>";
		return sourceData;
		
	}
	
	

	
	public static  JSONObject getTranData(String  sourceDat,String encryptType,String signKey ) throws ServletException, IOException {
		String sourceData = URLDecoder.decode(sourceDat,"UTF-8");		
		
		String tranData = ProcessMessage.Base64Encode(sourceData.getBytes(Charset.forName("GBK")));
		String signMsg = "";
		if ("MD5".equalsIgnoreCase(encryptType)) {
			signMsg = DigestUtil.hmacSign(sourceData, signKey);
		}
		if("CERT".equalsIgnoreCase(encryptType)){
		//	System.out.println("签名模式：CERT");
			// 对消息签名
			signMsg = ProcessMessage.signMessage(sourceData, PFX_PATH, PASSWORD);
			}
		if ("ITRUS".equalsIgnoreCase(encryptType)) { 
		//	System.out.println("签名模式：ITRUS");
			signMsg = CertTools.signMessage(sourceData);
			//签名自检
			//System.out.println("签名自检:"+CertTools.verifyMessage(signMsg, sourceData, "UTF-8"));
		}
		if (encryptType == null) {
			signMsg = DigestUtil.hmacSign(sourceData, "123123");
		}
		
	
		Map<String, String> map = new HashMap<String, String>();
		map.put("tranData",  URLEncoder.encode( tranData,"utf-8"));
		map.put("merSignMsg",URLEncoder.encode( signMsg,"utf-8" ) );		
	
		
		JSONObject json=new JSONObject();
		json.accumulateAll(map);
		
		//System.out.println("json : "+json);
		return json;
	}
	
	public static void main(String[] args) throws Exception {
		JSONObject map = new JSONObject() ;
		String merchantId = "M100002653";
		String orderNo = "XS201903181708A31013";
		String orderAmt = "1";
		String curType = "CNY";
		String bankId = "888C";
		String returnURL = "http://ad-idh.dderp.cn/mob/weixinmonitor";
		String notifyURL = "http://ad-idh.dderp.cn/mob/weixinmonitor";
		//String notifyURL = "https://www.baidu.com";
		String remark = "艾订货";
		String cardType = "X";
		String userId = "13052017211";    //商城登记的发起支付交易的用户号（可传手机号）
		String goodsType = "0";   //0-实体商品,1-虚拟商品
		String isBind = "0";    //值为1时需要商户送客户的卡信息
		String mobile = "";
		String certNo = "";
		String userName = "";
		String cardNo = "";
		/*
		String cardNo = "6217001180018868813";*/
		/*String mobile = "13552535506";
		String certNo = "341126197709218366";
		String userName = "全渠道";
		String cardNo = "6216261000000000018";*/
	
		String bankType = "";
		String returnFlag = "1";
		String subAppid = "";
		String subUserid = "";
		String MSMerchantIdB = "00461750"; //子商户号
		String holdAmt = "0.01";
		
		String  subType = "1";
		
		
		 map.put("merchantId",URLDecoder.decode(merchantId,"UTF-8"));
		 map.put("orderNo", URLDecoder.decode(orderNo,"UTF-8"));
		 map.put("orderAmt", URLDecoder.decode(orderAmt,"UTF-8"));
		 map.put("curType", URLDecoder.decode(curType,"UTF-8"));
		 map.put("bankId", URLDecoder.decode(bankId,"UTF-8"));
		 map.put("returnURL", URLDecoder.decode(returnURL,"UTF-8"));
		 map.put("notifyURL", URLDecoder.decode(notifyURL,"UTF-8"));
		 map.put("remark", URLDecoder.decode(remark,"UTF-8"));
		 map.put("cardType", URLDecoder.decode(cardType,"UTF-8"));
		 map.put("userId", URLDecoder.decode(userId,"UTF-8"));
		 map.put("goodsType", URLDecoder.decode(goodsType,"UTF-8"));
		 map.put("isBind", URLDecoder.decode(isBind,"UTF-8"));
		 map.put("mobile", URLDecoder.decode(mobile,"UTF-8"));
		 map.put("certNo", URLDecoder.decode(certNo,"UTF-8"));
		 map.put("userName", URLDecoder.decode(userName,"UTF-8"));
		 map.put("cardNo", URLDecoder.decode(cardNo,"UTF-8"));
		 map.put("bankType", URLDecoder.decode(bankType,"UTF-8"));
		 map.put("returnFlag", URLDecoder.decode(returnFlag,"UTF-8"));
		 

		 map.put("MSMerchantIdB", URLDecoder.decode(MSMerchantIdB,"UTF-8"));
		 map.put("holdAmt", URLDecoder.decode(holdAmt,"UTF-8"));
		 map.put("subType", URLDecoder.decode(subType,"UTF-8"));
		 map.put("subNo", URLDecoder.decode(MSMerchantIdB,"UTF-8"));
		 
		 
		 map.put("subAppid", URLDecoder.decode(subAppid,"UTF-8"));
		 map.put("subUserid", URLDecoder.decode(subUserid,"UTF-8"));
	
		 String sourceData = XMLHandler.getRequestXml(map);
		 
		 System.out.println("sourceData:"+sourceData);
		//JSONObject json=getTranData(sourceData,"MD5","123123" );CERT
		 JSONObject json=getTranData(sourceData,"CERT","" );
		System.out.println("merSignMsg-- : "+json.get("merSignMsg"));
		System.out.println("tranData--:"+json.get("tranData"));
		 String tojsonstring=json.toString();//将json格式的数据转换为字符格式
         tojsonstring=URLEncoder.encode(tojsonstring,"utf-8");//将数据进行编码
		System.out.println("json："+"http://192.168.0.106:8080/mob/payOrder.html?tranData="+ URLEncoder.encode(json.get("tranData").toString(),"utf-8")+"&merSignMsg="+URLEncoder.encode(json.get("merSignMsg").toString(),"utf-8"));
		/*json.put("interfaceName", "anonymousPayOrder");
		json.put("version", "B2C1.0");
		
		//json.put("merchantId", "M100001520"); //测试子商户
		json.put("merchantId", "M100002653");
		json.put("goodsName", "艾订货");
		
		//String url="http://60.12.221.84:28080/pay/anonymousPayOrder.do"; //沙箱环境
		String url = "https://pay.yoyipay.com/pay/anonymousPayOrder.do";
		System.out.println("json : "+json);
		
		String s = HttpUtil.doPostJson( url, json.toString()); 		
		System.out.println("s:"+s);*/
		/*String xml=Base64Util.decode(s,"UTF-8");
		System.out.println(xml);
		XMLHandler handler = new XMLHandler();
        handler.stringToXmlByDom4j(xml,"UTF-8");*/
	}
	
}
