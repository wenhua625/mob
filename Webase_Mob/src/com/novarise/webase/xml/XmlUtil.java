package com.novarise.webase.xml;

//�ļ��� 
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

public class XmlUtil {
	
		/** 
	    * doc2String 
	    * ��xml�ĵ�����תΪString 
	    * @return �ַ��� 
	    * @param document 
	    */ 
	   public static String doc2String(Document document) 
	   { 
	      String s = ""; 
	      try 
	      { 
	           //ʹ�������������ת�� 
	           ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	           //ʹ��GB2312���� 
	           OutputFormat format = new OutputFormat("  ", true, "GB2312"); 
	           XMLWriter writer = new XMLWriter(out, format); 
	           writer.write(document); 
	           s = out.toString("GB2312"); 
	      }catch(Exception ex) 
	      {             
	           ex.printStackTrace(); 
	      }       
	      return s; 
	   }
	   
	   /** 
	    * string2Document 
	    * ���ַ���תΪDocument 
	    * @return  
	    * @param s xml��ʽ���ַ��� 
	    */ 
	   public static Document string2Document(String s) 
	   { 
	      Document doc = null; 
	      try 
	      { 
	           doc = DocumentHelper.parseText(s); 
	      }catch(Exception ex) 
	      {             
	           ex.printStackTrace(); 
	      } 
	      return doc; 
	   }
	   
