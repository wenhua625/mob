package com.novarise.webase;

public interface BConstants {
	
	//ϵͳ������Ϣ
	public static final String CONFIG_FILE = "dsconfig.xml";
	public static final String SYSTEM_ROOT = "/datasource-configuration/systemConfig/root";
	public static final String SYSTEM_TITLE = "/datasource-configuration/systemConfig/title";
	public static final String SYSTEM_AUTH = "/datasource-configuration/systemConfig/authorization";
	public static final String SYSTEM_ERRORPAGE = "/datasource-configuration/systemConfig/errorpage";
	public static final String SYSTEM_ERRORLOGINPAGE = "/datasource-configuration/systemConfig/errorloginpage";
	public static final String SYSTEM_ERRORSESSIONPAGE = "/datasource-configuration/systemConfig/errorsessionpage";
	public static final String SYSTEM_BGCOLOR = "/datasource-configuration/systemConfig/bgColor";
	public static final String SYSTEM_BGIMAGE = "/datasource-configuration/systemConfig/bgImage";
	public static final String BHD = "/datasource-configuration/systemConfig/systemBHD";
	public static final String FHD = "/datasource-configuration/systemConfig/systemFHD";
	public static final String TYD = "/datasource-configuration/systemConfig/systemTYD";
	public static final String AUTHAMOUNT = "/datasource-configuration/systemConfig/authAmount";
	public static final String ZOOM = "/datasource-configuration/systemConfig/zoom";
	//APPӦ��KEY
	public static final String AGLAPPKEY = "/datasource-configuration/systemConfig/aglappkey";
	public static final String AGLAPPSECRET = "/datasource-configuration/systemConfig/aglappsecret";
	public static final String KCCAPPKEY = "/datasource-configuration/systemConfig/kccappkey";
	public static final String KCCAPPSECRET = "/datasource-configuration/systemConfig/kccappsecret";

	public static final String HUAWEIKCCAPPKEY = "/datasource-configuration/systemConfig/huaweikccappkey";
	public static final String HUAWEIKCCAPPSECRET = "/datasource-configuration/systemConfig/huaweikccappsecret";
	
	public static final String OPPOAPPKEY = "/datasource-configuration/systemConfig/oppoappkey";
	public static final String  OPPOMASTERSECRET = "/datasource-configuration/systemConfig/oppomastersecret";
	
	public static final String VIVOAPPID = "/datasource-configuration/systemConfig/vivoappid";
	public static final String  VIVOAPPKEY = "/datasource-configuration/systemConfig/vivoappkey";
	public static final String  VIVOAPPSECRET = "/datasource-configuration/systemConfig/vivoappsecret";
	

	public static final String  XIAOMIAPPSECRET = "/datasource-configuration/systemConfig/xiaomiappsecret";
	public static final String  XIAOMIMYPACKAGENAME = "/datasource-configuration/systemConfig/xiaomimypackagename";
	
	//�˳�ϵͳʱָ����URL
	public static final String EXIT_URL = "/index.htm";
	
	//��½�ɹ������ҳ��
	public static final String LOGIN_MAIN = "/display?proname=mainframe";
	public static final String LOGIN_MAIN1 = "/display?proname=mainframe_zd";
	
