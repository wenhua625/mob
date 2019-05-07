package com.novarise.webase.servlet;

import javax.servlet.http.*;
import java.util.*;

public class SessionListener implements HttpSessionListener {
	private static HashMap hUserName = new HashMap();// ����sessionID��username��ӳ��
														// /**������ʵ��HttpSessionListener�еķ���**/

	public void sessionCreated(HttpSessionEvent se) {
		//System.out.println("Session Created...");
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		hUserName.remove(se.getSession().getId());
		//System.out.println("Session Destoryed..."+se.getSession().getId());
	}

	/*
	 * isAlreadyEnter-�����ж��û��Ƿ��Ѿ���¼�Լ���Ӧ�Ĵ����� @param sUserName String-��¼���û�����
	 * @return boolean-���û��Ƿ��Ѿ���¼���ı�־
	 */

	public static boolean isAlreadyEnter(HttpSession session, String sUserName) {
		boolean flag = false;
		if (hUserName.containsValue(sUserName)) {
			// ������û��Ѿ���¼������ʹ�ϴε�¼���û�����(����ʹ�û����Ƿ���hUserName��)
			flag = true;
			// ����ԭ����hUserName��ɾ��ԭ�û�����Ӧ��sessionID(��ɾ��ԭ����sessionID��username)
			Iterator iter = hUserName.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (((String) val).equals(sUserName)) {
					hUserName.remove(key);
				}
			}
			hUserName.put(session.getId(), sUserName);// ������ڵ�sessionID��username
			
			//System.out.println("hUserName = " + hUserName);
		} else {
			// ������û�û��¼����ֱ��������ڵ�sessionID��username
			flag = false;
			//((ActionContext) session).put(LOGIN_INFO, loginfo);
			hUserName.put(session.getId(), sUserName);
			//System.out.println("hUserName = " + hUserName);
		}
		return flag;
	}

	/*
	 * isOnline-�����ж��û��Ƿ����� @param session HttpSession-��¼���û����� @return
	 * boolean-���û��Ƿ����ߵı�־
	 */
	public static boolean isOnline(HttpSession session) {
		boolean flag = true;
		if (hUserName.containsKey(session.getId())) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}
}
