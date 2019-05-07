package com.novarise.webase.framework;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.novarise.webase.BConstants;
import com.novarise.webase.util.AliMsgSender;
import com.novarise.webase.util.DateHelper;
import com.novarise.webase.util.JsonUtil;
import com.novarise.webase.util.SmsSender;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.SQLUpdater;

public class DwrControl {

	/**
	 * ʵ��HTML��������ʾ�ķ��� ʵ�ַ���: 1,����HTMLҳ�� 2,�������ݿ�,ȡ��ҵ��SQL 3,����ҵ��SQLȡ�����������ļ�¼
	 * 4,����HTMLҳ�� 5,����
	 */
	public List parseDisplayMJ(String s_kjname, HttpServletRequest request,
			Map formMap) throws Exception {
		// ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("����" + s_kjname + "����!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("�ؼ���" + s_kjname + "û�ҵ�!");
		}
		// ��ȡҵ����������
		String ljh = y_sql[0][1].trim(); // �������Ӻ�
		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); // ���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); // �Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); // ÿҳ����
		String defaults = y_sql[0][8].trim(); // ��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); // û���ҵ�����ʱ��ʾ

		// SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, s_kjname, formMap);
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "", formMap);

		// ��ʼ��ȡʵ������
		SQLQuery query = DSManager.getSQLQuery();
		List result = new ArrayList();

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			//e.printStackTrace();
            System.out.println("DWRCOMM.pareMJ����"+s_kjname+"!"+sql);
			return result;
		}
		return result;
	}

	// ȡ����sql���
	private static String gettjsql(HttpServletRequest request, String kjname)
			throws Exception {
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

	// ȡ����sql���
	private static String gettjsql(HttpServletRequest request, String kjname,
			Map formMap) throws Exception {
		HttpSession session = request.getSession(true);
		String[][] mjcstj = new String[0][0];
		try {
			mjcstj = XmlUtil.find(BConstants.PAGE_MJ_TJ, "KJM", kjname);
		} catch (SQLException se1) {
			throw new Exception("ȡ����SQLʱ����" + se1.toString());
		}
		String s_tjsql = " ";
		String value;
		if (mjcstj.length != 0) {
			for (int i = 0; i < mjcstj.length; i++) {
				if (formMap.get(mjcstj[i][1].trim()) == null)
					value = "";
				else
					value = (String) formMap.get(mjcstj[i][1].trim());
				if (value.length() != 0) {
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
	
	/**
	 * ��AJAX��ʽ�������������ʾ ʵ�ַ���: 1,�������ݿ� ȡ��SQL 2,����SQLȡ������������ֵ 3,����HTML��ֵ 4,����
	 */

	public List parseDisplayXL(String s_kjname, HttpServletRequest request,Map formMap)
			throws Exception {

		List result = new ArrayList();

		// �������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0) {
				return result;
			}

		} catch (Exception e) {
			return result;
		}
//		 SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, s_kjname, formMap);
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		String sql = y_sql[0][3]+ sql_tj ;
		sql = HtmlFunction.parseVar(sql, request, "", formMap);
		

		// ȡ������������ֵ

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			return result;
		}

		return result;
	}


	/**
	 * ��AJAX��ʽ�������������ʾ ʵ�ַ���: 1,�������ݿ� ȡ��SQL 2,����SQLȡ������������ֵ 3,����HTML��ֵ 4,����
	 */

	public List parseDisplayAL(String s_kjname, HttpServletRequest request,Map formMap)
			throws Exception {

		List result = new ArrayList();

		// �������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0) {
				return result;
			}

		} catch (Exception e) {
			return result;
		}

		String sql = HtmlFunction.parseVar(y_sql[0][3], request, "",formMap);

		// ȡ������������ֵ

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	

	public static synchronized String parseModifyIN(String kjname,
			HttpServletRequest request, String s_action, Map formMap)
			throws Exception {

		String tmpKj = kjname;
		kjname = kjname.substring(0, 6);// ����ؼ������ؼ���Ϊ6λ

		SQLUpdater updater = DSManager.getSQLUpdater();
		SQLQuery query = DSManager.getSQLQuery();

		if (s_action == null || s_action.trim().length() == 0)
			s_action = "insert";
		// ȡҵ������
		String[][] s_modifycs = new String[0][0];
		try {
			s_modifycs = XmlUtil.find(BConstants.PAGE_UP, "KJM", kjname);
			if (s_modifycs.length == 0)
				return "Ajax:��������[" + kjname + "]�����ڣ�����δ�ɹ���";
		} catch (SQLException e) {
			return "Ajax:��������[" + kjname + "]��λSQL����!";
		}
		String s_selectsql = s_modifycs[0][2], s_insertsql = s_modifycs[0][3], s_deletesql = s_modifycs[0][5], s_updatesql = s_modifycs[0][4], s_cs = s_modifycs[0][1], s_url = s_modifycs[0][6];

		if (s_action.equals("delete")) {
			s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "",
					formMap);

			try {
				updater.executeUpdate(s_deletesql);
			} catch (SQLException e) {
				System.out.println(s_deletesql);
				return "Ajax:��������[" + kjname + "]ɾ��SQL����!SQL=" + s_deletesql;
			}

		}
		if (s_action.equals("insert")) {

			
			// �ж��Ƿ���Ҫ���,'1'Ϊ��
			if (s_cs.equals("1")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
						formMap);
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "",
						formMap);
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:��������[" + kjname + "]���ʱ����!SQL=" + s_selectsql;
				}
				if (checkValue.length != 0)
					return "��¼[" + checkValue[0][0] + "]�Ѵ��ڣ�����δ�ɹ�!";
				else
				{
					try {
						updater.executeUpdate(s_insertsql);
					} catch (SQLException e) {
						System.out.println(s_insertsql);
						return "Ajax:��������[" + kjname + "]����SQL����!SQL="
								+ s_insertsql;
					}
				}
			}

			else if (s_cs.equals("3")) {
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "",
						formMap);
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:��������[" + kjname + "]���ʱ����!SQL=" + s_selectsql;
				}
				if (checkValue.length == 0) {

					return "�ö������޴�������Ĳ�Ʒ!";
				}

			}// �����⵽��־Ϊ2,���м�¼�͸��¼�¼��������ʾ���޼�¼��ֱ�Ӳ���
			else if (s_cs.equals("2")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
						formMap);
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "",
						formMap);
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:��������[" + kjname + "]���ʱ����!SQL=" + s_selectsql;
				}
				if (checkValue.length != 0) {
					s_updatesql = HtmlFunction.parseVar(s_updatesql, request,
							"", formMap);
					updater.executeUpdate(s_updatesql);
				} else {
					updater.executeUpdate(s_insertsql);
				}

			} 
			//DSSession dsn=DSManager.getDSSession();
			else if(s_cs.equals("4")){//����Json����
				
				String jsonData = (String)formMap.get("d_JsonData");
				if(jsonData == null) jsonData = "";
				if(!jsonData.equals(""))
				{
					try{
						//dsn.beginTransaction();
						String jsonType = (String)formMap.get("d_JsonType");
						if(jsonType == null) jsonType = "";
						if(jsonType.equalsIgnoreCase("Easyui")){
						   jsonData=java.net.URLDecoder.decode(jsonData,"utf-8");
						}
						
						s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "",formMap);
						updater.executeUpdate(s_deletesql);//ɾ����ϸ����
						List jsonList = JsonUtil.getList4Json(jsonData, Map.class);
						//ѭ��������ϸ����
						for(int i=0;i<jsonList.size();i++){
							Map map=(Map)jsonList.get(i);
							map.put("xh", i);//��Ŵ���
							String updatesql=HtmlFunction.parseVar(s_updatesql, request,"", map);
							//System.out.println(updatesql);
							try{
							updater.executeUpdate(updatesql);
							}catch(Exception ex){
								System.out.println(updatesql);
								return "Ajax:��������[" + kjname + "]����SQL����!SQL="
								+ updatesql;
							}
							
						}
						
						if(!s_insertsql.equals("")){
							s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
									formMap);
							updater.executeUpdate(s_insertsql);//���붩������
						}
						
						//dsn.endTransaction();
					}catch(Exception e){
						//dsn.rollback();
						System.out.println(s_insertsql+"ERROR:"+e.toString());
						return "Ajax:��������[" + kjname + "]����SQL����!SQL="
						+ s_insertsql;
						
						
					}
				}
			}
			else {
				try {
					s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
							formMap);
					updater.executeUpdate(s_insertsql);
				} catch (SQLException e) {
					System.out.println(s_insertsql);
					return "Ajax:��������[" + kjname + "]����SQL����!SQL="
							+ s_insertsql;
				}
			}
			

		}
		if (s_action.equals("update")) {
			s_updatesql = HtmlFunction.parseVar(s_updatesql, request, "",
					formMap);

			try {
				updater.executeUpdate(s_updatesql);
			} catch (SQLException e) {
				return "Ajax:��������[" + kjname + "]����SQL����!SQL=" + s_updatesql;
			}

		}

		if (s_action.equals("call")) {
			s_url = HtmlFunction.parseVar(s_url, request, "", formMap);

			try {

				updater.executeCall(s_url);
			} catch (SQLException e) {
				return "Ajax:��������[" + kjname + "]ִ�д�̹��̳���!URL=" + s_url
						+ e.toString();
			}

		}

		return "ok";
	}

	// ������ʾ
	public String parseDisplayTip(HttpServletRequest request) throws Exception {

		String returnString = "";
		HttpSession session = request.getSession(true);
		
        //��������Ȩ��
		List l_gndm=new ArrayList();
		String gndm = (String)session.getAttribute("LS.QXTS");
		if (gndm == null) gndm="";
		if(gndm.length()>0){
		   String gndms[]=gndm.split(",");
		   for(int i=0;i<gndms.length;i++)
		   {
			l_gndm.add(gndms[i]);
		   }
		}
//		ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		for(int i=0;i<l_gndm.size();i++){
			String s_gndm=(String)l_gndm.get(i);
			String s_kjname=s_gndm.substring(1);
			s_kjname=s_kjname.substring(0,s_kjname.length()-1);
			try {
				y_sql = XmlUtil.find(BConstants.PAGE_TP, "KJM", s_kjname);
			} catch (Exception e) {
				throw new Exception("����" + s_kjname + "����!" + e.toString());
			}
			
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("�ؼ���" + s_kjname + "û�ҵ�!");
			}
			// ��ȡҵ����������
			String ljh = y_sql[0][1].trim(); // �������Ӻ�
			String cs = y_sql[0][3].trim(); // ���ڼ���order by ,group by ���
			String sql = y_sql[0][9].trim(); // ҵ���õ�SQL
			
			sql = sql   + cs;
			sql = HtmlFunction.parseVar(sql, request, "");
			
			SQLQuery query = DSManager.getSQLQuery();
			String resultData[][] = query.ArrayQuery(sql);
			if (resultData.length == 0) {
				returnString+="";
			}else{
				returnString += "<tr><td>"+resultData[0][0]+ "</td></tr>";
			}
		}
		if(returnString.length()>0){
			returnString = "<table border=0 class=\"w01_13px_black\">" +returnString +"</table>";
		}
		return returnString;
	}

	public String parseDisplayKEY(String s_kjname, HttpServletRequest request,
			Map formMap) throws Exception {
		// ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("����" + s_kjname + "����!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("�ؼ���" + s_kjname + "û�ҵ�!");
		}
		// ��ȡҵ����������

		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL

		// SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, s_kjname);
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "sql", formMap);

		// ��ʼ��ȡʵ������
		SQLQuery query = DSManager.getSQLQuery();
		String result[][] = new String[0][0];
		result = query.ArrayQuery(sql);
		if (result.length == 0) {
			throw new Exception("û��ȡ����Ψһ��ֵ(" + s_kjname + "sql:"+sql+")!");
		}
		return result[0][0];

	}

	public String parseDisplayBaseKEY(String codeType) throws Exception {
		// ȡ��ҵ��SQL
		String sql = "select code,code_date,code_type from Code_Gen where Code_Type='"
				+ codeType
				+ "' and code_date='"
				+ DateHelper.getShortDate()
				+ "'";
		String insert_sql = " insert into Code_Gen(code_type,code,code_date) values('"
				+ codeType + "','2','" + DateHelper.getShortDate() + "')";
		String update_sql = "update Code_Gen set code=code+1 where Code_Type='"
				+ codeType + "' and code_date='" + DateHelper.getShortDate()
				+ "'";
		DecimalFormat format = new DecimalFormat("000");
		// ��ʼ��ȡʵ������
		SQLQuery query = DSManager.getSQLQuery();
		SQLUpdater updater = DSManager.getSQLUpdater();
		String result[][] = new String[0][0];
		result = query.ArrayQuery(sql);
		if (result.length == 0) {
			updater.executeUpdate(insert_sql);
			return DateHelper.getShortDate() + format.format(1);
		} else {
			updater.executeUpdate(update_sql);
			return DateHelper.getShortDate()
					+ format.format(Long.parseLong(result[0][0]));
		}

	}
	
	/**
	 * ����֪ͨ�ӿ�
	 * @param request
	 * @param formMap
	 * @return
	 * @throws Exception
	 */
	public String parseDisplaySMS(String s_kjname,HttpServletRequest request,String dxmb,Map formMap) throws Exception {

		
		 
		
       // ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("����" + s_kjname + "����!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("�ؼ���" + s_kjname + "û�ҵ�!");
		}
		
       // ��ȡҵ����������
		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL
		// SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, s_kjname);
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "sql", formMap);
		
       // ��ʼ��ȡ��������
		SQLQuery query = DSManager.getSQLQuery();
		SQLUpdater updater = DSManager.getSQLUpdater();
		List result = new ArrayList();
		result = query.ListQuery(sql);
		if (result.size() == 0) {
			throw new Exception("û��Ҫ���Ͷ��ŵ�����(" + s_kjname + ")!"+sql);
		}
		Map data=(Map)result.get(0);
		String smsNR="";
		smsNR=HtmlFunction.parseSMSTemplate(dxmb, data);
		//SmsSender sender=SmsSender.newInstance("http://202.91.244.252:8080/qd/SMSSendYD","5084","zc888","47");
		String hm="";
		if (data.get("�ֻ�����") == null)
		{
			hm="";
		}else hm=data.get("�ֻ�����").toString();
		if (hm.equals(""))
		{
			return "";
		}else{
		  String jz_sql = "insert into sms_js(ckbm,dwdm,sj,xm,dxnr,rq,lb) select lb,bm,sj,mc,'"+smsNR+"',getdate(),'ϵͳ����' from bm_wldw where sj in ('"+hm+"')";	
           //System.out.println(jz_sql);
           updater.executeUpdate(jz_sql);
           hm="86"+hm;
		   String smsResult = SmsSender.send_url(smsNR, hm);
		   return smsResult;
		}
	}
	
	
	
	/**
	 * ����֪ͨ�ӿ�
	 * @param request
	 * @param formMap
	 * @return
	 * @throws Exception
	 */
	public String sendAppMsg(String s_kjname,HttpServletRequest request,String dxmb,Map formMap) throws Exception {

		
		 
		
       // ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("����" + s_kjname + "����!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("�ؼ���" + s_kjname + "û�ҵ�!");
		}
		
       // ��ȡҵ����������
		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL
		// SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, s_kjname);
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "sql", formMap);
		
       // ��ʼ��ȡ��������
		SQLQuery query = DSManager.getSQLQuery();
		SQLUpdater updater = DSManager.getSQLUpdater();
		List result = new ArrayList();
		result = query.ListQuery(sql);
		if (result.size() == 0) {
			throw new Exception("û��Ҫ���Ͷ��ŵ�����(" + s_kjname + ")!"+sql);
		}
		Map data=(Map)result.get(0);
		String smsNR="";
		smsNR=HtmlFunction.parseSMSTemplate(dxmb, data);
		//SmsSender sender=SmsSender.newInstance("http://202.91.244.252:8080/qd/SMSSendYD","5084","zc888","47");
		String hm="";
		if (data.get("�ֻ�����") == null)
		{
			hm="";
		}else hm=data.get("�ֻ�����").toString();
		if (hm.equals(""))
		{
			return "";
		}else{
		  String jz_sql = "insert into msglist(msgtitle,msgcontent,msgdate,msgurl,msgtype,msgtel,msgtoken,sendtel,appname) select lb,bm,sj,mc,'"+smsNR+"',getdate(),'ϵͳ����' from bm_wldw where sj in ('"+hm+"')";	
           //System.out.println(jz_sql);
           updater.executeUpdate(jz_sql);
           hm="86"+hm;
		   String smsResult = SmsSender.send_url(smsNR, hm);
		   return smsResult;
		}
	}
	
	/**
	 * ����֪ͨ�ӿ�
	 * @param request
	 * @param formMap
	 * @return
	 * @throws Exception
	 */
	public String parseDisplaySMS(HttpServletRequest request,String dxmb,String hm) throws Exception {

        String smsNR=dxmb;
		
           SQLQuery query = DSManager.getSQLQuery();
		   SQLUpdater updater = DSManager.getSQLUpdater();
		   
		   
		  /* String[] hms=hm.split("\n");
		   String jz_hm="";
           String qf_hm="";
           for(int i=0;i<hms.length;i++){
        	   String s_hm=hms[i].trim();
        	   if(!s_hm.equals(""))
        	      qf_hm+="86"+hms[i].trim()+",";
        	      jz_hm+="'"+hms[i].trim()+"',";
           }
           qf_hm=qf_hm.substring(0,qf_hm.length()-1);
           jz_hm=jz_hm.substring(0,jz_hm.length()-1);*/
           
           
		   String smsResult = SmsSender.send_url(smsNR, hm);
		   if(smsResult.equals("0"))
		   {
			   String jz_sql = "insert into sms_js(sj,dxnr,rq) values ('"+hm+"','"+dxmb+"',getdate())";
	           //System.out.println(qf_hm);
	           updater.executeUpdate(jz_sql); 
	           smsResult = "ok";
		   }
		    
		   return smsResult;
		//}
	}
	
	/**
	 * ������ڶ���֪ͨ�ӿ�
	 * @param request
	 * @param formMap
	 * @return
	 * @throws Exception
	 */
	public String parseDisplayAliSms(HttpServletRequest request,String dxmb,String hm) throws Exception {

	      AliMsgSender a=new AliMsgSender();
	      String smsResult= a.sendMsg(dxmb, hm);
		    
		   return smsResult;
		
	}

}
