package com.novarise.webase.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONObject;

import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
public class XMLHandler {
    public XMLHandler(){
        
    }
    
 

    public JSONObject stringToXmlByDom4j(String content,String encoding){
    	JSONObject result = new JSONObject();
        try {
        SAXReader saxReader=new SAXReader();
            org.dom4j.Document docDom4j=saxReader.read(new ByteArrayInputStream(content.getBytes(encoding)));
            org.dom4j.Element root = docDom4j.getRootElement();
          
             
            List<org.dom4j.Element> childElements = root.elements();
            String e1name = "";
             
            for (org.dom4j.Element e1 : childElements) {
             //   System.out.println("第一层："+e1.getName() + ": " + e1.getText());
                if(e1.getText().equals("") && e1.nodeCount() >0 ){
                	 HashMap<String, Object> result2 = new HashMap<String, Object>();  
                     
                	 List<HashMap<String, Object>> result3 = new ArrayList<HashMap<String, Object>>();   
                	for (org.dom4j.Element child : childElements) {  
                        List<org.dom4j.Element> elementList = child.elements();                        
                        for (org.dom4j.Element ele : elementList) {
                        	// System.out.println("第二层1："+ele.getName() + ": " + ele.getText());                         
                            if(ele.getText().equals("") &&  ele.nodeCount() >0 ){
								List<org.dom4j.Element> lidList = ele.elements(); 
                                int size = lidList.size();
                                if(size>0){
                                    HashMap<String, Object> result4 = new HashMap<String, Object>();    
                                    for (org.dom4j.Element e2 : lidList) {
	                                   //  System.out.println("第三层："+e2.getName() + ": " + e2.getText());
	                                     result4.put(e2.getName(), e2.getText());
                                    } 
                                    result3.add(result4);
                                }

                                result2.put(ele.getName(), result3);
                            }else{
                            	 result2.put(ele.getName(), ele.getText());
                            }
                        }
                    
                       }
                	result.put(e1.getName(), result2);
                	/*if(result2.size()> 0)
                	 result.put(e1.getName(), result2);
                	else
                   	 result.put(e1.getName(), "");*/
                }else{
                	 result.put(e1.getName(), e1.getText());
                }
                
               } 
             
          
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("result:"+result.toString());
        return result;
    }
    
    
    public static String getRequestXml(JSONObject parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\" ?><B2CReq>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            sb.append("<"+k+">"+v+"</"+k+">");
        }
        sb.append("</B2CReq>");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        XMLHandler handler = new XMLHandler();
        // String xml="<?xml version=\"1.0\" encoding=\"gb2312\" ?><B2CReq><result>0000</result><resultDesc>预下单成功</resultDesc><qrCode>weixin://wxpay/bizpayurl?pr=fQReGkK</qrCode><token/><pay_url/><state/></B2CReq>";
        //  String  xml="<?xml version=\"1.0\" encoding=\"GBK\" ?><B2CRep><bankCount>29</bankCount><bankList><bankRow><bankName>网联快捷工行</bankName><bankID>502Q</bankID><otherBankID/><cardType>02</cardType></bankRow><bankRow><bankName>网联快捷工行</bankName><bankID>502Q</bankID><otherBankID/><cardType>01</cardType></bankRow><bankRow><bankName>网联光大银行网关</bankName><bankID>599C</bankID><otherBankID>303</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信公众号小程序-线下</bankName><bankID>902C</bankID><otherBankID>902C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信扫码支付-线下</bankName><bankID>903C</bankID><otherBankID>903C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信APP支付-线下</bankName><bankID>904C</bankID><otherBankID>904C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信H5支付-线下</bankName><bankID>905C</bankID><otherBankID>905C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信扫码枪支付-线下</bankName><bankID>906C</bankID><otherBankID>906C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝扫码支付</bankName><bankID>911C</bankID><otherBankID>911C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝刷卡支付</bankName><bankID>912C</bankID><otherBankID>912C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝扫描枪支付-减免类</bankName><bankID>913C</bankID><otherBankID>913C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝扫描枪支付-优惠类</bankName><bankID>914C</bankID><otherBankID>914C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝扫描枪支付-互联网类</bankName><bankID>915C</bankID><otherBankID>915C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝扫描支付-减免类</bankName><bankID>916C</bankID><otherBankID>916C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝扫描支付-优惠类</bankName><bankID>917C</bankID><otherBankID>917C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联支付宝扫描支付-互联网类</bankName><bankID>918C</bankID><otherBankID>918C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信公众号小程序-缴费</bankName><bankID>921C</bankID><otherBankID>921C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信扫码支付-缴费</bankName><bankID>922C</bankID><otherBankID>922C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信APP支付-缴费</bankName><bankID>923C</bankID><otherBankID>923C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信H5支付-缴费</bankName><bankID>924C</bankID><otherBankID>924C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信扫码枪支付-缴费</bankName><bankID>925C</bankID><otherBankID>925C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>医院银联无感支付</bankName><bankID>932C</bankID><otherBankID>932C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联扫码</bankName><bankID>969C</bankID><otherBankID>969C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>医院扫码枪B</bankName><bankID>972C</bankID><otherBankID>972C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信公众号小程序-线上</bankName><bankID>981C</bankID><otherBankID>981C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信扫码支付-线上</bankName><bankID>982C</bankID><otherBankID>982C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信APP支付-线上</bankName><bankID>983C</bankID><otherBankID>983C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信H5支付-线上</bankName><bankID>984C</bankID><otherBankID>984C</otherBankID><cardType>X</cardType></bankRow><bankRow><bankName>银联微信刷卡支付-线上</bankName><bankID>985C</bankID><otherBankID>985C</otherBankID><cardType>X</cardType></bankRow></bankList></B2CRep>";
      /*  String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root><result><bankCount></bankCount></result><TelePhone><type><price>599</price><operator>CMCC</operator></type><type><price>699</price><operator>ChinaNet</operator></type></TelePhone></root>";
        System.out.println(xml);
        handler.stringToXmlByDom4j(xml,"GBK");*/
        JSONObject map = new JSONObject();
        map.put("result", "0000");
        map.put("bankCount","22");
        map.put("resultDesc", "预下单成功");
        String aaa = getRequestXml( map);
        System.out.println(aaa);
    }
}
