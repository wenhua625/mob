package com.novarise.webase.util;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

public class AliMsgSender {

	//������֤��
	public String sendMsg(String code,String mobilecode) throws ApiException{
	       
	      String url = "http://gw.api.taobao.com/router/rest";
	     //�����˻�
	      //String appkey = "23540417";//�Լ�����
	      //String secret = "bf7c95361ccc5011d4f5db45cc5aaf41";//�Լ�����
	      //�������
	      String appkey = "23764730";//�Լ�����
	      String secret = "8e1cb450d345b488f1d7e13c5bfc1c49";//�Լ�����
	       
	      TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
	      AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
	      req.setExtend( "" );
	      req.setSmsType( "normal" );
	      req.setSmsFreeSignName( "�ʹ���" );//ģ������
	      req.setSmsParamString( "{code:'"+code+"'}" );
	      req.setRecNum( mobilecode);//���͵��ֻ���
	      
	      //req.setSmsTemplateCode( "SMS_80340028" );
	     //�����˺�
	     // req.setSmsTemplateCode( "SMS_27640017" );//�����ڴ�����ģ����
	      //�������
	      req.setSmsTemplateCode( "SMS_62965185" );
	      try {  
	    	    AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);  
	    	    //System.out.println("ddd="+rsp.getBody());
	    	    return "1";  
	    	} catch (Exception e) {  
	    		
	    	    // TODO: handle exception  
	    	    return "-1";  
	    	}  
	  }
	
	//������֤��
		public String sendMsg(String code,String mobilecode,String template,String product) throws ApiException{
		       
		      String url = "http://gw.api.taobao.com/router/rest";
		      //String appkey = "23540417";//�Լ�����
		      //String secret = "bf7c95361ccc5011d4f5db45cc5aaf41";//�Լ�����
		      
		      String appkey = "23764730";//�Լ�����
		      String secret = "8e1cb450d345b488f1d7e13c5bfc1c49";//�Լ�����
		      
		      
		       
		      TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		      AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		      req.setExtend( "" );
		      req.setSmsType( "normal" );
		      req.setSmsFreeSignName( "�ʹ���" );//ģ������
		      req.setSmsParamString( "{code:'"+code+"',product:'"+product+"'}" );
		      req.setRecNum( mobilecode);//���͵��ֻ���
		      
		      req.setSmsTemplateCode( template );
		      //req.setSmsTemplateCode( "SMS_27640017" );//�����ڴ�����ģ����
		      try {  
		    	    AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);  
		    	    System.out.println(rsp.getBody());  
		    	    return "1";  
		    	} catch (Exception e) {  
		    	    // TODO: handle exception  
		    	    return "-1";  
		    	}  
		  }
	
	//����������Ϣ
		public String sendLogistsMsg(String signName,String custname,String taf_company,String pzh,String hj,String zyf,String mobilecode) throws ApiException{
			
			
		       
		      String url = "http://gw.api.taobao.com/router/rest";
		      //String appkey = "23540417";//�Լ�����
		      //String secret = "bf7c95361ccc5011d4f5db45cc5aaf41";//�Լ�����
		      
		      String appkey = "23764730";//�Լ�����
		      String secret = "8e1cb450d345b488f1d7e13c5bfc1c49";//�Լ�����
		      
		      String fh_date=DateHelper.getShortAppDateTime();
		      TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		      AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		      req.setExtend( "" );
		      req.setSmsType( "normal" );
		      req.setSmsFreeSignName(signName);//ģ������
		      req.setSmsParamString( "{custname:'"+custname+"',taf_company:'"+taf_company+"',pzh:'"+pzh+"',hj:'"+hj+"',zyf:'"+zyf+"',fh_date:'"+fh_date+"'"+"}" );
		      req.setRecNum( mobilecode);//���͵��ֻ���
		      //������Ϣģ��
		      req.setSmsTemplateCode( "SMS_62845121" );
		      //req.setSmsTemplateCode( "SMS_27640017" );//�����ڴ�����ģ����
		      try {  
		    	    AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);  
		    	   // System.out.println("ddd="+rsp.getBody());  
		    	    return "1";  
		    	} catch (Exception e) {  
		    	    // TODO: handle exception  
		    	    return "-1";  
		    	}  
		  }
		
		public String sendMsgforSql(String sql){
			
			
			try { 
		      String url = "http://gw.api.taobao.com/router/rest";
		      //String appkey = "23540417";//�Լ�����
		      //String secret = "bf7c95361ccc5011d4f5db45cc5aaf41";//�Լ�����
		      
		      String appkey = "23764730";//�Լ�����
		      String secret = "8e1cb450d345b488f1d7e13c5bfc1c49";//�Լ�����
		      
		      TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		      AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		      SQLQuery query = DSManager.getSQLQuery();
			  String nr[][]= query.ArrayQuery(sql);
			  
			   
			  
			  for(int i=0;i<nr.length;i++)
			  {
				 String mobilecode=nr[i][0];
				 String templatecode=nr[i][1];
				 
				 JSONObject param=new JSONObject();
				 
				 param.put("param1", nr[i][2]);
				 param.put("param2", nr[i][3]);
				 param.put("param3", nr[i][4]);
				 param.put("param4", nr[i][5]);
				 param.put("param5", nr[i][6]);

			      req.setExtend( "" );
			      req.setSmsType( "normal" );
			      req.setSmsFreeSignName("�ʹ���");//ģ������
			      req.setSmsParamString( param.toString() );
			      req.setRecNum( mobilecode);//���͵��ֻ���
			      req.setSmsTemplateCode( templatecode );
			      AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);  
			    	   // System.out.println(rsp.getBody());  
			    	   // return "1";  
			    	
				 
			  }
			  return  "1";
			  
			} catch (Exception e) {  
	    	    // TODO: handle exception  
	    		e.printStackTrace();
	    	    return "-1";  
	    	} 
			  
		      
		      
		  }
	   
	  public static void main(String[] args) {
		 AliMsgSender t1 = new AliMsgSender();
	    try {
			String x = t1.sendMsg("373343","18616989375");
			System.out.println("x11="+x);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		 // String x = t1.sendLogistsMsg("���չ�˾", "����", "��������", "1000222", "4", "120.34","18616989375");
	    //System.out.println(x);
		 /* JSONObject param=new JSONObject();
		  try {
			  
			param.put("param1", "15325235aaasss");
			 param.put("param2","2");
			 param.put("param3", "a");
			 param.put("param4", "b");
			 param.append("param5","2b");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			System.out.println (param.toString());*/
	  }
	
}
