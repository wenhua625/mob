package com.novarise.webase.framework;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.jacob.com.Dispatch;
import com.novarise.webase.BConstants;
import com.novarise.webase.xml.DataFormator;
import com.novarise.webase.xml.XmlUtil;


/**
 * Webϵͳ���õ��ĺ�����
 */

public class SystemFunction {

	

	/**
	 * �����ַ����滻�ķ���: ��:Ҫ��"This is a example"�е�"a"�滻Ϊ"an" �����µ��ü���: replace("This is
	 * a example","a","an") �򷵻�: "This is an axample"
	 */
	public static String replace(String src, String old_str, String new_str) {
		if (old_str.length() == 0 || old_str == null)
			return src;
		if (  new_str == null || new_str.length() == 0 )
			new_str = "";
		String newsrc = "";

		int posStart = 0, pos;
		while ((pos = src.indexOf(old_str, posStart)) != -1) {
			newsrc += src.substring(posStart, pos) + new_str;
			posStart = pos + old_str.length();

		}
		if (posStart < src.length()) {
			newsrc += src.substring(posStart);
		}
		return newsrc;
	}

	/**
	 * ��������ת���ķ��� ע��:�����׳��쳣, �쳣���ݣ�����ת������
	 */
	public static String converGB(String content) {
		String s_uncodestr = "";
		try {
			byte[] bytes = content.trim().getBytes("8859_1");
			s_uncodestr = new String(bytes, "GB2312");
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return s_uncodestr;

	}
	
	
	public static String converUTF8(String content) {
		String s_uncodestr = "";
		try {
			byte[] bytes = content.trim().getBytes("utf-8");
			s_uncodestr = new String(bytes, "GBK");
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return s_uncodestr;

	}

	/**
	 * ���ڷ�ת�����ĵķ��� ע��:�����׳��쳣, �쳣����:102
	 */
	public static String unConverGB(String content) {
		String s_uncodestr = "";
		try {
			byte[] bytes = content.trim().getBytes("GB2312");
			s_uncodestr = new String(bytes, "8859_1");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return s_uncodestr;
	}

	/**
	 * ����װ���ļ�,�����ļ�ת��Ϊ�����ļ����Ա���淽���ȡ����ļ�. ע��: ������ļ�·�������·������:��classpathһ�� ��:
	 * loadFile("config.properties");
	 */
	public  static Properties loadPropertiesFile(String filename) throws Exception {
		Properties prs = new Properties();//ʵ����һ�������ļ�
		ClassLoader cl = SystemFunction.class.getClassLoader();
		InputStream is = cl.getResourceAsStream(filename);//����һ���ļ�
		if (is == null)
			throw new Exception("�����ļ�" + filename + "û���ҵ�!");
		prs.load(is);//���ļ����������ļ��У��Ա���ȡ
		return prs;
	}

	/**
	 * �õ���ϵͳ�������ļ� "config.properties"
	 * 
	 * @return @throws
	 *         java.lang.Exception
	 */
	/*public  Properties getPropertiesFile() throws Exception {
		return loadPropertiesFile(PROPERTIESFILENAME);
	}*/

	/**
	 * ���ڶ�ȡ�ļ����ݵķ���,���ļ�������ת��Ϊ�ַ��������� ע��:�����׳��쳣���ļ�û�ҵ� ��:
	 * readFile("c:\","main.htm");
	 */
	public static String readFile(String filename) throws Exception {
        StringBuffer sb = new StringBuffer();
        //���·��
        String url = SystemFunction.class.getResource("/").getPath();
		url = url.substring(1, url.length()-16);
		File file = new File( url+filename);
        //����·��
        //File file = new File( filename);
        
        if (!file.exists())
            throw new Exception("�����ļ�" +  filename + "û�ҵ�!");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String filedate;
        /*ClassLoader load = SystemFunction.class.getClassLoader();
        InputStreamReader ir = new InputStreamReader(load.getResourceAsStream(filename));
        BufferedReader br = new BufferedReader(ir);*/
        while ((filedate = br.readLine()) != null) {
            sb.append(filedate);
            sb.append("\n");
        }
        br.close();
        return sb.toString();
	}

	/**
	 * ���ڻ�ô�����Ϣ�ķ��� ����: err_id ����ID�� err_msg ������ʾ��Ϣ err_details ���������Ϣ
	 * �йش���ID��,�Լ�ȡֵ��:dsconfig.xml�ļ�
	 */
	public  static String showError(int err_id, String err_msg, String err_details,HttpServletRequest request) {

		String errPageContent = "";
		try {
			String root = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			String errpage = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ERRORPAGE);
			errPageContent = readFile(root+errpage);
			errPageContent = replace(errPageContent, "$$err_id$$", String.valueOf(err_id));
			errPageContent = replace(errPageContent, "$$err_msg$$", err_msg);
			errPageContent = replace(errPageContent, "$$err_msg_detail$$",err_details);
			String title   = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE);
			errPageContent = replace(errPageContent, "$$msg_version$$",title);
			errPageContent = HtmlFunction.parseVar(errPageContent, request, "");
		} catch (Exception e) {
			errPageContent = "ϵͳִ��ʱ���������κ����⣬����ϵϵͳ����Ա��<br>" + e.toString();
			e.printStackTrace();
		}
		return errPageContent;

	}

	/**
	 * ���ڻ�ô�����Ϣ�ķ��� ����: err_id ����ID�� err_msg ������ʾ��Ϣ err_details ���������Ϣ
	 * �йش���ID��,�Լ�ȡֵ��:err_config.properties�ļ�
	 */
	public  static String showLoginError(int err_id, String err_msg, String err_details) {

		String errPageContent = "";
		try {
			String root = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			String errpage = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ERRORLOGINPAGE);
			String title   = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE);
			errPageContent = readFile(root+errpage);
			errPageContent = replace(errPageContent, "$$err_id$$", String.valueOf(err_id));
			errPageContent = replace(errPageContent, "$$err_msg$$", err_msg);
			errPageContent = replace(errPageContent, "$$err_msg_detail$$",err_details);
			errPageContent = replace(errPageContent, "$$msg_version$$",title);
		} catch (Exception e) {
			errPageContent = "ϵͳִ��ʱ���������κ����⣬����ϵϵͳ����Ա��<br>" + e.toString();
			
		}

