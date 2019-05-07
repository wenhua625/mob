package com.novarise.webase.framework;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.dom4j.DocumentException;

import push.UMengPush;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.czb.gateway.api.APITools;
import com.easemob.lmc.model.Authentic;
import com.easemob.lmc.service.TalkDataService;
import com.easemob.lmc.service.impl.TalkDataServiceImpl;
import com.easemob.lmc.service.impl.TalkHttpServiceImplApache;
import com.easemob.lmc.tool.JsonTool;
import com.jspsmart.upload.SmartUpload;
import com.novarise.webase.BConstants;
import com.novarise.webase.util.AliMsgSender;
import com.novarise.webase.util.CircularImages;
import com.novarise.webase.util.DateHelper;
import com.novarise.webase.util.GenerateSequenceUtil;
import com.novarise.webase.util.HttpClientUtil;
import com.novarise.webase.util.HttpUtil;
import com.novarise.webase.util.ImageCompress;
import com.novarise.webase.util.JPush;
import com.novarise.webase.util.JsonUtil;
import com.novarise.webase.util.LogoConfig;
import com.novarise.webase.util.MD5Util;
import com.novarise.webase.util.SmartAqrCode;
import com.novarise.webase.util.SmartTemplate;
import com.novarise.webase.util.UMengPushUtil;
import com.novarise.webase.util.WXAppletUserInfo;
import com.novarise.webase.util.WeChatNotice;
import com.novarise.webase.util.XMLHandler;
import com.novarise.webase.util.XMLUtil;
import com.novarise.webase.util.YYZF;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.SQLUpdater;

public class WebControl {

	/**
	 * 实现HTML中数据显示的方法 实现方法: 1,分离HTML页面 2,连接数据库,取出业务SQL 3,根据业务SQL取出符合条件的记录
	 * 4,解析HTML页面 5,返回
	 */
	public String parseDisplayMJ(String html, String s_kjname,
			HttpServletRequest request) throws Exception {

		// 给HTML页的头，内容，尾赋值

		String t_html = HtmlFunction.getHtmlHead(html, s_kjname);
		String c_html = HtmlFunction.getHtmlContext(html, s_kjname);
		String e_html = HtmlFunction.getHtmlEnd(html, s_kjname);

		// 取出业务SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("处理" + s_kjname + "出错!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("控件名" + s_kjname + "没找到!");
		}
		// 获取业务数据内容
		String ljh = y_sql[0][1].trim(); // 数据连接号
		String cs = y_sql[0][2].trim(); // 用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); // 业务用的SQL
		String sql2 = y_sql[0][4].trim(); // 业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); // 如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); // 是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); // 每页行数
		String defaults = y_sql[0][8].trim(); // 数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); // 没有找到数据时提示

		// SQL语名句中的查询条件
		String sql_tj = gettjsql(request, s_kjname);
		// SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "");

		// 开始获取实际数据
		SQLQuery query = DSManager.getSQLQuery();
		String result[][] = new String[0][0];
		String all_result[][] = new String[0][0];

		int pageid = 1, pagenum = 1, i_rownum = 0; // 定义第几页，总页数，总记录数
		int i_myhs = 0;

		if (sfxsym.equals("1")) // 是否显示页码 1:显示页码 2:不显示
		{
			pageid = request.getParameter("page") != null ? Integer
					.parseInt(request.getParameter("page")) : 1;
			if (pageid < 0)
				pageid = 1;

			i_myhs = Integer.parseInt(myhs);
			try {
				all_result = query.ArrayQuery(sql);

			} catch (Exception e) {
				throw new Exception("处理" + s_kjname + "出错!\n" + e.toString()
						+ "\n" + sql);
			}
			i_rownum = all_result.length;

			pagenum = ((int) (i_rownum - 1) / i_myhs) + 1; // 计算一共多少页
			result = query.ArrayPageQuery(sql, pageid, i_myhs);

		} else {
			try {
				result = query.ArrayQuery(sql);
			} catch (Exception e) {
				throw new Exception("处理" + s_kjname + "出错!\n" + e.toString()
						+ "\n" + sql);
			}
		}

		// 解析HTML页面
		t_html = HtmlFunction.parseHtmlContent(t_html, s_kjname, result);
		c_html = HtmlFunction.parseHtmlContent(c_html, s_kjname, result);
		e_html = HtmlFunction.parseHtmlContent(e_html, s_kjname, result);

		c_html = HtmlFunction.parseHtmlContent(c_html, s_kjname, result, msjts,
				ljh, defaults);
		String c_page = "";

		if (sfxsym.equals("1")) {// && i_rownum > 1
			if (result.length != 0) {
				String s_url = HtmlFunction.getURL(request);

				if (pagenum > 1) {
					if (pageid > 1) {
						c_page += "<tr><td height='20' align='right'><a href='"
								+ s_url + "&page=1'>首页</a>";
						c_page += "<a href='" + s_url + "&page=" + (pageid - 1)
								+ "'>上页</a>";
					} else {
						c_page += "<tr><td height='20' align='right'>首页  上页  ";
					}
					if (pageid < pagenum) {
						c_page += "<a href='" + s_url + "&page=" + (pageid + 1)
								+ "'>下页</a>";
						c_page += "<a href='" + s_url + "&page=" + pagenum
								+ "'>尾页</a>";
					} else {
						c_page += "下页  尾页  ";
					}
				} else {
					c_page += "<tr><td height='20' align='right'>";
				}

				int i_endrow = (pageid * i_myhs >= i_rownum) ? i_rownum
						: pageid * i_myhs;
				c_page += "第" + ((pageid - 1) * i_myhs + 1) + "到" + i_endrow
						+ "条/共<font color=\"red\">" + i_rownum + "</font>条第"
						+ pageid + "页/共" + pagenum + "页</td></tr>";
			}
			e_html = SystemFunction.replace(e_html, "$$page,", c_page);
			t_html = SystemFunction.replace(t_html, "$$page,", c_page);

		}

		return t_html + c_html + e_html;
	}
	
	
	// 订单手机提醒
		public String parseDisplayMobTip(HttpServletRequest request,String s_kjname) throws Exception {

			String returnString = "";
			HttpSession session = request.getSession(true);
			String y_sql[][] = new String[0][0];
			
			try {
				y_sql = XmlUtil.find(BConstants.PAGE_TP, "KJM", s_kjname);
			} catch (Exception e) {
				throw new Exception("处理" + s_kjname + "出错!" + e.toString());
			}
			
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("控件名" + s_kjname + "没找到!");
			}
			// 获取业务数据内容
			String ljh = y_sql[0][1].trim(); // 数据连接号
			String cs = y_sql[0][3].trim(); // 用于加入order by ,group by 语句
			String sql = y_sql[0][9].trim(); // 业务用的SQL
			String type = y_sql[0][8].trim(); // 业务用的SQL
			
			sql = sql   + cs;
			sql = HtmlFunction.parseVar(sql, request, "");
			
			SQLQuery query = DSManager.getSQLQuery();
			String resultData[][] = query.ArrayQuery(sql);
			if (resultData.length == 0) {
				returnString+="";
			}else{
				returnString += resultData[0][0];
			}
			UMengPush push=UMengPushUtil.getPush();
			UMengPush iospush=UMengPushUtil.getIOSPush();
			String device= resultData[0][1];
			if (device == null || device.equals(""))
			{
				device = request.getParameter("Device_Token");
			}
			
			String ret="";
			if (device.equals("all")){
				ret=push.sendAndroidBroadcast("通知", "广播", returnString);
				iospush.sendIOSBroadcast(returnString);
			}else
				ret=push.sendAndroidUnicast("通知", "获取密码", returnString, device);
			    iospush.sendIOSUnicast(returnString, device);
			return ret;
			
			
			
		}

	public void parseForward(String s_kjname, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// 取出业务SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_FW, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("处理" + s_kjname + "出错!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("控件名" + s_kjname + "没找到!");
		}

		String target = y_sql[0][1];

		target = HtmlFunction.parseVar(target, request, "");

		if (target == null)
			target = "";
		if (target.length() != 0) {
			request.getRequestDispatcher(target).forward(request, response);

		}

	}

	// 取条件sql语句
	private static String gettjsql(HttpServletRequest request, String kjname)
			throws Exception {
		HttpSession session = request.getSession(true);
		String[][] mjcstj = new String[0][0];
		try {
			mjcstj = XmlUtil.find(BConstants.PAGE_MJ_TJ, "KJM", kjname);
		} catch (SQLException se1) {
			throw new Exception("取条件SQL时出错！" + se1.toString());
		}
		String s_tjsql = " ";
		if (mjcstj.length != 0) {
			for (int i = 0; i < mjcstj.length; i++) {
				if ((request.getParameter(mjcstj[i][1].trim()) != null)
						&& (request.getParameter(mjcstj[i][1].trim()).trim()
								.length() != 0)) {
					if (mjcstj[i][1].indexOf("order") != -1) {
						s_tjsql += "  " + mjcstj[i][2] + " ";
					} else {
						s_tjsql += " and " + mjcstj[i][2] + " ";
					}

				} else {
					if ((String) session.getAttribute(mjcstj[i][1].trim()) != null)
						s_tjsql += " and " + mjcstj[i][2] + " ";
				}
			}
		}
		return s_tjsql;
	}

	// 处理import文件
	// 格式为<!--import=filename!-->
	// 可将filename为import文件的相对路径如filename=import/head.htm，请勿有多余的空格!
	public String parseDisplayIM(String s_html) throws Exception {
		String root = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT);
		int i_wz1, i_wz2;
		String s_filename = "", s_file;
		while (((i_wz1 = s_html.indexOf("<!--$$import=")) != -1)
				&& ((i_wz2 = s_html.indexOf("!-->", i_wz1)) != -1)) {
			s_filename = s_html.substring(i_wz1 + 13, i_wz2);
			s_file = SystemFunction.readFile(root + s_filename.trim());
			s_html = s_html.substring(0, i_wz1) + s_file
					+ s_html.substring(i_wz2 + 4);
		}
		return s_html;
	}

	// 退出系统
	public void parseDisplayQUIT(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(true);
		session.removeAttribute("LS.YHDM");
		//session.invalidate();
		request.getRequestDispatcher(BConstants.EXIT_URL).forward(request,
				response);
	}
	
	
	

	
