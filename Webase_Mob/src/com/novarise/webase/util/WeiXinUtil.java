package com.novarise.webase.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;



public class WeiXinUtil {

    public static JSONObject deencry(HttpServletRequest request){
    	
    	
    	String encryptedData =request.getParameter("encryptedData");//"n5FMEfgaY33sx8by/8yzLe4g3nyErQWBqdmx421E3mZ5aUiIP0py9K/3VMah2X2GVfdUHykpD2loLrfzVS71x2dj6diqqqf3IO5W5UIHf295srnpHnJ+YO0vaY1pG10++2NB0u27t3XuJQSnf5azEaqHz3N2glKjHBJq+8Xq8rwtPgWMajDMo+4pOtBABNX2C1qaAsPgn3g25y1r+r3xPw==";  
    	encryptedData=encryptedData.replaceAll(" ", "+");
    	String iv =request.getParameter("iv");//"5kSeDxNQBRd9vVr1XJq9kg=="; 
    	iv=iv.replaceAll(" ", "+");
		String session_key =request.getParameter("key");//"qgRgXdsp4Cr7v9ac0WziQg=="; 
		session_key=session_key.replaceAll(" ", "+");

		Map map = new HashMap();  
        try {  
                byte[] resultByte  = AES.decrypt(Base64.decodeBase64(encryptedData),  
                        Base64.decodeBase64(session_key),
                        Base64.decodeBase64(iv));  
                    if(null != resultByte && resultByte.length > 0){  
                        String userInfo = new String(resultByte, "UTF-8");                 
                        map.put("status", "1");  
                        map.put("msg", "解密成功");                 
                        map.put("userInfo", userInfo);  
                    }else{  
                        map.put("status", "0");  
                        map.put("msg", "解密失败");  
                    }  
            }catch (InvalidAlgorithmParameterException e) {  
                    e.printStackTrace();  
            } catch (UnsupportedEncodingException e) {  
                    e.printStackTrace();  
            } 
        
             JSONObject job = JSONObject.parseObject(JSON.toJSONString(map));
             return job;
             
             //Gson gson = new Gson();  
           // String decodeJSON = gson.toJson(map);  
            //System.out.println(decodeJSON);  
    }
    
