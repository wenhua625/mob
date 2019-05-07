package com.novarise.webase.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateHelper
{
	/**
	 * ȡ�õ�ǰϵͳʱ��
	 */
	public static String getDateTime()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
		return sdf.format( now );
	}
	
	public static String getDateTimeCN()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��Hʱm��s��");
		return sdf.format( now );
	}
	
	public static String getShortDateTime()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return sdf.format( now );
	}
	
	
	public static String getShowDateTime()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d HH:mm");
		return sdf.format( now );
	}
	
	public static String getAppDateTime()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format( now );
	}
	
	public static String getShortAppDateTime()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format( now );
	}
	/*
	 * ȡ�õ�ǰϵͳ����
	 */
	public static String getDate()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
		return sdf.format( now );
	}
	
	public static String getDateCN()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��");
		return sdf.format( now );
	}
	
	public static String getShortDate()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format( now );
	}
	
	public static String getShortDate1()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		return sdf.format( now );
	}
	
	public static String getShortDateTimeTrim()
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format( now );
	}
	/*
	 * ȡ�����ĵ�����
	 */
	public static String getDayCN()
	{
		Date now = new Date();
		int day = now.getDay();
		StringBuffer dayStr = new StringBuffer("����");
		if( day == 0 )
		{
			dayStr.append("��");
		}else if( day == 1 )
		{
			dayStr.append("һ");
		}else if( day == 2 )
		{
			dayStr.append("��");
		}else if( day == 3 )
		{
			dayStr.append("��");
		}else if( day == 4 )
		{
			dayStr.append("��");
		}else if( day == 5 )
		{
			dayStr.append("��");
		}else if( day == 6 )
		{
			dayStr.append("��");
		}
		return dayStr.toString();
	}
	
	/** *//**
     * ȡ��ĳ�����(��)�����һ��
     * 
     * @param date
     * @param num(�����ɸ�)
     * @return
     */
    public static Date getAnotherDate(Date date, int num) {
        Calendar c = Calendar.getInstance();
        
        c.setTime(date);
        c.add(Calendar.DAY_OF_YEAR, num);
        return c.getTime();
    }

    /** *//**
     * ȡ��ĳ�µĵ����һ��
     * 
     * @param year
     * @param month
     * @return
     */
    public static Date getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);// ��
        cal.set(Calendar.MONTH, month - 1);// �£���ΪCalendar������Ǵ�0��ʼ������Ҫ��1
        cal.set(Calendar.DATE, 1);// �գ���Ϊһ��
        cal.add(Calendar.MONTH, 1);// �·ݼ�һ���õ��¸��µ�һ��
        cal.add(Calendar.DATE, -1);// ��һ���¼�һΪ�������һ��
        return cal.getTime();// �����ĩ�Ǽ���
    }
    
    /** *//**
     * ȡ�õ�ǰ�µĵ����һ��
     * 
     * @param year
     * @param month
     * @return
     */
    public static String getLastDayOfCurrentMonth() {
        Date now =new Date();
        
        int year = now.getYear()+1900;
        int month = now.getMonth()+1;
    	Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);// ��
        cal.set(Calendar.MONTH, month - 1);// �£���ΪCalendar������Ǵ�0��ʼ������Ҫ��1
        cal.set(Calendar.DATE, 1);// �գ���Ϊһ��
        cal.add(Calendar.MONTH, 1);// �·ݼ�һ���õ��¸��µ�һ��
        cal.add(Calendar.DATE, -1);// ��һ���¼�һΪ�������һ��
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        
        return sdf.format(cal.getTime());// �����ĩ�Ǽ���
    }
    
    /** *//**
     * ȡ�õ�ǰ�µĵĵ�һ��
     * 
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDayOfCurrentMonth() {
        Date now =new Date();
        
        int year = now.getYear()+1900;
        int month = now.getMonth()+1;
    	Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);// ��
        cal.set(Calendar.MONTH, month - 1);// �£���ΪCalendar������Ǵ�0��ʼ������Ҫ��1
        cal.set(Calendar.DATE, 1);// �գ���Ϊһ��
        //cal.add(Calendar.MONTH, 1);// �·ݼ�һ���õ��¸��µ�һ��
        //cal.add(Calendar.DATE, -1);// ��һ���¼�һΪ�������һ��
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");
        
        return sdf.format(cal.getTime());// �����ĩ�Ǽ���
    }

    /** *//**
     * ȡ��ĳ����һ���еĶ�����
     * 
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /** *//**
     * ȡ��ĳ�������ܵĵ�һ��
     * 
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }

    /** *//**
     * ȡ��ĳ�������ܵ����һ��
     * 
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }

    /** *//**
     * ȡ��ĳһ�깲�ж�����
     * 
     * @param year
     * @return
     */
    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        return getWeekOfYear(c.getTime());
    }
    
    /** *//**
     * ȡ��ĳ��ĳ�ܵĵ�һ��
     * ���ڽ���:2008-12-29��2009-01-04����2008������һ��,2009-01-05Ϊ2009���һ�ܵĵ�һ��
     * @param year
     * @param week
     * @return
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar calFirst = Calendar.getInstance();
        calFirst.set(year, 0, 7);
        Date firstDate = getFirstDayOfWeek(calFirst.getTime());

        Calendar firstDateCal = Calendar.getInstance();
        firstDateCal.setTime(firstDate);

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, (week - 1) * 7);
        firstDate = getFirstDayOfWeek(cal.getTime());

        return firstDate;
    }

    /** *//**
     * ȡ��ĳ��ĳ�ܵ����һ��
     * ���ڽ���:2008-12-29��2009-01-04����2008������һ��,2009-01-04Ϊ2008�����һ�ܵ����һ��
     * @param year
     * @param week
     * @return
     */
    public static Date getLastDayOfWeek(int year, int week) {
        Calendar calLast = Calendar.getInstance();
        calLast.set(year, 0, 7);
        Date firstDate = getLastDayOfWeek(calLast.getTime());

        Calendar firstDateCal = Calendar.getInstance();
        firstDateCal.setTime(firstDate);

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, (week - 1) * 7);
        Date lastDate = getLastDayOfWeek(cal.getTime());
        
        return lastDate;
    }

    public static void main(String args[]){
    	//System.out.println(DateHelper.getFirstDayOfCurrentMonth());
    	String msgcontent="�ʹ���������";
    	
    	System.out.println(msgcontent.indexOf("����"));
    	
    	if(msgcontent.indexOf("����")!=-1 || msgcontent.indexOf("������")!=-1)
		 {
    		
		 }else{
			 System.out.println("dfdfd");
		 }
    }
	

}
