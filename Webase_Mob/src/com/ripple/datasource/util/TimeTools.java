package com.ripple.datasource.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TimeTools
{
	private static Logger logger = LogManager.getLogger( TimeTools.class );
	
	public static String getPeriodOfTime(long beginTime,long endTime)
	{
		if( beginTime == endTime )
		{
			return "0 ����";
		}else if( beginTime == endTime )
		{
			return "��ʱ����";
		}
		long periodOfTime = endTime - beginTime;
		long hourTime = periodOfTime/(60*60*1000);
		long minuteTime = ( periodOfTime%( 60*60*1000 ) )/(60*1000);
		long secondTime = (( periodOfTime%( 60*60*1000 ) )%(60*1000) )/1000;
		long millisecondTime = (( periodOfTime%( 60*60*1000 ) )%(60*1000) )%1000;
		
		StringBuffer timeStr = new StringBuffer();
		if( hourTime > 0 )
		{
			timeStr.append( hourTime ).append( " ʱ" );
		}
		if( minuteTime > 0 )
		{
			timeStr.append( minuteTime ).append( " ��" );
		}
		if( secondTime > 0 )
		{
			timeStr.append( secondTime ).append( " ��" );
		}
		if( millisecondTime > 0 )
		{
			timeStr.append( millisecondTime ).append( " ����" );
		}
		return timeStr.toString();
	}
}
