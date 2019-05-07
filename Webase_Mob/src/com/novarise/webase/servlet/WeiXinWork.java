package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.novarise.webase.BConstants;
import com.novarise.webase.framework.HtmlFunction;
import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.framework.WebControl;
import com.novarise.webase.util.HttpUtil;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

public class WeiXinWork extends HttpServlet {
	 private static String root = "";
	
	 private static final String CONTENT_TYPE = "text/html; charset=utf-8";
	 
	// 第三方用户唯一凭证
	 public static String appid = "wxb085018f401f8f5a";
			 // 第三方用户唯一凭证密钥
	 public static String appsecret = "45cd4b5fc30b2cd355d08196e80a7d35";
			 //商户ID
	 public static String mch_id="";
			 //获取openId
	
	 public static String oauth_url = "https://api.weixin.qq.com/sns/oauth/access_token?&grant_type=authorization_code";
	 

	/**
	 * Constructor of the object.
	 */
	public WeiXinWork() {
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
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		 
		
		
		
		
		
		response.setContentType(CONTENT_TYPE);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();
		
		
		String proname = request.getParameter("proname");
		if (proname == null || proname.trim().length() == 0) {
			String error = SystemFunction.showError(101, "错误!没有要处理的页面", "错误!没有要处理的页面",request);
			out.println(error);
			return;
		}
		if (proname.indexOf(".") == -1) proname += ".htm";
		
		String htmlfile = "";
		//String sql="select agent_name from agent_list where AGENT_CODE='"+state+"' ";
		
		if (root == null || root.trim().length() == 0) {
			out.println("错误!请在dsconfig.xml文件中配置rootdir!");
			return;
		}
		
		if(proname.indexOf("success") !=-1) 
		{
			try {
				htmlfile = SystemFunction.readFile(root+proname);
				out.println(htmlfile);
				return ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		String code=request.getParameter("code");
		String state = request.getParameter("state");
		String mobile="";
		
		
		
		
		String diststate = "";
		
		if(state == null)
		{
			state = "";
		}
		//System.out.println("state:"+state);
		if(state.indexOf(";")!=-1){
			String states1[] = state.split(";");
			
			state=states1[0];
			mobile = states1[1];
			
		}
		if(state.indexOf(",")!=-1){
			String states[] = state.split(",");
			state=states[0];
			diststate = states[1];
		}
		
		//System.out.println("mobile:"+mobile);
		//System.out.println("dist_state="+diststate);
		Map params= new HashMap();
		params.put("appid", appid);
		params.put("secret", appsecret);
		params.put("code", code);
		
		
		
		// https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code

		String oath_url= "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
		
		String access_url="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		
		String access_token=HttpUtil.httpsRequest(oath_url, "POST", null);
		
		 String nickname = "抛锚的蜗牛";
		 String headimgurl = "images/frame/org.gif";
		 String u_openid = "";
		try {
			JSONObject a = new JSONObject(access_token);
			String access_token1=(String)a.get("access_token");
			String openid=(String)a.get("openid");
			String refresh_token = (String)a.get("refresh_token");
			String scope = (String)a.get("scope");
			
			
			String user_url="https://api.weixin.qq.com/sns/userinfo?access_token="+access_token1+"&openid="+openid+"&lang=zh_CN";
			
			String userinfo=HttpUtil.httpsRequest(user_url, "POST", null);
			
			JSONObject user = new JSONObject(userinfo);
			
			 u_openid=(String)user.get("openid");
			nickname=(String)user.get("nickname");
			Integer sex=(Integer)user.get("sex");
			String province=(String)user.get("province");
			String city=(String)user.get("city");
			String country=(String)user.get("country");
			 headimgurl=(String)user.get("headimgurl");
			String unionid=(String)user.get("unionid");

			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("没有获取到access_token!");
		}finally{  
			
			String agent_name="";
			String brand_code="";
			try {
				SQLQuery query = DSManager.getSQLQuery();
				String sql="select agent_name,brand_code from agent_list where AGENT_CODE='"+state+"' ";
				String [][] s_qxcs = query.ArrayQuery(sql);
				if(s_qxcs.length>0)
				{
					agent_name = s_qxcs[0][0];
					brand_code = s_qxcs[0][1];
				}
				
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			
			//读取页面
			try {
				htmlfile = SystemFunction.readFile(root+proname);
				htmlfile = htmlfile.replaceAll("@@wx_headimage,",headimgurl);
				nickname = nickname.replace("$", "\\$");
				 htmlfile = htmlfile.replaceAll("@@wx_nick,",nickname);
				 htmlfile = htmlfile.replaceAll("@@wx_openid,", u_openid);
				 htmlfile = htmlfile.replaceAll("@@wx_code,", "");
				 htmlfile =  htmlfile.replaceAll("@@agent_name,", agent_name);
				 htmlfile =  htmlfile.replaceAll("@@agent_code,", state);
				 htmlfile =  htmlfile.replaceAll("@@dist_agent_code,", diststate);
				 htmlfile =  htmlfile.replaceAll("@@brand_code,", brand_code);
				 htmlfile =  htmlfile.replaceAll("@@mobile,", mobile);
				 
				 
			} catch (Exception e) {
				e.printStackTrace();
				out.print(SystemFunction.showError(102, "文件没找到!", e.toString(),request));
				return;
			}
			//处理request或session变量(@@)
			try {
				htmlfile = HtmlFunction.parseVar(htmlfile, request, " ");
			} catch (Exception e) {
				out.println(SystemFunction.showError(103, "替换 request或session变量时出错!", e.toString(),request));
				return;
			}
			//取页面控件集
			WebControl control = new WebControl();
			List kjm = new ArrayList();
			kjm = HtmlFunction.getPageMarket(kjm,htmlfile, "$$");
			kjm = HtmlFunction.getPageMarket(kjm, htmlfile, "!!");
			
			//处理开始
			
			for ( int i=0;i<kjm.size(); i ++){
				String kj = (String)kjm.get(i);
				try {
					if (kj.substring(0, 2).equals("MJ")) {
						htmlfile = control.parseDisplayMJ(htmlfile,kj,request);
					} 
					if (kj.substring(0, 2).equals("ZQ")) {
						htmlfile = control.parseDisplayZQ(htmlfile, kj,request, response);
						
					}
					if (kj.substring(0, 2).equals("XL")) {
						htmlfile = control.parseDisplayXL(htmlfile, kj,request, response);
					}
	                if (kj.substring(0, 2).equals("EX")) {
	                    htmlfile = control.parseDisplayEX(htmlfile, kj,request, response);
	                }
					
	                if (kj.substring(0, 3).equals("GEN")) {
	                    htmlfile = control.parseGenSeq(htmlfile, kj);
	                }
	                //产生报表
	                if (kj.substring(0, 2).equals("RP")) {
	                    htmlfile = control.parseReport(htmlfile, kj, request);
	                } 
	                if (kj.substring(0, 2).equals("UE")) {
	                    try {
	                      htmlfile = control.parseUpLoad(htmlfile, kj, request, response);
	                    }
	                    catch (Exception e) {
	                      htmlfile = "error:" + e.toString();
	                    }

	                  }
				}catch(Exception e){
					e.printStackTrace();
					htmlfile = SystemFunction.showError(101, kj + "处理时出错!", e.toString(),request);
				}
				
			}
			
				
			
		out.println(htmlfile);
		out.flush();
		out.close();
	}
		}

	
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		try{
			//title = XmlUtil.readXml(BConstants.CONFIG_FILE,BConstants.SYSTEM_TITLE );
			root  = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			//auth  = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_AUTH);
		}catch(Exception e){
			System.out.println("读取配置文件出错!\n" + e.toString());
		}
	}

}