//	 订单提示
	public String parseDisplayTip(HttpServletRequest request) throws Exception {

		String returnString = "";
		HttpSession session = request.getSession(true);
		SQLQuery query = DSManager.getSQLQuery();

		String sts = SystemFunction.null2SpaceString((String) session
				.getAttribute("LS.ZS"));
		String yhh = SystemFunction.null2SpaceString((String) session
				.getAttribute("LS.YHDM"));
		String yhz = SystemFunction.null2SpaceString((String) session
				.getAttribute("LS.YHZEG"));

		String stsStr = "";
		String stsArr[] = sts.split(",");
		for (int i = 0; i < stsArr.length; i++)
			stsStr = stsStr + "'" + stsArr[i] + "',";
		stsStr = stsStr.substring(0, stsStr.length() - 1);
		String sql = "";
		if (yhz.equals("ywy"))
			sql = "select sts_demo,count(1) sts_num  from order_list a,order_sts b where a.order_sts = b.order_sts  and a.order_sts in ("
					+ stsStr
					+ ") and agent_code in (select agent_code from agent_list where domain_man='"
					+ yhh + "') group by sts_demo";
		else
			sql = "select sts_demo,count(1) sts_num from order_list a,order_sts b where a.order_sts = b.order_sts  and a.order_sts in ("
					+ stsStr + ") group by sts_demo";
		String resultData[][] = query.ArrayQuery(sql);
		if (resultData.length == 0) {
			return returnString;
		}
		returnString = "<table border=0 class=\"w01_13px_black\">";
		for (int i = 0; i < resultData.length; i++) {
			returnString += "<tr><td>你有 " + resultData[i][1] + " 条单据要 "
					+ resultData[i][0] + "</td></tr>";
		}
		returnString += "</table>";
		return returnString;
	}

	// 登陆系统
	public JSONObject parseDisplayQX(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		
		HttpSession session = request.getSession(true);
		SQLQuery query = DSManager.getSQLQuery();
		SQLUpdater updater = DSManager.getSQLUpdater();
		JSONObject json_result = new JSONObject();
		String[][] s_qxcs = new String[0][0];
		String s_qxsql = "";
		String s_yhdm = "", s_yhmm = "", device_token = "";
		
		if (request.getParameter("username") != null)
			s_yhdm = request.getParameter("username").trim();
		if (request.getParameter("password") != null)
			s_yhmm = request.getParameter("password").trim();
		
		device_token=APITools.nvl(request.getParameter("device_token"));
		
		/*if (request.getParameter("ipmac") != null)
			s_ipmac = request.getParameter("ipmac").trim();
		     s_qxsql = "select Ip_Mac from tj_sys_Ip where Ip_Mac='" + s_ipmac + "'";
		
		
		try {
			s_qxcs = query.ArrayQuery(s_qxsql);
		} catch (SQLException e) {
			json_result.put("result", "false");
			json_result.put("reason", "权限SQL语句出错！"+e.getMessage()+s_qxsql);
			return json_result;
		}*/
		
		// if (s_qxcs.length == 0){
		// return SystemFunction.showLoginError(803,
		// "你的电脑还没有到总部备案(授权).\n\n不能访问系统，请联系系统管理员!", s_ipmac);
		// }
		String bossagent_code="";
		if (request.getParameter("boss_agentcode") != null)
			bossagent_code = request.getParameter("boss_agentcode").trim();
		//同一老板，多个店铺的情况
		if(!bossagent_code.equals("")){
			s_qxsql = "select Yhh,a.Yhz,Yhxm, agent_code Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(agent_code) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck,qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,lxfs sj,'老板' yhjb,(select brand_chnname from Brand_List  where Brand_Name=brand_code) brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(WX_OpenID,'') openid,allowdiscount  from tj_sys_yh a,agent_list b where a.dept=b.boss_agentcode  "
					+ " and a.lxfs='"
					+ SystemFunction.replace(s_yhdm, "'", "''")
					+ "' and agent_code='"+bossagent_code+"'";// STS为'1'时禁用,其它为活动
		}else{
		
		s_qxsql = "select Yhh,a.Yhz,Yhxm,Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(Dept) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck, qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,lxfs sj,yhjb,(select brand_chnname from Brand_List  where Brand_Name=a.warecode) brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(WX_OpenID,'') openid,allowdiscount,device_token  from tj_sys_yh a where 1=1"
				+ " and a.lxfs='"
				+ SystemFunction.replace(s_yhdm, "'", "''")
				+ "'";// STS为'1'时禁用,其它为活动
		}
		try {
			s_qxcs = query.ArrayQuery(s_qxsql);
		} catch (SQLException e) {
			json_result.put("result", "false");
			json_result.put("reason", "权限SQL语句出错！"+e.getMessage()+s_qxsql);
			return json_result;
		}
		/*if (s_qxcs.length == 0) {
			json_result.put("result", "false");
			json_result.put("reason", "用户名不存在!");
			return json_result;
		}*/
		
		//员工表没找到，查推单人表
				if (s_qxcs.length == 0) {
					String s_wxcs="select '' Yhh,'' Yhz,Yhxm,'' Dept,Mm,a.Sts,'' YhZMC,'2026-12-31' Yxq,'' Warecode,'' ZS,'' agent_name,'' qx_cd,'' qx_qt,'' qx_ck,'' qx_dj,'' qx_ts,'' qx_sh,lxfs sj,'普通用户' yhjb,'' brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(v_OpenID,'') openid,'10' allowdiscount,device_token  from WeiXinAppUser_open a where 1=1 ";
					s_wxcs=s_wxcs+" and a.lxfs='"+s_yhdm+"'";
					s_qxcs = query.ArrayQuery(s_wxcs);
					if(s_qxcs.length == 0){
						json_result.put("result", "false");
						json_result.put("reason", "用户名不存在!");
						return json_result;
					}
					/*else{
						update_sql = "update WeiXinAppUser_open set WX_headimage='"+wx_init_headimge+"',WX_nick='"+wx_init_nick+"' where v_openid='"+v_openid+"'";
					}*/
				}
		// if (s_qxcs[0][11].trim().indexOf(s_mac) == -1){
		// System.out.println("dd:"+s_mac);
		// return SystemFunction.showLoginError(805, "本机没有经过公司授权,请与公司系统管理员联系!",
		// s_mac);
		// }
		if (!(s_qxcs[0][4].equals(Encrypt.MD5(s_yhmm)))) {
			json_result.put("result", "false");
			json_result.put("reason", "用户密码错误!");
			return json_result;
		}
		
		
		s_yhdm =s_qxcs[0][0].trim();
		
		String s_yhz = s_qxcs[0][1].trim();
		String s_yhmc = s_qxcs[0][2].trim();
		String s_dwdm = s_qxcs[0][3].trim();
		String s_sts = s_qxcs[0][5].trim();
		String s_yhzmc = s_qxcs[0][6].trim();
		String s_yxq = s_qxcs[0][7].trim();
		String s_ware = s_qxcs[0][8].trim();
		String s_zs = s_qxcs[0][9].trim();
		String s_agentName = s_qxcs[0][10].trim();
		String s_lxfs = s_qxcs[0][17].trim();
		String s_yhjb = s_qxcs[0][18].trim();
		
		String s_brandchnName = s_qxcs[0][19].trim();
		String wx_headimage= s_qxcs[0][20].trim();
		String wx_nick= s_qxcs[0][21].trim();
		String wx_openid= s_qxcs[0][22].trim();
		String allowdiscount= s_qxcs[0][23].trim();
		
		String aready_device_token=APITools.nvl(s_qxcs[0][24].trim());
		if(!aready_device_token.equals("")){
			if (!aready_device_token.equals(device_token)){
				JPush.pushJGObject_all_regid_alert(aready_device_token,"你的账号已经在别处登录，你被强制下线了！","强制下线");
			}
		}
		
		
		// String s_authMac = s_qxcs[0][11].trim();
		String wxEwm="";
		String zbfEwm="";
		String agent_headiamge="";
		String max_gds="";
		String max_hsts="";
		String adh_agentcode="";
		String is_share="";
		String gxf="";
		String gxf_type="";
		String lat="0";
		String lon="0";
		
		
//		终端系统特加
		try {
			if(s_yhz.equals("终端店面")){
				String vod_sql="select case when end_date is not null then datediff(day,getdate(),convert(datetime,end_date)) else 0 end valid_days,open_flag,isnull(wx_ewm,'') wx_ewm,isnull(agent_tel1,'') zfb_ewm,agent_headimage,max_gds,max_hsts,adh_agentcode,is_share,gxf,isnull(lat,0) lat,isnull(lon,0) lon,brand_code,yf_xt from agent_list where agent_code='"+s_dwdm+"'";
				String s_v[][]= query.ArrayQuery(vod_sql);
				String validDays=s_v[0][0];
				String openFlag=s_v[0][1];
				 wxEwm=s_v[0][2];
				 zbfEwm=s_v[0][3];
				agent_headiamge=s_v[0][4];
				max_gds=s_v[0][5];
				max_hsts=s_v[0][6];	
				adh_agentcode=s_v[0][7];
				is_share=s_v[0][8];
				gxf=s_v[0][9];
				lat=s_v[0][10];
				lon=s_v[0][11];
				s_brandchnName =s_v[0][12]; 
				gxf_type =s_v[0][13]; 
				int i_vDays=Integer.parseInt(validDays);
				/*if(i_vDays<0){
					json_result.put("result", "false");
					json_result.put("reason", "合同已经到期,请续签合同或与区域经理联系!");
					//json_result.put("reason", "合同已经到期,请续费!");
					return json_result;
				}*/
				if(!openFlag.equals("启用")){
					json_result.put("result", "false");
					json_result.put("reason", "客户已被总部停用了!");
					return json_result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			json_result.put("result", "false");
			json_result.put("reason", "合同到期日期不正确，无法登陆系统，请与系统管理员联系！");
			return json_result;
		}

		if (s_sts.equals("1")) {
			json_result.put("result", "false");
			json_result.put("reason", "帐号已禁用,如果要激活,请联系所属店铺的老板!");
			return json_result;
		}
		if (s_sts.equals("2")) {
			json_result.put("result", "false");
			json_result.put("reason", "账号正在验证中...，请等待老板通过!");
			return json_result;
		}

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date d_yxq = null;
		try {
			d_yxq = formater.parse(s_yxq);
		} catch (Exception e) {
			json_result.put("result", "false");
			json_result.put("reason", "格式化日期出错,请联系系统管理员！");
			return json_result;
		}
		java.util.Date nowDate = new java.util.Date();
		long d_yxqts = (d_yxq.getTime() - nowDate.getTime())
				/ (1000 * 60 * 60 * 24);
		/*
		 * if(d_yxqts < 0) { return
		 * SystemFunction.showLoginError(1000,"帐号已过期，你的到期日为："+SystemFunction.getDate(d_yxq)+",请于相关人员联系！",String.valueOf(d_yxqts)); }
		 */

		/*try {
			s_qxcs = XmlUtil.find(BConstants.PAGE_QX, "YHZ", s_yhz);
		} catch (Exception e) {
			return SystemFunction.showLoginError(806, "取用户功能代码SQL语句出错！", e
					.toString());
		}
		String s_gndm = "000";
		for (int i = 0; i < s_qxcs.length; i++)
			s_gndm += "," + s_qxcs[i][1].trim();*/
		String s_gndm="000";
		s_gndm=s_qxcs[0][11].trim();
		String s_qxqt=s_qxcs[0][12].trim();
		String s_qxck=s_qxcs[0][13].trim();
		String s_qxdj=s_qxcs[0][14].trim();
		String s_qxts=s_qxcs[0][15].trim();
		String s_qxsh=s_qxcs[0][16].trim();
		//SessionListener.isAlreadyEnter(session,s_yhdm);
		/*if(SessionListener.isAlreadyEnter(session,s_yhdm))
		{
			return SystemFunction.showLoginError(807, "该账户已经在别的电脑上登陆!",
			"");
		}*/
		
		//JSONArray json_arr = JSONArray.fromObject(fy_result);
		//json_arr.add
		//System.out.println("原始功能代码："+s_gndm);
		String mob_gndm = "";
		if(s_gndm.length()>5){
		
		try {
			String vod_sql="select  gnid from TJ_SYS_MOBQX where GNID in ("+s_gndm+") order by Sort_Bm ";
			//System.out.println("vod_sql="+vod_sql);
			String s_v[][]= query.ArrayQuery(vod_sql);
			System.out.println(s_v.length);
			if(s_v.length > 0){
				for (int i = 0; i < s_v.length; i++){
					//if (mob_gndm.length()>0)
					   mob_gndm += "'" + s_v[i][0].trim()+"',";
					   //System.out.println("dd:"+s_v[i][0].trim());
				}
			}
		} catch (Exception e) {
			json_result.put("result", "false");
			json_result.put("reason", "取用户功能代码SQL语句出错！");
			return json_result;
			
		}
		if (mob_gndm.length()>0) 
			mob_gndm = mob_gndm.substring(1, mob_gndm.length()-1);
		else mob_gndm=s_gndm;
		}else mob_gndm=s_gndm;
		//System.out.println("功能代码："+mob_gndm);
		
		//判断环信是否注册
		
		
		final String final_lxfs=s_lxfs;
		final String final_yhmc=s_yhmc;
		Thread t=new Thread(){
		    public void run(){
		    	String chatusername=final_lxfs;
		    	//Authentic.Token TEST_TOKEN = new Authentic.Token("YWMt4EPcDvVpEeWmTm2uJUQPcwAAAVT1s8Bmn-wB5wwM9nqr6HgljAvlo79iDX8",1465203701330L);
		    	TalkDataService service = new TalkDataServiceImpl(new TalkHttpServiceImplApache());
		    	//service.setToken(TEST_TOKEN);
		    	try {
		    		String isRegedit=JsonTool.write(service.userGet(chatusername));
		    		if (isRegedit.indexOf("statusCode")!=-1)
		    		{
		    			JsonTool.write(service.userSave(chatusername,chatusername+"+kcc",final_yhmc));
		    		}
		    		
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
		   }
		};
		t.start();
		
		updater.executeUpdate(" begin update tj_sys_yh set device_token='"+device_token+"' where lxfs='"+s_lxfs+"' update WeiXinAppUser_open set  device_token='"+device_token+"' where lxfs='"+s_lxfs+"' end");
		
		
		
		JSONObject json_session = new JSONObject();
		
		json_session.put("TITLE", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE)); //系统标题
		json_session.put("YHDM", s_yhdm); //用户代码
		json_session.put("ZS", s_zs);
		json_session.put("WARE", s_ware);
		json_session.put("BRAND", s_ware);
		json_session.put("IS_SHARE", is_share);
		json_session.put("ALLOWDISCOUNT", allowdiscount);
		
		json_session.put("LAT", lat);
		json_session.put("LON", lon);
		json_session.put("GXF", gxf);
		json_session.put("GXF_TYPE", gxf_type);
		
		json_session.put("YHZ", s_yhzmc); //用户组名称
		json_session.put("YHZEG", s_yhz); //用户组名称英文
		json_session.put("XM", s_yhmc); //用户姓名
		json_session.put("DWDM", s_dwdm); //店铺编码
		//json_session.put("GNDM", s_gndm);
		json_session.put("QXCD", mob_gndm);//s_gndm
		json_session.put("QXQT", s_qxqt);
		json_session.put("QXCK", s_qxck);
		
		
		json_session.put("QXDJ", s_qxdj);
		json_session.put("QXTS", s_qxts);
		json_session.put("QXSH", s_qxsh);
		json_session.put("SJ", s_lxfs);
		json_session.put("FLAG", "yes");
		json_session.put("YEAR", SystemFunction.getYear()); //年
		json_session.put("MONTH", SystemFunction.getMonth()); //月
		json_session.put("DAY", SystemFunction.getDay()); //日
		json_session.put("IP", request.getRemoteAddr());
		json_session.put("AGENT", s_agentName); //店铺名称
		json_session.put("LASTDAYSOFCURMONTH", DateHelper
				.getLastDayOfCurrentMonth()); //一个月的最后一天
		json_session.put("FIRSTDAYSOFCURMONTH", DateHelper
				.getFirstDayOfCurrentMonth()); //一个月的第一天
		json_session.put("CURDATE", DateHelper.getDate()); //今天
		json_session.put("CURSHORTDATE", DateHelper.getShortDate1());
		json_session.put("URL", "http://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath());

		json_session.put("BGCOLOR", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_BGCOLOR));
		json_session.put("BGIMAGE", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_BGIMAGE));
		json_session.put("SYSTEMBHD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.BHD));
		json_session.put("SYSTEMFHD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.FHD));
		json_session.put("SYSTEMTYD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.TYD));
		
		json_session.put("AUTHAMOUNT", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.AUTHAMOUNT));
		
		json_session.put("ZOOM", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.ZOOM));
		json_session.put("CURTIME", DateHelper.getShowDateTime());
		json_session.put("DATE", DateHelper.getDateCN()); //中文日期
		json_session.put("DAYCN", DateHelper.getDayCN());
		json_session.put("NEWSDATEE", SystemFunction.getYear() + "-"
				+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
		json_session.put("NEWSDATE", SystemFunction.getYear() + "-"
				+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
		json_session.put("YHJB", s_yhjb); //用户级别
		json_session.put("BRAND_NAME", s_brandchnName);
		
		json_session.put("WX_HEADIMAGE", wx_headimage);
		json_session.put("WX_NICK", wx_nick);
		json_session.put("WX_OPENID", wx_openid);
		json_session.put("WX_EWM", wxEwm);
		json_session.put("ZFB_EWM", zbfEwm);
		json_session.put("AGENT_HEADIMAGE", agent_headiamge);
		json_session.put("MAX_GDS", max_gds);
		json_session.put("MAX_HSTS",max_hsts);
		json_session.put("ADH_AGENTCODE",adh_agentcode);
		
		
		
		
		JSONArray json_arr = new JSONArray();
		json_arr.add(json_session);
		
		json_result.put("result", "true");
		json_result.put("session", json_arr);
		session.setAttribute("LS.YHDM", s_yhdm);
		return json_result;

	}
	
	
	
	
	// 自动登陆系统
		public JSONObject parseDisplayQXAUTO(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			
			
			
			HttpSession session = request.getSession(true);
			SQLQuery query = DSManager.getSQLQuery();
			JSONObject json_result = new JSONObject();
			String[][] s_qxcs = new String[0][0];
			String s_qxsql = "";
			String s_yhdm = "", s_yhmm = "", s_ipmac = "";
			
			if (request.getParameter("username") != null)
				s_yhdm = request.getParameter("username").trim();
			if (request.getParameter("password") != null)
				s_yhmm = request.getParameter("password").trim();
			
			/*if (request.getParameter("ipmac") != null)
				s_ipmac = request.getParameter("ipmac").trim();
			s_qxsql = "select Ip_Mac from tj_sys_Ip where Ip_Mac='" + s_ipmac + "'";
			
			
			try {
				s_qxcs = query.ArrayQuery(s_qxsql);
			} catch (SQLException e) {
				json_result.put("result", "false");
				json_result.put("reason", "权限SQL语句出错！"+e.getMessage()+s_qxsql);
				return json_result;
			}*/
			// if (s_qxcs.length == 0){
			// return SystemFunction.showLoginError(803,
			// "你的电脑还没有到总部备案(授权).\n\n不能访问系统，请联系系统管理员!", s_ipmac);
			// }
			String bossagent_code="";
			if (request.getParameter("boss_agentcode") != null)
				bossagent_code = request.getParameter("boss_agentcode").trim();
			//同一老板，多个店铺的情况
			if(!bossagent_code.equals("")){
				s_qxsql = "select Yhh,a.Yhz,Yhxm, agent_code Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(agent_code) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck,qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,lxfs sj,'老板' yhjb,(select brand_chnname from Brand_List  where Brand_Name=brand_code) brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(WX_OpenID,'') openid,allowdiscount  from tj_sys_yh a,agent_list b where a.dept=b.boss_agentcode  "
						+ " and a.lxfs='"
						+ SystemFunction.replace(s_yhdm, "'", "''")
						+ "' and agent_code='"+bossagent_code+"'";// STS为'1'时禁用,其它为活动
			}else{
			
			s_qxsql = "select Yhh,a.Yhz,Yhxm,Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(Dept) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck, qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,lxfs sj,yhjb,(select brand_chnname from Brand_List  where Brand_Name=a.warecode) brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(WX_OpenID,'') openid,allowdiscount  from tj_sys_yh a where 1=1"
					+ " and a.lxfs='"
					+ SystemFunction.replace(s_yhdm, "'", "''")
					+ "'";// STS为'1'时禁用,其它为活动
			}
			try {
				s_qxcs = query.ArrayQuery(s_qxsql);
			} catch (SQLException e) {
				json_result.put("result", "false");
				json_result.put("reason", "权限SQL语句出错！"+e.getMessage()+s_qxsql);
				return json_result;
			}
			
			/*if (s_qxcs.length == 0) {
				json_result.put("result", "false");
				json_result.put("reason", "用户名不存在!");
				return json_result;
			}*/
			
			//员工表没找到，查推单人表
					if (s_qxcs.length == 0) {
						String s_wxcs="select '' Yhh,'' Yhz,Yhxm,'' Dept,Mm,a.Sts,'' YhZMC,'2026-12-31' Yxq,'' Warecode,'' ZS,'' agent_name,'' qx_cd,'' qx_qt,'' qx_ck,'' qx_dj,'' qx_ts,'' qx_sh,lxfs sj,'普通用户' yhjb,'' brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(v_OpenID,'') openid ,'10' allowdiscount from WeiXinAppUser_open a where 1=1 ";
						s_wxcs=s_wxcs+" and a.lxfs='"+s_yhdm+"'";
						s_qxcs = query.ArrayQuery(s_wxcs);
						if(s_qxcs.length == 0){
							json_result.put("result", "false");
							json_result.put("reason", "用户名不存在!");
							return json_result;
						}
						/*else{
							update_sql = "update WeiXinAppUser_open set WX_headimage='"+wx_init_headimge+"',WX_nick='"+wx_init_nick+"' where v_openid='"+v_openid+"'";
						}*/
					}
			// if (s_qxcs[0][11].trim().indexOf(s_mac) == -1){
			// System.out.println("dd:"+s_mac);
			// return SystemFunction.showLoginError(805, "本机没有经过公司授权,请与公司系统管理员联系!",
			// s_mac);
			// }
			//自动登录，无需判断密码		
			/*if (!(s_qxcs[0][4].equals(Encrypt.MD5(s_yhmm)))) {
				json_result.put("result", "false");
				json_result.put("reason", "用户密码错误!");
				return json_result;
			}*/
			
			
			s_yhdm =s_qxcs[0][0].trim();
			
			String s_yhz = s_qxcs[0][1].trim();
			String s_yhmc = s_qxcs[0][2].trim();
			String s_dwdm = s_qxcs[0][3].trim();
			String s_sts = s_qxcs[0][5].trim();
			String s_yhzmc = s_qxcs[0][6].trim();
			String s_yxq = s_qxcs[0][7].trim();
			String s_ware = s_qxcs[0][8].trim();
			String s_zs = s_qxcs[0][9].trim();
			String s_agentName = s_qxcs[0][10].trim();
			String s_lxfs = s_qxcs[0][17].trim();
			String s_yhjb = s_qxcs[0][18].trim();
			
			String s_brandchnName = s_qxcs[0][19].trim();
			String wx_headimage= s_qxcs[0][20].trim();
			String wx_nick= s_qxcs[0][21].trim();
			String wx_openid= s_qxcs[0][22].trim();
			String allowdiscount= s_qxcs[0][23].trim();
			
			// String s_authMac = s_qxcs[0][11].trim();
			String wxEwm="";
			String zbfEwm="";
			String agent_headiamge="";
			String max_gds="";
			String max_hsts="";
			String adh_agentcode="";
			String is_share="";
			String gxf="";
			String gxf_type="";
			String lat="0";
			String lon="0";
			String vod_sql="";
//			终端系统特加
			try {
				if(s_yhz.equals("终端店面")){
					 vod_sql="select case when end_date is not null then datediff(day,getdate(),convert(datetime,end_date)) else 0 end valid_days,open_flag,isnull(wx_ewm,'') wx_ewm,isnull(agent_tel1,'') zfb_ewm,agent_headimage,max_gds,max_hsts,adh_agentcode,is_share,gxf,isnull(lat,0) lat,isnull(lon,0) lon,brand_code,yf_xt from agent_list where agent_code='"+s_dwdm+"'";
					String s_v[][]= query.ArrayQuery(vod_sql);
					String validDays=s_v[0][0];
					String openFlag=s_v[0][1];
					 wxEwm=s_v[0][2];
					 zbfEwm=s_v[0][3];
					agent_headiamge=s_v[0][4];
					max_gds=s_v[0][5];
					max_hsts=s_v[0][6];	
					adh_agentcode=s_v[0][7];
					is_share=s_v[0][8];
					
					gxf=s_v[0][9];
					lat=s_v[0][10];
					lon=s_v[0][11];
					s_brandchnName=s_v[0][12];
					gxf_type=s_v[0][13];
					int i_vDays=Integer.parseInt(validDays);
					/*if(i_vDays<0){
						json_result.put("result", "false");
						json_result.put("reason", "合同已经到期,请续签合同或与区域经理联系!");
						//json_result.put("reason", "合同已经到期,请续费!");
						return json_result;
					}*/
					if(!openFlag.equals("启用")){
						json_result.put("result", "false");
						json_result.put("reason", "客户已被总部停用了!");
						return json_result;
					}
				}
			} catch (Exception e) {
				System.out.println(e.toString()+" sql:"+vod_sql);
				json_result.put("result", "false");
				json_result.put("reason", "合同到期日期不正确，无法登陆系统，请与系统管理员联系！");
				return json_result;
			}

			if (s_sts.equals("1")) {
				json_result.put("result", "false");
				json_result.put("reason", "帐号已禁用,如果要激活,请联系所属店铺的老板!");
				return json_result;
			}
			if (s_sts.equals("2")) {
				json_result.put("result", "false");
				json_result.put("reason", "账号正在验证中...，请等待老板通过!");
				return json_result;
			}

			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date d_yxq = null;
			try {
				d_yxq = formater.parse(s_yxq);
			} catch (Exception e) {
				json_result.put("result", "false");
				json_result.put("reason", "格式化日期出错,请联系系统管理员！");
				return json_result;
			}
			java.util.Date nowDate = new java.util.Date();
			long d_yxqts = (d_yxq.getTime() - nowDate.getTime())
					/ (1000 * 60 * 60 * 24);
			/*
			 * if(d_yxqts < 0) { return
			 * SystemFunction.showLoginError(1000,"帐号已过期，你的到期日为："+SystemFunction.getDate(d_yxq)+",请于相关人员联系！",String.valueOf(d_yxqts)); }
			 */

			/*try {
				s_qxcs = XmlUtil.find(BConstants.PAGE_QX, "YHZ", s_yhz);
			} catch (Exception e) {
				return SystemFunction.showLoginError(806, "取用户功能代码SQL语句出错！", e
						.toString());
			}
			String s_gndm = "000";
			for (int i = 0; i < s_qxcs.length; i++)
				s_gndm += "," + s_qxcs[i][1].trim();*/
			String s_gndm="000";
			s_gndm=s_qxcs[0][11].trim();
			String s_qxqt=s_qxcs[0][12].trim();
			String s_qxck=s_qxcs[0][13].trim();
			String s_qxdj=s_qxcs[0][14].trim();
			String s_qxts=s_qxcs[0][15].trim();
			String s_qxsh=s_qxcs[0][16].trim();
			//SessionListener.isAlreadyEnter(session,s_yhdm);
			/*if(SessionListener.isAlreadyEnter(session,s_yhdm))
			{
				return SystemFunction.showLoginError(807, "该账户已经在别的电脑上登陆!",
				"");
			}*/
			
			//JSONArray json_arr = JSONArray.fromObject(fy_result);
			//json_arr.add
			String mob_gndm = "";
			if(s_gndm.length()>5){
			
			try {
				 vod_sql="select  gnid from TJ_SYS_MOBQX where GNID in ("+s_gndm+") order by Sort_Bm ";
				//System.out.println("vod_sql="+vod_sql);
				String s_v[][]= query.ArrayQuery(vod_sql);
				System.out.println(s_v.length);
				if(s_v.length > 0){
					for (int i = 0; i < s_v.length; i++){
						//if (mob_gndm.length()>0)
						   mob_gndm += "'" + s_v[i][0].trim()+"',";
						   //System.out.println("dd:"+s_v[i][0].trim());
					}
				}
			} catch (Exception e) {
				json_result.put("result", "false");
				json_result.put("reason", "取用户功能代码SQL语句出错！");
				return json_result;
				
			}
			if (mob_gndm.length()>0) 
				mob_gndm = mob_gndm.substring(1, mob_gndm.length()-1);
			else mob_gndm=s_gndm;
			}else mob_gndm=s_gndm;
			
			
			
			
			
			
			
			JSONObject json_session = new JSONObject();
			
			json_session.put("TITLE", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE)); //系统标题
			json_session.put("YHDM", s_yhdm); //用户代码
			json_session.put("ZS", s_zs);
			json_session.put("WARE", s_ware);
			json_session.put("IS_SHARE", is_share);
			json_session.put("ALLOWDISCOUNT", allowdiscount);
			
			json_session.put("LAT", lat);
			json_session.put("LON", lon);
			json_session.put("GXF", gxf);
			json_session.put("GXF_TYPE", gxf_type);
			json_session.put("YHZ", s_yhzmc); //用户组名称
			json_session.put("YHZEG", s_yhz); //用户组名称英文
			json_session.put("XM", s_yhmc); //用户姓名
			json_session.put("DWDM", s_dwdm); //店铺编码
			//json_session.put("GNDM", s_gndm);
			json_session.put("QXCD", mob_gndm);
			json_session.put("QXQT", s_qxqt);
			json_session.put("QXCK", s_qxck);
			
			
			json_session.put("QXDJ", s_qxdj);
			json_session.put("QXTS", s_qxts);
			json_session.put("QXSH", s_qxsh);
			json_session.put("SJ", s_lxfs);
			json_session.put("FLAG", "yes");
			json_session.put("YEAR", SystemFunction.getYear()); //年
			json_session.put("MONTH", SystemFunction.getMonth()); //月
			json_session.put("DAY", SystemFunction.getDay()); //日
			json_session.put("IP", request.getRemoteAddr());
			json_session.put("AGENT", s_agentName); //店铺名称
			json_session.put("LASTDAYSOFCURMONTH", DateHelper
					.getLastDayOfCurrentMonth()); //一个月的最后一天
			json_session.put("FIRSTDAYSOFCURMONTH", DateHelper
					.getFirstDayOfCurrentMonth()); //一个月的第一天
			json_session.put("CURDATE", DateHelper.getDate()); //今天
			json_session.put("CURSHORTDATE", DateHelper.getShortDate1());
			json_session.put("URL", "http://" + request.getServerName()
					+ ":" + request.getServerPort() + request.getContextPath());

			json_session.put("BGCOLOR", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.SYSTEM_BGCOLOR));
			json_session.put("BGIMAGE", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.SYSTEM_BGIMAGE));
			json_session.put("SYSTEMBHD", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.BHD));
			json_session.put("SYSTEMFHD", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.FHD));
			json_session.put("SYSTEMTYD", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.TYD));
			
			json_session.put("AUTHAMOUNT", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.AUTHAMOUNT));
			
			json_session.put("ZOOM", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.ZOOM));
			json_session.put("CURTIME", DateHelper.getShowDateTime());
			json_session.put("DATE", DateHelper.getDateCN()); //中文日期
			json_session.put("DAYCN", DateHelper.getDayCN());
			json_session.put("NEWSDATEE", SystemFunction.getYear() + "-"
					+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
			json_session.put("NEWSDATE", SystemFunction.getYear() + "-"
					+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
			json_session.put("YHJB", s_yhjb); //用户级别
			json_session.put("BRAND_NAME", s_brandchnName);
			json_session.put("WX_HEADIMAGE", wx_headimage);
			json_session.put("WX_NICK", wx_nick);
			json_session.put("WX_OPENID", wx_openid);
			json_session.put("WX_EWM", wxEwm);
			json_session.put("ZFB_EWM", zbfEwm);
			json_session.put("AGENT_HEADIMAGE", agent_headiamge);
			json_session.put("MAX_GDS", max_gds);
			json_session.put("MAX_HSTS",max_hsts);
			json_session.put("ADH_AGENTCODE",adh_agentcode);
			
			
			
			
			JSONArray json_arr = new JSONArray();
			json_arr.add(json_session);
			
			json_result.put("result", "true");
			json_result.put("session", json_arr);
			session.setAttribute("LS.YHDM", s_yhdm);
			return json_result;

		}
	
	
	
	// 登陆系统短信验证码
		public JSONObject parseDisplayQXSMS(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			HttpSession session = request.getSession(true);
			SQLQuery query = DSManager.getSQLQuery();
			JSONObject json_result = new JSONObject();
			String[][] s_qxcs = new String[0][0];
			String s_qxsql = "";
			String s_yhdm = "", s_yhmm = "", s_ipmac = "";
			
			if (request.getParameter("username") != null)
				s_yhdm = request.getParameter("username").trim();
			if (request.getParameter("password") != null)
				s_yhmm = request.getParameter("password").trim();
			
			/*if (request.getParameter("ipmac") != null)
				s_ipmac = request.getParameter("ipmac").trim();
			s_qxsql = "select Ip_Mac from tj_sys_Ip where Ip_Mac='" + s_ipmac + "'";
			try {
				s_qxcs = query.ArrayQuery(s_qxsql);
			} catch (SQLException e) {
				json_result.put("result", "false");
				json_result.put("reason", "权限SQL语句出错！"+e.getMessage()+s_qxsql);
				return json_result;
			}*/
			// if (s_qxcs.length == 0){
			// return SystemFunction.showLoginError(803,
			// "你的电脑还没有到总部备案(授权).\n\n不能访问系统，请联系系统管理员!", s_ipmac);
			// }
			String bossagent_code="";
			if (request.getParameter("boss_agentcode") != null)
				bossagent_code = request.getParameter("boss_agentcode").trim();
			//同一老板，多个店铺的情况
			if(!bossagent_code.equals("")){
				s_qxsql = "select Yhh,a.Yhz,Yhxm, agent_code Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(agent_code) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck, qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,lxfs sj,'老板' yhjb,(select brand_chnname from Brand_List  where Brand_Name=brand_code) brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(WX_OpenID,'') openid ,allowdiscount from tj_sys_yh a,agent_list b where a.dept=b.boss_agentcode  "
						+ " and a.lxfs='"
						+ SystemFunction.replace(s_yhdm, "'", "''")
						+ "' and agent_code='"+bossagent_code+"'";// STS为'1'时禁用,其它为活动
			}else{
			
			s_qxsql = "select Yhh,a.Yhz,Yhxm,Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(Dept) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck,qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,lxfs sj,yhjb,(select brand_chnname from Brand_List  where Brand_Name=a.warecode) brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(WX_OpenID,'') openid,allowdiscount  from tj_sys_yh a where 1=1"
					+ " and a.lxfs='"
					+ SystemFunction.replace(s_yhdm, "'", "''")
					+ "'";// STS为'1'时禁用,其它为活动
			}
			try {
				s_qxcs = query.ArrayQuery(s_qxsql);
			} catch (SQLException e) {
				json_result.put("result", "false");
				json_result.put("reason", "权限SQL语句出错！"+e.getMessage()+s_qxsql);
				return json_result;
			}
			if (s_qxcs.length == 0) {
				json_result.put("result", "false");
				json_result.put("reason", "用户名不存在!");
				return json_result;
			}
			
			//员工表没找到，查推单人表
					/*if (s_qxcs.length == 0) {
						String s_wxcs="select '' Yhh,'' Yhz,Yhxm,'' Dept,Mm,a.Sts,'' YhZMC,'2026-12-31' Yxq,'' Warecode,'' ZS,'' agent_name,'' qx_cd,'' qx_qt,'' qx_ck,'' qx_dj,'' qx_ts,'' qx_sh,lxfs sj,'普通用户' yhjb,'' brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(v_OpenID,'') openid   from WeiXinAppUser_open a where 1=1 ";
						s_wxcs=s_wxcs+" and a.lxfs='"+s_yhdm+"'";
						s_qxcs = query.ArrayQuery(s_wxcs);
						if(s_qxcs.length == 0){
							json_result.put("result", "false");
							json_result.put("reason", "用户名不存在!");
							return json_result;
						}
						
					}*/
			// if (s_qxcs[0][11].trim().indexOf(s_mac) == -1){
			// System.out.println("dd:"+s_mac);
			// return SystemFunction.showLoginError(805, "本机没有经过公司授权,请与公司系统管理员联系!",
			// s_mac);
			// }
			//短信验证码登录，无需密码验证
			/*if (!(s_qxcs[0][4].equals(Encrypt.MD5(s_yhmm)))) {
				json_result.put("result", "false");
				json_result.put("reason", "用户密码错误!");
				return json_result;
			}*/
			s_yhdm =s_qxcs[0][0].trim();
			
			String s_yhz = s_qxcs[0][1].trim();
			String s_yhmc = s_qxcs[0][2].trim();
			String s_dwdm = s_qxcs[0][3].trim();
			String s_sts = s_qxcs[0][5].trim();
			String s_yhzmc = s_qxcs[0][6].trim();
			String s_yxq = s_qxcs[0][7].trim();
			String s_ware = s_qxcs[0][8].trim();
			String s_zs = s_qxcs[0][9].trim();
			String s_agentName = s_qxcs[0][10].trim();
			String s_lxfs = s_qxcs[0][17].trim();
			String s_yhjb = s_qxcs[0][18].trim();
			
			String s_brandchnName = s_qxcs[0][19].trim();
			String wx_headimage= s_qxcs[0][20].trim();
			String wx_nick= s_qxcs[0][21].trim();
			String wx_openid= s_qxcs[0][22].trim();
			String allowdiscount= s_qxcs[0][23].trim();
			// String s_authMac = s_qxcs[0][11].trim();
			String wxEwm="";
			String zbfEwm="";
			String agent_headiamge="";
			String max_gds="";
			String max_hsts="";
			String adh_agentcode="";
			String is_share="";
			String gxf="",lat="",lon="",gxf_type="";
//			终端系统特加
			try {
				if(s_yhz.equals("终端店面")){
					String vod_sql="select case when end_date is not null then datediff(day,getdate(),convert(datetime,end_date)) else 0 end valid_days,open_flag,isnull(wx_ewm,'') wx_ewm,isnull(agent_tel1,'') zfb_ewm,agent_headimage,max_gds,max_hsts,adh_agentcode,is_share,gxf,isnull(lat,0) lat,isnull(lon,0) lon,brand_code,yf_xt  from agent_list where agent_code='"+s_dwdm+"'";
					String s_v[][]= query.ArrayQuery(vod_sql);
					String validDays=s_v[0][0];
					String openFlag=s_v[0][1];
					 wxEwm=s_v[0][2];
					 zbfEwm=s_v[0][3];
					agent_headiamge=s_v[0][4];
					max_gds=s_v[0][5];
					max_hsts=s_v[0][6];	
					adh_agentcode=s_v[0][7];
					is_share=s_v[0][8];
					gxf=s_v[0][9];
					lat=s_v[0][10];
					lon=s_v[0][11];
					s_brandchnName=s_v[0][12];
					gxf_type= s_v[0][13];
					int i_vDays=Integer.parseInt(validDays);
					/*if(i_vDays<0){
						json_result.put("result", "false");
						json_result.put("reason", "合同已经到期,请续签合同或与区域经理联系!");
						//json_result.put("reason", "合同已经到期,请续费!");
						return json_result;
					}*/
					if(!openFlag.equals("启用")){
						json_result.put("result", "false");
						json_result.put("reason", "客户已被总部停用了!");
						return json_result;
					}
				}
			} catch (Exception e) {
				json_result.put("result", "false");
				json_result.put("reason", "合同到期日期不正确，无法登陆系统，请与系统管理员联系！");
				return json_result;
			}

			if (s_sts.equals("1")) {
				json_result.put("result", "false");
				json_result.put("reason", "帐号已禁用,如果要激活,请联系所属店铺的老板!");
				return json_result;
			}
			if (s_sts.equals("2")) {
				json_result.put("result", "false");
				json_result.put("reason", "账号正在验证中...，请等待老板通过!");
				return json_result;
			}

			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date d_yxq = null;
			try {
				d_yxq = formater.parse(s_yxq);
			} catch (Exception e) {
				json_result.put("result", "false");
				json_result.put("reason", "格式化日期出错,请联系系统管理员！");
				return json_result;
			}
			java.util.Date nowDate = new java.util.Date();
			long d_yxqts = (d_yxq.getTime() - nowDate.getTime())
					/ (1000 * 60 * 60 * 24);
			/*
			 * if(d_yxqts < 0) { return
			 * SystemFunction.showLoginError(1000,"帐号已过期，你的到期日为："+SystemFunction.getDate(d_yxq)+",请于相关人员联系！",String.valueOf(d_yxqts)); }
			 */

			/*try {
				s_qxcs = XmlUtil.find(BConstants.PAGE_QX, "YHZ", s_yhz);
			} catch (Exception e) {
				return SystemFunction.showLoginError(806, "取用户功能代码SQL语句出错！", e
						.toString());
			}
			String s_gndm = "000";
			for (int i = 0; i < s_qxcs.length; i++)
				s_gndm += "," + s_qxcs[i][1].trim();*/
			String s_gndm="000";
			s_gndm=s_qxcs[0][11].trim();
			String s_qxqt=s_qxcs[0][12].trim();
			String s_qxck=s_qxcs[0][13].trim();
			String s_qxdj=s_qxcs[0][14].trim();
			String s_qxts=s_qxcs[0][15].trim();
			String s_qxsh=s_qxcs[0][16].trim();
			//SessionListener.isAlreadyEnter(session,s_yhdm);
			/*if(SessionListener.isAlreadyEnter(session,s_yhdm))
			{
				return SystemFunction.showLoginError(807, "该账户已经在别的电脑上登陆!",
				"");
			}*/
			
			//JSONArray json_arr = JSONArray.fromObject(fy_result);
			//json_arr.add
			String mob_gndm = "";
			if(s_gndm.length()>5){
			
			try {
				String vod_sql="select  gnid from TJ_SYS_MOBQX where GNID in ("+s_gndm+") order by Sort_Bm ";
				//System.out.println("vod_sql="+vod_sql);
				String s_v[][]= query.ArrayQuery(vod_sql);
				System.out.println(s_v.length);
				if(s_v.length > 0){
					for (int i = 0; i < s_v.length; i++){
						//if (mob_gndm.length()>0)
						   mob_gndm += "'" + s_v[i][0].trim()+"',";
						   //System.out.println("dd:"+s_v[i][0].trim());
					}
				}
			} catch (Exception e) {
				json_result.put("result", "false");
				json_result.put("reason", "取用户功能代码SQL语句出错！");
				return json_result;
				
			}
			if (mob_gndm.length()>0) 
				mob_gndm = mob_gndm.substring(1, mob_gndm.length()-1);
			else mob_gndm=s_gndm;
			}else mob_gndm=s_gndm;
			
			//判断环信是否注册
			
			String chatusername=s_lxfs;
	    	Authentic.Token TEST_TOKEN = new Authentic.Token("YWMt4EPcDvVpEeWmTm2uJUQPcwAAAVT1s8Bmn-wB5wwM9nqr6HgljAvlo79iDX8",1465203701330L);
	    	TalkDataService service = new TalkDataServiceImpl(new TalkHttpServiceImplApache());
	    	service.setToken(TEST_TOKEN);
	    	try {
	    		String isRegedit=JsonTool.write(service.userGet(chatusername));
	    		if (isRegedit.indexOf("statusCode")!=-1)
	    		{
	    			JsonTool.write(service.userSave(chatusername,chatusername+"+kcc",s_yhmc));
	    		}
	    		
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			JSONObject json_session = new JSONObject();
			
			json_session.put("TITLE", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE)); //系统标题
			json_session.put("YHDM", s_yhdm); //用户代码
			json_session.put("ZS", s_zs);
			json_session.put("WARE", s_ware);
			json_session.put("IS_SHARE", is_share);
			json_session.put("ALLOWDISCOUNT", allowdiscount);
			
			json_session.put("LAT", lat);
			json_session.put("LON", lon);
			json_session.put("GXF", gxf);
			json_session.put("GXF_TYPE", gxf_type);
			json_session.put("YHZ", s_yhzmc); //用户组名称
			json_session.put("YHZEG", s_yhz); //用户组名称英文
			json_session.put("XM", s_yhmc); //用户姓名
			json_session.put("DWDM", s_dwdm); //店铺编码
			//json_session.put("GNDM", s_gndm);
			json_session.put("QXCD", mob_gndm);
			json_session.put("QXQT", s_qxqt);
			json_session.put("QXCK", s_qxck);
			json_session.put("QXDJ", s_qxdj);
			json_session.put("QXTS", s_qxts);
			json_session.put("QXSH", s_qxsh);
			json_session.put("SJ", s_lxfs);
			json_session.put("FLAG", "yes");
			json_session.put("YEAR", SystemFunction.getYear()); //年
			json_session.put("MONTH", SystemFunction.getMonth()); //月
			json_session.put("DAY", SystemFunction.getDay()); //日
			json_session.put("IP", request.getRemoteAddr());
			json_session.put("AGENT", s_agentName); //店铺名称
			json_session.put("LASTDAYSOFCURMONTH", DateHelper
					.getLastDayOfCurrentMonth()); //一个月的最后一天
			json_session.put("FIRSTDAYSOFCURMONTH", DateHelper
					.getFirstDayOfCurrentMonth()); //一个月的第一天
			json_session.put("CURDATE", DateHelper.getDate()); //今天
			json_session.put("CURSHORTDATE", DateHelper.getShortDate1());
			json_session.put("URL", "http://" + request.getServerName()
					+ ":" + request.getServerPort() + request.getContextPath());

			json_session.put("BGCOLOR", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.SYSTEM_BGCOLOR));
			json_session.put("BGIMAGE", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.SYSTEM_BGIMAGE));
			json_session.put("SYSTEMBHD", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.BHD));
			json_session.put("SYSTEMFHD", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.FHD));
			json_session.put("SYSTEMTYD", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.TYD));
			
			json_session.put("AUTHAMOUNT", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.AUTHAMOUNT));
			
			json_session.put("ZOOM", XmlUtil.readXml(
					BConstants.CONFIG_FILE, BConstants.ZOOM));
			json_session.put("CURTIME", DateHelper.getShowDateTime());
			json_session.put("DATE", DateHelper.getDateCN()); //中文日期
			json_session.put("DAYCN", DateHelper.getDayCN());
			json_session.put("NEWSDATEE", SystemFunction.getYear() + "-"
					+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
			json_session.put("NEWSDATE", SystemFunction.getYear() + "-"
					+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
			json_session.put("YHJB", s_yhjb); //用户级别
			json_session.put("BRAND_NAME", s_brandchnName);
			json_session.put("WX_HEADIMAGE", wx_headimage);
			json_session.put("WX_NICK", wx_nick);
			json_session.put("WX_OPENID", wx_openid);
			json_session.put("WX_EWM", wxEwm);
			json_session.put("ZFB_EWM", zbfEwm);
			json_session.put("AGENT_HEADIMAGE", agent_headiamge);
			json_session.put("MAX_GDS", max_gds);
			json_session.put("MAX_HSTS",max_hsts);
			json_session.put("ADH_AGENTCODE",adh_agentcode);
			
			
			
			
			JSONArray json_arr = new JSONArray();
			json_arr.add(json_session);
			
			json_result.put("result", "true");
			json_result.put("session", json_arr);
			session.setAttribute("LS.YHDM", s_yhdm);
			return json_result;

		}
	
	// 用微信登陆系统
	public JSONObject parseDisplayWXQX(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(true);
		SQLQuery query = DSManager.getSQLQuery();
		JSONObject json_result = new JSONObject();
		String[][] s_qxcs = new String[0][0];
		String s_qxsql = "";
		String s_yhdm = "", s_yhmm = "", v_openid = "",wx_init_headimge="",wx_init_nick="";
		//String update_sql="";
		if (request.getParameter("v_openid") != null)
			v_openid = request.getParameter("v_openid").trim();
		else  v_openid="11111111";
		if (request.getParameter("wx_headimage") != null)
			wx_init_headimge=request.getParameter("wx_headimage") ;
		
		if (request.getParameter("wx_nick") != null)
			wx_init_nick=request.getParameter("wx_nick") ;	
		
		
		
		//System.out.println("v_openid:"+v_openid);
		
		
		String bossagent_code="";
		if (request.getParameter("boss_agentcode") != null)
			bossagent_code = request.getParameter("boss_agentcode").trim();
		
		if(!bossagent_code.equals("")){
			s_qxsql = "select Yhh,a.Yhz,Yhxm, agent_code Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(agent_code) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck,qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,lxfs sj,'老板' yhjb,(select brand_chnname from Brand_List  where Brand_Name=brand_code) brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(WX_OpenID,'') openid,allowdiscount  from tj_sys_yh a,agent_list b where a.dept=b.boss_agentcode  "
					+ " and a.v_openid='"
					+ SystemFunction.replace(v_openid, "'", "''")
					+ "' and agent_code='"+bossagent_code+"'";// STS为'1'时禁用,其它为活动
			//System.out.println ("sql1:"+s_qxsql);
		}else{
		
			s_qxsql = "select Yhh,a.Yhz,a.Yhxm,Dept,a.Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(Dept) agent_name,qx_mob_cd qx_cd,a.qx_qt,qx_mob_dp qx_ck,qx_mob_sj qx_dj,a.qx_ts,a.qx_sh,a.lxfs sj,yhjb,(select brand_chnname from Brand_List  where Brand_Name=a.warecode) brand_chnname,isnull(b.WX_headimage,'') headimage,isnull(b.WX_nick,'') nick,isnull(a.WX_OpenID,'') openid,allowdiscount  from tj_sys_yh a,WeiXinAppUser_Open b  where a.lxfs=b.lxfs"
					+ " and b.v_openid='"
					+ SystemFunction.replace(v_openid, "'", "''")
					+ "'";// STS为'1'时禁用,其它为活动
			//System.out.println ("sql2:"+s_qxsql);
		}
		
		

		try {
			s_qxcs = query.ArrayQuery(s_qxsql);
		} catch (SQLException e) {
			json_result.put("result", "false");
			json_result.put("reason", "权限SQL语句出错！"+e.getMessage()+s_qxsql);
			return json_result;
		}
		//员工表没找到，查推单人表
		if (s_qxcs.length == 0) {
			String s_wxcs="select '' Yhh,'' Yhz,Yhxm,'' Dept,Mm,a.Sts,'' YhZMC,'2026-12-30' Yxq,'' Warecode,'' ZS,'' agent_name,'' qx_cd,'' qx_qt,'' qx_ck,'' qx_dj,'' qx_ts,'' qx_sh,lxfs sj,'普通用户' yhjb,'' brand_chnname,isnull(WX_headimage,'') headimage,isnull(WX_nick,'') nick,isnull(v_OpenID,'') openid,'10' allowdiscount   from WeiXinAppUser_open a where 1=1 ";
			s_wxcs=s_wxcs+" and a.v_openid='"+v_openid+"'";
			System.out.println ("sql3:"+s_wxcs);
			s_qxcs = query.ArrayQuery(s_wxcs);
			if(s_qxcs.length == 0){
			   json_result.put("result", "false");
			   json_result.put("reason", "没绑定微信");
			   return json_result;
			}
			/*else{
				update_sql = "update WeiXinAppUser_open set WX_headimage='"+wx_init_headimge+"',WX_nick='"+wx_init_nick+"' where v_openid='"+v_openid+"'";
			}*/
		}/*else{
			 update_sql = "update tj_sys_yh set WX_headimage='"+wx_init_headimge+"',WX_nick='"+wx_init_nick+"' where v_openid='"+v_openid+"'";
		}*/
		
		
		
		s_yhdm =s_qxcs[0][0].trim();
		
		String s_yhz = s_qxcs[0][1].trim();
		String s_yhmc = s_qxcs[0][2].trim();
		String s_dwdm = s_qxcs[0][3].trim();
		String s_sts = s_qxcs[0][5].trim();
		String s_yhzmc = s_qxcs[0][6].trim();
		String s_yxq = s_qxcs[0][7].trim();
		String s_ware = s_qxcs[0][8].trim();
		String s_zs = s_qxcs[0][9].trim();
		String s_agentName = s_qxcs[0][10].trim();
		String s_lxfs = s_qxcs[0][17].trim();
		String s_yhjb = s_qxcs[0][18].trim();
		
		String s_brandchnName = s_qxcs[0][19].trim();
		String wx_headimage= wx_init_headimge;
		String wx_nick= s_qxcs[0][21].trim();;
		String wx_openid= s_qxcs[0][22].trim();
		String allowdiscount= s_qxcs[0][23].trim();
		
		// String s_authMac = s_qxcs[0][11].trim();
		String wxEwm="";
		String zbfEwm="";
		String agent_headiamge="";
		String max_gds="";
		String max_hsts="";
		String adh_agentcode="";
		String is_share="";
		String gxf="";
		String gxf_type="";
		String lat="0";
		String lon="0";
		String vod_sql="";
//		终端系统特加
		try {
			if(s_yhz.equals("终端店面")){
				 vod_sql="select case when end_date is not null then datediff(day,getdate(),convert(datetime,end_date)) else 0 end valid_days,open_flag,isnull(wx_ewm,'') wx_ewm,isnull(agent_tel1,'') zfb_ewm,agent_headimage,max_gds,max_hsts,adh_agentcode,is_share,gxf,isnull(lat,0) lat,isnull(lon,0) lon,brand_code,yf_xt   from agent_list where agent_code='"+s_dwdm+"'";
				String s_v[][]= query.ArrayQuery(vod_sql);
				String validDays=s_v[0][0];
				String openFlag=s_v[0][1];
				 wxEwm=s_v[0][2];
				 zbfEwm=s_v[0][3];
				agent_headiamge=s_v[0][4];
				max_gds=s_v[0][5];
				max_hsts=s_v[0][6];	
				adh_agentcode=s_v[0][7];	
				is_share = s_v[0][8];
				gxf=s_v[0][9];
				lat=s_v[0][10];
				lon=s_v[0][11];
				s_brandchnName=s_v[0][12];
				gxf_type = s_v[0][13];
				int i_vDays=Integer.parseInt(validDays);
				/*if(i_vDays<0){
					json_result.put("result", "false");
					json_result.put("reason", "合同已经到期,请续签合同或与区域经理联系!");
					//json_result.put("reason", "合同已经到期,请续费!");
					return json_result;
				}*/
				if(!openFlag.equals("启用")){
					json_result.put("result", "false");
					json_result.put("reason", "客户已被总部停用了!");
					return json_result;
				}
			}
		} catch (Exception e) {
			json_result.put("result", "false");
			json_result.put("reason", "合同到期日期不正确，无法登陆系统，请与系统管理员联系！");
			System.out.println(e.toString()+" SQL:"+vod_sql);
			return json_result;
		}

		if (s_sts.equals("1")) {
			json_result.put("result", "false");
			json_result.put("reason", "帐号已禁用,如果要激活,请联系所属店铺的老板!");
			return json_result;
		}
		if (s_sts.equals("2")) {
			json_result.put("result", "false");
			json_result.put("reason", "账号正在验证中...，请等待老板通过!");
			return json_result;
		}

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date d_yxq = null;
		try {
			d_yxq = formater.parse(s_yxq);
		} catch (Exception e) {
			json_result.put("result", "false");
			json_result.put("reason", "格式化日期出错,请联系系统管理员！");
			return json_result;
		}
		java.util.Date nowDate = new java.util.Date();
		long d_yxqts = (d_yxq.getTime() - nowDate.getTime())
				/ (1000 * 60 * 60 * 24);
		/*
		 * if(d_yxqts < 0) { return
		 * SystemFunction.showLoginError(1000,"帐号已过期，你的到期日为："+SystemFunction.getDate(d_yxq)+",请于相关人员联系！",String.valueOf(d_yxqts)); }
		 */

		/*try {
			s_qxcs = XmlUtil.find(BConstants.PAGE_QX, "YHZ", s_yhz);
		} catch (Exception e) {
			return SystemFunction.showLoginError(806, "取用户功能代码SQL语句出错！", e
					.toString());
		}
		String s_gndm = "000";
		for (int i = 0; i < s_qxcs.length; i++)
			s_gndm += "," + s_qxcs[i][1].trim();*/
		String s_gndm="000";
		s_gndm=s_qxcs[0][11].trim();
		String s_qxqt=s_qxcs[0][12].trim();
		String s_qxck=s_qxcs[0][13].trim();
		String s_qxdj=s_qxcs[0][14].trim();
		String s_qxts=s_qxcs[0][15].trim();
		String s_qxsh=s_qxcs[0][16].trim();
		//SessionListener.isAlreadyEnter(session,s_yhdm);
		/*if(SessionListener.isAlreadyEnter(session,s_yhdm))
		{
			return SystemFunction.showLoginError(807, "该账户已经在别的电脑上登陆!",
			"");
		}*/
		
		//JSONArray json_arr = JSONArray.fromObject(fy_result);
		//json_arr.add
		
		String mob_gndm = "";
		if(s_gndm.length()>5){
		
		try {
			 vod_sql="select  gnid from TJ_SYS_MOBQX where GNID in ("+s_gndm+") order by Sort_Bm ";
			//System.out.println("vod_sql="+vod_sql);
			String s_v[][]= query.ArrayQuery(vod_sql);
			System.out.println(s_v.length);
			if(s_v.length > 0){
				for (int i = 0; i < s_v.length; i++){
					//if (mob_gndm.length()>0)
					   mob_gndm += "'" + s_v[i][0].trim()+"',";
					   //System.out.println("dd:"+s_v[i][0].trim());
				}
			}
		} catch (Exception e) {
			json_result.put("result", "false");
			json_result.put("reason", "取用户功能代码SQL语句出错！");
			return json_result;
			
		}
		if (mob_gndm.length()>0) 
			mob_gndm = mob_gndm.substring(1, mob_gndm.length()-1);
		else mob_gndm=s_gndm;
		}else mob_gndm=s_gndm;
		
		//判断环信是否注册
		
				String chatusername=s_lxfs;
		    	//Authentic.Token TEST_TOKEN = new Authentic.Token("YWMt4EPcDvVpEeWmTm2uJUQPcwAAAVT1s8Bmn-wB5wwM9nqr6HgljAvlo79iDX8",1465203701330L);
		    	TalkDataService service = new TalkDataServiceImpl(new TalkHttpServiceImplApache());
		    	//service.setToken(TEST_TOKEN);
		    	try {
		    		String isRegedit=JsonTool.write(service.userGet(chatusername));
		    		if (isRegedit.indexOf("statusCode")!=-1)
		    		{
		    			JsonTool.write(service.userSave(chatusername,chatusername+"+kcc",s_yhmc));
		    		}
		    		
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
		JSONObject json_session = new JSONObject();
		
		json_session.put("TITLE", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE)); //系统标题
		json_session.put("YHDM", s_yhdm); //用户代码
		json_session.put("ZS", s_zs);
		json_session.put("WARE", s_ware);
		json_session.put("IS_SHARE", is_share);
		json_session.put("ALLOWDISCOUNT", allowdiscount);
		json_session.put("GXF", gxf);
		json_session.put("GXF_TYPE", gxf_type);
		json_session.put("LAT", lat);
		json_session.put("LON", lon);
		
		


		json_session.put("YHZ", s_yhzmc); //用户组名称
		json_session.put("YHZEG", s_yhz); //用户组名称英文
		json_session.put("XM", s_yhmc); //用户姓名
		json_session.put("DWDM", s_dwdm); //店铺编码
		//json_session.put("GNDM", s_gndm);
		json_session.put("QXCD", mob_gndm);
		json_session.put("QXQT", s_qxqt);
		json_session.put("QXCK", s_qxck);
		json_session.put("QXDJ", s_qxdj);
		json_session.put("QXTS", s_qxts);
		json_session.put("QXSH", s_qxsh);
		json_session.put("SJ", s_lxfs);
		json_session.put("FLAG", "yes");
		json_session.put("YEAR", SystemFunction.getYear()); //年
		json_session.put("MONTH", SystemFunction.getMonth()); //月
		json_session.put("DAY", SystemFunction.getDay()); //日
		json_session.put("IP", request.getRemoteAddr());
		json_session.put("AGENT", s_agentName); //店铺名称
		json_session.put("LASTDAYSOFCURMONTH", DateHelper
				.getLastDayOfCurrentMonth()); //一个月的最后一天
		json_session.put("FIRSTDAYSOFCURMONTH", DateHelper
				.getFirstDayOfCurrentMonth()); //一个月的第一天
		json_session.put("CURDATE", DateHelper.getDate()); //今天
		json_session.put("CURSHORTDATE", DateHelper.getShortDate1());
		json_session.put("URL", "http://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath());

		json_session.put("BGCOLOR", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_BGCOLOR));
		json_session.put("BGIMAGE", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_BGIMAGE));
		json_session.put("SYSTEMBHD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.BHD));
		json_session.put("SYSTEMFHD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.FHD));
		json_session.put("SYSTEMTYD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.TYD));
		
		json_session.put("AUTHAMOUNT", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.AUTHAMOUNT));
		
		json_session.put("ZOOM", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.ZOOM));
		json_session.put("CURTIME", DateHelper.getShowDateTime());
		json_session.put("DATE", DateHelper.getDateCN()); //中文日期
		json_session.put("DAYCN", DateHelper.getDayCN());
		json_session.put("NEWSDATEE", SystemFunction.getYear() + "-"
				+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
		json_session.put("NEWSDATE", SystemFunction.getYear() + "-"
				+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
		json_session.put("YHJB", s_yhjb); //用户级别
		json_session.put("BRAND_NAME", s_brandchnName);
		json_session.put("WX_HEADIMAGE", wx_headimage);
		json_session.put("WX_NICK", wx_nick);
		json_session.put("WX_OPENID", wx_openid);
		json_session.put("WX_EWM", wxEwm);
		json_session.put("ZFB_EWM", zbfEwm);
		json_session.put("AGENT_HEADIMAGE", agent_headiamge);
		json_session.put("MAX_GDS", max_gds);
		json_session.put("MAX_HSTS",max_hsts);
		json_session.put("ADH_AGENTCODE",adh_agentcode);
		
		
		
		
		JSONArray json_arr = new JSONArray();
		json_arr.add(json_session);
		
		json_result.put("result", "true");
		json_result.put("session", json_arr);
		session.setAttribute("LS.YHDM", s_yhdm);
		return json_result;

	}

	// 处理用户功能权限
	public String parseDisplayZQ(String s_html, String kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session = request.getSession(true);

		String s_htmlt, s_htmlw, s_htmlc, s_tdt = "", s_tdc = "", s_tdw = "", s_temp = "", s_result = "", s_sfqxkz = "";
		s_htmlt = HtmlFunction.getHtmlHead(s_html, kjname);
		s_htmlc = HtmlFunction.getHtmlContext(s_html, kjname);
		s_htmlw = HtmlFunction.getHtmlEnd(s_html, kjname);

		String s_ywx = (String) session.getAttribute("LS.GNDM");
		if (s_ywx == null)
			s_ywx = "";

		String[][] s_zqcs = new String[0][0];
		int i_mhljs = 1;
		try {
			s_zqcs = XmlUtil.find(BConstants.PAGE_ZQ, "KJM", kjname);
			if (s_zqcs.length == 0)
				return SystemFunction.replace(s_html, "$$" + kjname + ",", "控件"
						+ kjname + "找不到！");
			i_mhljs = Integer.valueOf(s_zqcs[0][1].trim()).intValue();
		} catch (SQLException se1) {
			return SystemFunction.showError(807, kjname + "定位SQL出错!", se1
					.toString(), request);
		}

		String[][] s_zqmx = new String[0][0];
		try {
			s_zqmx = XmlUtil.find(BConstants.PAGE_ZQ_MX, "KJM", kjname);
			if (s_zqcs.length == 0)
				return SystemFunction.replace(s_html, "$$" + kjname + ",", "控件"
						+ kjname + "明细找不到！");
		} catch (SQLException se1) {
			return SystemFunction.showError(808, kjname + "SQL出错!", se1
					.toString(), request);
		}
		if (i_mhljs == 1) {
			for (int i = 0; i < s_zqmx.length; i++) {
				s_sfqxkz = s_zqmx[i][5];
				if (s_sfqxkz.trim().length() == 0
						|| !s_sfqxkz.trim().equals("1")
						|| (s_ywx.indexOf(s_zqmx[i][0]) != -1)) {
					if (s_zqmx[i][4].indexOf("@@") != -1)
						s_zqmx[i][4] = HtmlFunction.parseVar(s_zqmx[i][4],
								request, "html");
					s_temp = "<a href=\"" + s_zqmx[i][4] + "\"";
					if (s_zqmx[i][6].trim().length() != 0)
						s_temp += " class=" + s_zqmx[i][6];
					s_temp += ">";
					s_temp += s_zqmx[i][3] + "</a>";
					s_result += SystemFunction.replace(s_htmlc, "$$" + kjname
							+ ",", s_temp)
							+ " \n";
				} else {

					if (s_zqmx[i][7].trim().length() == 0
							|| s_zqmx[i][7].trim().equals("1")) {
						s_temp = "<font  color=\"#CECECE\">" + s_zqmx[i][3]
								+ "</font>";
						s_result += SystemFunction.replace(s_htmlc, "$$"
								+ kjname + ",", s_temp)
								+ "\n";
					} else {
						s_temp = " ";
						s_result += s_temp;
					}

				}
			}
		} else {
			s_tdt = HtmlFunction.gettdhead(s_htmlc, kjname);
			s_tdc = HtmlFunction.gettdcontext(s_htmlc, kjname);
			s_tdw = HtmlFunction.gettdend(s_htmlc, kjname);
			int i = 0;
			String s_tr = "";
			while ((i < s_zqmx.length)) {
				s_tr = "";
				int i_hljs = 0; // 行链接数
				for (int j = 1; (j <= i_mhljs) && (i < s_zqmx.length); j++, i++) {
					s_sfqxkz = s_zqmx[i][4];
					if (s_sfqxkz.trim().length() == 0
							|| (!s_sfqxkz.trim().equals("1"))
							|| (s_ywx.indexOf(s_zqmx[i][3]) != -1)) {
						if (s_zqmx[i][4].indexOf("@@") != -1)
							s_zqmx[i][4] = HtmlFunction.parseVar(s_zqmx[i][4],
									request, "html");
						s_temp = "<a href=\"" + s_zqmx[i][4] + "\"";
						if (s_zqmx[i][6].trim().length() != 0)
							s_temp += " class=" + s_zqmx[i][6];
						s_temp += ">";
						s_temp += s_zqmx[i][3] + "</a>";
						s_tr += SystemFunction.replace(s_tdc, "$$" + kjname
								+ ",", s_temp)
								+ "\n";
						i_hljs++;
					} else {
						// s_temp = " ";
						s_temp = "<font  color=\"#CECECE\">" + s_zqmx[i][3]
								+ "</font>";

						s_tr += SystemFunction.replace(s_tdc, "$$" + kjname
								+ ",", s_temp)
								+ "\n";
						i_hljs++;
					}
				}
				if (i == s_zqmx.length)
					for (int k = i_hljs; k < i_mhljs; k++) {
						s_temp = "";
						for (int m = 0; m < s_zqmx[0][0].getBytes().length; m++) {
							s_temp += "&nbsp;";
						}
						s_tr += SystemFunction.replace(s_tdc, "$$" + kjname
								+ ",", s_temp);
					}
				s_result += s_tdt + s_tr + s_tdw;
			}
		}
		return s_htmlt + s_result + s_htmlw;
	}

	 /**
	 * 处理树型结构的显示 实现方法: 1,连接数据库 取出SQL 2,根据SQL取出符合条件的值 3,解析HTML的值 4,返回
	 * 节点格式为：Node(id, pid, name, url, title, target, icon, iconOpen)
	 */
	public String parseDisplayTREE( String kjname,
			HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession(true);
        kjname="E"+kjname.substring(1);
        //得到树SQL
		String treeSql[][] = new String[0][0];
		try {
			treeSql = XmlUtil.find(BConstants.PAGE_TREE, "KJM", kjname);
			if (treeSql == null || treeSql.length == 0)
				//return new JSONArray();
				return SystemFunction.replace("", "$$" + kjname + ",", "没有找到控件"+ kjname + "!");
		} catch (Exception e) {
			return SystemFunction.showError(800, "定位" + kjname + "SQL出错!", e.toString(),request);
		}
		//处理树SQL,得到传参后的SQL
		String sql = HtmlFunction.parseVar(treeSql[0][1], request, "sql");
		
		//根据树SQL取出树内容
		String[][] nodeArr = new String[0][0];
		
		
	    String parentMenuId = request.getParameter("ParentMenuId");
	    if (parentMenuId == null) parentMenuId="1";
		try {
			SQLQuery query = DSManager.getSQLQuery();
			if (treeSql[0][2].equalsIgnoreCase("xml")){
				nodeArr=XmlUtil.xmlFile2Array(sql);
				//nodeArr = XmlUtil.find(sql, "ParentMenuId", parentMenuId);
				
			}else{
				nodeArr = query.ArrayQuery(sql);
			}
			
		} catch (Exception se1) {
			se1.printStackTrace();
			
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		
		for(int menu=0;menu<nodeArr.length;menu++){
            
			if (nodeArr[menu][7].equals("false")) {//不是叶子
            	sb.append("{ id:'"+nodeArr[menu][0]+"',leaf:"+nodeArr[menu][7].trim()+",text:'"+nodeArr[menu][2].trim()+"',cls:'"+nodeArr[menu][5]+"', hrefTarget:'"+nodeArr[menu][4]+"'");
            }else{//是叶子
            	sb.append("{ id:'"+nodeArr[menu][0]+"',leaf:"+nodeArr[menu][7].trim()+",text:'"+nodeArr[menu][2].trim()+"',cls:'"+nodeArr[menu][5]+"', href:'"+nodeArr[menu][3].replaceAll("&", "&amp;")+"', hrefTarget:'"+nodeArr[menu][4]+"'");
            }
                
			 sb.append("},");	
                
           
			
             
		}

		String rs=sb.toString();
		
		
		//sb.append("</TreeNode>");
		
		
		 
		//System.out.println(rs.substring(0,rs.length()-1)+"]");
		//return JSONArray.fromObject(rs.substring(0,rs.length()-1)+"]");
		return rs.substring(0,rs.length()-1)+"]";

	}
	
	
	/**
	 * 
	 * @param kjname
	 * @param request
	 * @param response
	 * @return
	 */
	public String parseDisplayJSONTREE( String kjname,
			HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession(true);
        //得到树SQL
		String treeSql[][] = new String[0][0];
		try {
			treeSql = XmlUtil.find(BConstants.PAGE_TREE, "KJM", kjname);
			if (treeSql == null || treeSql.length == 0)
				//return new JSONArray();
				return SystemFunction.replace("", "$$" + kjname + ",", "没有找到控件"+ kjname + "!");
		} catch (Exception e) {
			return SystemFunction.showError(800, "定位" + kjname + "SQL出错!", e.toString(),request);
		}
		String sql=treeSql[0][1];
		String ljh=treeSql[0][2];
		String cs=treeSql[0][3];
		String chk_sql=treeSql[0][4];
		
       
		
		 //菜单权限
		Map m_gndm=new HashMap();
		//选中的值
		Map m_chkValue=new HashMap();
		
		sql = HtmlFunction.parseVar(treeSql[0][1], request, "");
		
		//根据树SQL取出树内容
		String[][] nodeArr = new String[0][0];
	    String parentMenuId = request.getParameter("ParentMenuId");
	    if (parentMenuId == null) parentMenuId="1";
	    
		try {
			//获取数的内容,如为xml:读xml文件,其他：读取数据库
			SQLQuery query = DSManager.getSQLQuery();
			if (ljh.equalsIgnoreCase("xml")){
				nodeArr=XmlUtil.xmlFile2Array(sql);
				//nodeArr = XmlUtil.find(sql, "ParentMenuId", parentMenuId);
			}else{
				nodeArr = query.ArrayQuery(sql);
			}
			//获取选中的值
			if(cs.equals("1"))
			{
				chk_sql = HtmlFunction.parseVar(chk_sql, request, "");
				String chks[][] = new String[0][0];
				chks=query.ArrayQuery(chk_sql);
				String chkValue="";
				if(chks.length >0){
				  chkValue=chks[0][0];
				  String qxs[]=chkValue.split(",");
				
				  for(int i=0;i<qxs.length ;i++)
				  {
					m_chkValue.put(i, qxs[i]);
				  }
				}
			} else if(cs.equals("0")){
				String gndm = (String)session.getAttribute("LS.QXCD");
				if (gndm == null) gndm="";
				String gndms[]=gndm.split(",");
				for(int i=0;i<gndms.length;i++)
				{
					m_gndm.put(i, gndms[i]);
				}
			}
			
		} catch (Exception se1) {
			System.out.println(se1.toString()+sql);
			return SystemFunction.showError(808, kjname + "SQL出错!", se1.toString()+ sql,request);
		}
		
		//系统管理员
		//String yhdm = (String)session.getAttribute("LS.YHDM");
		//if (yhdm == null) yhdm="";
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		
		for(int menu=0;menu<nodeArr.length;menu++){
			//如果是根节点，直接跳过
			if(nodeArr[menu][0].trim().equals("1")) continue;
             
                
				//普通树
				if(cs.equals("1"))
				{
					String name=nodeArr[menu][2];
					if(nodeArr[menu][8].length()>0)
					{
						name=name+"【"+nodeArr[menu][8]+"】";
					}
					
					if (nodeArr[menu][7].equals("0")) {//不是叶子
                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+name+"',icon:'"+nodeArr[menu][5]+"', target:'"+nodeArr[menu][4]+"'");
                    }else{//是叶子
                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+name+"',icon:'"+nodeArr[menu][5]+"', url:'"+nodeArr[menu][3].replaceAll("&", "&amp;")+"', target:'"+nodeArr[menu][4]+"'");
                    }
					 //如果选中了，则增加选中属性   
	                if(m_chkValue.containsValue("'"+nodeArr[menu][0].trim()+"'"))
	                {
	                	sb.append(",checked:true");
	                }
	                sb.append("},");
				}
				//菜单树
				else if(cs.equals("0")){
					if(m_gndm.containsValue("'"+nodeArr[menu][0].trim()+"'") || nodeArr[menu][6].equals("0")){
						if (nodeArr[menu][7].equals("0")) {//不是叶子
	                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+nodeArr[menu][2]+"',icon:'"+nodeArr[menu][5]+"', target:'"+nodeArr[menu][4]+"'");
	                    }else{//是叶子
	                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+nodeArr[menu][2]+"',icon:'"+nodeArr[menu][5]+"', url:'"+nodeArr[menu][3].replaceAll("&", "&amp;")+"', target:'"+nodeArr[menu][4]+"'");
	                    } 
						 sb.append("},");   
	                }
				}
               
                
               
                
           
		}
		String rs=sb.toString();
		
		//System.out.println(rs.substring(0,rs.length()-1)+"]");
		return rs.substring(0,rs.length()-1)+"]";

	}

	/**
	 * 处理下拉框的显示 实现方法: 1,连接数据库 取出SQL 2,根据SQL取出符合条件的值 3,解析HTML的值 4,返回
	 */

	public String parseDisplayXL(String html, String s_kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String s_marker = "<option>$$" + s_kjname + ",</option>";

		// 连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0)
				return SystemFunction.replace(html, "$$" + s_kjname + ",",
						"没有找到控件" + s_kjname + "!");
		} catch (Exception e) {
			return SystemFunction.showError(800, "定位" + s_kjname + "SQL出错!", e
					.toString(), request);
		}

		String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");

		// 取出符合条件的值
		String result[][] = new String[0][0];
		try {
			result = query.ArrayQuery(sql);
		} catch (Exception e) {
			return SystemFunction.showError(801, s_kjname + "SQL出错", e
					.toString()
					+ "\n" + sql, request);
		}

		// 解析HTML的值

		String s_selectedvalue = request
				.getParameter(y_sql[0][1].toUpperCase());

		if (s_selectedvalue == null || s_selectedvalue.length() == 0) {
			HttpSession session = request.getSession();
			s_selectedvalue = (String) session.getAttribute("LS."
					+ y_sql[0][1].toUpperCase());
		}
		String s_option = "", s_value = "";
		for (int row = 0; row < result.length; row++) {
			s_option += "<option value=\"";
			s_value = result[row][0];
			s_option += s_value + "\"";

			if (s_selectedvalue != null
					&& s_selectedvalue.trim().equals(result[row][0])) {
				s_option += " selected";
			}
			s_option += ">" + result[row][1] + "</option>\n";

		}

		return SystemFunction.replace(html, s_marker, s_option);
	}

	public JSONArray parseDisplayXLJSON(String s_kjname,
			HttpServletRequest request) throws Exception {

		List result = new ArrayList();

		// 连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0) {
				return JSONArray.fromObject(result);
			}

		} catch (Exception e) {
			return JSONArray.fromObject(result);
		}

		String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");

		// 取出符合条件的值

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			return JSONArray.fromObject(result);
		}

		return JSONArray.fromObject(result);
	}

	public JSONObject parseDisplayMJJSON(String s_kjname,
			HttpServletRequest request) throws Exception {
        List allList=null;
		List fy_result =null;
		//连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("控件名："+s_kjname +" 控件没找到。");
			}
				
		} catch (Exception e) {
			throw new Exception("找控件时出错："+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //数据连接号
		String cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); //业务用的SQL
		String sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); //如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); //是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); //每页行数
		String defaults = y_sql[0][8].trim(); //数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); //没有找到数据时提示
		
