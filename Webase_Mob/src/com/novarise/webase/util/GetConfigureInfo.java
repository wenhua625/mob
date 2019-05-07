package com.novarise.webase.util;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
 
public class GetConfigureInfo {
	
	public static void main(String[] args) { // lat 39.97646 //log 116.3039
		//String add = getAdd(25.782800,113.015040);
		//String arr[]=getAdd(29.691690,98.595030);
		
		//Map arr=getAdd(40.785050,107.435360);
		//System.out.println(add);
		//System.out.println("ʡ��" + arr.get("prov")+ "\n�У�" +arr.get("city")+ "\n����" + arr.get("town"));
	}
 
	/**
	 * 
	* @Description: ���ݾ�γ�� ��ѯ��ַ 
	* @param @param log γ��
	* @param @param lat ����
	* @return String    String���͵�json��
	* @throws
	 */
	public static Map getAdd(HttpServletRequest request) { // lat С log ��
		String lat = request.getParameter("lat");
		String log = request.getParameter("log");
		// ��������: γ��,���� type 001 (100�����·��010����POI��001������ַ��111����ͬʱ��ʾǰ����) 
		String urlString = "http://gc.ditu.aliyun.com/regeocoding?l=" + lat + "," + log + "&type=010";
		String res = "";
		BufferedReader in = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				res += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject jsonObject = JSONObject.fromObject(res);
		
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.getString("addrList"));
		JSONObject j_2 = JSONObject.fromObject(jsonArray.get(0));
		String allAdd = j_2.getString("admName");
		String arr[] = allAdd.split(",");
		
		Map city=new HashMap();
		String prov="";
		if (arr[0].startsWith("������") || arr[0].startsWith("���ɹ�"))
			prov= arr[0].substring(0,3);
		else
			prov= arr[0].substring(0,2);
		city.put("prov", prov);
		city.put("city", arr[1]);
		city.put("town", arr[2]);
		return com.alibaba.fastjson.JSONObject.parseObject(JSON.toJSONString(city));
	}

}
