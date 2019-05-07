package com.ripple.datasource;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ripple.datasource.connection.ConnectionPool;
import com.ripple.datasource.dialect.Dialect;
/*
 ִ��INSERT��UPDATE��DELETE��DDL�����ݶ������ԣ����
 ����ֵ��һ������,��ʾ��ִ�е�SQL�����Ӱ������ݿ��еı�����������¼�����
*/
public class SQLUpdater
{
	private static Logger logger = LogManager.getLogger( SQLUpdater.class );
	
	//����Դ����
	private String DSName;
	//���ݿ����
	//private String DBEncoding;
	//����Դ����
	//private String DSEncoding;
	//���ݿ�����
	//private Dialect dialect;
	//���ݿ�����
	private ConnectionPool cp;
	//�Ƿ���ʾ���
	private boolean ShowResult;
	
	public SQLUpdater( String DSName,ConnectionPool cp,String DBEncoding,String DSEncoding,boolean ShowResult,Dialect dialect )  
	{
		this.DSName = DSName;
		this.cp = cp;
		//this.DBEncoding = DBEncoding;
		//this.DSEncoding = DSEncoding;
		//this.dialect = dialect;
		this.ShowResult = ShowResult;
	}

	public int executeUpdate( String SQL ) throws SQLException
	{
		int result = 0;
		if( ShowResult )
		{
			logger.info( "[SQL���]: " + SQL );
		}
		Statement stmt = null;
		try
		{
			stmt = cp.createStatement();
			result = stmt.executeUpdate( SQL );
		}catch(SQLException sqle)
		{
			logger.info("���³���: " + sqle.getMessage() );
		
			throw sqle;
		}finally
		{
			stmt.close();
			stmt = null;
		}
		return result;
	}
	
	public int executePrepareUpdate( String SQL, Object[] object ) throws SQLException
	{
		int result = 0;
		if( ShowResult )
		{
			logger.info( "[SQL���]: " + SQL );
		}
		PreparedStatement ps = null;
		try
		{
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				if(object[i] == null)
				{
					ps.setObject(i+1, null,Types.VARCHAR);
				}else
				{
					ps.setObject( i+1,object[i] );
				}
				
			}
			result = ps.executeUpdate();
		}catch(SQLException sqle)
		{
			logger.info("���³���: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			ps.close();
			ps = null;
		}
		return result;
	}
	
	public int executeCall( String SQL ) throws SQLException
	{
		int result = 0;
		if( ShowResult )
		{
			logger.info( "[SQL���]: " + SQL );
		}
		CallableStatement stmt = null;
		try
		{
			
			stmt = cp.prepareCall( SQL );
			if( stmt.execute() ){
				result = 1;
			}
			
			
			
		}catch(SQLException sqle)
		{
			logger.info("���³���: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			stmt.close();
			stmt = null;
		}
		return result;
	}

	public String getDSName()
	{
		return DSName;
	}

	public boolean isShowResult()
	{
		return ShowResult;
	}
}