		return errPageContent;

	}
	
	/**
	 * ���ڻ�ô�����Ϣ�ķ��� ����: err_id ����ID�� err_msg ������ʾ��Ϣ err_details ���������Ϣ
	 * �йش���ID��,�Լ�ȡֵ��:err_config.properties�ļ�
	 */
	public  static String showSessionError(int err_id, String err_msg, String err_details) {

		String errPageContent = "";
		try {
			String root = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			
			String errpage = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ERRORSESSIONPAGE);
			errPageContent = readFile(root+errpage);
			errPageContent = replace(errPageContent, "$$err_id$$", String
					.valueOf(err_id));
			errPageContent = replace(errPageContent, "$$err_msg$$", err_msg);
			errPageContent = replace(errPageContent, "$$err_msg_detail$$",
					err_details);
			String title   = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE);
			errPageContent = replace(errPageContent, "$$msg_version$$",
					title);
		} catch (Exception e) {
			errPageContent = "ϵͳִ��ʱ���������κ����⣬����ϵϵͳ����Ա��<br>" + e.toString();
			
		}

		return errPageContent;

	}

	public static String getYear() {
		Date currDate = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currDate);
		return parseNormalString(calendar.get(calendar.YEAR));
	}

	public static String getMonth() {
		Date currDate = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currDate);

		return parseNormalString(calendar.get(Calendar.MONTH) + 1);
	}

	public static String getDay() {
		Date currDate = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currDate);
		return parseNormalString(calendar.get(Calendar.DATE));

	}
	
	public static String getDate(Date date)
	{
		return parseNormalString((date.getYear() + 1900))+"-"+parseNormalString(date.getMonth()+1)+"-"+parseNormalString(date.getDate());
	}

	/**
	 * ����ϵͳ���ִ���ʱ,������д��Log�ļ����� ����: err_details ������Ϣ log�ļ���ʽ ϵͳʱ��:||err_details
	 * ע��:log�ļ����������ļ�config.properties
	 */
	public  void writeErrorLog(String err_details) throws Exception {
		// BufferedOutputStream outfile=null;
		try {
			String path = getClass().getResource("SysFun.class").toString();
			if (path.indexOf("/") != -1)
				path = path.substring(6, path.lastIndexOf("/") + 1);
			FileOutputStream file = new FileOutputStream(path + "error.log");
			BufferedOutputStream outfile = new BufferedOutputStream(file);
			outfile.write(err_details.getBytes());

			outfile.close();
		} catch (Exception e) {
			throw new Exception("��־�ļ�:" + "error.log" + "û�ҵ�!" + e.toString());
		} finally {
			// outfile.close() ;
		}
	}

	/**
	 * ת��Ϊϵͳ��׼�ַ��� ����:str Ҫת�����ַ��� ����:ת������ַ��� ת����׼:��һλ���������ַ�����ǰ��'0'������λ
	 * ��:getNormalString("1");->"01"
	 */
	public static String parseNormalString(int num) {
		String numstr = String.valueOf(num);
		if (numstr.length() == 1) {
			numstr = "0" + numstr;
		}
		return numstr;
	}
	
	
	public static String null2str(String str) {
		str = null2SpaceString(str);
		if(str.equals("&nbsp;"))
		{
		    return "";
		}
		return str;
	}
	
	public static String null2SpaceString(Object obj) {
		if(obj == null || obj.equals("null") || obj.equals("NULL")){
			return "";
		}
		return (String)obj;
		
	}
	
	public static String toOriginString(String str) {
		/*
		* ��һ������ʵ��ת����ַ�����ʵ��ת��ת��ԭ���ĸ�ʽ
		*/
		StringBuffer buf = new StringBuffer();
		Matcher m = Pattern.compile("&#\\d+;").matcher(str);
		int start = 0;
		String findstr = null;
		while (m.find()) {
		findstr = m.group(); 
		buf.append(str.substring(start, m.start())+(char)Integer.parseInt(findstr.substring(2, findstr.length() - 1)));
		start = m.end();
		}
		buf.append(str.substring(start));
		return buf.toString();
		}
    
    /**
     * ��������excelFile
     * �������ļ�·��,����
     * ���أ��Ƿ�ɹ�
     * 
     */
    
    public static  String createExcelFile(String fileName,Object[][] data) throws Exception{
        try{
            WritableWorkbook wwb = Workbook.createWorkbook(new File(fileName));
            WritableSheet     ws  = wwb.createSheet("Sheet1",0);
           
            
            NumberFormat nf = new NumberFormat("0.00");
            WritableCellFormat wcfN = new WritableCellFormat(nf);
            
            NumberFormat nf1 = new NumberFormat("0");
            WritableCellFormat wcfN1 = new WritableCellFormat(nf1);
            for(int i=0;i<data.length;i++){
                for(int j=0;j<data[i].length;j++){
                	ws.setColumnView(j, 20);
                	Object obj = data[i][j];
                     
                	if (obj instanceof Integer){
                		int value = ((Integer)obj).intValue();
                		jxl.write.Number labelNF = new  jxl.write.Number(j, i, value,wcfN1);
                		
                    	ws.addCell(labelNF);
                	}
                	if(obj instanceof String){
                		String value=(String)obj;
                		Label label = new Label(j,i,value);
                        ws.addCell(label); 
                	}
                	if(obj instanceof BigDecimal){
                		double value=((BigDecimal)obj).doubleValue();
                		jxl.write.Number labelNF = new  jxl.write.Number(j, i, value,wcfN);
                    	ws.addCell(labelNF);
                	}
                	
                		
                	
       
                    
                }
                
            }
            wwb.write();
            wwb.close();
            return "ok";
        }catch(Exception e){
           throw new Exception("����Excel�ļ�ʱ����! " + e.toString()); 
        }
        
           
    }
    
    
    
    /**
     * ��������txtFile
     * ���������ݣ��ļ�·��,�зָ���
     * ���أ��Ƿ�ɹ�
     * 
     */
    public static boolean createTxtFile( Object[][] data,String path,String martet) throws Exception{

        boolean flag = true;
        
        PrintStream out1 ;
        StringBuffer sb = new  StringBuffer();
       
        try {
            out1 =new PrintStream(new FileOutputStream(path));
            for(int i=0;i<data.length;i++){
               String row="";
               for(int j=0;j<data[i].length-1;j++){
            	   row +=data[i][j]+martet;
                   
                }
               sb.append(row.substring(0,row.length()-1));
               sb.append("\r\n");
                
            }
            out1.print(sb.toString());
        } catch (Exception e) {
            flag = false;
            throw new Exception("����txt�ļ�ʱ����!"+e.toString());
           
            
        }
        
        
        return flag;

    }
    
    /**
     * ��������txtFile
     * ���������ݣ��ļ�·��,�зָ���
     * ���أ��Ƿ�ɹ�
     * 
     */
    public static void data2StringBuffer( Object[][] data,String martet, StringBuffer sb,String tableName) throws Exception{

       
        sb.append(tableName);
        sb.append("\r\n");
        try {
            
            for(int i=0;i<data.length;i++){
               String row="";
               for(int j=0;j<data[i].length;j++){
            	   row +=data[i][j]+martet;
                   
                }
               sb.append(row.substring(0,row.length()-1));
               sb.append("\r\n");
                
            }
           
        } catch (Exception e) {
            throw new Exception("ת��ΪStringBufferʱ����!"+e.toString());
           
            
        }
        
        

    }
    
    /**
     * ��������txtFile
     * ���������ݣ��ļ�·��,�ֶγ��ȣ�������յ��ַ�
     * ���أ��Ƿ�ɹ�
     * 
     */
    public static boolean data2TxtFile( String[][] data,String path,int colLen[],String fillstring) throws Exception{

        boolean flag = true;
        
        PrintStream out1 ;
        StringBuffer sb = new  StringBuffer();
       
        try {
            out1 =new PrintStream(new FileOutputStream(path));
            for(int i=0;i<data.length;i++){
               for(int j=0;j<data[i].length-1;j++){
                   sb.append(DataFormator.stringAddEOF(data[i][j], colLen[j], fillstring));
                }
               sb.append("\r\n");
                
            }
            out1.print(sb.toString());
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            throw new Exception("����txt�ļ�ʱ����!"+e.toString());
           
            
        }
        
        
        return flag;

    }
   
	public static  String getReportContent(String dataCon,String sql,String reportFile){
         String reportContent="";
         Dispatch dispatch = new Dispatch("printx.adocon");
         reportContent = dispatch.call(dispatch, "fprint", dataCon, sql, reportFile).toString();
         return reportContent;
    }


	public String s_exception;
	//public static final String PROPERTIESFILENAME = "config.properties";
   
    public static void main(String agrs[]){
        //Dispatch test = new Dispatch("printx.adocon");
        String con="Provider=SQLOLEDB.1;Password=salsa;Persist Security Info=True;User ID=sa;Initial Catalog=drp;Data Source=127.0.0.1\\salsa";
        String sql="select a.product_code,a.product_name,a.product_size,a.product_color,a.product_untl,a.product_price,a.fact_num-isnull(a.del_num,0) rel_num,a.fact_amount-isnull(a.del_amount,0) rel_amount,b.Pricetype_name,a.package_demo,a.order_code,agent_name,dbo.getdatestr1(out_date) out_date  from order_product_list a,price_type b,order_list c  where a.price_type=b.pricetype_id and a.order_code=c.order_code and a.order_code='10020080827007' and a.fact_num-isnull(a.del_num,0) !=0 order by a.id";
        //System.out.println(Dispatch.call(test, "fprint", con, sql, "C:\\book6\\printx\\diaoyong\\report\\salsa_fhd.frf"));
        System.out.println(SystemFunction.getReportContent(con, sql, "C:\\book6\\printx\\diaoyong\\report\\salsa_fhd.frf"));
    }
	

}