//		SQL语名句中的查询条件
		String sql_tj = gettjsql(request,s_kjname);
		//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");

		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
        String start=request.getParameter("start");
        if (start == null) start="";
        String limit=request.getParameter("limit");
        if (limit == null) start="";
		//取出符合条件的值
		String all_result[][] = new String[0][0];
		int pageid = 0,pagenum=0,i_rownum = 0; //定义第几页，总页数，总记录数
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
	    	  pagenum = pageid+i_myhs;
	    	  if (pagenum>i_rownum) pagenum=i_rownum;
	    	  for(int i=pageid;i<pagenum;i++)
	    	  {
	    		  fy_result.add(allList.get(i));  
	    	  }
	          
	       }
              
		
	    }catch(Exception ex)
	    {
	    	throw new Exception("控件名："+s_kjname +" 查询时出错。SQL="+sql+"\n\n"+ex.toString());
	    }
			
	    JSONObject json_result = new JSONObject();
		json_result.put("total", Integer.valueOf(i_rownum));
		if(fy_result == null){
			JSONArray json_arr = JSONArray.fromObject(allList);
			json_result.put("root", json_arr);
		}else{
			JSONArray json_arr = JSONArray.fromObject(fy_result);
			json_result.put("root", json_arr);
		}
		
		

		
		return json_result;
	}

	public static JSONObject parseDisplayPushMsg(String s_kjname,
			HttpServletRequest request) throws Exception {
      
		//连接数据库
		//SQLQuery query = DSManager.getSQLQuery();
		 JSONObject json_result = new JSONObject();
		String y_sql[][] = new String[0][0];
		try {
			//s_kjname = "JJ"+s_kjname.substring(2, 6);
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				json_result.put("result", "false");
			}
				
		} catch (Exception e) {
			json_result.put("result", "false");
		}
		
		String ljh = y_sql[0][1].trim(); //数据连接号
		String cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); //业务用的SQL
		String sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); //如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); //是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); //每页行数
		String defaults = y_sql[0][8].trim(); //数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); //没有找到数据时提示
		
