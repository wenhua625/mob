package com.novarise.webase.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;

import com.novarise.webase.util.XMLUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;

public class WeiXinMonitor extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public WeiXinMonitor() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//response.setContentType("text/html");
		
		//System.out.println("-------------开始");
		//request = ServletActionContext.getRequest();
	    // response = ServletActionContext.getResponse();
	    InputStream inStream = request.getInputStream();
	    ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int len = 0;
	    while ((len = inStream.read(buffer)) != -1) {
	        outSteam.write(buffer, 0, len);
	    }
	    
	    outSteam.close();
	    inStream.close();
	    String result  = new String(outSteam.toByteArray(),"utf-8");//获取微信调用我们notify_url的返回信息
	    System.out.println("result:"+result);
	    
	   // result:amount=2.80&bank_code=CCBQBY&base64_memo=77yf77yf77yf&busi_code=PRE_PAY&merchant_no=549440148160099&order_no=PY7595215996&pay_no=31547600&pay_result=1&pay_time=20180829162848&sett_date=20180829&sett_time=162848&sign=c9bcd1185558dbc06563cf6d8516b1b52e414afe599ecb204e7ee80c7b4211d0&sign_type=SHA256&terminal_no=21671824&user_bank_card_no=532450******1512&user_cert_no=4310211983****7111&user_mobile=18616989375&user_name=5bC55oWn5Y2O
	    Map map=new HashMap();
	    
	    //高汇通支付
	    if(result.startsWith("amount")){
	    	 response.getWriter().write("success"); 
	    	String [] get_Amount=result.split("&");
	    	for(int i=0;i<get_Amount.length;i++){
	    		String keyvalue[]=get_Amount[i].split("=");
	    		if(keyvalue.length == 2){
	    		     String key=keyvalue[0];
	    		     String value=keyvalue[1];
	    		     map.put(key, value);
	    		}
	    		
	    	}
	    	
	    System.out.print(map.toString());
	    
	    if(map.get("pay_result").equals("1")){
	    	
	    	 
	    	 String agent_code=((String)map.get("order_no")).split("A")[1];
	    	 String sk_amount = (String)map.get("amount");
	    	 float f_amount = Float.parseFloat(sk_amount);
	    	 double j_amount = f_amount/1.00755625;
	    	 
	    	 String sj_amount = new java.text.DecimalFormat("#.00").format(j_amount);
	    	 String bz=(String)map.get("pay_no")+" "+(String)map.get("pay_time");
	    	 
	    	 String sql="{ call sp_cwsk_mob('@@agent_code,','@@sk_amount,','货款','@@bz,') }";
	    	 sql=sql.replaceAll("@@agent_code,", agent_code);
	    	 sql=sql.replaceAll("@@sk_amount,", sj_amount);
	    	 sql=sql.replaceAll("@@bz,", bz);
	    	// System.out.print("sql="+sql);
	    	 try {
					SQLUpdater updater = DSManager.getSQLUpdater();
					updater.executeCall(sql);
					
					
					response.getWriter().write("success"); 
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	 
	    }
	    
	    }else
	    //微信支付
	    {
	   
	    
		try {
			map = XMLUtil.doXMLParse(result);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
	        //TODO 对数据库的操作
	        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");   //告诉微信服务器，我收到信息了，不要在调用回调action了
	        String mch_id = map.get("mch_id").toString();
	        String out_trade_no = map.get("out_trade_no").toString();
	        String transaction_id =map.get("transaction_id").toString();
	        String openid =map.get("openid").toString();
	        String total_fee =map.get("total_fee").toString();
	        int i_fee=Integer.parseInt(total_fee)/100;
	        
	        
	        //艾管理
	        if(mch_id.equals("1403890602")){
	        	
	        	
	        	int pos= out_trade_no.indexOf("S");
	        	int pos_m=out_trade_no.indexOf("M");
	        	int pos_c=out_trade_no.indexOf("C");
		        String agent_code=out_trade_no.substring(pos+1, pos_m);
		        String month = out_trade_no.substring(pos_m+1, pos_c);
		        String product = out_trade_no.substring(pos_c+1, out_trade_no.length());
		        
		        String sql="update agent_list set ";
		        if(product.equals("CRM")){
		        	sql+="end_date=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(end_date,getdate()))),join_date=dbo.getdatestr1(getdate()) ";
		        	sql+=",wx_endDate=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(wx_endDate,getdate()))) ";
		        }else if(product.equals("Video")){
		        	sql+=" video_endDate=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(video_endDate,getdate()))),join_date=dbo.getdatestr1(getdate()) ";
		        }else if(product.equals("AfterSales")){
		        	sql+=" wx_endDate=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(wx_endDate,getdate()))),join_date=dbo.getdatestr1(getdate()) ";
		        }
		        

		        		sql+= " where agent_code ='@@agent_code,'";
		        sql = sql.replaceAll("@@yxq,", month);
		        sql = sql.replaceAll("@@agent_code,", agent_code);
		        
		        
		        String insert_sql="insert into bm_gn_price_paylist(buy_month,product_name,product_fee,agent_code,buy_date) values('@@yxq,','@@product,','@@i_fee,','@@agent_code,',GETDATE())";
		        insert_sql = insert_sql.replaceAll("@@yxq,", month);
		        insert_sql = insert_sql.replaceAll("@@agent_code,", agent_code);
		        insert_sql = insert_sql.replaceAll("@@product,", product);
		        insert_sql = insert_sql.replaceAll("@@i_fee,", String.valueOf(i_fee));

		        try {
					SQLUpdater updater = DSManager.getSQLUpdater();
					updater.executeUpdate(sql);
					updater.executeUpdate(insert_sql);
					response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
	        	
	        }else if(mch_id.equals("1460486902"))
	        {
	        	int pos= out_trade_no.indexOf("S");
	        	int pos_m=out_trade_no.indexOf("M");
	        	int pos_c=out_trade_no.indexOf("C");
		        String agent_code=out_trade_no.substring(pos+1, pos_m);
		        String month = out_trade_no.substring(pos_m+1, pos_c);
		        String product = out_trade_no.substring(pos_c+1, out_trade_no.length());
		        
		        String sql="update agent_list set ";
		        if(product.equals("CRM")){
		        	sql+="end_date=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(end_date,getdate()))),join_date=dbo.getdatestr1(getdate()) ";
		        	sql+=",wx_endDate=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(wx_endDate,getdate()))),join_date=dbo.getdatestr1(getdate())";
		        }else if(product.equalsIgnoreCase("Video")){
		        	sql+="video_endDate=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(video_endDate,getdate()))),join_date=dbo.getdatestr1(getdate()) ";
		        }

		        		sql+= "where agent_code ='@@agent_code,'";
		        sql = sql.replaceAll("@@yxq,", month);
		        sql = sql.replaceAll("@@agent_code,", agent_code);
		        
		        
		        String insert_sql="insert into bm_gn_price_paylist(buy_month,product_name,product_fee,agent_code,buy_date) values('@@yxq,','@@product,','@@i_fee,','@@agent_code,',GETDATE())";
		        insert_sql = insert_sql.replaceAll("@@yxq,", month);
		        insert_sql = insert_sql.replaceAll("@@agent_code,", agent_code);
		        insert_sql = insert_sql.replaceAll("@@product,", product);
		        insert_sql = insert_sql.replaceAll("@@i_fee,", String.valueOf(i_fee));

		        try {
					SQLUpdater updater = DSManager.getSQLUpdater();
					updater.executeUpdate(sql);
					updater.executeUpdate(insert_sql);
					response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        else if(mch_id.equals("1356421102")){
	        	response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
	        }//公众账号
	        else if(mch_id.equals("1355185802")){
	        	
	        	
	        	int pos= out_trade_no.indexOf("S");
	        	int pos_m=out_trade_no.indexOf("M");
	        	int pos_c=out_trade_no.indexOf("C");
		        String agent_code=out_trade_no.substring(pos+1, pos_m);
		        String month = out_trade_no.substring(pos_m+1, pos_c);
		        String product = out_trade_no.substring(pos_c+1, out_trade_no.length());
		        
		        if(product.equals("Recharge")){
		        	String inser_sql="insert into OrderCharge (agent_code,charge_date,init_amount,charge_amount,store_amount,charge_memo,open_id,Charge_content)  select agent_code,getdate(),isnull(agent_amount, 0) agent_amount,@@fee,*0.01,isnull(agent_amount, 0)+@@fee,*0.01 store_amount ,'红包','@@openid,','@@content,'  from agent_list where agent_code='@@agent_code,'" ;
		        	inser_sql = inser_sql.replaceAll("@@agent_code,", agent_code);
		        	inser_sql = inser_sql.replaceAll("@@fee,", total_fee);
		        	inser_sql = inser_sql.replaceAll("@@openid,", openid);
		        	inser_sql = inser_sql.replaceAll("@@content,", month);
		        	String upd_sql="update agent_list set Agent_Amount += @@fee,*0.01 where agent_code='@@agent_code,'";
		        	upd_sql =	upd_sql.replaceAll("@@agent_code,", agent_code);
		        	upd_sql =	upd_sql.replaceAll("@@fee,", total_fee);
		        	try {
//		        		System.out.println("--upd_sql:"+upd_sql);
//		        		System.out.println("--"+inser_sql);
						SQLUpdater updater = DSManager.getSQLUpdater();
						updater.executeUpdate(inser_sql);
						updater.executeUpdate(upd_sql);
						response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }else{
		        	 String sql="update agent_list set ";
				        if(product.equals("CRM")){
				        	sql+="end_date=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(end_date,getdate()))),join_date=dbo.getdatestr1(getdate()) ";
				        	sql+=",wx_endDate=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(wx_endDate,getdate()))),join_date=dbo.getdatestr1(getdate())";
				        }else if(product.equalsIgnoreCase("Video")){
				        	sql+="video_endDate=dbo.getdatestr1(DATEADD(month,@@yxq,,isnull(video_endDate,getdate()))),join_date=dbo.getdatestr1(getdate()) ";
				        }

				        		sql+= "where agent_code ='@@agent_code,'";
				        sql = sql.replaceAll("@@yxq,", month);
				        sql = sql.replaceAll("@@agent_code,", agent_code);
				        
				        
				        String insert_sql="insert into bm_gn_price_paylist(buy_month,product_name,product_fee,agent_code,buy_date) values('@@yxq,','@@product,','@@i_fee,','@@agent_code,',GETDATE())";
				        insert_sql = insert_sql.replaceAll("@@yxq,", month);
				        insert_sql = insert_sql.replaceAll("@@agent_code,", agent_code);
				        insert_sql = insert_sql.replaceAll("@@product,", product);
				        insert_sql = insert_sql.replaceAll("@@i_fee,", String.valueOf(i_fee));

				        try {
							SQLUpdater updater = DSManager.getSQLUpdater();
							updater.executeUpdate(sql);
							updater.executeUpdate(insert_sql);
							response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        }
		        
	        	
	        	
	        /*	int pos= out_trade_no.indexOf("H");
		        String agent_code=out_trade_no.substring(pos+1, out_trade_no.length());
		       String sql="begin if not exists (select * from OrderCharge where ChargeCode='@@KEY,') begin insert into OrderCharge(Agent_Code,ChargeCode,OrderCode,Charge_Date,Init_Amount,Charge_Amount,Store_Amount,Charge_Memo,charge_man,payment_no,open_id) select top 1 agent_code,'@@KEY,','',GETDATE(),agent_amount,'@@charge_amount,',agent_amount+'@@charge_amount,' store_amount,'@@charge_memo,','@@charge_man,','@@payment_no,','@@open_id,' from Agent_list where AGENT_CODE='@@agent_code,' update agent_list set agent_amount=isnull(agent_amount,0)+'@@charge_amount,' where agent_code='@@agent_code,' end end";
		       
		       sql = sql.replaceAll("@@KEY,", out_trade_no);
		       sql = sql.replaceAll("@@charge_amount,", String.valueOf(i_fee));
		       sql = sql.replaceAll("@@charge_memo,", "充值");
		       sql = sql.replaceAll("@@charge_man,", "JSAPI");
		       sql = sql.replaceAll("@@payment_no,", transaction_id);
		       sql = sql.replaceAll("@@open_id,", openid);
		       sql = sql.replaceAll("@@agent_code,", agent_code);
		       try {
					SQLUpdater updater = DSManager.getSQLUpdater();
					updater.executeUpdate(sql);
					response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	        }
	        
	        
		}
	        
	      
	      
	       
	      
	    }
		
		
	
		
		
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
