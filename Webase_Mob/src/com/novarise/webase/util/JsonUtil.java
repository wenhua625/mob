package com.novarise.webase.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.easemob.lmc.model.Authentic;
import com.easemob.lmc.service.TalkDataService;
import com.easemob.lmc.service.impl.TalkDataServiceImpl;
import com.easemob.lmc.service.impl.TalkHttpServiceImplApache;
import com.easemob.lmc.tool.JsonTool;



public class JsonUtil {
	

	    /** *//**
	     * ��һ��JSON �����ַ���ʽ�еõ�һ��java����
	     * @param jsonString
	     * @param pojoCalss
	     * @return
	     */
	    public static Object getObject4JsonString(String jsonString,Class pojoCalss){
	        Object pojo;
	        JSONObject jsonObject = JSONObject.fromObject( jsonString );  
	        pojo = JSONObject.toBean(jsonObject,pojoCalss);
	        return pojo;
	    }
	    
	    public static JSONObject getObject4String(String jsonString){
	    	JSONObject pojo;
	        JSONObject jsonObject = JSONObject.fromObject( jsonString );  
	        pojo = jsonObject;
	        return pojo;
	    }
	    
	    
	    
	    /** *//**
	     * ��json HASH���ʽ�л�ȡһ��map����map֧��Ƕ�׹���
	     * @param jsonString
	     * @return
	     */
	    public static Map getMap4Json(String jsonString){
	        JSONObject jsonObject = JSONObject.fromObject( jsonString );  
	        Iterator  keyIter = jsonObject.keys();
	        String key;
	        Object value;
	        Map valueMap = new HashMap();

	        while( keyIter.hasNext())
	        {
	            key = (String)keyIter.next();
	            value = jsonObject.get(key);
	            valueMap.put(key, value);
	        }
	        
	        return valueMap;
	    }
	    
	    
	    /** *//**
	     * ��json�����еõ���Ӧjava����
	     * @param jsonString
	     * @return
	     */
	    public static Object[] getObjectArray4Json(String jsonString){
	        JSONArray jsonArray = JSONArray.fromObject(jsonString);
	        return jsonArray.toArray();
	        
	    }
	    
	    
	    /** *//**
	     * ��json���󼯺ϱ��ʽ�еõ�һ��java�����б�
	     * @param jsonString
	     * @param pojoClass
	     * @return
	     */
	    public static List getList4Json(String jsonString, Class pojoClass){
	        
	        JSONArray jsonArray = JSONArray.fromObject(jsonString);
	        JSONObject jsonObject;
	        Object pojoValue;
	        
	        List list = new ArrayList();
	        for ( int i = 0 ; i<jsonArray.size(); i++){
	            
	            jsonObject = jsonArray.getJSONObject(i);
	            pojoValue = JSONObject.toBean(jsonObject,pojoClass);
	            list.add(pojoValue);
	            
	        }
	        return list;

	    }
	    
       public static List getList4JsonArray(String key, JSONObject jsonObejct){
	        
	        JSONArray jsonArray = jsonObejct.getJSONArray(key);
	        JSONObject jsonObject;
	        Object pojoValue;
	        
	        List list = new ArrayList();
	        for ( int i = 0 ; i<jsonArray.size(); i++){
	            
	            jsonObject = jsonArray.getJSONObject(i);
	            
	            pojoValue = JSONObject.toBean(jsonObject,Map.class);
	            list.add(pojoValue);
	            
	        }
	        return list;

	    }
       
       /** *//**
	     * ��json HASH���ʽ�л�ȡһ��map����map֧��Ƕ�׹���
	     * @param jsonString
	     * @return
	     */
	    public static Map getMap4Key(String key1, JSONObject jsonObejct){
	        JSONObject jsonObject = jsonObejct.getJSONObject(key1);  
	        Iterator  keyIter = jsonObject.keys();
	        String key;
	        Object value;
	        Map valueMap = new HashMap();

	        while( keyIter.hasNext())
	        {
	            key = (String)keyIter.next();
	            value = jsonObject.get(key);
	            valueMap.put(key, value);
	        }
	        
	        return valueMap;
	    }
	    
