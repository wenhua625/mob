package com.novarise.webase.servlet;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.czb.gateway.api.APIConstance;
import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.util.QuartzJob;
import com.novarise.webase.util.SmsSender;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.config.Configuration;
import com.ripple.datasource.config.MyDataSource;

public class InitDataSource extends HttpServlet {
	private static Logger logger = LogManager.getLogger(InitDataSource.class);

	public void init(ServletConfig cfg) {
		String default_ds = cfg.getInitParameter("default");
		String cfg_file = cfg.getInitParameter("config");
		String sms = cfg.getInitParameter("sms");
		if (sms == null)
			sms = "";

		String timer = cfg.getInitParameter("timer");
		if (timer == null)
			timer = "";
		String sms_url = cfg.getInitParameter("sms_url");
		if (sms_url == null)
			sms_url = "";
		String sms_user = cfg.getInitParameter("sms_user");
		if (sms_user == null)
			sms_user = "";
		String sms_pwd = cfg.getInitParameter("sms_pwd");
		if (sms_pwd == null)
			sms_pwd = "";
		String sms_type = cfg.getInitParameter("sms_type");
		if (sms_type == null)
			sms_type = "";

		Configuration ds_cfg = new Configuration();
		List dss = null;
		try {
			dss = ds_cfg.readFileConfig(cfg_file);
		} catch (ConfigurationException e) {
			// e.printStackTrace();
			logger.info("��ȡ����Դ�����ļ�����: " + e.getMessage());
		}
		for (int i = 0; i < dss.size(); i++) {
			MyDataSource dso = (MyDataSource) dss.get(i);
			try {
				DSManager.initDataSource(dso);
			} catch (SQLException sqle) {
				logger.info(sqle.getMessage());
			}
		}
		// ����Ĭ�ϵ�����Դ
		DSManager.setDefaultDS(default_ds);

		// ������������
		if (sms.equals("1")) {
			try {
				SmsSender smws = SmsSender.newInstance(sms_url, sms_user,
						sms_pwd, sms_type);
				System.out.println("���ŷ���������!");
			} catch (Exception smse) {
				// logger.info(smse.getMessage());
				smse.printStackTrace();
			}
		}

		if (timer.equals("1")) {
			// ������ʱ��
			QuartzJob job = new QuartzJob();
			try {
				job.go();
				System.out.println("��ʱ��������!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

	}
}
