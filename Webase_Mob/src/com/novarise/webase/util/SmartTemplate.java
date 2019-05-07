package com.novarise.webase.util;

import java.util.HashMap;
import java.util.Map;

public class SmartTemplate {
	/*
	 * 
	 * template_id
	 * 
	 * 
	 */
	public static Map<String, Object> WxTemplate(String access_token,String form_id,String page,String template_id,String touser,Map data){
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("page", page);
		map.put("template_id", template_id);
		map.put("touser", touser);
		map.put("access_token", access_token);
		//map.put("topcolor", topcolor);
		map.put("data", data);
		map.put("form_id", form_id);
		return map;
	}
	public static Map<String, Object> TemplateData(String value,String color) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("value", value);
		map.put("color", color);
		return map;
	}
}