	//ҳ��������Ϣ
	public static final String PAGE_QX = "qxConfig.xml";
	public static final String PAGE_MJ = "mjConfig.xml";
	public static final String PAGE_MJ_TJ = "mj4tjConfig.xml";
	public static final String PAGE_XL = "xlConfig.xml";
	public static final String PAGE_ZQ = "zqConfig.xml";
	public static final String PAGE_ZQ_MX = "zq4mxConfig.xml";
	public static final String PAGE_UP = "upConfig.xml";
	public static final String PAGE_TREE = "treeConfig.xml";
	public static final String PAGE_FW = "fwConfig.xml";
    public static final String PAGE_EX = "exConfig.xml";
    public static final String PAGE_RP = "rpConfig.xml";
    public static final String PAGE_TP = "tipConfig.xml";
    public static final String PAGE_UE = "ueConfig.xml";
    public static final String PAGE_MB = "mbConfig.xml";
	//����Session�ı�����Ϣ
	public static final String SESSION_TITLE = "LS.TITLE";
	
	
	//������Ϣ
	public static final String BANK=
			"{'SRCB': '����ũ����ҵ����'," 
			+ "'BGB': '��������������',"
			+"'SHRCB': '�Ϻ�ũ����ҵ����',"
			+ "'BJBANK': '��������'," 
			+ "'WHCCB': '��������ҵ����'," 
  + "'BOZK': '�ܿ�����'," 
  + "'KORLABANK': '���������ҵ����'," 
  + "'SPABANK': 'ƽ������'," 
  + "'SDEB': '˳��ũ������'," 
  + "'HURCB': '����ʡũ��������'," 
  + "'WRCB': '����ũ����ҵ����'," 
  + "'BOCY': '��������'," 
  + "'CZBANK': '��������'," 
  + "'HDBANK': '��������'," 
  + "'BOC': '�й�����'," 
  + "'BOD': '��ݸ����'," 
  + "'CCB': '�й���������'," 
  + "'ZYCBANK': '��������ҵ����'," 
  + "'SXCB': '��������'," 
  + "'GZRCU': '����ʡũ��������'," 
  + "'ZJKCCB': '�żҿ�����ҵ����'," 
  + "'BOJZ': '��������'," 
  + "'BOP': 'ƽ��ɽ����'," 
  + "'HKB': '��������'," 
  + "'SPDB': '�Ϻ��ֶ���չ����'," 
  + "'NXRCU': '���Ļƺ�ũ����ҵ����'," 
  + "'NYNB': '�㶫��������'," 
  + "'GRCB': '����ũ������'," 
  +  "'BOSZ': '��������'," 
  + "'HZCB': '��������'," 
  + "'HSBK': '��ˮ����'," 
  + "'HBC': '��������'," 
  + "'JXBANK': '��������'," 
  + "'HRXJB': '�����潭����'," 
  + "'BODD': '��������'," 
 + " 'AYCB': '��������'," 
 + " 'EGBANK': '�������'," 
 + " 'CDB': '���ҿ�������'," 
 + " 'TCRCB': '����̫��ũ����ҵ����'," 
 + " 'NJCB': '�Ͼ�����'," 
 + " 'ZZBANK': '֣������'," 
 + " 'DYCB': '������ҵ����'," 
+ "  'YBCCB': '�˱�����ҵ����'," 
+ "  'SCRCU': '�Ĵ�ʡũ������'," 
 + " 'KLB': '��������'," 
 + " 'LSBANK': '��������'," 
 + " 'YDRCB': 'Ң��ũ����'," 
 + " 'CCQTGB': '������Ͽ����'," 
 + " 'FDB': '��������'," 
 + " 'JSRCU': '����ʡũ������������'," 
 + " 'JNBANK': '��������'," 
 + " 'CMB': '��������'," 
 + " 'JINCHB': '��������JCBANK'," 
 + " 'FXCB': '��������'," 
 + " 'WHRCB': '�人ũ����ҵ����'," 
 + " 'HBYCBANK': '���������˲�����'," 
 + " 'TZCB': '̨������'," 
+ "  'TACCB': '̩������ҵ����'," 
 + " 'XCYH': '�������'," 
 + " 'CEB': '�й��������'," 
+ "  'NXBANK': '��������'," 
+ "  'HSBANK': '��������'," 
 + "  'JJBANK': '�Ž�����'," 
 + " 'NHQS': 'ũ������������'," 
  + " 'MTBANK': '�㽭��̩��ҵ����'," 
 + " 'LANGFB': '�ȷ�����'," 
  + "'ASCB': '��ɽ����'," 
 + " 'KSRB': '��ɽũ����ҵ����'," 
  + " 'YXCCB': '��Ϫ����ҵ����'," 
  + " 'DLB': '��������'," 
  + "  'DRCBCL': '��ݸũ����ҵ����'," 
  + "  'GCB': '��������'," 
  + "  'NBBANK': '��������'," 
  + "  'BOYK': 'Ӫ������'," 
  + " 'SXRCCU': '�����ź�'," 
  + "  'GLBANK': '��������'," 
  + "  'BOQH': '�ຣ����'," 
  + " 'CDRCB': '�ɶ�ũ������'," 
  + " 'QDCCB': '�ൺ����'," 
  + "  'HKBEA': '��������'," 
+ "  'HBHSBANK': '�������л�ʯ����'," 
  + "  'WZCB': '��������'," 
  + " 'TRCB': '���ũ������'," 
  + " 'QLBANK': '��³����'," 
  + " 'GDRCC': '�㶫ʡũ��������������'," 
  + " 'ZJTLCB': '�㽭̩¡��ҵ����'," 
  + "  'GZB': '��������'," 
  + " 'GYCB': '��������ҵ����'," 
  + " 'CQBANK': '��������'," 
  + " 'DAQINGB': '��������'," 
  + "  'CGNB': '�ϳ�����ҵ����'," 
  + " 'SCCB': '����Ͽ����'," 
  + " 'CSRCB': '����ũ����ҵ����'," 
  + "  'SHBANK': '�Ϻ�����'," 
  + "  'JLBANK': '��������'," 
  + "  'CZRCB': '����ũ����������'," 
  + "  'BANKWF': 'Ϋ������'," 
  + " 'ZRCBANK': '�żҸ�ũ����ҵ����'," 
  + " 'FJHXBC': '������Ͽ����'," 
  + " 'ZJNX': '�㽭ʡũ��������������'," 
  + " 'LZYH': '��������'," 
  + " 'JSB': '��������'," 
 + " 'BOHAIB': '��������'," 
  + " 'CZCB': '�㽭������ҵ����'," 
  + " 'YQCCB': '��Ȫ����'," 
  + " 'SJBANK': 'ʢ������'," 
  + " 'XABANK': '��������'," 
  + " 'BSB': '��������'," 
  + " 'JSBANK': '��������'," 
  + " 'FSCB': '��˳����'," 
  + " 'HNRCU': '����ʡũ������'," 
  + " 'COMM': '��ͨ����'," 
  + "'XTB': '��̨����'," 
  + " 'CITIC': '��������'," 
 + " 'HXBANK': '��������'," 
  + " 'HNRCC': '����ʡũ��������'," 
  + "'DYCCB': '��Ӫ����ҵ����'," 
  + " 'ORBANK': '������˹����'," 
 + " 'BJRCB': '����ũ����ҵ����'," 
  + "'XYBANK': '��������'," 
  + " 'ZGCCB': '�Թ�����ҵ����'," 
  + " 'CDCB': '�ɶ�����'," 
  + " 'HANABANK': '��������'," 
  + " 'CMBC': '�й���������'," 
  + " 'LYBANK': '��������'," 
  + " 'GDB': '�㶫��չ����'," 
  + " 'ZBCB': '��������'," 
  + " 'CBKF': '��������ҵ����'," 
  + " 'H3CB': '���ɹ�����'," 
  + " 'CIB': '��ҵ����'," 
  + " 'CRCBANK': '����ũ����ҵ����'," 
  + " 'SZSBK': 'ʯ��ɽ����'," 
  + " 'DZBANK': '��������'," 
  + " 'SRBANK': '��������'," 
  + " 'LSCCB': '��ɽ����ҵ����'," 
 + " 'JXRCU': '����ʡũ������'," 
  + " 'ICBC': '�й���������'," 
  + " 'JZBANK': '��������ҵ����'," 
  + " 'HZCCB': '��������ҵ����'," 
  + " 'NHB': '�Ϻ�ũ����������'," 
  + " 'XXBANK': '��������'," 
  + " 'JRCB': '���ս���ũ����ҵ����'," 
  + " 'YNRCC': '����ʡũ��������'," 
  + " 'ABC': '�й�ũҵ����'," 
  + " 'GXRCU': '����ʡũ������'," 
  + " 'PSBC': '�й�������������'," 
  + " 'BZMD': 'פ�������'," 
 + " 'ARCU': '����ʡũ��������'," 
  + " 'GSRCU': '����ʡũ������'," 
  + "'LYCB': '��������ҵ����'," 
  + " 'JLRCU': '����ũ��'," 
  + " 'URMQCCB': '��³ľ������ҵ����'," 
  + " 'XLBANK': '��ɽС魴�������'," 
  + " 'CSCB': '��ɳ����'," 
  + " 'JHBANK': '������'," 
  + " 'BHB':'�ӱ�����'," 
  + " 'NBYZ': '۴������'," 
  + "'LSBC': '��������'," 
  + "'BOCD': '�е�����'," 
  + "'SDRCU': 'ɽ��ũ��'," 
  + " 'NCB': '�ϲ�����'," 
  + "'TCCB': '�������'," 
  + "'WJRCB': '�⽭ũ������'," 
  + "'CBBQS': '������ҵ�����ʽ���������'," 
  + "'HBRCU': '�ӱ�ʡũ��������'"
+"}";
	
	

}