	    /** *//**
	     * ��json�����н�����java�ַ�������
	     * @param jsonString
	     * @return
	     */
	    public static String[] getStringArray4Json(String jsonString){
	        
	        JSONArray jsonArray = JSONArray.fromObject(jsonString);
	        String[] stringArray = new String[jsonArray.size()];
	        for( int i = 0 ; i<jsonArray.size() ; i++ ){
	            stringArray[i] = jsonArray.getString(i);
	            
	        }
	        
	        return stringArray;
	    }
	    
	    /** *//**
	     * ��json�����н�����javaLong�Ͷ�������
	     * @param jsonString
	     * @return
	     */
	    public static Long[] getLongArray4Json(String jsonString){
	        
	        JSONArray jsonArray = JSONArray.fromObject(jsonString);
	        Long[] longArray = new Long[jsonArray.size()];
	        for( int i = 0 ; i<jsonArray.size() ; i++ ){
	            longArray[i] = jsonArray.getLong(i);
	            
	        }
	        return longArray;
	    }
	    
	    /** *//**
	     * ��json�����н�����java Integer�Ͷ�������
	     * @param jsonString
	     * @return
	     */
	    public static Integer[] getIntegerArray4Json(String jsonString){
	        
	        JSONArray jsonArray = JSONArray.fromObject(jsonString);
	        Integer[] integerArray = new Integer[jsonArray.size()];
	        for( int i = 0 ; i<jsonArray.size() ; i++ ){
	            integerArray[i] = jsonArray.getInt(i);
	            
	        }
	        return integerArray;
	    }
	    
	    /** *//**
	     * ��json�����н�����java Date �Ͷ������飬ʹ�ñ��������뱣֤
	     * @param jsonString
	     * @return
	     */
	   /* public static Date[] getDateArray4Json(String jsonString,String DataFormat){
	        
	        JSONArray jsonArray = JSONArray.fromObject(jsonString);
	        Date[] dateArray = new Date[jsonArray.size()];
	        String dateString;
	        Date date;
	        
	        for( int i = 0 ; i<jsonArray.size() ; i++ ){
	            dateString = jsonArray.getString(i);
	            date = DateUtil.stringToDate(dateString, DataFormat);
	            dateArray[i] = date;
	            
	        }
	        return dateArray;
	    }*/
	    
	    /** *//**
	     * ��json�����н�����java Integer�Ͷ�������
	     * @param jsonString
	     * @return
	     */
	    public static Double[] getDoubleArray4Json(String jsonString){
	        
	        JSONArray jsonArray = JSONArray.fromObject(jsonString);
	        Double[] doubleArray = new Double[jsonArray.size()];
	        for( int i = 0 ; i<jsonArray.size() ; i++ ){
	            doubleArray[i] = jsonArray.getDouble(i);
	            
	        }
	        return doubleArray;
	    }
	    
	    
	    /** *//**
	     * ��java����ת����json�ַ���
	     * @param javaObj
	     * @return
	     */
	    public static String getJsonString4JavaPOJO(Object javaObj){
	        
	        JSONObject json;
	        json = JSONObject.fromObject(javaObj);
	        return json.toString();
	        
	    }
	    
	    
	    
	    
	   
	    
	    
	    /** *//**
	     * ��java����ת����json�ַ���,���趨���ڸ�ʽ
	     * @param javaObj
	     * @param dataFormat
	     * @return
	     */
	    /*public static String getJsonString4JavaPOJO(Object javaObj , String dataFormat){
	        
	        JSONObject json;
	        JsonConfig jsonConfig = configJson(dataFormat);
	        json = JSONObject.fromObject(javaObj,jsonConfig);
	        return json.toString();
	        
	        
	    }*/
	    
	    
	    
