package com.novarise.webase.framework;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class FirstMail {
	
	 public  boolean sendMail(String from,String username,String password,String to,String subject,String content,String filename) throws Exception
	  {
	     Properties prps=SystemFunction.loadPropertiesFile("jmail.properties") ;
	     Session s=Session.getInstance(prps);
	     MimeMessage message=new MimeMessage(s);
	     Multipart mp=new MimeMultipart();

	     InternetAddress i_from=new InternetAddress(from);
	     InternetAddress  i_to=new InternetAddress(to);

	     message.setFrom(i_from) ;
	     message.setRecipient(Message.RecipientType.TO,i_to);

	     message.setSubject(subject);
	     message.setSentDate(new Date());
	     BodyPart bp=new MimeBodyPart();
	     bp.setContent(content,"text/html;charset=GB2312");

	     mp.addBodyPart(bp);

	     BodyPart bp1=new MimeBodyPart();
	     FileDataSource files=new FileDataSource(filename);
	     bp1.setDataHandler(new DataHandler(files));
	     bp1.setFileName(MimeUtility.encodeWord( files.getName() ,"GB2312",null));


	     mp.addBodyPart(bp1);


	     message.setContent(mp) ;
	     message.saveChanges() ;



	     Transport transport=s.getTransport("smtp");
	     transport.connect(prps.getProperty("mail.smtp.host"),username,password);
	     transport.sendMessage(message,message.getAllRecipients());

	     transport.close();

	     return true;



	  }
	 /*
	  * ���������ʼ�����
	  */
	 public  boolean sendMail(String to,String subject,String content,String filename)
	 {

	   try
	   {
	     //��ȡ�����ʼ���������������Ϣ
	     Properties prps = SystemFunction.loadPropertiesFile("jmail.properties");
	     Session s = Session.getInstance(prps);
	     MimeMessage message = new MimeMessage(s);
	     Multipart mp = new MimeMultipart();
	     //�����ʼ��ķ������ʼ���ַ
	     InternetAddress i_from = new InternetAddress(prps.getProperty("from"));
	     InternetAddress i_to = new InternetAddress(to);
	     message.setFrom(i_from);
	     message.setRecipient(Message.RecipientType.TO, i_to);
	     //�����ʼ�������
	     message.setSubject(subject);
	     message.setSentDate(new Date());
	     BodyPart bp = new MimeBodyPart();
	     bp.setContent(content, "text/html;charset=GB2312");
	     mp.addBodyPart(bp);

	     //��Ӹ���
	     BodyPart bp1=new MimeBodyPart();
	     FileDataSource files=new FileDataSource(filename);
	     bp1.setDataHandler(new DataHandler(files));
	     bp1.setFileName(MimeUtility.encodeWord( files.getName() ,"GB2312",null));
	     mp.addBodyPart(bp1);


	     message.setContent(mp);
	     message.saveChanges();

	     //�����ʼ���Ϣ
	     Transport transport = s.getTransport("smtp");

	     transport.connect(prps.getProperty("mail.smtp.host"), prps.getProperty("username"), prps.getProperty("passwd"));
	     transport.sendMessage(message, message.getAllRecipients());

	     transport.close();
	   }
	   catch(Exception e)
	   {
	        return  false;
	   }

	   return  true;
	 }
	 
	 public  boolean sendMail(String to,String subject,String content)
	  {

	    try
	    {
	      //��ȡ�����ʼ���������������Ϣ
	      Properties prps = SystemFunction.loadPropertiesFile("jmail.properties");
	      Session s = Session.getInstance(prps);
	      MimeMessage message = new MimeMessage(s);
	      Multipart mp = new MimeMultipart();
	      //�����ʼ��ķ������ʼ���ַ
	      InternetAddress i_from = new InternetAddress(prps.getProperty("from"));
	      InternetAddress i_to = new InternetAddress(to);
	      message.setFrom(i_from);
	      message.setRecipient(Message.RecipientType.TO, i_to);
	      //�����ʼ�������
	      message.setSubject(subject);
	      message.setSentDate(new Date());
	      BodyPart bp = new MimeBodyPart();
	      bp.setContent(content, "text/html;charset=GB2312");
	      mp.addBodyPart(bp);



	      message.setContent(mp);
	      message.saveChanges();

	      //�����ʼ���Ϣ
	      Transport transport = s.getTransport("stmp");

	      transport.connect(prps.getProperty("mail.smtp.host"), prps.getProperty("username"), prps.getProperty("passwd"));
	      transport.sendMessage(message, message.getAllRecipients());

	      transport.close();
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    	return  false;
	    }

	   return  true;
	  }
	 
	 public static void main(String args[]){
		 FirstMail ff=new FirstMail();
		
		 boolean  ss=ff.sendMail("tangst@adtec.com.cn", "Happy ToDay!(���ֵ�һ��)","Today is Thursday,and my test is being!...............");
		 System.out.println(ss);
	 }
}
