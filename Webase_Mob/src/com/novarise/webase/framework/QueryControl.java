package com.novarise.webase.framework;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.novarise.webase.BConstants;
import com.novarise.webase.util.GenerateSequenceUtil;
import com.novarise.webase.util.KdniaoTrackQueryAPI;
import com.novarise.webase.util.UserServiceImpl;
import com.novarise.webase.xml.XmlUtil;
import com.pingan.scf.core.server.entity.api.ApiCryptionResult;
import com.pingan.scf.core.server.entity.api.ApiRequestVo;
import com.pingan.scf.core.server.service.ApiSecurityUserService;
import com.pingan.scf.core.server.service.security.ApiSecurityService;
import com.pingan.scf.core.server.service.security.impl.ApiSecurityServiceImpl2;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

public class QueryControl {
	
	/**
	 * 
	 * @param s_kjname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseDisplayMJJSON(String s_kjname,HttpServletRequest request) throws Exception {

		List fy_result =null;
		List allList=null;
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		
        String start=request.getParameter("page");
        if (start == null) start="";
        String limit=request.getParameter("rows");
        if (limit == null) start="";
		//ȡ������������ֵ
		String all_result[][] = new String[0][0];
		int pageid = 0,pagenum=0,i_rownum = 0; //����ڼ�ҳ����ҳ�����ܼ�¼��
		int i_myhs = 0;
		
		
	    try
	    {
	       if(allList == null)
	       {
	    	   allList = query.ListQuery(sql);
	    	   
	       }
	       i_rownum=allList.size();
	       
	       if(!start.equals("")){
	    	  fy_result = new ArrayList();
	    	  pageid = Integer.parseInt(start);
	    	  i_myhs = Integer.parseInt(limit);
	    	  pagenum=pageid*i_myhs;
	    	  if(pagenum>i_rownum) pagenum=i_rownum;
	    	  int begin=(pageid-1)*i_myhs;
	    	  //fy_result = query.ListPageQuery(sql, pageid, i_myhs);
	    	  //pagenum = pageid+i_myhs;
	    	  //if (pagenum>i_rownum) pagenum=i_rownum;
	    	  for(int i=begin;i<pagenum;i++)
	    	  {
	    		  fy_result.add(allList.get(i));  
	    	  }
	          
	       }
              
		
	    }catch(Exception ex)
	    {
	    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
	    }
	    
	   
			
	    JSONObject json_result = new JSONObject();
		json_result.put("total", Integer.valueOf(i_rownum).toString());
		if(fy_result == null){
			JSONArray json_arr = JSONArray.fromObject(allList);
			json_result.put("rows", json_arr);
		}else{
			JSONArray json_arr = JSONArray.fromObject(fy_result);
			json_result.put("rows", json_arr);
		}
		//����ϼ�
		y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname.substring(0, 2)+"H"+s_kjname.substring(3,s_kjname.length()));
		if (y_sql == null || y_sql.length == 0)
		{
		}else{
			 ljh = y_sql[0][1].trim(); //�������Ӻ�
			 cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
			 sql = y_sql[0][3].trim(); //ҵ���õ�SQL
			 sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
			 xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
			 sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
			 myhs = y_sql[0][7].trim(); //ÿҳ����
			 defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
			 msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
			
            //SQL�������еĲ�ѯ����
			 sql_tj = gettjsql(request,s_kjname);
			//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
			sql = sql + sql2 + sql_tj + cs;
			sql = HtmlFunction.parseVar(sql, request, "");
			
			try{
				allList = query.ListQuery(sql);
				if (allList.size()>0)
				{
					JSONArray hj_json = JSONArray.fromObject(allList);
					json_result.put("footer", hj_json);
				}
			}catch(Exception ex)
			{
				throw new Exception("�ؼ�����"+s_kjname +" �ϼƲ�ѯʱ����SQL="+sql+"\n\n"+ex.toString());
			}
			
			
			
		}
		//System.out.println(json_result.toString());
		

		
		return json_result;
	}
	
	public JSONObject parseDisplayJJJSON(String s_kjname,HttpServletRequest request) throws Exception {

		if(s_kjname.startsWith("JJO")){
			return parseDisplayJJOJSON(s_kjname,request);
		}
		List allList=new ArrayList();
		
		JSONObject json_result = new JSONObject();
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		String total_sql="";
		
		if(ljh == null) ljh="";
		
		if (ljh.equals("group"))
		{
			return  parseDisplayJJGroupJSON( s_kjname, request);
		}else if(ljh.startsWith("select"))
		{
		   	total_sql=ljh;
		}
		
		if (myhs == null || myhs.length() == 0)
		{
			myhs="0";
		}
		
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");
		
		if(!total_sql.equals("")){
		   total_sql = total_sql+sql_tj;
		   total_sql = HtmlFunction.parseVarEasyui(total_sql, request, "");
		}
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		
       
       
		//ȡ������������ֵ
		
		int pageid =1; //����ڼ�ҳ����ҳ�����ܼ�¼��
		int i_myhs = 0;
		//System.out.println("page="+request.getParameter("page"));
		pageid = request.getParameter("page") != null ? Integer
				.parseInt(request.getParameter("page")) : 1;
		if (pageid < 0)
			pageid = 1;
		
		i_myhs = Integer.parseInt(myhs);
		
	    try
	    {
	       if(Integer.parseInt(myhs) >0){ 
	    	   json_result = query.JsonPageQuery(sql, pageid, i_myhs); 
	    	}
	       else
	       {
	    	 allList = query.ListQuery(sql); 
	   	     JSONArray json_arr = JSONArray.fromObject(allList);
	   		 json_result.put("rows", json_arr); 
	       }
	       
	       if(!total_sql.equals("")){
	    	  // System.out.println("total_sql:"+total_sql);
	    	   allList = query.ListQuery(total_sql);
	    	   JSONArray json_arr = JSONArray.fromObject(allList);
	    	   json_result.put("totals", json_arr);
	       }
           
	      
	      
	       return json_result;
	    }catch(Exception ex)
	    {
	    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
	    }
	 
	   
			
		}
	
	
	
	
   public JSONObject parseDisplayJJOJSON(String s_kjname,HttpServletRequest request) throws Exception {

		
		List allList=new ArrayList();
		
		JSONObject json_result = new JSONObject();
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		String total_sql="";
		
		if(ljh == null) ljh="";
		
		if (ljh.equals("group"))
		{
			return  parseDisplayJJGroupJSON( s_kjname, request);
		}else if(ljh.startsWith("select"))
		{
		   	total_sql=ljh;
		}
		
		if (myhs == null || myhs.length() == 0)
		{
			myhs="0";
		}
		
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyuiDecry(s_kjname,sql, request, "");
	//	sql = HtmlFunction.parseVarEasyui(sql, request, "");
		//sql = AAA.decry(sql);
		System.out.println("sql:"+sql);
		if(!total_sql.equals("")){
		   total_sql = total_sql+sql_tj;
		   total_sql = HtmlFunction.parseVarEasyuiDecry(s_kjname,total_sql, request, "");
		}
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		
       
       
		//ȡ������������ֵ
		
		int pageid =1; //����ڼ�ҳ����ҳ�����ܼ�¼��
		int i_myhs = 0;
		//System.out.println("page="+request.getParameter("page"));
		pageid = request.getParameter("page") != null ? Integer
				.parseInt(request.getParameter("page")) : 1;
		if (pageid < 0)
			pageid = 1;
		
		i_myhs = Integer.parseInt(myhs);
		
	    try
	    {
	       if(Integer.parseInt(myhs) >0){ 
	    	   json_result = query.JsonPageQuery(sql, pageid, i_myhs); 
	    	}
	       else
	       {
	    	 allList = query.ListQuery(sql); 
	   	     JSONArray json_arr = JSONArray.fromObject(allList);
	   		 json_result.put("rows", strReplace(json_arr.toString())); 
	       }
	       
	       if(!total_sql.equals("")){
	    	  // System.out.println("total_sql:"+total_sql);
	    	   allList = query.ListQuery(total_sql);
	    	   JSONArray json_arr = JSONArray.fromObject(allList);
	    	   json_result.put("totals", strReplace(json_arr.toString()));
	       }
           
	       ApiSecurityUserService  userService = new UserServiceImpl();
		   ApiSecurityService apiSecurityService = new ApiSecurityServiceImpl2();
		   apiSecurityService.setUserService(userService);	
	      
		   ApiRequestVo requestVo = new ApiRequestVo();
		  
		   requestVo.setData(json_result);
		   requestVo.setRequestNo(userService.getChennelId());
		   requestVo.setChnId(userService.getChennelId());
		   
		   ApiCryptionResult result = apiSecurityService.encapsulateApiServerResponse(requestVo, json_result);
		   
	       return JSONObject.fromObject(result);
	  //     return json_result;
	    }catch(Exception ex)
	    {
	    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
	    }
	 
	   
			
		}
	
	public String  strReplace(String content){
		content	= content.replaceAll("��", "(");

		content	= content.replaceAll("��", ")");
		return content ;
	}
	
	
	public JSONObject fetchWxData(String s_kjname,HttpServletRequest request) throws Exception {

		
		List allList=new ArrayList();
		
		JSONObject json_result = new JSONObject();
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		String total_sql="";
		String group_sql="";
		if(ljh == null) ljh="";
		
		if (ljh.equals("group"))
		{
			return  parseDisplayJJGroupJSON( s_kjname, request);
		}else if(ljh.startsWith("select"))
		{
		   	total_sql=ljh;
		}else if(ljh.startsWith("JJ")){
			group_sql=ljh;
		}
		
		if (myhs == null || myhs.length() == 0)
		{
			myhs="0";
		}
		
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");
		
		if(!total_sql.equals("")){
		   total_sql = total_sql+sql_tj;
		   total_sql = HtmlFunction.parseVarEasyui(total_sql, request, "");
		}
		
		if(!group_sql.equals("")){
			group_sql = group_sql+sql_tj;
			group_sql = HtmlFunction.parseVarEasyui(group_sql, request, "");
			}
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		
       
       
		//ȡ������������ֵ
		
		int pageid =1; //����ڼ�ҳ����ҳ�����ܼ�¼��
		int i_myhs = 0;
		//System.out.println("page="+request.getParameter("page"));
		pageid = request.getParameter("page") != null ? Integer
				.parseInt(request.getParameter("page")) : 1;
		if (pageid < 0)
			pageid = 1;
		
		i_myhs = Integer.parseInt(myhs);
		
	    try
	    {
	       if(Integer.parseInt(myhs) >0){ 
	    	   json_result = query.JsonPageQuery(sql, pageid, i_myhs); 
	    	}
	       else
	       {
	    	 allList = query.ListQuery(sql); 
	   	     JSONArray json_arr = JSONArray.fromObject(allList);
	   		 json_result.put("rows", json_arr); 
	       }
	       
	       if(!group_sql.equals("")){
		    	  // System.out.println("total_sql:"+total_sql);
		    	   allList = query.ListQuery(group_sql);
		    	   JSONArray json_arr = JSONArray.fromObject(allList);
		    	   json_result.put("totals", json_arr);
		       }
	       
	       if(!total_sql.equals("")){
	    	  // System.out.println("total_sql:"+total_sql);
	    	   allList = query.ListQuery(total_sql);
	    	   JSONArray json_arr = JSONArray.fromObject(allList);
	    	   json_result.put("totals", json_arr);
	       }
           
	      
	      
	       return json_result;
	    }catch(Exception ex)
	    {
	    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
	    }
	 
	   
			
		}
	
	public JSONObject parseDisplayJJGroupJSON(String s_kjname,HttpServletRequest request) throws Exception {

		
		List allList=null;
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		
		
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql2 = sql2  + sql_tj + cs;
		//sql = HtmlFunction.parseVarEasyui(sql, request, "");
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		String chird_sql="";
		JSONObject json_result = new JSONObject();
		allList = query.ListQuery(sql);
		JSONArray a=new JSONArray();
		for(int i_group=0;i_group<allList.size();i_group++)
		{
			
			
			
			JSONObject o= new JSONObject();
			Map group_map=((Map)allList.get(i_group));
			o.putAll(group_map);
			
			request.setAttribute("Group_Code", ((Map)allList.get(i_group)).get("group_code").toString());
			chird_sql= HtmlFunction.parseVarAttr(sql2, request, "");
			List z_list=query.ListQuery(chird_sql);
			if(z_list.size()>0){
			  JSONArray json_arr = JSONArray.fromObject(z_list);
			  o.put("content", json_arr);
			}
			
			a.add(o);
			
		}
		
		json_result.put("result", a);
	   
		
		return json_result;
			
		}
		
		

		
	
	
	/**
	 * �����ֻ��˷�װ���MJ�����
	 * @param s_kjname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseDisplayMBJSON(String s_kjname,HttpServletRequest request) throws Exception {

		
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		
		String y_kjm[][] = new String[0][0];
		try {
			
			y_kjm = XmlUtil.find(BConstants.PAGE_MB, "KJM", s_kjname);
			if (y_kjm == null || y_kjm.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		 JSONObject json_result = new JSONObject();
		
		String kjm= y_kjm[0][2];
		//Json����
		String jsbm= y_kjm[0][3];
	
		String kjms[]= kjm.split(",");
		String jsbms[]=jsbm.split(",");
		
		for(int i=0;i<kjms.length;i++)
		{
			String y_sql[][] = new String[0][0];
			try {
				
				 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", kjms[i]);
				if (y_sql == null || y_sql.length == 0){
					throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
				}
					
			} catch (Exception e) {
				throw new Exception("�ҿؼ�ʱ����"+e.toString() );
			}
			
			
			String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
			String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
			String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
			String myhs = y_sql[0][7].trim(); //ÿҳ����
			if (myhs == null || myhs.length() == 0){
				myhs="0";
			}
			
//			SQL�������еĲ�ѯ����
			String sql_tj = gettjsql(request,s_kjname);
			//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
			sql = sql + sql2 + sql_tj + cs;
			sql = HtmlFunction.parseVarEasyui(sql, request, "");
			List allList=null;
			String start=request.getParameter("page");
	        if (start == null) start="";
	       
	        String limit=myhs;
	        if (limit == null) start="";
	        
	        int pageid = 0,pagenum=0,i_rownum = 0; //����ڼ�ҳ����ҳ�����ܼ�¼��
			int i_myhs = 0;
	        List fy_result=null;
	        try
		    {
		       if(allList == null)
		       {
		    	   allList = query.ListQuery(sql);
		    	   
		       }
		       i_rownum=allList.size();
		       
		      if(Integer.parseInt(myhs) >0){ 
		      if(!start.equals("")){
		    	  
		    	  fy_result = new ArrayList();
		    	  pageid = Integer.parseInt(start);
		    	  i_myhs = Integer.parseInt(limit);
		    	  pagenum = pageid+i_myhs;
		    	  if (pagenum>i_rownum) pagenum=i_rownum;
		    	  for(int i1=pageid;i1<pagenum;i1++)
		    	  {
		    		  fy_result.add(allList.get(i1));  
		    	  }
		       }
		      }    
			
		    }catch(Exception ex)
		    {
		    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
		    }
	        if (fy_result == null) fy_result = allList;

			JSONArray json_arr = JSONArray.fromObject(fy_result);
			json_result.put(jsbms[i], json_arr);
			
		}

		//System.out.println(json_result.toString());
		return json_result;
	}
	
	
	
	
	
public JSONObject parseDisplayKEY(String s_kjname,HttpServletRequest request) throws Exception {

		
		List allList=null;
		String key=GenerateSequenceUtil.generateSequenceNo();
		JSONObject o=new JSONObject();
		o.put("key", key);
		
		
		return o;
	}
	
	public JSONArray parseDisplaySigleJSON(String s_kjname,HttpServletRequest request) throws Exception {

		
		List allList=null;
		//�������ݿ�
		s_kjname="MJ"+s_kjname.substring(2);
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "");
       
       
		
		
	    try
	    {
	       if(allList == null)
	       {
	    	   allList = query.ListQuery(sql);  
	       }
	       
	       
	       
              
		
	    }catch(Exception ex)
	    {
	    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
	    }
	    JSONArray json_arr = JSONArray.fromObject(allList);

		return json_arr;
	}
//	ȡ����sql���
	private static String gettjsql(HttpServletRequest request, String kjname) throws Exception {
		HttpSession session = request.getSession(true);
		String[][] mjcstj = new String[0][0];
		try {
			mjcstj = XmlUtil.find(BConstants.PAGE_MJ_TJ, "KJM", kjname);
		} catch (SQLException se1) {
			throw new Exception("ȡ����SQLʱ����" + se1.toString());
		}
		String s_tjsql = " ";
		if (mjcstj.length != 0) {
			for (int i = 0; i < mjcstj.length; i++) {
				if ((request.getParameter(mjcstj[i][1].trim()) != null)
						&& (request.getParameter(mjcstj[i][1].trim()).trim()
								.length() != 0)){
                        if(mjcstj[i][1].indexOf("order") != -1){
                            s_tjsql += "  " + mjcstj[i][2] + " ";
                        }else{
                            s_tjsql += " and " + mjcstj[i][2] + " ";
                        }
					
					}
				else{ if ((String)session.getAttribute(mjcstj[i][1].trim()) != null)
			    	s_tjsql += " and " + mjcstj[i][2] + " ";
				}
			}
		}
		return s_tjsql;
	}

	public String  parseKdniaoJSON(String proname, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		 String expCode = request.getParameter("expCode");
		    if (expCode == null) {
		      expCode = "";
		    }
		    String expNo = request.getParameter("expNo");
		    if (expNo == null) {
		      expNo = "";
		    }
		    KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
		    
		    String result = api.getOrderTracesByJson(expCode, expNo);
		    
		    return result;
	}
}