    public static void main(String[] args) {
		//String response="encryptData=5z+wYEWiGr/cFtU9WNP51l/aw4ldOfrcyS8YB9SlZpEpVVsQDYe1rTCfIVu5ZIEhfEK/sDmMB/oQsljI5Q3XyiHIPk3Bkz67TYP6/NGCj7fTZf9HyMXK6AyfV4tiO4sTUZwtBaRUdzvd0bUiD6K94icl+8k9V7KZyY7HepSswbfbRMBkq+HIWAD4G8mW12NiFuR1wWDqgu9jYNusxBTneEOXkCxt012kbB6AcW92UdE+vJTDeCS1nGkpl5mwGDiK0JzaZb3vZY2JwNa0Io4ms7GoOlAEIWpJckVi0bj+EC00aHqXwMwr3KOafKKvARdVD187qmehJEPFVExRSuaI/wikyLe+ZEIYOlpGHlgSHi6RaY6/hi+5NeeGJiUk+vTNE7UeMxgqSuMZw+RAaUQY1GoHQzAU/Dz+0OvRI99N9Jzn5CKN6ALuqI8InQ8DFLJbxXW3XBbIjTCHcM4EXMbz5bBSCmYYyGc0ImS87yj6DHhmZd8+Rm1LFfwdYKEXto2b8/fU0sEFFnunngJkRothgViUvde5Ayvg7hjtrcDtlthVDjToJdsJz72LgwrC8fGj0b9japR3TiKQyiNgwirJFnZWHK0QvUzz9Aeb1IK6OudGYK91O9VPdLDir5neHFkzlDJsfyciB2l2J+3bxAoqgYIzEQ4YG2NmJSxxQ6HMKW1Ws0qSd+MAo4hEoLZD4kqZBF8/7HZAA3ByM2CtpyjWvg==&encryptKey=kXvxdbs7NSCYeCzMs/RzcVCKQLLNgk1zMfL56GXGj4xBxIvrPcMwCMnI5d+50F2RuRWLp4Rb3dSFtyULzI5mhBrdQeCGB2yeyAhxjds675Jlz8QRQkAo9nKovCyUEDMJKfvfkXWyCOH9KU9jZXJtMi1516jJEaA/bM7BZlmFvQ5e1wpT0DJEM6A7hMDPP5jQM6vR9djkKPcf+9uP7A1/1zakDu9505IZ3+RydvcF4HNoadhhPC9U9JwgnHTnm9RUtbQJ8eVk4eQ4Y4+p3wtNNF1W3uwtaNQn56FspE2rgxfTnEQSLk1Mqv8bqM4e7hvZrIB5m+m2xKCLFLegr7RuiQ==&signData=CYnIfZonIhK8AM/kDRGR8hK98qCzZTU4Pf829ylHKY8c1aOXhQ+7qfHX1nfZpr1QAhZVEyx9evLoXm6sOp6nFS6ova8saGdPiizHaEo+Vsoc9HPAC4VtttJihsN89DfcbRPDMGVuqw2tCjg2StQ1nFz6xmj+L9mfJKz+twVmm4dQVGAKR+NctVOnn0+d/XXDfTf9AOmJxZZ+Ars/vuwOH4ISB8an71sSjssKRzl4u71cJ1DhShyF974cS4jMpIFqb6TycqYWbwTEPMCdkzSEfWVBODRKOWp8F//Dy9G0bZcRfjQo++HSbv8akXGmKiE63cI1bl1P4akUNo8J2RsGdA==&tranCode=IFP001";
		WeiXinUtil tt=new WeiXinUtil();
		
		String encryptedData="n5FMEfgaY33sx8by/8yzLe4g3nyErQWBqdmx421E3mZ5aUiIP0py9K/3VMah2X2GVfdUHykpD2loLrfzVS71x2dj6diqqqf3IO5W5UIHf295srnpHnJ+YO0vaY1pG10++2NB0u27t3XuJQSnf5azEaqHz3N2glKjHBJq+8Xq8rwtPgWMajDMo+4pOtBABNX2C1qaAsPgn3g25y1r+r3xPw==";
		String iv="5kSeDxNQBRd9vVr1XJq9kg==";
		String sessionId="qgRgXdsp4Cr7v9ac0WziQg==";
		//HttpServletRequest request =null;
		//tt.deencry(request);
		
		Map paramMap = new HashMap();
		String aa="n5FMEfgaY33sx8by/8yzLe4g3nyErQWBqdmx421E3mZ5aUiIP0py9K/3VMah2X2GVfdUHykpD2loLrfzVS71x2dj6diqqqf3IO5W5UIHf295srnpHnJ+YO0vaY1pG10++2NB0u27t3XuJQSnf5azEaqHz3N2glKjHBJq+8Xq8rwtPgWMajDMo+4pOtBABNX2C1qaAsPgn3g25y1r+r3xPw==";
		
		paramMap.put("encryptedData", aa);
		paramMap.put("iv", "5kSeDxNQBRd9vVr1XJq9kg==");
		paramMap.put("key", "qgRgXdsp4Cr7v9ac0WziQg==");
		HttpUtil.sendPost("http://127.0.0.1:8080/mob/work?proname=AUTHWXPHONE", paramMap);
		//tt.deencry(encryptedData, iv, sessionId);
		
		//System.out.println(tt.parseMap(response).toString());
		//System.out.println("解密后数据: "+tt.respDecryption(response));
	}

	/*public static JSONObject deencry(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String encryptedData = request.getParameter("encryptedData");
		String iv = request.getParameter("iv");
		String session_key = request.getParameter("key");
		
    	Map map = new HashMap();  
    	
        try {  
        	byte[] resultByte  = AES.decrypt(Base64.decodeBase64(encryptedData),  
                    Base64.decodeBase64(session_key),
                    Base64.decodeBase64(iv));   
                    if(null != resultByte && resultByte.length > 0){  
                        String userInfo = new String(resultByte, "UTF-8");                 
                        map.put("status", "1");  
                        map.put("msg", "解密成功");                 
                        map.put("userInfo", userInfo);  
                    }else{  
                        map.put("status", "0");  
                        map.put("msg", "解密失败");  
                    }  
            }catch (InvalidAlgorithmParameterException e) {  
                    e.printStackTrace();  
            } catch (UnsupportedEncodingException e) {  
                    e.printStackTrace();  
            }                
            Gson gson = new Gson();  
            String decodeJSON = gson.toJson(map);  
            System.out.println(decodeJSON); 
            return JSON.parseObject(decodeJSON);
		
	}*/
}
