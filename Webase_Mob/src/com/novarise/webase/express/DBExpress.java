package com.novarise.webase.express;


import java.io.StringReader;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.util.XMLHandler;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;
import com.ruirong.certUtil.ProcessMessage;

public class DBExpress {

	private static String appKey = "d8ac278cc2ca7dafc7b3e50302ce4f32"; 
		 
	private static String sign = "MEYX";
	
	private static String companyCode = "EWBSHSFJMYQYXGS";
	
	private static String url ="http://dpsanbox.deppon.com/sandbox-web/dop-standard-ewborder/ewbNewSaveOrder.action";
	
	private static final String certPath= "D:/cer/yoyiTestNew.cer";
	
	public JSONObject  getParams(){
		String cargoName= "货物名称";//货物名称
		String totalNumber= "1"; //货物件数
		String totalWeight = "1.27";//总重量
		String payType = "0";   //0:发货人付款（现付） 1:收货人付款（到付）; 2：发货人付款（月结）
		String transportType = "EPEP"; //运送方式HK_JZKY:精准空运 QC_JZKH:精准卡航 QC_JZQYC:精准汽运（长） QC_JZQYD:精准汽运（短） QC_JZCY:精准城运; 快递运输方式----PACKAGE： 标准快递; RCP :360特惠件; EPEP:电商尊享; ZBRH: 3.60特重件; NZBRH : 重包入户
		String deliveryType = "1";  //送货方式 0:自提 1:送货（不含上楼） 2:机场自提 3:送货上楼
		String backSignBill = "0";  //签收回单  0:无需返单 1：签收单原件返回 2:客户签收单传真返回 4: 运单到达联传真返回
		long  random= System.currentTimeMillis();
		JSONObject params = new JSONObject();
		params.put("logisticCompanyID", "DEPPON");

		params.put("logisticID", sign+  random);
		params.put("orderSource", companyCode);
		params.put("serviceType", "2");
		params.put("customerCode", "1243567654");
		params.put("customerID", companyCode);
			JSONObject sender = new JSONObject();
			String sender_name = "杨妍卉";
			String sender_phone ="021-63238808"; //电话
			String sender_mobile =""; //手机
			String sender_province = "上海";
			String sender_city = "上海市";
			String sender_address = "黄浦区中山南路969号谷泰滨江大厦1603室 ";
			sender.put("name", sender_name);
			sender.put("phone", sender_phone);
			sender.put("mobile", sender_mobile);
			sender.put("province", sender_province);
			sender.put("city", sender_city);
			sender.put("address", sender_address);
			params.put("sender", sender);
			JSONObject receiver = new JSONObject();
			String receiver_name = "蒋德坤" ;
			String receiver_phone= "";
			String receiver_mobile = "13611684584";
			String receiver_province = "上海";
			String receiver_city = "上海市";
			String receiver_address = "闵行区吴泾镇龙吴路4250号，摩根新材料上海";
			receiver.put("name", receiver_name);
			receiver.put("phone", receiver_phone);
			receiver.put("mobile", receiver_mobile);
			receiver.put("province", receiver_province);
			receiver.put("city", receiver_city);
			receiver.put("address", receiver_address);
			params.put("receiver", receiver);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	        
		params.put("gmtCommit", df.format(new Date()));  // new Date()为获取当前系统时间
		params.put("cargoName", cargoName);
		params.put("totalNumber", totalNumber);
		params.put("totalWeight", totalWeight);
		params.put("payType", payType);
		params.put("transportType", transportType);
		params.put("deliveryType", deliveryType);
		params.put("backSignBill", backSignBill);
		return params;
	}
	
	public String getDigest(String plainText)
    {
	return Base64.encodeBase64String(DigestUtils.md5Hex(plainText).getBytes());
    }
	