	   /** 
	    * doc2XmlFile 
	    * ��Document���󱣴�Ϊһ��xml�ļ������� 
	    * @return true:����ɹ�  flase:ʧ�� 
	    * @param filename ������ļ��� 
	    * @param document ��Ҫ�����document���� 
	    */ 
	   public static boolean doc2XmlFile(Document document,String filename) 
	   { 
	      boolean flag = true; 
	      try 
	      { 
	            /* ��document�е�����д���ļ��� */ 
	            //Ĭ��ΪUTF-8��ʽ��ָ��Ϊ"GB2312" 
	            OutputFormat format = OutputFormat.createPrettyPrint(); 
	            format.setEncoding("GB2312"); 
	            XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)),format); 
	            writer.write(document); 
	            writer.close();             
	        }catch(Exception ex) 
	        { 
	            flag = false; 
	            ex.printStackTrace(); 
	        } 
	        return flag;       
	   }
       
       
       /**
         * ��������txtFile
         * 
         */
        public static boolean data2TxtFile( String sql,String path){

            boolean flag = true;
            File file=new File(path);
            PrintWriter out1 ;
            SQLQuery query;
            String data[][];
            StringBuffer sb = new  StringBuffer();
            try {
                out1 =new PrintWriter(new BufferedWriter(new FileWriter(file)));
                query = DSManager.getSQLQuery();
                data = query.ArrayQuery(sql);
                for(int i=0;i<data.length;i++){
                    sb.append(DataFormator.stringAddEOF(data[i][0],30," "));
                    sb.append(DataFormator.stringAddEOF(data[i][1],20," "));
                    sb.append("\n");
                    
                }
                out1.print(sb.toString());
            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
                
            }
            
            
            return flag;

        }
	   
	   /** 
	    * string2XmlFile 
	    * ��xml��ʽ���ַ�������Ϊ�����ļ�������ַ�����ʽ������xml�����򷵻�ʧ�� 
	    * @return true:����ɹ�  flase:ʧ�� 
	    * @param filename ������ļ��� 
	    * @param str ��Ҫ������ַ��� 
	    */ 
	   public static boolean string2XmlFile(String str,String filename) 
	   { 
	      boolean flag = true; 
	      try 
	      { 
	         Document doc =  DocumentHelper.parseText(str);        
	         flag = doc2XmlFile(doc,filename); 
	      }catch (Exception ex) 
	      { 
	         flag = false; 
	         ex.printStackTrace(); 
	      } 
	      return flag; 
	   }
	   
	   /** 
	    * load 
	    * ����һ��xml�ĵ� 
	    * @return �ɹ�����Document����ʧ�ܷ���null 
	    * @param uri �ļ�·�� 
	    */ 
	   public static Document load(String filename) throws Exception
	   { 
		  ClassLoader cl = XmlUtil.class.getClassLoader();
		  InputStreamReader fs = new InputStreamReader(cl
					.getResourceAsStream(filename));
		  Document document = null; 
	      SAXReader saxReader = new SAXReader(); 
	      document = saxReader.read(fs); 
	      return document; 
	   }
	   
	   /** 
	    * load 
	    * ����һ��xml�ĵ� 
	    * @return �ɹ�����Document����ʧ�ܷ���null 
	    * @param uri �ļ�·�� 
	    */ 
	   public static Document load(InputStream is) 
	   { 
	      Document document = null; 
	      try  
	      {  
	          SAXReader saxReader = new SAXReader(); 
	          document = saxReader.read(is); 
	          
	      } 
	      catch (Exception ex){ 
	          ex.printStackTrace(); 
	      }   
	      return document; 
	   }
	   
	   /** 
	    * xmlWriteDemoByString 
	    * ��ʾString����Ϊxml�ļ� 
	    */ 
	   public void xmlWriteDemoByString() 
	   { 
	      String s = ""; 
	      /** xml��ʽ���� "<?xml version='1.0' encoding='GB2312'?>" ���Բ���д*/ 
	      s = "<config>\r\n" 
	         +"   <ftp name='DongDian'>\r\n" 
	         +"     <ftp-host>127.0.0.1</ftp-host>\r\n" 
	         +"     <ftp-port>21</ftp-port>\r\n" 
	         +"     <ftp-user>cxl</ftp-user>\r\n" 
	         +"     <ftp-pwd>longshine</ftp-pwd>\r\n" 
	         +"     <!-- ftp��ೢ�����Ӵ��� -->\r\n" 
	         +"     <ftp-try>50</ftp-try>\r\n" 
	         +"     <!-- ftp���������ӳ�ʱ�� -->\r\n" 
	         +"     <ftp-delay>10</ftp-delay>\r\n" 
	         +"  </ftp>\r\n" 
	         +"</config>\r\n"; 
	      //���ļ����ɵ�classes�ļ������ڵ�Ŀ¼��    
	      string2XmlFile(s,"xmlWriteDemoByString.xml");    
	      //���ļ����ɵ�classes�ļ�����    
	      string2XmlFile(s,"classes/xmlWriteDemoByString.xml");   
	   }
	   
	   /** 
	    * ��ʾ�ֶ�����һ��Document��������ΪXML�ļ� 
	    */ 
	   public void xmlWriteDemoByDocument() 
	   { 
	        /** ����document���� */ 
	        Document document = DocumentHelper.createDocument(); 
	        /** ����config���ڵ� */ 
	        Element configElement = document.addElement("config"); 
	        /** ����ftp�ڵ� */ 
	        configElement.addComment("����ftp����"); 
	        Element ftpElement = configElement.addElement("ftp"); 
	        ftpElement.addAttribute("name","DongDian"); 
	        /** ftp �������� */ 
	        Element hostElement = ftpElement.addElement("ftp-host"); 
	        hostElement.setText("127.0.0.1"); 
	        (ftpElement.addElement("ftp-port")).setText("21"); 
	        (ftpElement.addElement("ftp-user")).setText("cxl"); 
	        (ftpElement.addElement("ftp-pwd")).setText("longshine"); 
	        ftpElement.addComment("ftp��ೢ�����Ӵ���"); 
	        (ftpElement.addElement("ftp-try")).setText("50"); 
	        ftpElement.addComment("ftp���������ӳ�ʱ��"); 
	        (ftpElement.addElement("ftp-delay")).setText("10");     
	        /** ����Document */ 
	        doc2XmlFile(document,"classes/xmlWriteDemoByDocument.xml"); 
	   }
	   
	   public static String[][] xmlFile2Array(String xmlFile) throws Exception{
			  List ls=new ArrayList();
			  Document doc=load(xmlFile);
			  List rowList = doc.selectNodes("/data/row");
			  
			  Iterator rowIt = rowList.iterator();
			  while (rowIt.hasNext()){
				  Element row = (Element)rowIt.next();
				  
					  Iterator colIt = row.elementIterator();
					  String [] rows = new String[row.elements().size()];
					  int i=0;
					  while(colIt.hasNext()){
						  Element col = (Element)colIt.next();
						  rows[i] = col.getText();
						  i++;
					  }
					  ls.add(rows);
				  
			  }
			  String r_data[][] =new String[ls.size()][];
			  Object s[]=ls.toArray();
			  for(int i=0;i<s.length;i++){
				  r_data[i] = (String[])s[i];
			  }
			  
			  return r_data; 
		  }
	   
	  public static String[][] find(String xmlFile,String key,String value) throws Exception{
		  List ls=new ArrayList();
		  Document doc=load(xmlFile);
		  List rowList = doc.selectNodes("/data/row");
		  
		  Iterator rowIt = rowList.iterator();
		  while (rowIt.hasNext()){
			  Element row = (Element)rowIt.next();
			  if(row.element(key).getText().equals(value))
			  {
				  Iterator colIt = row.elementIterator();
				  String [] rows = new String[row.elements().size()];
				  int i=0;
				  while(colIt.hasNext()){
					  Element col = (Element)colIt.next();
					  rows[i] = col.getText();
					  i++;
				  }
				  ls.add(rows);
			  }
		  }
		  String r_data[][] =new String[ls.size()][];
		  Object s[]=ls.toArray();
		  for(int i=0;i<s.length;i++){
			  r_data[i] = (String[])s[i];
		  }
		  
		  return r_data; 
	  }
	  
	  
	  /** 
	   * ��xmlDocumentת��ΪJSON���� 
	   * @param xmlDocument XML Document 
	   * @return JSON���� 
	   */  
	  public static String getJSONTreeXml(String xmlFile) throws Exception {
		  /*Document xmlDocument=load(xmlFile);
	      String xmlString = xmlDocument.toString(); 
	      XMLSerializer xmlSerializer = new XMLSerializer();  
	      JSON json = xmlSerializer.read(xmlString);*/
		  StringBuffer sb = new StringBuffer();
		  sb.append("[");
		  String[][] nodeArr = new String[0][0];
		  nodeArr=xmlFile2Array(xmlFile);
		  
	      return sb.toString();  
	  }  
	  
	  public static String readXml(String xmlFile,String xPath) throws Exception{
		  String text="";
		  try{
		  Document doc = load(xmlFile);
		  Node node=doc.selectSingleNode(xPath);
		  text=node.getText();
		  }catch(Exception e){
			 throw new Exception ("��ȡ["+xmlFile+"]ʱ����"+e.toString()); 
		  }
		  return text;
		  
	  }
	   
	   /** 
	    *  ��ʾ��ȡ�ļ��ľ���ĳ���ڵ��ֵ  
	    */ 
	   public static void xmlReadDemo() throws Exception
	   { 
		   String [][] data=find("mj4tjConfig.xml","KJM","MJ0038");
		   for(int i=0;i<data.length;i++){
			   for(int j=0;j<data[i].length;j++){
				   System.out.println(data[i][j]);  
			   }
		   }
		   
	      
	   }
	   
	   public static void main(String args[])
	   {
		   
	   }
	  
	   
	   
}
