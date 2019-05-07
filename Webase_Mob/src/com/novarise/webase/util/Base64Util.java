package com.novarise.webase.util;



import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;










import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import sun.misc.BASE64Decoder;

/**
 * base64 编码、解码util
 * 
 * @author lifq
 * @date 2015-3-4 上午09:23:33
 */
public class Base64Util {

	/**
	 * 将 s 进行 BASE64 编码
	 * @param s
	 * @return
	 */
    public static String encode(String s) {
        if (s == null)
            return null;
        String res = "";
        try {
            res = new sun.misc.BASE64Encoder().encode(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 将 BASE64 编码的字符串 s 进行解码
     * @param s
     * @return
     */
    public static String decode(String s ,String encoding) {
        if (s == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b,encoding);
        } catch (Exception e) {
            return null;
        }
    }


  
   public static JSONObject  parserXMLSingle(String strXML) throws DocumentException{
	   Document document=DocumentHelper.parseText(strXML);//xmlStr为上图格式的字符串
       String result=(document.selectSingleNode("//result")).getText();//获取节点对象,注意引号内的“//”必须加 ，否则报错
       String resultDesc= (document.selectSingleNode("//resultDesc")).getText();
       String qrCode= (document.selectSingleNode("//qrCode")).getText();
       String token= (document.selectSingleNode("//token")).getText();
       String pay_url= (document.selectSingleNode("//pay_url")).getText();
       String state= (document.selectSingleNode("//state")).getText();    
      
       JSONObject json=new JSONObject();
       json.put("result", result);
       json.put("resultDesc", resultDesc);
       json.put("qrCode", qrCode);
       json.put("token", token);
       json.put("pay_url", pay_url);
       json.put("state", state);
       System.out.println(json.toString());
       return json;
   }
   
   
   
    public static void main(String[] args) throws DocumentException { 
    	XMLHandler handler = new XMLHandler();
       String xml=Base64Util.decode("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iR0JLIiA/PjxCMkNSZXA+PGJhbmtDb3VudD4yOTwvYmFua0NvdW50PjxiYW5rTGlzdD48YmFua1Jvdz48YmFua05hbWU+zfjBqr/svd25pNDQPC9iYW5rTmFtZT48YmFua0lEPjUwMlE8L2JhbmtJRD48b3RoZXJCYW5rSUQvPjxjYXJkVHlwZT4wMjwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7N+MGqv+y93bmk0NA8L2JhbmtOYW1lPjxiYW5rSUQ+NTAyUTwvYmFua0lEPjxvdGhlckJhbmtJRC8+PGNhcmRUeXBlPjAxPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPs34waq54rTz0vjQ0M34udg8L2JhbmtOYW1lPjxiYW5rSUQ+NTk5QzwvYmFua0lEPjxvdGhlckJhbmtJRD4zMDM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warOotDFuavW2rrF0KGzzNDyLc/fz8I8L2JhbmtOYW1lPjxiYW5rSUQ+OTAyQzwvYmFua0lEPjxvdGhlckJhbmtJRD45MDJDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxcmowuvWp7i2Lc/fz8I8L2JhbmtOYW1lPjxiYW5rSUQ+OTAzQzwvYmFua0lEPjxvdGhlckJhbmtJRD45MDNDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxUFQUNanuLYtz9/PwjwvYmFua05hbWU+PGJhbmtJRD45MDRDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjkwNEM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warOotDFSDXWp7i2Lc/fz8I8L2JhbmtOYW1lPjxiYW5rSUQ+OTA1QzwvYmFua0lEPjxvdGhlckJhbmtJRD45MDVDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxcmowuvHudanuLYtz9/PwjwvYmFua05hbWU+PGJhbmtJRD45MDZDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjkwNkM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warWp7i2sabJqMLr1qe4tjwvYmFua05hbWU+PGJhbmtJRD45MTFDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjkxMUM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warWp7i2sabLor+o1qe4tjwvYmFua05hbWU+PGJhbmtJRD45MTJDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjkxMkM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warWp7i2sabJqMPox7nWp7i2Lbz1w+LA4DwvYmFua05hbWU+PGJhbmtJRD45MTNDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjkxM0M8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warWp7i2sabJqMPox7nWp7i2LdPFu93A4DwvYmFua05hbWU+PGJhbmtJRD45MTRDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjkxNEM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warWp7i2sabJqMPox7nWp7i2LbulwarN+MDgPC9iYW5rTmFtZT48YmFua0lEPjkxNUM8L2JhbmtJRD48b3RoZXJCYW5rSUQ+OTE1Qzwvb3RoZXJCYW5rSUQ+PGNhcmRUeXBlPlg8L2NhcmRUeXBlPjwvYmFua1Jvdz48YmFua1Jvdz48YmFua05hbWU+0vjBqtanuLaxpsmow+jWp7i2Lbz1w+LA4DwvYmFua05hbWU+PGJhbmtJRD45MTZDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjkxNkM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warWp7i2sabJqMPo1qe4ti3TxbvdwOA8L2JhbmtOYW1lPjxiYW5rSUQ+OTE3QzwvYmFua0lEPjxvdGhlckJhbmtJRD45MTdDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGq1qe4trGmyajD6NanuLYtu6XBqs34wOA8L2JhbmtOYW1lPjxiYW5rSUQ+OTE4QzwvYmFua0lEPjxvdGhlckJhbmtJRD45MThDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxbmr1tq6xdChs8zQ8i29ybfRPC9iYW5rTmFtZT48YmFua0lEPjkyMUM8L2JhbmtJRD48b3RoZXJCYW5rSUQ+OTIxQzwvb3RoZXJCYW5rSUQ+PGNhcmRUeXBlPlg8L2NhcmRUeXBlPjwvYmFua1Jvdz48YmFua1Jvdz48YmFua05hbWU+0vjBqs6i0MXJqMLr1qe4ti29ybfRPC9iYW5rTmFtZT48YmFua0lEPjkyMkM8L2JhbmtJRD48b3RoZXJCYW5rSUQ+OTIyQzwvb3RoZXJCYW5rSUQ+PGNhcmRUeXBlPlg8L2NhcmRUeXBlPjwvYmFua1Jvdz48YmFua1Jvdz48YmFua05hbWU+0vjBqs6i0MVBUFDWp7i2Lb3Jt9E8L2JhbmtOYW1lPjxiYW5rSUQ+OTIzQzwvYmFua0lEPjxvdGhlckJhbmtJRD45MjNDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxUg11qe4ti29ybfRPC9iYW5rTmFtZT48YmFua0lEPjkyNEM8L2JhbmtJRD48b3RoZXJCYW5rSUQ+OTI0Qzwvb3RoZXJCYW5rSUQ+PGNhcmRUeXBlPlg8L2NhcmRUeXBlPjwvYmFua1Jvdz48YmFua1Jvdz48YmFua05hbWU+0vjBqs6i0MXJqMLrx7nWp7i2Lb3Jt9E8L2JhbmtOYW1lPjxiYW5rSUQ+OTI1QzwvYmFua0lEPjxvdGhlckJhbmtJRD45MjVDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7SvdS60vjBqs7euNDWp7i2PC9iYW5rTmFtZT48YmFua0lEPjkzMkM8L2JhbmtJRD48b3RoZXJCYW5rSUQ+OTMyQzwvb3RoZXJCYW5rSUQ+PGNhcmRUeXBlPlg8L2NhcmRUeXBlPjwvYmFua1Jvdz48YmFua1Jvdz48YmFua05hbWU+0vjBqsmowus8L2JhbmtOYW1lPjxiYW5rSUQ+OTY5QzwvYmFua0lEPjxvdGhlckJhbmtJRD45NjlDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7SvdS6yajC68e5QjwvYmFua05hbWU+PGJhbmtJRD45NzJDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjk3MkM8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warOotDFuavW2rrF0KGzzNDyLc/fyc88L2JhbmtOYW1lPjxiYW5rSUQ+OTgxQzwvYmFua0lEPjxvdGhlckJhbmtJRD45ODFDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxcmowuvWp7i2Lc/fyc88L2JhbmtOYW1lPjxiYW5rSUQ+OTgyQzwvYmFua0lEPjxvdGhlckJhbmtJRD45ODJDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxUFQUNanuLYtz9/JzzwvYmFua05hbWU+PGJhbmtJRD45ODNDPC9iYW5rSUQ+PG90aGVyQmFua0lEPjk4M0M8L290aGVyQmFua0lEPjxjYXJkVHlwZT5YPC9jYXJkVHlwZT48L2JhbmtSb3c+PGJhbmtSb3c+PGJhbmtOYW1lPtL4warOotDFSDXWp7i2Lc/fyc88L2JhbmtOYW1lPjxiYW5rSUQ+OTg0QzwvYmFua0lEPjxvdGhlckJhbmtJRD45ODRDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjxiYW5rUm93PjxiYW5rTmFtZT7S+MGqzqLQxcuiv6jWp7i2Lc/fyc88L2JhbmtOYW1lPjxiYW5rSUQ+OTg1QzwvYmFua0lEPjxvdGhlckJhbmtJRD45ODVDPC9vdGhlckJhbmtJRD48Y2FyZFR5cGU+WDwvY2FyZFR5cGU+PC9iYW5rUm93PjwvYmFua0xpc3Q+PC9CMkNSZXA+","GBK");
        System.out.println(xml);
        handler.stringToXmlByDom4j(xml,"GBK");
    }

}