	    /** *//**
	     * @param args
	     */
	    public static void main(String[] args) {
	        // TODO �Զ����ɷ������
	    	String username="13045672325";
	    	Authentic.Token TEST_TOKEN = new Authentic.Token("YWMt4EPcDvVpEeWmTm2uJUQPcwAAAVT1s8Bmn-wB5wwM9nqr6HgljAvlo79iDX8",1465203701330L);
	    	TalkDataService service = new TalkDataServiceImpl(new TalkHttpServiceImplApache());
	    	service.setToken(TEST_TOKEN);
	    	try {
				//System.out.println(JsonTool.write(service.userSave(username,username+"+kcc","���ط�")));
				String aa="{\"ddd\":404}";
				System.out.println(aa.indexOf("statusCode"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	String cd = "''0101'',''0102'',''0103'',''0104''";

	    	/*String jsonData="{'backdetails': [{ 'product_id': 232, 'product_num': 10,            'product_price': 230        },        {            'product_id': 234,            'product_num': 12,            'product_price': 345        }    ],    'bt_list': [        {            'bt_code': 'BT99884444',            'bt_amount': '340.00'        }    ],    'backlist': {        'tel': '18916899823',        'address': '�Ϻ��ֶ�����·���㳡',        'arr_man': 'С��',        'order_num': '12',        'order_amount': '3784.00',        'agent_code': '0106',        'bz': '������Ϣ'    }}";
	    	
	    	
	    	JSONObject myJsonObject =JsonUtil.getObject4String(jsonData);
	    	
	    	
	    	
	    	
	    	Map jsonList=JsonUtil.getMap4Key("backlist",myJsonObject);
	    	System.out.println(jsonList.get(1));*/
	    	
	    	//String s="{'rows':[{'product_color':null,'type_sales':432,'product_id':1333,'rand':0.867014859686018,'pack_num':0,'product_name':'GT-D33K(��ͭɫLED����)','product_image':'29c7a0fbbde642f381eab370de494702.jpg','product_code':'2.01.010030','code':1333,'product_untl':'̨','price_type':'����','iszx':'��','pack_type':'','product_size':'330X330','type_agent':0,'product_md':null,'demo':null,'product_num':'-176','product_price':178}]}  ";
	    	
	    	//Object o=JsonUtil.getObject4JsonString(s, Object.class);
	    	//JSONObject myJsonObject =JsonUtil.getObject4String(s);
	    	
	    	//ArrayList l=(ArrayList) JsonUtil.getList4JsonArray("rows",myJsonObject);
	    	
	    	
	    	//System.out.println(myJsonObject);
	    	
	    	
	    	
	    	//String a=myJsonObject.getString("backdetails");
	    	//System.out.println(a);
	    	//myJsonObject.to
	    			
	    	//Object object=JsonUtil.getObject4JsonString(jsonData,Object.class);
	    }
	    
	    /** *//**
	     * JSON ʱ���������
	     * @param datePattern
	     * @return
	     */
	    /*public static JsonConfig configJson(String datePattern) {   
	            JsonConfig jsonConfig = new JsonConfig();   
	            jsonConfig.setExcludes(new String[]{""});   
	            jsonConfig.setIgnoreDefaultExcludes(false);   
	            jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);   
	            jsonConfig.registerJsonValueProcessor(Date.class,   
	                new DateJsonValueProcessor(datePattern));   
	          
	            return jsonConfig;   
	        } */ 
	    
	    /** *//**
	     * 
	     * @param excludes
	     * @param datePattern
	     * @return
	     */
	    /*public static JsonConfig configJson(String[] excludes,   
	            String datePattern) {   
	            JsonConfig jsonConfig = new JsonConfig();   
	            jsonConfig.setExcludes(excludes);   
	            jsonConfig.setIgnoreDefaultExcludes(false);   
	            jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);   
	            jsonConfig.registerJsonValueProcessor(Date.class,   
	                new DateJsonValueProcessor(datePattern));   
	          
	            return jsonConfig;   
	        }  */

}
