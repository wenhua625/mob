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
		String cargoName= "��������";//��������
		String totalNumber= "1"; //�������
		String totalWeight = "1.27";//������
		String payType = "0";   //0:�����˸���ָ��� 1:�ջ��˸��������; 2�������˸���½ᣩ
		String transportType = "EPEP"; //���ͷ�ʽHK_JZKY:��׼���� QC_JZKH:��׼���� QC_JZQYC:��׼���ˣ����� QC_JZQYD:��׼���ˣ��̣� QC_JZCY:��׼����; ������䷽ʽ----PACKAGE�� ��׼���; RCP :360�ػݼ�; EPEP:��������; ZBRH: 3.60���ؼ�; NZBRH : �ذ��뻧
		String deliveryType = "1";  //�ͻ���ʽ 0:���� 1:�ͻ���������¥�� 2:�������� 3:�ͻ���¥
		String backSignBill = "0";  //ǩ�ջص�  0:���践�� 1��ǩ�յ�ԭ������ 2:�ͻ�ǩ�յ����淵�� 4: �˵����������淵��
		long  random= System.currentTimeMillis();
		JSONObject params = new JSONObject();
		params.put("logisticCompanyID", "DEPPON");

		params.put("logisticID", sign+  random);
		params.put("orderSource", companyCode);
		params.put("serviceType", "2");
		params.put("customerCode", "1243567654");
		params.put("customerID", companyCode);
			JSONObject sender = new JSONObject();
			String sender_name = "������";
			String sender_phone ="021-63238808"; //�绰
			String sender_mobile =""; //�ֻ�
			String sender_province = "�Ϻ�";
			String sender_city = "�Ϻ���";
			String sender_address = "��������ɽ��·969�Ź�̩��������1603�� ";
			sender.put("name", sender_name);
			sender.put("phone", sender_phone);
			sender.put("mobile", sender_mobile);
			sender.put("province", sender_province);
			sender.put("city", sender_city);
			sender.put("address", sender_address);
			params.put("sender", sender);
			JSONObject receiver = new JSONObject();
			String receiver_name = "������" ;
			String receiver_phone= "";
			String receiver_mobile = "13611684584";
			String receiver_province = "�Ϻ�";
			String receiver_city = "�Ϻ���";
			String receiver_address = "����������������·4250�ţ�Ħ���²����Ϻ�";
			receiver.put("name", receiver_name);
			receiver.put("phone", receiver_phone);
			receiver.put("mobile", receiver_mobile);
			receiver.put("province", receiver_province);
			receiver.put("city", receiver_city);
			receiver.put("address", receiver_address);
			params.put("receiver", receiver);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
	        
		params.put("gmtCommit", df.format(new Date()));  // new Date()Ϊ��ȡ��ǰϵͳʱ��
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
		String tranDataGBK = new String(ProcessMessage.Base64Decode(tranDataBase64),"GBK");//֪ͨbase64������GBK
		String tranDataUTF8 = new String(ProcessMessage.Base64Decode(tranDataBase64),"UTF-8");//֪ͨbase64������UTF-8
		XMLHandler handler = new XMLHandler();
		net.sf.json.JSONObject result2 = handler.stringToXmlByDom4j(tranDataGBK,"GBK");

   	 String agent_code=((String)result2.get("orderNo")).split("A")[1];
   	 if( (result2.get("tranStat").toString()).equals("1")){
   		String sj_amount  = result2.get("orderAmt").toString() ;
   	 String bz=(String)result2.get("tranSerialNo")+" "+(String)result2.get("tranTime");
   	 
   	 String sql="{ call sp_cwsk_mob('@@agent_code,','@@sk_amount,','����','@@bz,') }";
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
		/*System.out.println("-------ǩ����֤-------");
		//��CERT��MD5��ITRUS���ַ�ʽ������ʱ����ѡ�񣬺���ֻ����CERT��ʽ��
		//CERT
		String verifyGBK = ProcessMessage.verifyMessage(tranDataGBK, signData, certPath);
		System.out.println("CERT��ǩ���(GBK)��" + verifyGBK);
		if (verifyGBK.equals("0")) {
			xmlParse(tranDataGBK);
		}
		String verifyUTF8 = ProcessMessage.verifyMessage(tranDataUTF8, signData, certPath);
		System.out.println("CERT��ǩ(UTF-8)�����" + verifyUTF8);
		if (verifyUTF8.equals("0")) {
		//	xmlParse(tranDataUTF8);
		}*/
		
	}
	
	
}