//		SQL语名句中的查询条件
		String sql_tj = gettjsql(request,s_kjname);
		//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");
		
		final String t_sql = HtmlFunction.parseVar(sql, request, "");
		Thread t=new Thread(){
		    public void run(){
		    	AppSmsAynSender a= new AppSmsAynSender();
		    	//a.setMsgContent(tsnr);
		    	a.sendMsgForSql(t_sql);
		   }
		};
		t.start();

		
			
	
		
		
		json_result.put("result", "ok");
		
		return json_result;
	}

    
    /**
	 * 上传文件
	 * 
	 * @param request
	 * @param response
	 * @param config
	 * @throws Exception
	 */
	public JSONObject parseUpLoadFile(String html, String kj,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		JSONObject json_result = new JSONObject();

		SQLUpdater updater = DSManager.getSQLUpdater();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_UE, "KJM", kj);
			if (y_sql == null || y_sql.length == 0) {
				json_result.put("result", "fasle");
				throw new Exception("没有找到控件" + kj + "!");
			}

		} catch (Exception e) {
			json_result.put("result", "fasle");
			throw new Exception("定位" + kj + "SQL出错!" + e.toString());
		}

		String path = y_sql[0][2];
		String ue_sql = y_sql[0][3];
		String filetype = y_sql[0][4];
		String Compress_w = y_sql[0][5];
		String Compress_h = y_sql[0][6];

		ServletFileUpload upload = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(-1);

		// 相对路径
		String url = SystemFunction.class.getResource("/").getPath();
		url = url.substring(1, url.length() - 16);
		String filename="";
		List items = upload.parseRequest(request);
		for (int i = 0; i < items.size(); i++) {

			FileItem fi = (FileItem) items.get(i);

			// 对于图片表单
			if (!fi.isFormField()) {
				File newFile = null;
				//System.out.print(fi.getName().toString());
				 String houz = fi.getName().split("\\.")[1];
				String s_filename = java.util.UUID.randomUUID().toString()
						+ "."+houz;
				s_filename = s_filename.replace("-", "");
				 filename = path + s_filename;

				if (fi.getName() != null && fi.getSize() != 0) {

					File fp = new File(url + path);
					// 创建目录
					if (!fp.exists()) {
						fp.mkdirs();// 目录不存在的情况下，创建目录。
					}
					newFile = new File(url + filename);

					if (!newFile.exists()) {
						newFile.createNewFile();
					}

					fi.write(newFile);

				} else {
					filename = "";
				}
				request.setAttribute("FileName", s_filename);
				request.setAttribute("FileFullName", filename);

				if (filetype.equals("图像")) {
					// 取像素
					BufferedImage bi = null;
					try {
						bi = ImageIO.read(newFile);
						int width = bi.getWidth(); // 像素
						int height = bi.getHeight(); // 像素
						request.setAttribute("Pix_W", String.valueOf(width));
						request.setAttribute("Pix_H", String.valueOf(height));
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (Compress_w.length() > 0) {
						String smallPath = url + path + "small\\";

						File fp = new File(smallPath);

						if (!fp.exists()) {
							fp.mkdirs();
						}
						ImageCompress.imageCompress(newFile, smallPath,
								s_filename, 0.5f, 1.0f,
								Integer.parseInt(Compress_w),
								Integer.parseInt(Compress_h));
					}

				}
			} else // 对于字段表单
			{
				String name = fi.getFieldName();
				String value = fi.getString();
				request.setAttribute(name, value);
			}

		}

		if (ue_sql.length() > 0) {
			String sql = HtmlFunction.parseVarAttr(ue_sql, request, "");

			try {
				updater.executeUpdate(sql);
			} catch (Exception ex) {
				System.out.println("sql=" + sql);
				ex.printStackTrace();
			}
		}
		
		json_result.put("result", "ok");
		
		

		return json_result;

	}
	
	
	 /**
		 * 上传视频文件
		 * 
		 * @param request
		 * @param response
		 * @param config
		 * @throws Exception
		 */
		public JSONObject parseUpLoadVideoFile(String html, String kj,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			
			JSONObject json_result = new JSONObject();

			SQLUpdater updater = DSManager.getSQLUpdater();
			String y_sql[][] = new String[0][0];
			try {

				y_sql = XmlUtil.find(BConstants.PAGE_UE, "KJM", kj);
				if (y_sql == null || y_sql.length == 0) {
					json_result.put("result", "fasle");
					throw new Exception("没有找到控件" + kj + "!");
				}

			} catch (Exception e) {
				json_result.put("result", "fasle");
				throw new Exception("定位" + kj + "SQL出错!" + e.toString());
			}

			String path = y_sql[0][2];
			String ue_sql = y_sql[0][3];
			String filetype = y_sql[0][4];
			String Compress_w = y_sql[0][5];
			String Compress_h = y_sql[0][6];

			ServletFileUpload upload = null;
			DiskFileItemFactory factory = new DiskFileItemFactory();
			upload = new ServletFileUpload(factory);
			upload.setFileSizeMax(-1);

			// 相对路径
			String url = SystemFunction.class.getResource("/").getPath();
			url = url.substring(1, url.length() - 16);
			String filename="";
			List items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {

				FileItem fi = (FileItem) items.get(i);

				// 对于图片表单
				if (!fi.isFormField()) {
					File newFile = null;
					//System.out.print(fi.getName().toString());
					 String houz = fi.getName().split("\\.")[1];
					String s_filename = java.util.UUID.randomUUID().toString()
							+ "."+houz;
					s_filename = s_filename.replace("-", "");
					 filename = path + s_filename;

					if (fi.getName() != null && fi.getSize() != 0) {

						File fp = new File(url + path);
						// 创建目录
						if (!fp.exists()) {
							fp.mkdirs();// 目录不存在的情况下，创建目录。
						}
						newFile = new File(url + filename);
						if (!newFile.exists()) {
							newFile.createNewFile();
						}
						fi.write(newFile);
						Map params= new HashMap();
						HttpClientUtil hcu = new HttpClientUtil();
						
						
						params.put("username", "admin");
						params.put("password", "4297f44b13955235245b2497399d7a93");
						String login=hcu.doLoginPost("http://ad-kcc.dderp.cn:10080/login",params);
						
						//File file=new File("C:\\Users\\Administrator\\Desktop\\video\\3ccef36a08ee31fd178f633929dd66ca.mp4");
						
						String video_id=hcu.doUploadFilePost("http://ad-kcc.dderp.cn:10080/vod/upload", params,newFile );
						filename="http://ad-kcc.dderp.cn:10080/vod/"+video_id+"/video.m3u8";
						

					} else {
						filename = "";
					}
					request.setAttribute("FileName", s_filename);
					
					request.setAttribute("FileFullName", filename);

					
				} else // 对于字段表单
				{
					String name = fi.getFieldName();
					String value = fi.getString();
					request.setAttribute(name, value);
				}

			}

			if (ue_sql.length() > 0) {
				String sql = HtmlFunction.parseVarAttr(ue_sql, request, "");

				try {
					updater.executeUpdate(sql);
				} catch (Exception ex) {
					System.out.println("sql=" + sql);
					ex.printStackTrace();
				}
			}
			
			json_result.put("result", "ok");
			
			

			return json_result;

		}
	
	/**
	 * 生成店铺二维码
	 * C接口
	 * @param request
	 * @param response
	 * @param config
	 * @throws Exception
	 */
	public JSONObject parseSmartAqr(String html, String kj,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		JSONObject json_result = new JSONObject();
		String agent_code=request.getParameter("agent_code");
		if(agent_code == null){
			json_result.put("result", "fail");
			json_result.put("msg", "门店编码为空");
			return json_result;
		}
		String url = null;
        url = ("http://ad-kcc.dderp.cn/mob/salsa/product_photo/" + agent_code + "_smartaqr.png");
        String fileUrl = ("D:/Tomcat7.0/ad-kcc/mob/salsa/product_photo/" + agent_code + "_smartaqr.png");
        
        File file=new File(fileUrl);    
        if(!file.exists())    
        {   
       	 	String access_token = SmartAqrCode.getAccessToken(
				"https://api.weixin.qq.com/cgi-bin/token", "client_credential",
					"wxf85e801e63bb2164", "9287b9eb47c7f15a112f2c82f15c80c2");			
       	 	String smartpath = "pages/index/index?agent_code=" + agent_code;
			InputStream is = SmartAqrCode.createwxaqrcode(
					"https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode",
					access_token, smartpath, "430");
			System.out.println("is:"+is);
			String qrcodeUrl = SmartAqrCode.saveToImgByInputStream(is, "salsa/product_photo/",agent_code+"_smartaqr.png");
			System.out.println("-------------"+qrcodeUrl);
        }   
        json_result.put("result", "ok");  
		json_result.put("msg", url);
		return json_result;
	}
	/**
	 * 生成店铺产品二维码
	 * C接口
	 * @param request
	 * @param response
	 * @param config
	 * @throws Exception
	 */
	public JSONObject parseProductAqr(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		JSONObject json_result = new JSONObject();
		String agent_code=request.getParameter("agent_code");
		String product_id= request.getParameter("product_id");
		if(agent_code == null){
			json_result.put("result", "fail");
			json_result.put("msg", "门店编码为空");
			return json_result;
		}
		if(product_id == null){
			json_result.put("result", "fail");
			json_result.put("msg", "产品编码为空");
			return json_result;
		}
		String url = null;
		url = ("http://ad-kcc.dderp.cn/mob/salsa/product_photo/product/"+agent_code+ "_" + product_id +"_smartaqr.png");
	       
        String fileUrl = ("D:/Tomcat7.0/ad-kcc/mob/salsa/product_photo/product/"+agent_code+ "_" + product_id +"_smartaqr.png");
        File file=new File(fileUrl);    
        if(!file.exists())    
        {   
       	 	String access_token = SmartAqrCode.getAccessToken(
				"https://api.weixin.qq.com/cgi-bin/token", "client_credential",
					"wxf85e801e63bb2164", "9287b9eb47c7f15a112f2c82f15c80c2");			
       	 	String smartpath = "pages/index/index?agent_code=" + agent_code+"&product_id="+product_id;
			InputStream is = SmartAqrCode.createwxaqrcode(
					"https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode",
					access_token, smartpath, "430");
			System.out.println("is:"+is);
			String qrcodeUrl = SmartAqrCode.saveToImgByInputStream(is, "salsa/product_photo/product/",agent_code+ "_" + product_id +"_smartaqr.png");
			System.out.println("-------------"+qrcodeUrl);
			String ext =SmartAqrCode.getFileExt(qrcodeUrl);
			if(ext == "jpg")
				SmartAqrCode.cutImage(qrcodeUrl, qrcodeUrl, 20, 20, 430, 430);
			else if(ext == "png")
				SmartAqrCode.cutPNG(qrcodeUrl, qrcodeUrl, 20, 20, 430, 430);
        }   
        json_result.put("result", "ok");  
		json_result.put("msg", url);
		return json_result;
	}
	/**
	 * 生成店铺产品二维码  
	 * b接口
	 * 
	 * @param request
	 * @param response
	 * @param config
	 * @throws Exception
	 */
	public JSONObject parseSmartProductAqr(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		JSONObject json_result = new JSONObject();
		String agent_code=request.getParameter("agent_code");
		String product_id= request.getParameter("product_id");
		if(agent_code == null){
			json_result.put("result", "fail");
			json_result.put("msg", "门店编码为空");
			return json_result;
		}
		if(product_id == null){
			json_result.put("result", "fail");
			json_result.put("msg", "产品编码为空");
			return json_result;
		}
		String fileUrl = null;
        fileUrl = ("http://ad-kcc.dderp.cn/mob/salsa/product_photo/product/"+agent_code + "_" + product_id +"_smartaqr.png");
        String urlPath="D:\\Tomcat7.0\\ad-kcc\\mob\\salsa\\product_photo\\product\\"+agent_code+ "_" + product_id+"_smartaqr.png";
        File file=new File(urlPath);    
        if(!file.exists())    
        {   
        	String access_token = SmartAqrCode.getAccessToken(
				"https://api.weixin.qq.com/cgi-bin/token", "client_credential",
				"wxf85e801e63bb2164", "9287b9eb47c7f15a112f2c82f15c80c2");
        	 URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+access_token);
             HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
             httpURLConnection.setRequestMethod("POST");// 提交模式
             // conn.setConnectTimeout(10000);//连接超时 单位毫秒
             // conn.setReadTimeout(2000);//读取超时 单位毫秒
             // 发送POST请求必须设置如下两行
             httpURLConnection.setDoOutput(true);
             httpURLConnection.setDoInput(true);
             // 获取URLConnection对象对应的输出流
             PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
             // 发送请求参数
             JSONObject paramJson = new JSONObject();
             paramJson.put("scene", "a-"+agent_code+"'b-"+product_id);
             paramJson.put("page", "pages/index/index");
             paramJson.put("width", 430);
             paramJson.put("auto_color", true);
             printWriter.write(paramJson.toString());
           //图片保存位置
              // flush输出流的缓冲
             printWriter.flush();
            //开始获取数据
             BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
             OutputStream os = new FileOutputStream(new File(urlPath));
             int len;
             byte[] arr = new byte[1024];
             while ((len = bis.read(arr)) != -1)
             {
                 os.write(arr, 0, len);
                 os.flush();
             }
             os.close();
             
          /* // 是产品地址 获取图片的流
			 BufferedImage url1 =    CircularImages.getUrlByBufferedImage("https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqoIXjSX9Nd1zIjicf7HrWiawRy50yp6saJ1PGotTHfb7LcQWYVwMsnBZff0DCq9228BDbv4f7Q3DDA/132");
			// 处理图片将其压缩成正方形的小图
			BufferedImage convertImage = CircularImages.scaleByPercentage(url1, 100, 100);
			// 裁剪成圆形 （传入的图像必须是正方形的 才会 圆形 如果是长方形的比例则会变成椭圆的）
			convertImage = CircularImages.convertCircular(url1);
			// 生成的图片位置
			String imagePath = "D:\\Tomcat7.0\\ad-kcc\\mob\\salsa\\product_photo\\product\\Imag.png";
			ImageIO.write(convertImage, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath));
			*/
             //logo图片所在位置
            String imagePath = "D:\\Tomcat7.0\\ad-kcc\\mob\\salsa\\product_photo\\product\\logo.png";

             LogoConfig logoConfig = new LogoConfig(); //LogoConfig中设置Logo的属性
	        SmartAqrCode.addLogo_QRCode(new File(urlPath), new File(imagePath), logoConfig,urlPath);
        }   
        json_result.put("result", "ok");  
		json_result.put("msg", fileUrl);
		return json_result;
	}
	public JSONObject parseAccessToken(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		JSONObject json_result = new JSONObject();
		
		String url="https://api.weixin.qq.com/cgi-bin/token";
		String grantType="client_credential";
		String appid="wxf85e801e63bb2164";
		String secret="9287b9eb47c7f15a112f2c82f15c80c2";
		String access_token = "";
			
		access_token = SmartAqrCode.getAccessToken(url, grantType,
				appid, secret);
			
		json_result.put("result", "ok");
		json_result.put("msg", access_token);
		return json_result;
		       	
	}
	public JSONObject parseOPenid(
			HttpServletRequest request,  HttpServletResponse response)
			throws Exception {
		 request.setCharacterEncoding("UTF-8");
		JSONObject json_result = new JSONObject();
		String access_token = WeChatNotice.getAccessToken("https://api.weixin.qq.com/cgi-bin/token", "client_credential",
				"wxf85e801e63bb2164", "9287b9eb47c7f15a112f2c82f15c80c2");
		String url="https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token;

		String[] str =  WeChatNotice.getValidFormId(request);
		if( str.length == 0){
			json_result.put("result", "false");
			json_result.put("msg", "发送失败,没有from_id可用");
		}
		String agent_name = WeChatNotice.getAgentNameByCode(request);
		String agent_code = request.getParameter("agent_code");
		String conStr = request.getParameter("content");
		String path = "pages/index/index?agent_code=" + agent_code;
		String template_id = "JYD4XWPQr3USuX1nhxfkcCRnSv34T0cQh83iQlMXT2k";
		String content = "";
		//System.out.println("conStr:"+conStr);
		String proname = request.getParameter("proname");
		if( ("OP0001").equals(proname) ){
			if(conStr == "" || conStr == null){
				content = "本店新发布了一些产品视频，欢迎您来观看";
			}else{
				content = conStr;
			}
		}else if( ("OP0002").equals(proname) ) {
			if(conStr == "" || conStr == null){
				content = "您有一条未读消息，请进行查看";
			}else{
				content = conStr;
			}
		}else if( ("OP0003").equals(proname) ) {
			if(conStr == "" || conStr == null){
				content = "您有一个新的预约客户，请及时进行查看";
			}else{
				content = conStr;
			}
			
		}
		
		Map keyword1 = SmartTemplate.TemplateData( content ,"#173177");
		Map keyword2 = SmartTemplate.TemplateData(agent_name,"#173177");
		Map data = new HashMap();
		data.put("keyword1", keyword1);
		data.put("keyword2", keyword2);
		String open_id = request.getParameter("open_id");
		String touser = "";
		if(open_id == ""){
			for(int i = 0; i < str.length ; i++ ){
				touser = WeChatNotice.getOPenid(request, str[i]) ;
				String form_id =  str[i];
				Map temp = SmartTemplate.WxTemplate(access_token,form_id,path, template_id, touser, data);
				JSONObject json = JSONObject.fromObject(JSON.toJSONString(temp,true));
				//System.out.println("JSON:"+json);
				//System.out.println("open_id:"+open_id);
				
				String result =  WeChatNotice.httpsRequest(url,"POST",json.toString());
	            if(result != null){  
		            com.alibaba.fastjson.JSONObject jsonResult = JSON.parseObject(result);
		            int errorCode=jsonResult.getInteger("errcode");  
		            String errorMessage=jsonResult.getString("errmsg");  
		            if(errorCode == 0){  
		    			WeChatNotice.delFromId(request, str[i]);
		            }else{            	
		            	System.out.println("错误时的json:"+json);
		                System.out.println("模板消息发送失败:"+errorCode+","+errorMessage);  
		                json_result.put("result", "false");
		    			json_result.put("msg", "发送失败"+errorCode+","+errorMessage);
		    			return json_result;
		            }  
		        }  
			}
		}else{
			touser = open_id ;
			String form_id = WeChatNotice.getFromid(request);
			Map temp = SmartTemplate.WxTemplate(access_token,form_id,path, template_id, touser, data);
			JSONObject json = JSONObject.fromObject(JSON.toJSONString(temp,true));
			//System.out.println("JSON:"+json);
			//System.out.println("open_id:"+open_id);
			String result =  WeChatNotice.httpsRequest(url,"POST",json.toString());
            if(result != null){  
	            com.alibaba.fastjson.JSONObject jsonResult = JSON.parseObject(result);
	            int errorCode=jsonResult.getInteger("errcode");  
	            String errorMessage=jsonResult.getString("errmsg");  
	            if(errorCode == 0){  
	    			WeChatNotice.delFromId(request, form_id);
	            }else{            	
	            	System.out.println("错误时的json:"+json);
	                System.out.println("模板消息发送失败:"+errorCode+","+errorMessage);  
	                json_result.put("result", "false");
	    			json_result.put("msg", "发送失败"+errorCode+","+errorMessage);
	    			return json_result;
	            }  
	        } 
		}
		
		
		json_result.put("result", "ok");
		json_result.put("msg", "发送成功");
		
		return json_result;
		       	
	}
	
	public JSONObject parseDownloadFile(String html, String kj,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String url_string=request.getParameter("url_path");
		
		JSONObject json_result = new JSONObject();

		SQLUpdater updater = DSManager.getSQLUpdater();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_UE, "KJM", kj);
			if (y_sql == null || y_sql.length == 0) {
				json_result.put("result", "fasle");
				throw new Exception("没有找到控件" + kj + "!");
			}

		} catch (Exception e) {
			json_result.put("result", "fasle");
			throw new Exception("定位" + kj + "SQL出错!" + e.toString());
		}

		String path = y_sql[0][2];
		String ue_sql = y_sql[0][3];
		String filetype = y_sql[0][4];
		String Compress_w = y_sql[0][5];
		String Compress_h = y_sql[0][6];

	
		// 相对路径
		String url = SystemFunction.class.getResource("/").getPath();
		url = url.substring(1, url.length() - 16);
		String filename="";
		URL url_str = new URL(url_string);
		DataInputStream dataInputStream = new DataInputStream(url_str.openStream());
		
		
				File newFile = null;
				//System.out.print(fi.getName().toString());
				 String houz = url_string.split("\\.")[1];
				String s_filename = java.util.UUID.randomUUID().toString()
						+ "."+houz;
				s_filename = s_filename.replace("-", "");
				 filename = path + s_filename;

				//if (fi.getName() != null && fi.getSize() != 0) {

					File fp = new File(url + path);
					// 创建目录
					if (!fp.exists()) {
						fp.mkdirs();// 目录不存在的情况下，创建目录。
					}
					newFile = new File(url + filename);

					if (!newFile.exists()) {
						newFile.createNewFile();
					}
					
					
					 FileOutputStream fileOutputStream = new FileOutputStream(newFile);
			            ByteArrayOutputStream output = new ByteArrayOutputStream();

			            byte[] buffer = new byte[1024];
			            int length;

			            while ((length = dataInputStream.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }
			            byte[] context=output.toByteArray();
			            fileOutputStream.write(output.toByteArray());
			            dataInputStream.close();
			            fileOutputStream.close();

					//fi.write(newFile);

				
				request.setAttribute("FileName", s_filename);
				request.setAttribute("FileFullName", filename);

				if (filetype.equals("图像")) {
					// 取像素
					BufferedImage bi = null;
					try {
						bi = ImageIO.read(newFile);
						int width = bi.getWidth(); // 像素
						int height = bi.getHeight(); // 像素
						request.setAttribute("Pix_W", String.valueOf(width));
						request.setAttribute("Pix_H", String.valueOf(height));
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (Compress_w.length() > 0) {
						String smallPath = url + path + "small\\";

						File fp1 = new File(smallPath);

						if (!fp1.exists()) {
							fp1.mkdirs();
						}
						ImageCompress.imageCompress(newFile, smallPath,
								s_filename, 0.5f, 1.0f,
								Integer.parseInt(Compress_w),
								Integer.parseInt(Compress_h));
					}

				}
			

		

		if (ue_sql.length() > 0) {
			String sql = HtmlFunction.parseVarAttr(ue_sql, request, "");

			try {
				updater.executeUpdate(sql);
			} catch (Exception ex) {
				System.out.println("sql=" + sql);
				ex.printStackTrace();
			}
		}
		
		json_result.put("result", "ok");
		
		

		return json_result;

	}
	
	
	
	public JSONObject getBankInfo(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String cardNo=request.getParameter("cardNo");
		
		JSONObject json_result = new JSONObject();
		
		
		Map params= new HashMap();
    	params.put("cardNo",cardNo);
    	String s=HttpUtil.doPost("https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardBinCheck=true", params);
    	Map object= JsonUtil.getMap4Json(s);
    	String bank=(String)object.get("bank");
    	//String validated=(String)object.get("validated");
    	//System.out.println("bb="+object.get("bank"));
    	
    	Map bankObject= JsonUtil.getMap4Json(BConstants.BANK);
    	String bankName=(String)bankObject.get(bank);
    	object.put("bankChnName", bankName);
    	
    	json_result=JSONObject.fromObject(object);
    	//System.out.println("bank="+bankObject);
    	//System.out.println(bankObject.get(object.get("bank")));
		
		

		return json_result;

	}
	
	
	 private  void downloadPicture(String urlList) {
	        URL url = null;
	        int imageNumber = 0;

	        try {
	            url = new URL(urlList);
	            DataInputStream dataInputStream = new DataInputStream(url.openStream());

	            String imageName =  "F:/test.jpg";

	            FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
	            ByteArrayOutputStream output = new ByteArrayOutputStream();

	            byte[] buffer = new byte[1024];
	            int length;

	            while ((length = dataInputStream.read(buffer)) > 0) {
	                output.write(buffer, 0, length);
	            }
	            byte[] context=output.toByteArray();
	            fileOutputStream.write(output.toByteArray());
	            dataInputStream.close();
	            fileOutputStream.close();
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	
	
	public static synchronized JSONObject parseModifyIN(String kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String tmpKj = kjname;
		
		JSONObject json_result = new JSONObject();
		
		kjname = kjname.substring(0, 6);// 处理控件名，控件名为6位
		String s_action = request.getParameter("action");// 判断是删除还是插入;
		SQLUpdater updater = DSManager.getSQLUpdater();
		SQLQuery query = DSManager.getSQLQuery();
		

		if (s_action == null || s_action.trim().length() == 0)
			s_action = "insert";
		// 取业务数据
		String[][] s_modifycs = new String[0][0];
		try {
			s_modifycs = XmlUtil.find(BConstants.PAGE_UP, "KJM", kjname);
			if (s_modifycs.length == 0){
				json_result.put("result", "fasle");
				json_result.put("reason", "Ajax:操作对象[" + kjname + "]不存在，操作未成功！");
				
				return json_result;
			}
				
		} catch (SQLException e) {
			json_result.put("result", "fasle");
			json_result.put("reason", "Ajax:操作对象[" + kjname + "]定位SQL出错!");
			return json_result;
			
		}
		String s_selectsql = s_modifycs[0][2], s_insertsql = s_modifycs[0][3], s_deletesql = s_modifycs[0][5], s_updatesql = s_modifycs[0][4], s_cs = s_modifycs[0][1], s_url = s_modifycs[0][6];
		String s_sftz =  s_modifycs[0][7],s_tsnr=s_modifycs[0][8];
		
		
		
		
		if (s_action.equals("delete")) {
			s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "");

			try {
				updater.executeUpdate(s_deletesql);
			} catch (SQLException e) {
				System.out.println(s_deletesql);
				json_result.put("result", "fasle");
				json_result.put("reason",  "Ajax:操作对象[" + kjname + "]删除SQL出错!SQL=" + s_deletesql);
				System.out.println("Ajax:操作对象[" + kjname + "]删除SQL出错!SQL=" + s_deletesql);
				return json_result;
				
			}

		}
		if (s_action.equals("insert")) {

			
			// 判断是否需要检测,'1'为是
			if (s_cs.equals("1")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "");
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					json_result.put("result", "fasle");
					json_result.put("reason",  "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql);
					System.out.println("Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql);
					return json_result;
					
				}
				if (checkValue.length != 0){
					json_result.put("result", "fasle");
				   json_result.put("reason",  "记录[" + checkValue[0][0] + "]已存在，操作未成功!");
				   return json_result;
				}
					
				try {
					updater.executeUpdate(s_insertsql);
				} catch (SQLException e) {
					System.out.println(s_insertsql);
					  json_result.put("result", "fasle");
					   json_result.put("reason",  "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="+ s_insertsql);
					   System.out.println("Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="+ s_insertsql);
					   return json_result;
					
				}
			}
			//// 如果检测到标志为2,则有记录就更新记录，不再提示，无记录，直接插入
			else if (s_cs.equals("2")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "");
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					json_result.put("result", "fasle");
					   json_result.put("reason",  "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql);
					   System.out.println("Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql);
					   return json_result;
					
				}
				if (checkValue.length != 0) {
					s_updatesql = HtmlFunction.parseVar(s_updatesql, request,
							"");
					updater.executeUpdate(s_updatesql);
				} else {
					updater.executeUpdate(s_insertsql);
				}

			}
			else if (s_cs.equals("3")) {
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "");
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					   json_result.put("result", "fasle");
					   json_result.put("reason",  "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql);
					   System.out.println( "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql);
					   return json_result;
					
				}
				if (checkValue.length == 0) {
					
					  json_result.put("result", "fasle");
					   json_result.put("reason",  "该订单中无此条形码的产品!");
					   return json_result;

					
				}

			}
			//插入Json数据组合订单表
			else if(s_cs.equals("4"))
			{
				String jsonData = (String)request.getParameter("d_JsonData");
				//System.out.println("json="+jsonData);
				if(jsonData == null) jsonData = "";
				if(!jsonData.equals(""))
				{
					try{
						//dsn.beginTransaction();
						
						jsonData=java.net.URLDecoder.decode(jsonData,"utf-8");
						//System.out.println("jsonutf8decode="+jsonData);
						s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "");
						updater.executeUpdate(s_deletesql);//删除明细数据
						List jsonList = JsonUtil.getList4Json(jsonData, Map.class);
						//循环插入明细数据
						for(int i=0;i<jsonList.size();i++){
							Map map=(Map)jsonList.get(i);
							map.put("xh", i);//序号存入
							String updatesql=HtmlFunction.parseVar(s_updatesql, request,"",map);
							//System.out.println(updatesql);
							try{
							updater.executeUpdate(updatesql);
							}catch(Exception ex){
								System.out.println(updatesql);
								 json_result.put("result", "fasle");
								   json_result.put("reason",   "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="
											+ updatesql);
								   return json_result;
								
							}
							
						}
						s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
						updater.executeUpdate(s_insertsql);//插入订单数据
						//dsn.endTransaction();
					}catch(Exception e){
						//dsn.rollback();
						System.out.println(s_insertsql);
						
						 json_result.put("result", "fasle");
						   json_result.put("reason",   "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="+ s_insertsql);
						   return json_result;
						
						
						
					}
				}
				
			}
			//插入Json数据表
			else if(s_cs.equals("5"))
			{
                //设置请求编码  
				//request.setCharacterEncoding("utf-8");  


				// 获取编辑数据 这里获取到的是json字符串
				String deleted = request.getParameter("deleted");
				String inserted = request.getParameter("inserted");
				String updated = request.getParameter("updated");
				
				if (deleted != null) {
					// 把json字符串转换成对象
					deleted=java.net.URLDecoder.decode(deleted,"utf-8");
					List listDeleted = JsonUtil.getList4Json(deleted, Map.class);

					for(int i=0;i<listDeleted.size();i++){
						Map map=(Map)listDeleted.get(i);
						//map.put("xh", i);//序号存入
						String deletesql=HtmlFunction.parseVar(s_deletesql, request,"", map);
						try {
							updater.executeUpdate(deletesql);
						} catch (SQLException e) {
							 json_result.put("result", "fasle");
							   json_result.put("reason",   "Json:操作对象[" + kjname + "]删除SQL出错!SQL="
										+ deletesql);
							   return json_result;
							
						}
						
						
					}
					
				}
				if (inserted != null) {
					// 把json字符串转换成对象
					inserted=java.net.URLDecoder.decode(inserted,"utf-8");
					List listInserted = JsonUtil.getList4Json(inserted, Map.class);
					for(int i=0;i<listInserted.size();i++){
						Map map=(Map)listInserted.get(i);
						//map.put("xh", i);//序号存入
						String insertsql=HtmlFunction.parseVar(s_insertsql, request,"", map);
						try {
							updater.executeUpdate(insertsql);
						} catch (SQLException e) {
							json_result.put("result", "fasle");
							 return json_result;
							//return "Json:操作对象[" + kjname + "]插入SQL出错!SQL="+ insertsql;
						}
						
					}
				}
				if (updated != null) {
					// 把json字符串转换成对象
					updated=java.net.URLDecoder.decode(updated,"utf-8");
					List listUpdated = JsonUtil.getList4Json(updated, Map.class);
					for(int i=0;i<listUpdated.size();i++){
						Map map=(Map)listUpdated.get(i);
						//map.put("xh", i);//序号存入
						String updatesql=HtmlFunction.parseVar(s_updatesql, request,"", map);
						try {
							updater.executeUpdate(updatesql);
						} catch (SQLException e) {
							json_result.put("result", "fasle");
							 return json_result;
							//return "Json:操作对象[" + kjname + "]更新SQL出错!SQL="+ updatesql;
						}
						
						
					}
				}
			}
			//手机报文插入
			else if(s_cs.equals("6"))
			{
				String jsonData = (String)request.getParameter("d_JsonData");
				if(jsonData == null) jsonData = "";
				
				if(!jsonData.equals(""))
				{
					try{
						//dsn.beginTransaction();
						
						
						
						//jsonData=java.net.URLEncoder.encode(jsonData,"utf-8");
						jsonData=java.net.URLDecoder.decode(jsonData,"utf-8");
						
						
						String[][] checkValue = new String[0][0];
						s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "");
						try {
							checkValue = query.ArrayQuery(s_selectsql);
						} catch (Exception e) {
							   json_result.put("result", "fasle");
							   json_result.put("reason",  "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql);
							   return json_result;
							
						}
						String backcode=checkValue[0][0];
						//jsonData= SystemFunction.converUTF8(jsonData);
						//System.out.println("JsonData="+jsonData);
						JSONObject myJsonObject=JSONObject.fromObject(jsonData);
						//s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "");
						//updater.executeUpdate(s_deletesql);//删除明细数据
						List jsonList = JsonUtil.getList4JsonArray("backdetails", myJsonObject);
						//循环插入明细数据
						for(int i=0;i<jsonList.size();i++){
							Map map=(Map)jsonList.get(i);
							map.put("back_code", backcode);//序号存入
							String updatesql=HtmlFunction.parseVar(s_insertsql, request,"",map);
							//System.out.println(updatesql);
							try{
							updater.executeUpdate(updatesql);
							}catch(Exception ex){
								//System.out.println(updatesql);
								json_result.put("result", "fasle");
								 return json_result;
								//return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="+ updatesql;
							}
							
						}
						
						List jsonList1 = JsonUtil.getList4JsonArray("bt_list", myJsonObject);
						//循环插入明细数据
						for(int i=0;i<jsonList1.size();i++){
							Map map=(Map)jsonList1.get(i);
							map.put("back_code", backcode);//序号存入
							String updatesql=HtmlFunction.parseVar(s_updatesql, request,"",map);
							//System.out.println(updatesql);
							try{
							updater.executeUpdate(updatesql);
							}catch(Exception ex){
								//System.out.println(updatesql);
								json_result.put("result", "fasle");
								 return json_result;
								//return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="
								//+ updatesql;
							}
							
						}
						Map jsonMap=JsonUtil.getMap4Key("backlist",myJsonObject);
						jsonMap.put("back_code", backcode);
						s_insertsql = HtmlFunction.parseVar(s_deletesql, request, "",jsonMap);
						s_url = HtmlFunction.parseVar(s_url, request, "",jsonMap);
						updater.executeUpdate(s_insertsql);//插入订单数据
						
						updater.executeCall(s_url);//计算优惠
						
						json_result.put("back_code", backcode);
						//dsn.endTransaction();
					}catch(Exception e){
						//dsn.rollback();
						System.out.println(s_insertsql);
						json_result.put("result", "fasle");
						 return json_result;
						//return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="
						//+ s_insertsql;
						
						
					}
				}
				
			}
			//分享客户
			else if(s_cs.equals("7"))
			{

				String key= GenerateSequenceUtil.generateSequenceNo();
				request.setAttribute("KEY", key);
				s_insertsql = HtmlFunction.parseVarAttr(s_insertsql, request, "");
				updater.executeUpdate(s_insertsql);
				
				if(s_sftz.equals("insert")){
					final String sql = HtmlFunction.parseVar(s_tsnr, request, "");
					Thread t=new Thread(){
					    public void run(){
					    	AppSmsAynSender a= new AppSmsAynSender();
					    	//a.setMsgContent(tsnr);
					    	
					    	a.sendMsgForSql(sql);
					   }
					};
					t.start();
					
					
					
					
				}
				
				json_result.put("back_code", key);
				json_result.put("result", "ok");
				
				return json_result;
				
				
				
				
					
			}
			//直接做插入操作
			else {
				try {
					
					s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
					
					updater.executeUpdate(s_insertsql);
					
					if(s_sftz.equals("insert")){
						final String sql = HtmlFunction.parseVar(s_tsnr, request, "");
						
						Thread t=new Thread(){
						    public void run(){
						    	AppSmsAynSender a= new AppSmsAynSender();
						    	//a.setMsgContent(tsnr);
						    	System.out.println("AppSmsAynSender:"+sql);
						    	a.sendMsgForSql(sql);
						   }
						};
						t.start();
					}
					
				
					
					if(s_sftz.equals("sms")){
						final String sql = HtmlFunction.parseVar(s_tsnr, request, "");
						
						Thread t=new Thread(){
						    public void run(){
						    	AliMsgSender a= new AliMsgSender();
						    	//System.out.println("sms sql:"+sql);
						    	//a.setMsgContent(tsnr);
						    	a.sendMsgforSql(sql);
						   }
						};
						t.start();
						
						
					}
					
				} catch (SQLException e) {
					System.out.println(s_insertsql);
					json_result.put("result", "fasle");
					 return json_result;
					//return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="+ s_insertsql;
				}
			}

		}
		if (s_action.equals("update")) {
			s_updatesql = HtmlFunction.parseVar(s_updatesql, request, "");
			try {
				updater.executeUpdate(s_updatesql);
			} catch (SQLException e) {
				 json_result.put("result", "fasle");
				 System.out.println(s_updatesql);
				 return json_result;
				 
				//return "Ajax:操作对象[" + kjname + "]更新SQL出错!SQL=" + s_updatesql;
			}
			
			if(s_sftz.equals("update")){
				final String sql = HtmlFunction.parseVar(s_tsnr, request, "");
				
				Thread t=new Thread(){
				    public void run(){
				    	AppSmsAynSender a= new AppSmsAynSender();
				    	//a.setMsgContent(tsnr);
				    	a.sendMsgForSql(sql);
				   }
				};
				t.start();
				
			}
			
			//登录环信
			if(s_sftz.equals("sm")){
				final String final_tsnr = HtmlFunction.parseVar(s_tsnr, request, "");
				/*String nickname= java.net.URLDecoder.decode(request.getParameter("nickname"),"utf-8");
				nickname = new String(nickname.getBytes("iso8859-1"), "utf-8");
				final String final_yhmc = nickname;
				*/
				String mobtel= request.getParameter("mob_tel");
				
				
				if(mobtel == null || mobtel == ""){
					mobtel= WeChatNotice.getTelByAgent_Code(request);
				}
				final String final_mobtel=mobtel;
				Thread t=new Thread(){
				    public void run(){
				    	//String chatusername=final_lxfs;
				    	
				    	try {
				    		
				    			JPush push = new JPush();
						 		   String result=push.pushObject_all_regid_alert(final_mobtel, final_tsnr,"艾来客");
						 		   if(result.equals("0")){
						 			 System.out.println("消息推送失败:"+final_mobtel+"  "+"艾来客访问客户提示！");
						 		   }
				    		
						} catch (Exception e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
				   }
				};
				t.start();
				
			}

		}

		if (s_action.equals("call")) {
			
			try {
			 if(s_cs.equals("7"))
				{

				 String key= GenerateSequenceUtil.generateSequenceNo();
					request.setAttribute("KEY", key);
					s_url = HtmlFunction.parseVarAttr(s_url, request, "");
					updater.executeCall(s_url);	
				 
					json_result.put("back_code", key);
					json_result.put("result", "ok");
					
					
					
					//return json_result;
					
				}else{
			
            
			
				  s_url = HtmlFunction.parseVar(s_url, request, "");
				   updater.executeCall(s_url);
				
				}
				
				if(s_sftz.equals("call")){
					final String sql = HtmlFunction.parseVarAttr(s_tsnr, request, "");
					
					Thread t=new Thread(){
					    public void run(){
					    	AppSmsAynSender a= new AppSmsAynSender();
					    	//a.setMsgContent(tsnr);
					    	a.sendMsgForSql(sql);
					   }
					};
					t.start();
					
					
				}
				//登录环信
				if(s_sftz.equals("sm")){
					
					final String final_lxfs = request.getParameter("open_id");
					String nickname= java.net.URLDecoder.decode(request.getParameter("nickname"),"utf-8");
					nickname = new String(nickname.getBytes("iso8859-1"), "utf-8");
					String mobtel= request.getParameter("mob_tel");
					
                    
					final String final_yhmc = nickname;
					if(mobtel == null || mobtel == ""){
						mobtel= WeChatNotice.getTelByAgent_Code(request);
					}
					final String final_mobtel=mobtel;
					Thread t=new Thread(){
					    public void run(){
					    	String chatusername=final_lxfs;
					    	//Authentic.Token TEST_TOKEN = new Authentic.Token("YWMt4EPcDvVpEeWmTm2uJUQPcwAAAVT1s8Bmn-wB5wwM9nqr6HgljAvlo79iDX8",1465203701330L);
					    	TalkDataService service = new TalkDataServiceImpl(new TalkHttpServiceImplApache());
					    	//service.setToken(TEST_TOKEN);
					    	try {
					    		String isRegedit=JsonTool.write(service.userGet(chatusername));
					    		if (isRegedit.indexOf("statusCode")!=-1)
					    		{
					    			
					    			JsonTool.write(service.userSave(chatusername,chatusername+"+kcc",final_yhmc));
					    			JPush push = new JPush();
							 		   String result=push.pushObject_all_regid_alert(final_mobtel, "艾来客给你带来了新客户！客户昵称："+final_yhmc,"艾来客");
							 		   if(result.equals("0")){
							 			 System.out.println("消息推送失败:"+final_mobtel+"  "+"艾来客给你带来了新客户！");
							 		   }
					    		}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
					   }
					};
					t.start();
					
				}
			
			
				} catch (Exception e) {
					System.out.println(s_url);
					 json_result.put("result", "fasle");
					   json_result.put("reason",  "Ajax:操作对象[" + kjname + "]执行存程过程出错!URL=" + s_url
								+ e.toString());
					   return json_result;
					
				}
			

		}
		
		
		
		
		
		json_result.put("result", "ok");
		return json_result;
	}
	
	
	public JSONObject parseDisplayWXPay(String s_kjname,
			HttpServletRequest request) throws Exception {
		
		JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
	
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return json_result;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			
			ip="192.168.3.235";
		}
		
		String mob=request.getParameter("lxfs");
		
		if(mob ==null || "".equals(mob))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "手机号码为为空");
			return json_result;
		}
       String month=request.getParameter("month");
		
		if(month ==null || "".equals(month))
		{
			month="3";
		}
		 String product=request.getParameter("product");
		 
		 if(product ==null || "".equals(product))
		{
				product="";
		}
			
		 
		String body = "艾订货";
		
		SortedMap params = new TreeMap();
		
		if(s_kjname.equals("WX0002")){
			//客串串APP支付（安卓以及苹果内侧端）
			params.put("appid","wx3abdcc7e64a1a956");
			params.put("mch_id","1403890602");
			
		}else if(s_kjname.equals("WX0003")){
			//客串串APP支付（苹果appstore端）
			params.put("appid","wx8cf04457b6293ee9");
			params.put("mch_id","1460486902");
		}
		else{
			//艾订货APP支付
			params.put("appid","wxf61ff34d243a4f2d");
			params.put("mch_id","1356421102");
		
		}
		
		/*公众号支付*/
		//params.put("appid","wxb085018f401f8f5a"); //公众号id
		//params.put("mch_id","1355185802");
		
		//params.put("device_info",""); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		params.put("nonce_str",random);
		
		params.put("body",body);
		
		//params.put("detail","");//否
		//params.put("attach","");//否
		String out_trade_no=GenerateSequenceUtil.getRandom()+'S'+mob+'M'+month+'C'+product;
		params.put("out_trade_no",out_trade_no);
		params.put("fee_type","CNY");
		params.put("total_fee",fee);
		params.put("spbill_create_ip",ip);
		//params.put("time_start","");//否
		//params.put("time_expire","");//否
		//params.put("goods_tag","");//否
		params.put("notify_url","http://ad-kcc.dderp.cn/mob/weixinmonitor");
		params.put("trade_type","APP");
		//params.put("limit_pay","");//否
		String sign=MD5Util.createSign("UTF-8", params);
		params.put("sign",sign);
		
		String requestXML = HttpUtil.getRequestXml(params);
		
		//System.out.println("wx pay result:"+requestXML);
		
		String result =HttpUtil.httpsRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", requestXML);
		
		System.out.println("response:"+result);
		
		
		
		
		SortedMap map = new TreeMap();
		
		try {
			 map = HttpUtil.doXmlParse(result);
			 json_result.put("result", "ok");
			 Iterator it=map.keySet().iterator();    
			 while(it.hasNext()){    
			     String key;    
			     String value;    
			     key= it.next().toString();    
			     value= map.get(key).toString(); 
			    // json_result.put(key, value);
			}
			//String mysign = MD5Util.createSign("UTF-8", params);
			//json_result.put("sign", mysign);
			json_result.put("out_trade_no",out_trade_no);
			
			
			SortedMap mapkey = new TreeMap();
			mapkey.put("appid", (String)map.get("appid"));
			mapkey.put("partnerid", (String)map.get("mch_id"));
			mapkey.put("prepayid", (String)map.get("prepay_id"));
			mapkey.put("package", "Sign=WXPay");
			
			mapkey.put("noncestr",(String)map.get("nonce_str") );
			
			mapkey.put("timestamp", String.valueOf(System.currentTimeMillis()).substring(0, 10));
			
			String paysign=MD5Util.createSign("UTF-8", mapkey);
			mapkey.put("sign", paysign);
			
			
			//System.out.println("dd="+mapkey);
			 Iterator it1=mapkey.keySet().iterator();    
			 while(it1.hasNext()){    
			     String key1;    
			     String value1;    
			     key1= it1.next().toString();   
			    // System.out.println("dfd="+key1);
			     value1= mapkey.get(key1).toString(); 
			     json_result.put(key1, value1);
			}
			

		} catch (UnsupportedEncodingException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
        
		//System.out.println("wx pay result:"+res);

		
		return json_result;
	}
	
	
	
	
	//高汇通快捷支付
	public JSONObject parseDisplayGHTQuickPay(String s_kjname,
			HttpServletRequest request) throws Exception {
		
		JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
	
	
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return json_result;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			json_result.put("result", "fail");
			json_result.put("reason", "ip地址为空");
			return json_result;
		}
		String child_merchant_no=request.getParameter("child_merchant_no");
		if(child_merchant_no == null || "".equals(child_merchant_no)){
			json_result.put("result", "fail");
			json_result.put("reason", "子商户号为空，请检查是否开通！");
			return json_result;
		}
		String agent_code=request.getParameter("agent_code");
		if(agent_code == null || "".equals(agent_code)){
			json_result.put("result", "fail");
			json_result.put("reason", "店铺编号为空，无法支付！");
			return json_result;
		}
		String qz=request.getParameter("http_mob_qz");
		if(qz == null || "".equals(qz)){
			json_result.put("result", "fail");
			json_result.put("reason", "HTTP前缀为空，无法支付！");
			return json_result;
		}
		
		
		
		String mob=request.getParameter("lxfs");
		
		if(mob ==null || "".equals(mob))
		{
			//json_result.put("result", "fail");
			//json_result.put("reason", "手机号码为为空");
			//return json_result;
			mob="";
		}
		/* String month=request.getParameter("month");
		
		if(month ==null || "".equals(month))
		{
			month="3";
			//json_result.put("result", "fail");
			//json_result.put("reason", "购买月份为空");
			//return json_result;
		}*/
		String body = "艾订货";
		
		
		SortedMap params = new TreeMap();
	
		
		/*公众号支付*/
		//params.put("appid","wxb085018f401f8f5a"); //公众号id
		//params.put("mch_id","1355185802");
		
		//params.put("device_info",""); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		String out_trade_no=GenerateSequenceUtil.getRandom()+"A"+agent_code;
		params.put("version","2.0.0");
		 params.put("busi_code","PRE_PAY");
		 
		 if(s_kjname.equals("GHT001")){
			 //测试用
			 //params.put("merchant_no","102100000125");//商户号
			 //params.put("terminal_no","20000147");
			 params.put("merchant_no","549440148160099");//商户号
			 params.put("terminal_no","21671824");
			 params.put("child_merchant_no", child_merchant_no);
			 //正式用
			// params.put("merchant_no","102100000125");//商户号
			// params.put("terminal_no","20000147");
		 }else if(s_kjname.equals("GHT002")){
			 params.put("merchant_no","549440148160099");//商户号
			 params.put("terminal_no","21671824");
			 params.put("child_merchant_no", child_merchant_no);
			
			// params.put("child_merchant_no","18616989375");
			    //params.put("split","");
				//params.put("user_id","");
			 
		 }
		   
			
			
			
		 
		    params.put("user_id",mob);
			params.put("order_no",out_trade_no);
			params.put("amount",fee);
			params.put("currency_type","CNY");
			params.put("sett_currency_type","CNY");
			params.put("product_name",body);
			
			params.put("product_desc","KY301板子");
			//params.put("product_type","");
			
			
			params.put("user_name","");
			
			//params.put("user_cert_type","");
			//params.put("user_cert_no","");
			//params.put("user_bank_card_no","");
			//params.put("user_mobile","");
			params.put("return_url",qz+"/weixinmonitor");
		    
			params.put("notify_url",qz+"/weixinmonitor");
			
			//params.put("user_cert_type","");
			//params.put("user_cert_no","");
			
			params.put("client_ip",ip);
			//params.put("bank_code","ABCQBY");
			params.put("bank_code","SMALL_QBY");
			
			 
			params.put("base64_memo",body);
			
			params.put("sign_type","SHA256");
			params.put("access_type","2");
			//params.put("key",ght_Key);
						
		    //params.put("reserved1","");
			//params.put("reserved2","");
			//params.put("reserved3","");
		
		
		
		
	   
		String sign=MD5Util.createSHA256Sign("UTF-8", params);
		params.put("sign",sign);
		
		//String requestXML = HttpUtil.getRequestXml(params);
		
		//System.out.println("wx pay result:"+requestXML);
		
		//String result =HttpUtil.httpsRequest("http://test.pengjv.com/backStageEntry.do", "POST", null);
		//测试用的地址
		//String result =HttpUtil.sendPost("http://test.pengjv.com/backStageEntry.do", params);
		//生产的地址
		//String result =HttpUtil.sendPost("https://epay.gaohuitong.com/backStageEntry.do", params);
		//小额快捷支付
		String result =HttpUtil.sendPost("https://pg.sicpay.com/backStageEntry.do", params);
		
		
		System.out.println("response:"+result);
		String change_amount="0.1";
        String change_rate="0.0075";
        
        float f_change=Float.parseFloat(fee)*Float.parseFloat(change_rate);
        
        if(f_change>0.1)
        {
        	change_amount = String.valueOf(f_change);
        }
		SortedMap map = new TreeMap();
		
		map = HttpUtil.doGHTQuickXmlParse(result);
		map.put("amount", fee);
		map.put("change_amount", change_amount);
		map.put("change_rate", change_rate);
		json_result = json_result.fromObject(map);
		

		
		return json_result;
	}
	
	
	//甬易支付
		public JSONObject parseDisplayYYZFQuickPay(String s_kjname,
				HttpServletRequest request) throws Exception {
			
			JSONObject json_result = new JSONObject();
			String fee = request.getParameter("fee");
		
		
			if(fee ==null || "".equals(fee))
			{
				json_result.put("result", "fail");
				json_result.put("reason", "费用为空");
				return json_result;
			}
			String MSMerchantIdB = request.getParameter("child_merchant_no");//子商户号
			if(MSMerchantIdB == null || "".equals(MSMerchantIdB)){
				json_result.put("result", "fail");
				json_result.put("reason", "子商户号为空，请检查是否开通！");
				return json_result;
			}
			String agent_code = request.getParameter("agent_code");
			if(agent_code == null || "".equals(agent_code)){
				json_result.put("result", "fail");
				json_result.put("reason", "店铺编号为空，无法支付！");
				return json_result;
			}
			String qz=request.getParameter("http_mob_qz");
			if(qz == null || "".equals(qz)){
				json_result.put("result", "fail");
				json_result.put("reason", "HTTP前缀为空，无法支付！");
				return json_result;
			}
			
			
			
			String lxfs=request.getParameter("lxfs");
			
			if(lxfs ==null || "".equals(lxfs))
			{
				json_result.put("result", "fail");
				json_result.put("reason", "手机号码为为空");
				return json_result;
			}
			
			
			
			SortedMap params = new TreeMap();
		
			String url ="http://ad-idh.dderp.cn/";
			
			String random=java.util.UUID.randomUUID().toString();
			random = random.replace("-", "");
			String orderNo=GenerateSequenceUtil.getRandom()+"A"+agent_code;
			
			 
			Double cny = Double.parseDouble(fee);//    这个是转为double类型	
			DecimalFormat df = new DecimalFormat("0.00"); 	
			String CNY = df.format(cny);
				
				String merchantId = "M100002653";
				String orderAmt = df.format(cny);
				String curType = "CNY";
				String bankId = "888C";
				String returnURL = qz+"/yyzfmonitorurl";
				String notifyURL = qz+"/yyzfmonitor";
				//String notifyURL = "https://www.baidu.com";
				String remark = "艾订货";
				String cardType = "X";
				String userId = lxfs;    //商城登记的发起支付交易的用户号（可传手机号）
				String goodsType = "0";   //0-实体商品,1-虚拟商品
				String isBind = "0";    //值为1时需要商户送客户的卡信息
				String mobile = "";
				String certNo = "";
				String userName = "";
				String cardNo = "";
				
			
				String bankType = "";
				String returnFlag = "1";
				String subAppid = "";
				String subUserid = "";
			//	String MSMerchantIdB = "00461750"; 
				 
				String holdAmt="0.1";
		        String change_rate="0.0075";
		        
		        float f_change=Float.parseFloat(fee)*Float.parseFloat(change_rate);
		        
		        if(f_change>0.1)
		        {
		        	holdAmt = String.valueOf(f_change);
		        }
			
				
				
				String  subType = "1";
				

				JSONObject map = new JSONObject() ;
				 map.put("merchantId",URLDecoder.decode(merchantId,"UTF-8"));
				 map.put("orderNo", URLDecoder.decode(orderNo,"UTF-8"));
				 map.put("orderAmt", URLDecoder.decode(orderAmt,"UTF-8"));
				 map.put("curType", URLDecoder.decode(curType,"UTF-8"));
				 map.put("bankId", URLDecoder.decode(bankId,"UTF-8"));
				 map.put("returnURL", URLDecoder.decode(returnURL,"UTF-8"));
				 map.put("notifyURL", URLDecoder.decode(notifyURL,"UTF-8"));
				 map.put("remark", URLDecoder.decode(remark,"UTF-8"));
				 map.put("cardType", URLDecoder.decode(cardType,"UTF-8"));
				 map.put("userId", URLDecoder.decode(userId,"UTF-8"));
				 map.put("goodsType", URLDecoder.decode(goodsType,"UTF-8"));
				 map.put("isBind", URLDecoder.decode(isBind,"UTF-8"));
				 map.put("mobile", URLDecoder.decode(mobile,"UTF-8"));
				 map.put("certNo", URLDecoder.decode(certNo,"UTF-8"));
				 map.put("userName", URLDecoder.decode(userName,"UTF-8"));
				 map.put("cardNo", URLDecoder.decode(cardNo,"UTF-8"));
				 map.put("bankType", URLDecoder.decode(bankType,"UTF-8"));
				 map.put("returnFlag", URLDecoder.decode(returnFlag,"UTF-8"));
				 

				 map.put("MSMerchantIdB", URLDecoder.decode(MSMerchantIdB,"UTF-8"));
				 map.put("holdAmt", URLDecoder.decode(holdAmt,"UTF-8"));
				 map.put("subType", URLDecoder.decode(subType,"UTF-8"));
				 map.put("subNo", URLDecoder.decode(MSMerchantIdB,"UTF-8"));
				 
				 
				 map.put("subAppid", URLDecoder.decode(subAppid,"UTF-8"));
				 map.put("subUserid", URLDecoder.decode(subUserid,"UTF-8"));
			
				 String sourceData = XMLHandler.getRequestXml(map);
				 
				 System.out.println("甬易请求sourceData:"+sourceData);
				 JSONObject json=YYZF.getTranData(sourceData,"CERT","" );

					json.put("reason", url+"/payOrder.html");
				 json.put("result", "ok");
			return json;
		}
		
	
	
	
	public JSONObject parseDisplayWXJSAPIPay(String s_kjname,
			HttpServletRequest request) throws Exception {
		
		JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
		String openId=request.getParameter("open_Id");
		String agent_code=request.getParameter("agent_code");
		String product=request.getParameter("product");
		
		if(agent_code ==null || "".equals(agent_code))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "AgentCode无法获取");
			return json_result;
		}
		if(openId ==null || "".equals(openId))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "OPENID无法获取");
			return json_result;
		}
	
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return json_result;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			json_result.put("result", "fail");
			json_result.put("reason", "ip地址为空");
			return json_result;
		}
		if(product ==null || "".equals(product))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "产品不能为空");
			return json_result;
			//trans_code="";
		}
		 String month=request.getParameter("month");
			
			if(month ==null || "".equals(month))
			{
				
				json_result.put("result", "fail");
				json_result.put("reason", "购买月份为空");
				return json_result;
			}
		String body = "艾来客产品";
		
		SortedMap params = new TreeMap();
		if(s_kjname.equals("WJ0002")){
			params.put("appid","wxf85e801e63bb2164");
		}else
		  params.put("appid","wxb085018f401f8f5a"); //公众号id
		params.put("mch_id","1355185802");
		//params.put("device_info",""); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		params.put("nonce_str",random);
		
		params.put("body",body);
		
		//params.put("detail","18620412503以艾围家18620412503company梦天木门上海市普陀区真北路1108号上海上海市普陀区e10adc3949ba59abbe56e057f20f883e门窗玻璃09:0017:00121.3848831.23766上海市普陀区真北路1108号门好好学习monthoAYH94zJEEwqNURkqSP5sqQWq8RA");//否
		//params.put("attach","18620412503以艾围家18620412503company梦天木门上海市普陀区真北路1108号上海上海市普陀区e10adc3949ba59abbe56e057f20f883e门窗玻璃09:0017:00121.3848831.23766上海市普陀区真北路1108号门好好学习monthoAYH94zJEEwqNURkqSP5sqQWq8RA");//否
		
		String out_trade_no=GenerateSequenceUtil.getRandom()+'S'+agent_code+'M'+month+'C'+product;
		params.put("out_trade_no",out_trade_no);
		params.put("fee_type","CNY");
		params.put("total_fee",fee);
		params.put("spbill_create_ip",ip);
		//params.put("time_start","");//否
		//params.put("time_expire","");//否
		//params.put("goods_tag","");//否
		params.put("notify_url","http://ad-kcc.dderp.cn/mob/weixinmonitor");
		params.put("trade_type","JSAPI");
		//params.put("openid","oxhqXjj6njtlsKRB0hg2TMw4vCoM");
		params.put("openid",openId);
		
		//params.put("limit_pay","");//否
		
		String sign=MD5Util.createSign("UTF-8", params);
		params.put("sign",sign);
		
		String requestXML = HttpUtil.getRequestXml(params);
		
		//System.out.println("wx pay result:"+requestXML);

		
		String result =HttpUtil.httpsRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", requestXML);
		
		//System.out.println("response:"+result);
		
		
		
		
		SortedMap map = new TreeMap();
		
		try {
			 map = HttpUtil.doXmlParse(result);
			 //json_result.put("result", "ok");
			 Iterator it=map.keySet().iterator();    
			 while(it.hasNext()){    
			     String key;    
			     String value;    
			     key= it.next().toString();    
			     value= map.get(key).toString(); 
			    // json_result.put(key, value);
			}
			//String mysign = MD5Util.createSign("UTF-8", params);
			//json_result.put("sign", mysign);
			//json_result.put("out_trade_no",out_trade_no);
			
			
			SortedMap mapkey = new TreeMap();
			mapkey.put("appId", (String)map.get("appid"));
			mapkey.put("timeStamp", String.valueOf(System.currentTimeMillis()).substring(0, 10));
			mapkey.put("nonceStr",(String)map.get("nonce_str") );
			mapkey.put("package", "prepay_id="+ (String)map.get("prepay_id"));
			mapkey.put("signType","MD5");
			String paysign=MD5Util.createSign("UTF-8", mapkey);
			mapkey.put("paySign", paysign);
			
			
			//System.out.println("dd="+mapkey);
			 Iterator it1=mapkey.keySet().iterator();    
			 while(it1.hasNext()){    
			     String key1;    
			     String value1;    
			     key1= it1.next().toString();   
			    // System.out.println("dfd="+key1);
			     value1= mapkey.get(key1).toString(); 
			     json_result.put(key1, value1);
			}
			

		} catch (UnsupportedEncodingException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (DocumentException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
				
        
		//System.out.println("wx pay result:"+res);

		
		return json_result;
	}
	
	/**
	 * 处理微信H5支付
	 * @param s_kjname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseDisplayWXH5Pay(String s_kjname,
			HttpServletRequest request) throws Exception {
		
		JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
		//String openId=request.getParameter("open_Id");
		String agent_code=request.getParameter("agent_code");
		
		if(agent_code ==null || "".equals(agent_code))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "AgentCode无法获取");
			return json_result;
		}
		/*if(openId ==null || "".equals(openId))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "OPENID无法获取");
			return json_result;
		}*/
	
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return json_result;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			json_result.put("result", "fail");
			json_result.put("reason", "ip地址为空");
			return json_result;
		}
		String body = "客串串充值";
		
		SortedMap params = new TreeMap();
		params.put("appid","wxb085018f401f8f5a"); //公众号id
		params.put("mch_id","1355185802");
		params.put("device_info","WEB"); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		params.put("nonce_str",random);
		
		params.put("body",body);
		
		//params.put("detail","");//否
		//params.put("attach","");//否
		
		String out_trade_no="PY"+GenerateSequenceUtil.generateSequenceNo()+"H"+agent_code;
		params.put("out_trade_no",out_trade_no);
		params.put("fee_type","CNY");
		params.put("total_fee",fee);
		params.put("spbill_create_ip",ip);
		//params.put("time_start","");//否
		//params.put("time_expire","");//否
		//params.put("goods_tag","");//否
		params.put("notify_url","http://ad-kcc.dderp.cn/mob/weixinmonitor");
		params.put("trade_type","MWEB");
		//params.put("openid","oxhqXjj6njtlsKRB0hg2TMw4vCoM");
		//params.put("openid",openId);
		
		//params.put("limit_pay","");//否
		
		String sign=MD5Util.createSign("UTF-8", params);
		params.put("sign",sign);
		
		String requestXML = HttpUtil.getRequestXml(params);
		
		//System.out.println("wx pay result:"+requestXML);
		
		String result =HttpUtil.httpsRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", requestXML);
		
		System.out.println("response:"+result);
		
		
		
		
		SortedMap map = new TreeMap();
		
		try {
			 map = HttpUtil.doXmlParse(result);
			 //json_result.put("result", "ok");
			 Iterator it=map.keySet().iterator();    
			 while(it.hasNext()){    
			     String key;    
			     String value;    
			     key= it.next().toString();    
			     value= map.get(key).toString(); 
			    // json_result.put(key, value);
			}
			//String mysign = MD5Util.createSign("UTF-8", params);
			//json_result.put("sign", mysign);
			//json_result.put("out_trade_no",out_trade_no);
			
			
			/*SortedMap mapkey = new TreeMap();
			mapkey.put("appId", (String)map.get("appid"));
			mapkey.put("timeStamp", String.valueOf(System.currentTimeMillis()).substring(0, 10));
			mapkey.put("nonceStr",(String)map.get("nonce_str") );
			mapkey.put("package", "prepay_id="+ (String)map.get("prepay_id"));
			mapkey.put("signType","MD5");
			String paysign=MD5Util.createSign("UTF-8", mapkey);
			mapkey.put("paySign", paysign);
			
			
			//System.out.println("dd="+mapkey);
			 Iterator it1=mapkey.keySet().iterator();    
			 while(it1.hasNext()){    
			     String key1;    
			     String value1;    
			     key1= it1.next().toString();   
			    // System.out.println("dfd="+key1);
			     value1= mapkey.get(key1).toString(); 
			     json_result.put(key1, value1);
			}*/
			

		} catch (UnsupportedEncodingException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (DocumentException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
				
        
		//System.out.println("wx pay result:"+res);

		
		return json_result;
	}
	
	/**
	 * 微信扫码支付
	 * @param s_kjname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	
	public JSONObject parseDisplayWSPay(String s_kjname,
			HttpServletRequest request) throws Exception {
		
		JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
		//String openId=request.getParameter("open_Id");
		String lxfs=request.getParameter("lxfs");
		
		if(lxfs ==null || "".equals(lxfs))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "手机号码为空");
			return json_result;
		}
		
	
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return json_result;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			json_result.put("result", "fail");
			json_result.put("reason", "ip地址为空");
			return json_result;
		}
		String body = "客串串产品";
		
		SortedMap params = new TreeMap();
		params.put("appid","wxb085018f401f8f5a"); //公众号id
		params.put("mch_id","1355185802");
		//params.put("device_info",""); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		params.put("nonce_str",random);
		
		params.put("body",body);
		
		//params.put("detail","");//否
		//params.put("attach","");//否
		
		String out_trade_no="PY"+GenerateSequenceUtil.generateSequenceNo()+"H"+lxfs;
		params.put("out_trade_no",out_trade_no);
		params.put("fee_type","CNY");
		params.put("total_fee",fee);
		params.put("spbill_create_ip",ip);
		//params.put("time_start","");//否
		//params.put("time_expire","");//否
		//params.put("goods_tag","");//否
		params.put("notify_url","http://ad-kcc.dderp.cn/mob/weixinmonitor");
		params.put("trade_type","NATIVE");
		//params.put("openid","oxhqXjj6njtlsKRB0hg2TMw4vCoM");
		params.put("product_id",fee); //商品ID
		
		//params.put("limit_pay","");//否
		
		String sign=MD5Util.createSign("UTF-8", params);
		params.put("sign",sign);
		
		String requestXML = HttpUtil.getRequestXml(params);
		
		//System.out.println("wx pay result:"+requestXML);
		
		String result =HttpUtil.httpsRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", requestXML);
		
		//System.out.println("response:"+result);
		
		
		
		
		SortedMap map = new TreeMap();
		
		try {
			 map = HttpUtil.doXmlParse(result);
			 //json_result.put("result", "ok");
			 Iterator it=map.keySet().iterator();    
			 while(it.hasNext()){    
			     String key;    
			     String value;    
			     key= it.next().toString();    
			     value= map.get(key).toString(); 
			    // json_result.put(key, value);
			}
			//String mysign = MD5Util.createSign("UTF-8", params);
			//json_result.put("sign", mysign);
			//json_result.put("out_trade_no",out_trade_no);
			
			
			SortedMap mapkey = new TreeMap();
			mapkey.put("appId", (String)map.get("appid"));
			mapkey.put("timeStamp", String.valueOf(System.currentTimeMillis()).substring(0, 10));
			mapkey.put("nonceStr",(String)map.get("nonce_str") );
			mapkey.put("package", "prepay_id="+ (String)map.get("prepay_id"));
			mapkey.put("signType","MD5");
			mapkey.put("code_url",(String)map.get("code_url") );
			String paysign=MD5Util.createSign("UTF-8", mapkey);
			mapkey.put("paySign", paysign);
			
			
			//System.out.println("dd="+mapkey);
			 Iterator it1=mapkey.keySet().iterator();    
			 while(it1.hasNext()){    
			     String key1;    
			     String value1;    
			     key1= it1.next().toString();   
			    // System.out.println("dfd="+key1);
			     value1= mapkey.get(key1).toString(); 
			     json_result.put(key1, value1);
			}
			

		} catch (UnsupportedEncodingException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (DocumentException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
				
        
		//System.out.println("wx pay result:"+res);

		
		return json_result;
	}
	

	
	
	public JSONObject parseDisplayWXEnterPricePay(String s_kjname,
			HttpServletRequest request) throws Exception {
		
		JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
	    /*String agentName = request.getParameter("agent_name");
	    
	    if(agentName ==null || "".equals(agentName))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "店铺名称为空");
			return json_result;
		}
	    
	    try {
	    	agentName =java.net.URLDecoder.decode(agentName,"utf-8");
		} catch (UnsupportedEncodingException e) {
			//value="";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	    
	    //System.out.println("店铺编码："+agentName);
	    
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return json_result;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			json_result.put("result", "fail");
			json_result.put("reason", "ip地址为空");
			return json_result;
		}
		String body = "客串串提现";
		
		SortedMap params = new TreeMap();
		
		
		params.put("mch_appid","wxb085018f401f8f5a");
		params.put("mchid","1355185802");  
		
		//params.put("device_info",""); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		params.put("nonce_str",random);
		
	   //params.put("body",body);
		
		//params.put("detail","");//否
		//params.put("attach","");//否
		String out_trade_no="ZZ"+GenerateSequenceUtil.generateSequenceNo();
		params.put("partner_trade_no",out_trade_no);
		//String openid="oxhqXjj6njtlsKRB0hg2TMw4vCoM";
		String openid = request.getParameter("open_id");
		params.put("openid",openid);
		
		params.put("check_name","NO_CHECK");
		//params.put("re_user_name","尹慧华");
		
		
		
		
		//agentName="法狮龙吊顶杭州专卖店";
		params.put("amount",fee); //转账金额分
		params.put("desc","来自【客串串】钱包 的提现，用【客串串】推送订单，获取零花钱！");
		
		params.put("spbill_create_ip",ip);
		
		String sign=MD5Util.createSign("UTF-8", params);
		params.put("sign",sign);
		
		String requestXML = HttpUtil.getRequestXml(params);
		
		//System.out.println("wx pay result:"+requestXML);
		
		String result =HttpUtil.httpsRequestForCer("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", "POST", requestXML);
		
		System.out.println("response:"+result);
		if(result!=null&&!"".equals(result)){
		Map resultMap = XMLUtil.doXMLParse(result);
		
		/*Iterator it = resultMap.keySet().iterator();
		while(it.hasNext()){
			String key;
			String value;
			key = it.next().toString();
			value = resultMap.get(key).toString();
			json_result.put(key, value);
			//System.out.println(key+":"+value);
		}
		*/
		
		if ( resultMap.get("return_code").toString().equalsIgnoreCase("SUCCESS") ){
			if(resultMap.get("result_code").toString().equalsIgnoreCase("SUCCESS") )
			{
				json_result.put("result", "ok");
			    json_result.put("payment_no", resultMap.get("payment_no").toString());
			}
			else{
				json_result.put("result", "fail");
			    json_result.put("reason", resultMap.get("err_code_des").toString());
			}
			    
			
		}
		
		}
			
			
		
		//System.out.println("wx pay result:"+res);

		
		return json_result;
	}
	
	public JSONObject parseDisplayWXRedPackPay(String s_kjname,
			HttpServletRequest request) throws Exception {
		
		JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
	
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return json_result;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			json_result.put("result", "fail");
			json_result.put("reason", "ip地址为空");
			return json_result;
		}
		//String body = "KY301扣板";
		
		SortedMap params = new TreeMap();
		params.put("wxappid","wxb085018f401f8f5a");
		params.put("mch_id","1355185802");  
		
		//params.put("device_info",""); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		params.put("nonce_str",random);
		
	   //params.put("body",body);
		
		//params.put("detail","");//否
		//params.put("attach","");//否
		String out_trade_no="1355185802"+DateHelper.getShortDate()+GenerateSequenceUtil.getRandom();
		params.put("mch_billno",out_trade_no);
		
		params.put("send_name","法狮龙吊顶");
		
		String openid="oxhqXjj6njtlsKRB0hg2TMw4vCoM";
		params.put("re_openid",openid);
		
		//params.put("check_name","NO_CHECK");
		//params.put("re_user_name","尹慧华");
		
		
		
		
		
		params.put("total_amount",fee); //转账金额分
		params.put("total_num","1"); //转账金额分
		
		params.put("wishing","感谢你给我店分享客户，你分享的客户已成交，再次感谢您的参与");
		
		params.put("client_ip",ip);
		
		params.put("act_name","分享客户送红包活动");
		
		params.put("remark","分享越多，打赏越多，大家动起来");
		
		
		String sign=MD5Util.createSign("UTF-8", params);
		params.put("sign",sign);
		
		String requestXML = HttpUtil.getRequestXml(params);
		
		System.out.println("wx pay result:"+requestXML);
		
		String result =HttpUtil.httpsRequestForCer("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack", "POST", requestXML);
		
		System.out.println("response:"+result);
		if(result!=null&&!"".equals(result)){
			Map resultMap = XMLUtil.doXMLParse(result);
			json_result.put("result", "ok");
			Iterator it = resultMap.keySet().iterator();
			while(it.hasNext()){
				String key;
				String value;
				key = it.next().toString();
				value = resultMap.get(key).toString();
				json_result.put(key, value);
				System.out.println(key+":"+value);
			}
			}
		
		
		
		/*SortedMap map = new TreeMap();
		
		try {
			 map = HttpUtil.doXmlParse(result);
			 json_result.put("result", "ok");
			 Iterator it=map.keySet().iterator();    
			 while(it.hasNext()){    
			     String key;    
			     String value;    
			     key= it.next().toString();    
			     value= map.get(key).toString(); 
			    // json_result.put(key, value);
			}
			//String mysign = MD5Util.createSign("UTF-8", params);
			//json_result.put("sign", mysign);
			json_result.put("out_trade_no",out_trade_no);
			
			
			SortedMap mapkey = new TreeMap();
			mapkey.put("appid", (String)map.get("appid"));
			mapkey.put("partnerid", (String)map.get("mch_id"));
			mapkey.put("prepayid", (String)map.get("prepay_id"));
			mapkey.put("package", "Sign=WXPay");
			
			mapkey.put("noncestr",(String)map.get("nonce_str") );
			
			mapkey.put("timestamp", String.valueOf(System.currentTimeMillis()).substring(0, 10));
			
			String paysign=MD5Util.createSign("UTF-8", mapkey);
			mapkey.put("sign", paysign);
			
			
			//System.out.println("dd="+mapkey);
			 Iterator it1=mapkey.keySet().iterator();    
			 while(it1.hasNext()){    
			     String key1;    
			     String value1;    
			     key1= it1.next().toString();   
			    // System.out.println("dfd="+key1);
			     value1= mapkey.get(key1).toString(); 
			     json_result.put(key1, value1);
			}
			

		} catch (UnsupportedEncodingException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (DocumentException e) {
			json_result.put("result", "fail");
			json_result.put("reason", e.toString());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}*/
				
        
		//System.out.println("wx pay result:"+res);

		
		return json_result;
	}
	

	public String parseDisplayEX(String s_html, String kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String s_t_url = "downLoad?proname=";
		String s_urlstring;
		String s_url = "";// + request.getParameter("proname");
		HttpSession session=request.getSession(true);
		boolean b = request.getMethod().equals("POST");
		String s_queryString = request.getQueryString() == null ? "" : request
				.getQueryString();
		s_queryString = SystemFunction.replace(s_queryString, "%20", " ");
		s_queryString = SystemFunction.converGB(s_queryString);// 中文转换

		Enumeration em_params = request.getParameterNames();
		String s_param, s_value;
		while (em_params.hasMoreElements()) {
			s_param = (String) em_params.nextElement();
			if (!b) {
				s_value = HtmlFunction.getParam(s_queryString, s_param);
			} else {
				s_value = request.getParameter(s_param);
			}

			if (s_value != null && s_value.trim().length() != 0
					&& !s_param.equals("proname")) {
				s_url += "&" + s_param + "=" + s_value;
			}
		}

		s_url += "&inexcel=1";
		s_urlstring = "<input type=button name=exportE id=exportE value=导出到EXCEL onclick=\"window.location.href='"
				+ s_t_url + kjname + s_url + "'\">"
				+"\r\n <script language=\"javascript\">"
				+ "<!-- \r\n"
				+ "function go() \r\n"
				+ "{  var dj=\""+SystemFunction.null2SpaceString((String)session.getAttribute("LS.QXSH"))+"\";"
                + "  if (dj.indexOf('A04')== -1){document.getElementById('exportE').disabled = true;}"
				+ "} \n"
				+ "go()"
				+ "//--> \n"
				+ "</script> \n";
		// + "<a href=\"javascript:window.location.href='"
		// + s_t_url+kjname+s_url
		// + "';\">【转到EXCEL】</a>";
		if (request.getParameter("inexcel") == null
				|| !request.getParameter("inexcel").equals("1")) {
			s_html = SystemFunction.replace(s_html, "$$" + kjname + ",",
					s_urlstring);
			// 当非excel中下显示页面时单元格有时不显示的问题将页面替换掉下面两种style
			s_html = SystemFunction
					.replace(
							s_html,
							" class=\"tablestyle\"",
							" borderColor=\"#ffffff\" borderColorDark=\"#ffffff\" borderColorLight=\"#006600\" ");
			s_html = SystemFunction.replace(s_html, " class=\"tdstyle1\"", "");
		} /*
			 * else { //在Excel中显示
			 * response.setContentType("application/vnd.ms-excel;charset=gb2312");
			 * s_html = SystemFunction.replace(s_html, "content=\"text/html;
			 * charset=gb2312\"",
			 * "content=\"application/vnd.ms-excel;charset=gb2312\""); s_html =
			 * SystemFunction.replace(s_html, "$$" + kjname + ",", ""); }
			 */
		return s_html;

	}

	/**
	 * 生成数据库唯一键值
	 * 
	 */
	public String parseGenSeq(String s_html, String kjname) throws Exception {

		SQLQuery query;
		String seqId[][];
		try {
			query = DSManager.getSQLQuery();
			seqId = query.ArrayQuery("select abs(checksum(newid())) SeqId");
		} catch (SQLException e) {
			throw new Exception("处理" + kjname + "出错!\n" + e.toString());

		}

		String seqIdValue = seqId[0][0];

		s_html = SystemFunction
				.replace(s_html, "$$" + kjname + ",", seqIdValue);

		return s_html;

	}

	public void downLoadTxt(HttpServletRequest request,
			HttpServletResponse response, ServletConfig config)
			throws Exception {

		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "\\basicdata.txt";
		SQLQuery query;
		String data[][];
		query = DSManager.getSQLQuery();
		data = query
				.ArrayQuery("select  Product_txm, product_code from product_list where len(product_txm) >0   order by Product_txm");
		int colLen[] = { 30, 20 };
		SystemFunction.data2TxtFile(data, path, colLen, " ");
		// 新建一个SmartUpload对象
		SmartUpload su = new SmartUpload();
		// 初始化

		su.initialize(config, request, response);
		// 设定contentDisposition为null以禁止浏览器自动打开文件，
		// 保证点击链接后是下载文件。若不设定，则下载的文件扩展名为
		// doc时，浏览器将自动用word打开它。扩展名为pdf时，
		// 浏览器将用acrobat打开。
		su.setContentDisposition(null);
		// 下载文件

		su.downloadFile(path);

	}

	/**
	 * 下载数据
	 * 
	 */
	public static void downLoadExcel(String s_kjname,
			HttpServletRequest request, HttpServletResponse response,
			ServletConfig config) throws Exception {

		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", "MJ"
					+ s_kjname.substring(2));
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("没有找到控件" + s_kjname + "!");
			}

		} catch (Exception e) {
			throw new Exception("定位" + s_kjname + "SQL出错!" + e.toString());
		}
		String cs = y_sql[0][2].trim(); // 用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); // 业务用的SQL
		String sql2 = y_sql[0][4].trim(); // 业务用的SQL2,用于连接上面的SQL
		// SQL语名句中的查询条件
		String sql_tj = gettjsql(request, "MJ" + s_kjname.substring(2));
		// SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "");
		// String sql = HtmlFunction.parseVar(y_sql[0][1], request, "");
		String url = WebControl.class.getResource("/").getPath();
		url = url.substring(1, url.length()-16);
		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "/download/report" + System.currentTimeMillis() + ".xls";
		path=url+path;
		// String path = new
		// File("/").getAbsolutePath()+"\\report"+System.currentTimeMillis()+".xls";
		SQLQuery query;
		Object data[][];
		query = DSManager.getSQLQuery();
		data = query.ArrayMetaQuery(sql);
		// int colLen[] = {30,20};
		// SystemFunction.data2TxtFile(data,path,colLen," ");
		SystemFunction.createExcelFile(path, data);
		// 新建一个SmartUpload对象
		SmartUpload su = new SmartUpload();
		// 初始化

		su.initialize(config, request, response);
		// 设定contentDisposition为null以禁止浏览器自动打开文件，
		// 保证点击链接后是下载文件。若不设定，则下载的文件扩展名为
		// doc时，浏览器将自动用word打开它。扩展名为pdf时，
		// 浏览器将用acrobat打开。
		su.setContentDisposition(null);
		// 下载文件

		su.downloadFile(path);

	}
	
	/**
	 * 下载数据
	 * 
	 */
	public static void downLoadFile(String s_kjname,
			HttpServletRequest request, HttpServletResponse response,
			ServletConfig config) throws Exception {

		
		String filename=s_kjname.substring(2);
		String url = WebControl.class.getResource("/").getPath();
		url = url.substring(1, url.length()-16);
		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "/download/" + filename + ".xls";
		path=url+path;
		
		// 新建一个SmartUpload对象
		SmartUpload su = new SmartUpload();
		// 初始化

		su.initialize(config, request, response);
		// 设定contentDisposition为null以禁止浏览器自动打开文件，
		// 保证点击链接后是下载文件。若不设定，则下载的文件扩展名为
		// doc时，浏览器将自动用word打开它。扩展名为pdf时，
		// 浏览器将用acrobat打开。
		su.setContentDisposition(null);
		// 下载文件

		su.downloadFile(path);

	}

	public void upLoad(HttpServletRequest request,
			HttpServletResponse response, ServletConfig config)
			throws Exception {

		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "\\upload";
		SmartUpload su = new SmartUpload();

		su.initialize(config, request, response);
		// su.setMaxFileSize(10000);
		// su.setTotalMaxFileSize(20000) ;
		// su.setAllowedFilesList("txt,doc");
		// su.setDeniedFilesList("xls,bat,exe,jsp,htm,html") ;

		su.upload();

		int count = su.save(path);
		if (count > 0) {
			String targetPage = su.getRequest().getParameter("targetPage");
			com.jspsmart.upload.File dFile = su.getFiles().getFile(0);
			String fileName = dFile.getFileName();
			if (fileName.startsWith("PDD")) {
				this.parsePDDFile(path + "\\" + fileName);
			}
			if (fileName.startsWith("RKD")) {
				this.parseRKDFile(path + "\\" + fileName);
			}
			if (fileName.startsWith("BHD")) {
				this.parseBHDFile(path + "\\" + fileName);
			}

			request.getRequestDispatcher(targetPage).forward(request, response);
		} else
			throw new Exception("文件没有上传成功!");

	}

	public String parseBHDFile(String path) throws Exception {

		File file = new File(path);
		String filedate;
		if (!file.exists())
			throw new Exception("载入文件" + path + "没找到!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		SQLUpdater updater = DSManager.getSQLUpdater();
		updater.executeUpdate("delete from prepare_txm_list_tmp");
		while ((filedate = br.readLine()) != null) {
			String bhdh = filedate.substring(0, 20);
			String xh = filedate.substring(21, 41);
			String productTxm = filedate.substring(42, 62);
			String sql = "insert into prepare_txm_list_tmp(BHDH,XH,PRODUCTTXM) values( '"
					+ bhdh + "','" + xh + "','" + productTxm + "')";
			updater.executeUpdate(sql);
		}
		updater.executeCall("{ call sp_pack4txm }");
		br.close();
		return "ok";
	}

	public String parseRKDFile(String path) throws Exception {

		File file = new File(path);
		String filedate;
		if (!file.exists())
			throw new Exception("载入文件" + path + "没找到!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		SQLUpdater updater = DSManager.getSQLUpdater();
		updater.executeUpdate("delete from enter_txm_list");
		while ((filedate = br.readLine()) != null) {

			String productTxm = filedate.substring(21, 41).trim();
			String enterNum = filedate.substring(42, 52).trim();
			String sql = "";

			sql = "insert into ENTER_TXM_LIST (PRODUCT_TXM,PRODUCT_CODE,SHOW_NUM,PRODUCT_NAME) select PRODUCT_TXM,PRODUCT_CODE,'"
					+ enterNum
					+ "',PRODUCT_NAME  from PRODUCT_LIST  where PRODUCT_TXM = '"
					+ productTxm + "'";
			updater.executeUpdate(sql);
		}
		updater.executeCall("{ call sp_enter4txm }");
		br.close();
		return "ok";
	}

	public String parsePDDFile(String path) throws Exception {

		File file = new File(path);
		String filedate;
		if (!file.exists())
			throw new Exception("载入文件" + path + "没找到!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		SQLUpdater updater = DSManager.getSQLUpdater();
		updater.executeUpdate("delete from check_txm_list");
		while ((filedate = br.readLine()) != null) {
			String checkCode = filedate.substring(0, 20).trim();
			String productTxm = filedate.substring(21, 41).trim();
			String enterNum = filedate.substring(42, 52).trim();
			String checkDate = filedate.substring(53, 73).trim();
			String sql = "";

			sql = "insert into CHECK_TXM_LIST (CHECK_CODE,PRODUCT_TXM,PRODUCT_CODE,SHOW_NUM,PRODUCT_NAME,Product_size,product_untl,check_date) select '"
					+ checkCode
					+ "',PRODUCT_TXM,PRODUCT_CODE,'"
					+ enterNum
					+ "',PRODUCT_NAME,product_size,product_untl,'"
					+ checkDate
					+ "'  from PRODUCT_LIST where PRODUCT_TXM = '"
					+ productTxm
					+ "'";
			updater.executeUpdate(sql);
		}
		updater.executeCall("{ call sp_check4txm }");

		br.close();
		return "ok";
	}

	public String parseReport(String html, String kjm,
			HttpServletRequest request) throws Exception {

		// 取出业务SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_RP, "KJM", kjm);
		} catch (Exception e) {
			throw new Exception("处理" + kjm + "出错!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("控件名" + kjm + "没找到!");
		}

		// 获取业务数据内容
		String ljh = y_sql[0][1].trim(); // 数据连接号
		String cs = y_sql[0][2].trim(); // 用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); // 业务用的SQL
		String sql2 = y_sql[0][4].trim(); // 业务用的SQL2,用于连接上面的SQL
		String reportFile = y_sql[0][5].trim();

		sql = sql + sql2 + cs;
		sql = HtmlFunction.parseVar(sql, request, "");
		String reportContent = "";
		try {

			reportContent = SystemFunction.getReportContent(ljh, sql,
					reportFile);
		} catch (Exception e) {
			throw new Exception(kjm + "生成报表内容出错!" + e.toString());
		}
		String newHtml = SystemFunction.replace(html, "$$" + kjm + ",",
				reportContent);
		return newHtml;
	}

	public static synchronized void parseLicense(String s_kjname)
			throws Exception {
		DataExport de = new DataExport();
		String agentCode = s_kjname.substring(3);
		de.exportLicense(agentCode);
	}
	
	public String parseUpLoad(String html, String kj, HttpServletRequest request, HttpServletResponse response) throws Exception
   {
    String path = XmlUtil.readXml("dsconfig.xml", "/datasource-configuration/systemConfig/root") + "upload";
   
    ServletFileUpload upload = null;
    DiskFileItemFactory factory = new DiskFileItemFactory();
    upload = new ServletFileUpload(factory);
    upload.setFileSizeMax(-1L);
    String url = WebControl.class.getResource("/").getPath();
    url = url.substring(1, url.length() - 16);
    String filename = url + path + "\\" + DateHelper.getShortDateTimeTrim() + ".xls";

    List items = upload.parseRequest(request);
    for (int i = 0; i < items.size(); ++i)
    {
      FileItem fi = (FileItem)items.get(i);
      if (fi.isFormField()) {
        continue;
      }
      if ((fi.getName() != null) && (fi.getSize() != 0L))
      {
        java.io.File newFile = new java.io.File(filename);
        fi.write(newFile);
        filename = filename.replaceAll("\\\\", "\\\\\\\\");
      }
      else {
        filename = "";
      }

    }
    return SystemFunction.replace(html, "$$" + kj + ",", filename);
   }
	
	
	/**
	 * 阿里大于短信通知接口
	 * @param request
	 * @param formMap
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseDisplayAliSms(HttpServletRequest request) {
          
		 JSONObject json_result = new JSONObject(); 
		 String dxmb="";
		 String hm="";
		 String template="";
		 String product="";
		 dxmb = request.getParameter("dxmb");
		 hm= request.getParameter("hm");
		 template=request.getParameter("template");
		 product=request.getParameter("product");
		 if(hm == null || hm.equals(""))
		 {
			 json_result.put("result", "false");
		     json_result.put("reason", "手机号码为空");  
		     return json_result;
		 }
		 if(dxmb == null || dxmb.equals(""))
		 {
			 json_result.put("result", "false");
		     json_result.put("reason", "短信内容为空");  
		     return json_result;
		 }
		 if(template == null )
		 {
			 template="";
		 }
		 if(product == null )
		 {
			 product="";
		 }
		 
		 
		 try{
			 AliMsgSender a=new AliMsgSender();
			 String smsResult="";
			 try {
					product =java.net.URLDecoder.decode(product,"utf-8");
				} catch (UnsupportedEncodingException e) {
					//value="";
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 if(!template.equals("") && !product.equals( "")){
				  smsResult= a.sendMsg(dxmb, hm,template,product);
			 }else{
				 smsResult= a.sendMsg(dxmb, hm);
			 }
		    
		     json_result.put("result", "ok");
		     json_result.put("reason", smsResult);
			 
		 }catch (Exception ex)
		 {
			 json_result.put("result", "false");
		     json_result.put("reason", ex.toString()); 
		 }
	      
		   return json_result;
		
	}

}