	private static void xmlParse(String xmlString){
		try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            
            Element root = doc.getDocumentElement();
            NodeList books = root.getChildNodes();
           if (books != null) {
               for (int i = 0; i < books.getLength(); i++) {
                    Node book = books.item(i);
                    System.out.println(book.getNodeName() + " : "
                            + book.getFirstChild().getNodeValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public static void main(String[] args) throws Exception {
		/*DBExpress db = new DBExpress();
		JSONObject params = db.getParams();
		long timestamp  = System.currentTimeMillis();
		String plainText = params + appKey + timestamp;
		String digest= db.getDigest(plainText);
		System.out.println("params:"+params);
		JSONObject json = new JSONObject();
		//import net.sf.json.JSONObject;
		json.put("params", params);
		json.put("digest", digest);
		json.put("timestamp", timestamp);
		json.put("companyCode", "EWBSHSFJMYQYXGS");
		
		System.out.println("json:"+json);
		String result = HttpUtil.doPost(url, json.toString());
		System.out.println("result--:"+result);*/
		String result = "tranData=PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iR0JLIiA%2FPjxCMkNSZXM%2BPG1lcmNoYW50SWQ%2BTTEwMDAwMjY1MzwvbWVyY2hhbnRJZD48b3JkZXJObz4xMTQxNDQ4ODIzQTIzMDAyPC9vcmRlck5vPjx0cmFuU2VyaWFsTm8%2BWkYyMDE5MDMyMDEwNjQyNTMyODM8L3RyYW5TZXJpYWxObz48b3JkZXJBbXQ%2BMS4wMDwvb3JkZXJBbXQ%2BPGN1clR5cGU%2BQ05ZPC9jdXJUeXBlPjx0cmFuVGltZT4yMDE5MDMyMDE1MTMzOTwvdHJhblRpbWU%2BPHRyYW5TdGF0PjE8L3RyYW5TdGF0PjxyZW1hcms%2BsKy2qbv1PC9yZW1hcms%2BPHN1Ym1lcm5vPjAwNDYxNzUwPC9zdWJtZXJubz48L0IyQ1Jlcz4%3D&signData=VVvLD%2F7VxibAu81oaAmwk0a%2BNaUNnvQkJyG5dOVAevbP1ge6Aa%2BN%2BoeUj4qg3MJAMCxnsuOLMYTspKfQZPPpIlm8MNxhjCb7hNaVOX1rEJ44N31qafNIyaLwHPfdiyDK2%2FGfvuRuiPVOKygoGKE7HboqE00IokijBNaVX9WnuPpJemzfjmBFzh9g18OeT%2Bta9PNxhY0aK4ClR5kTA5KyDKrVzaxRv2Rilmt0Ue6i%2BkfUHT4a2jpS4yIgDn%2BGKLYEbCosNhwZFUblnoU41pojZMeft3KrcA5ltsaUSW05hbbtMj0glBJ9IHk55YgOej4KPypp7KdOzII8sWpA5MUVfA%3D%3D&interfaceName=PayOrderNotify&version=B2C1.0";
		JSONObject jsonData = new JSONObject();

    	String [] get_Amount=result.split("&");
		
		for(int i=0;i<get_Amount.length;i++){
    		String keyvalue[]=get_Amount[i].split("=");
    		if(keyvalue.length == 2){
    		     String key=keyvalue[0];
    		     String value=keyvalue[1];
    		     jsonData.put(key, value);
    		}
    		
    	}
		System.out.println("jsonData:"+jsonData);
		
		
		
		String interfaceName = jsonData.get("interfaceName").toString();
		String tranDataBase64 = jsonData.get("tranData").toString();
		tranDataBase64 =URLDecoder.decode(tranDataBase64);
		String signData = jsonData.get("signData").toString();
		signData =URLDecoder.decode(signData);
		String version = jsonData.get("version").toString();
		String tranDataGBK = new String(ProcessMessage.Base64Decode(tranDataBase64),"GBK");//通知base64解密用GBK
		String tranDataUTF8 = new String(ProcessMessage.Base64Decode(tranDataBase64),"UTF-8");//通知base64解密用UTF-8
		XMLHandler handler = new XMLHandler();
		net.sf.json.JSONObject result2 = handler.stringToXmlByDom4j(tranDataGBK,"GBK");

   	 String agent_code=((String)result2.get("orderNo")).split("A")[1];
   	 if( (result2.get("tranStat").toString()).equals("1")){
   		String sj_amount  = result2.get("orderAmt").toString() ;
   	 String bz=(String)result2.get("tranSerialNo")+" "+(String)result2.get("tranTime");
   	 
   	 String sql="{ call sp_cwsk_mob('@@agent_code,','@@sk_amount,','货款','@@bz,') }";
   	 sql=sql.replaceAll("@@agent_code,", agent_code);
   	 sql=sql.replaceAll("@@sk_amount,", sj_amount);
   	 sql=sql.replaceAll("@@bz,", bz);
   	 System.out.print("sql="+sql);
   /*	 try {
				//SQLUpdater updater = DSManager.getSQLUpdater();
				//updater.executeCall(sql);
				
				
				//response.getWriter().write("success"); 
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
   	 }
		/*System.out.println("-------签名验证-------");
		//用CERT、MD5、ITRUS三种方式，测试时按需选择，后续只保留CERT方式。
		//CERT
		String verifyGBK = ProcessMessage.verifyMessage(tranDataGBK, signData, certPath);
		System.out.println("CERT验签结果(GBK)：" + verifyGBK);
		if (verifyGBK.equals("0")) {
			xmlParse(tranDataGBK);
		}
		String verifyUTF8 = ProcessMessage.verifyMessage(tranDataUTF8, signData, certPath);
		System.out.println("CERT验签(UTF-8)结果：" + verifyUTF8);
		if (verifyUTF8.equals("0")) {
		//	xmlParse(tranDataUTF8);
		}*/
		
	}
	
	
}
