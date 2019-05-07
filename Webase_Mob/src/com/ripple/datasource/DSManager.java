package com.ripple.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ripple.Constants;
import com.ripple.datasource.config.Configuration;
import com.ripple.datasource.config.MyDataSource;
import com.ripple.datasource.connection.ConnectionPool;
import com.ripple.datasource.dialect.DB2Dialect;
import com.ripple.datasource.dialect.Dialect;
import com.ripple.datasource.dialect.InformixDialect;
import com.ripple.datasource.dialect.MSSQLDialect;
import com.ripple.datasource.dialect.MySQLDialect;
import com.ripple.datasource.dialect.OracleDialect;

public class DSManager
{
	private static Logger logger = LogManager.getLogger( DSManager.class );
	
	//private static volatile Hashtable DSPools = new Hashtable();
	private static Hashtable DSPools = new Hashtable();
	
	private static String default_ds =  "local";
	
	/*
	 * ��ʼ������Դ���ﶨ������Դ�б���
	 */
	public static void initDataSource( MyDataSource ds ) throws SQLException
	{
		boolean flag = false;
		if( ds == null )
		{
			throw new SQLException("����ԴΪ��!");
		}
		//�������ӻ��弶�Ļ����
        ConnectionPool connectionPool = null;
        //���ػỰ���弶�Ļ����
        ConnectionPool statementPool = null;

		if( ds.getMyConnection().getCoonnectionLevelPoolSize() > 0 )
		{
			try
			{
				connectionPool = new ConnectionPool( ds.getName(),Constants.ConnectionLevel,ds.getMyConnection() );
				ds.setConnectionPool( connectionPool );
				if( connectionPool.isAutoShrink() )
		        {
		            Thread connPoolThread = new Thread( connectionPool );
		            connPoolThread.setDaemon(true);
		            connPoolThread.start();
		        }
			} catch (Exception e)
			{
				logger.info( "��������Դ[" + ds.getName() + "]���ӳس���" + e.getMessage() );
			}
		}

		if( ds.getMyConnection().getStatementLevelPoolSize() > 0 )
		{
			try
			{
				statementPool = new ConnectionPool( ds.getName(),Constants.StatementLevel,ds.getMyConnection() );
				ds.setStatementPool( statementPool );
		        if( statementPool.isAutoShrink() )
		        {
		            Thread stmtPoolThread = new Thread( statementPool );
		            stmtPoolThread.setDaemon(true);
		            stmtPoolThread.start();
		        }
			} catch (Exception e)
			{
				logger.info( "��������Դ[" + ds.getName() + "]�Ự�س���" + e.getMessage() );
			}
		}
		
        if( connectionPool != null && statementPool != null )
        {
        	try 
        	{
				bindDataSource( ds );
				logger.info( "��������Դ[" + ds.getName() + "]�ɹ���" );
			} catch (SQLException e) 
			{
				logger.info( "��������Դ[" + ds.getName() + "]ʧ�ܣ�" );
				e.printStackTrace();
			}
        }else
        {
        	logger.info( "��������Դ[" + ds.getName() + "]ʧ�ܣ�" );
        }
	}
	
	/*
	 * ������Դ�������һ������Դ
	 */
	public static void bindDataSource( MyDataSource ds ) throws SQLException
	{
		if( ds == null )
		{
			throw new SQLException("����ԴΪ��!");
		}
		String DSName = ds.getName();
		if( lookupDataSource( DSName ) == null )
		{
			DSPools.put( DSName, ds );
		}else
		{
			throw new SQLException("����Դ[" + ds.getName() + "]�Ѿ�����!");
		}
	}
	/*
	 * �ͷ�����Դ synchronized
	 */
	public static void unBindDataSource( String DSName ) throws SQLException
	{
		MyDataSource ds = lookupDataSource( DSName );
		if( ds != null )
		{
			try
			{
				//�ر����е�����
				ds.getConnectionPool().close();
				ds.getStatementPool().close();
			} catch ( SQLException e )
			{
				logger.info( "�ͷ�����Դ���� : " + e.getMessage() );
			}
			if( DSPools.remove( DSName ) == null )
			{
				ds = null;
			}
		}else
		{
			throw new SQLException("����Դ[" + DSName + "]û���ҵ�!");
		}
	}
	
	/*
	 * �������ƻ������Դ
	 */
    public static MyDataSource lookupDataSource( String DSName )
    {
        return (MyDataSource)DSPools.get( DSName );
    }   
    /*
     * ������ݿ�����
     */
    public static Connection getConnection() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("û��ָ��Ĭ������Դ��");
    	}
		MyDataSource dso = DSManager.lookupDataSource( default_ds );
		if( dso == null )
		{
			throw new SQLException("û���ҵ�Ĭ������Դ���ã�");
		}
		return dso.getConnectionPool().getConnection();
    }
    
    public static Connection getConnection( String DSName ) throws SQLException
    {
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			if( dso != null )
			{
				initDataSource( dso );
			}else
			{
				throw new SQLException("û���ҵ�Ĭ������Դ���ã�");
			}
		}
		return dso.getConnectionPool().getConnection();
    }
    
    //-------------------------����SQL��������--------------------------------
    /*
     * ���Ĭ�ϵĻỰ����
     */
    public static DSSession getDSSession() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("û��ָ��Ĭ������Դ��");
    	}

    	MyDataSource dso = DSManager.lookupDataSource( default_ds );
    	Connection conn = dso.getConnectionPool().getConnection();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		boolean showResult = dso.showResult;
		String dialectStr = dso.getDialect();
		Dialect dialect = getDBDialect( dialectStr );
		
		return new DSSession( default_ds,conn,DBEncoding,DSEncoding,showResult,dialect );
    }
    /*
     * ���ָ������Դ�ĻỰ����
     */
    public static DSSession getDSSession( String DSName ) throws SQLException
    {
    	if( DSName == null )
    	{
    		throw new SQLException("û��ָ������Դ��");
    	}
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			if( dso != null )
			{
				initDataSource( dso );
			}else
			{
				throw new SQLException("û���ҵ�Ĭ������Դ���ã�");
			}
		}
		
		Connection conn = dso.getConnectionPool().getConnection();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		boolean showResult = dso.showResult;
		String dialectStr = dso.getDialect();
		Dialect dialect = getDBDialect( dialectStr );	
		
		return new DSSession( DSName,conn,DBEncoding,DSEncoding,showResult,dialect );
    }
    /*
     * ���Ĭ�ϸ��¶���
     */
    public static SQLUpdater getSQLUpdater() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("û��ָ��Ĭ������Դ��");
    	}
		MyDataSource dso = DSManager.lookupDataSource( default_ds );
		if( dso == null )
		{
			throw new SQLException("û���ҵ�Ĭ������Դ���ã�");
		}
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );
		
		return new SQLUpdater( default_ds,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
    /*
     * ���ָ������Դ�ĸ��¶���
     */
    public static SQLUpdater getSQLUpdater( String DSName ) throws SQLException
    {
    	if( DSName == null )
    	{
    		throw new SQLException("û��ָ������Դ��");
    	}
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		//�ж�����Դ�Ƿ��Ѿ����أ����û�м�����ʼ��ʼ��������Դ
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			initDataSource( dso );
		}
		
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );

		return new SQLUpdater( DSName,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
    /*
     * ���Ĭ�ϵĲ��Ҷ���
     */
    public static SQLQuery getSQLQuery() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("û��ָ��Ĭ������Դ��");
    	}
		MyDataSource dso = DSManager.lookupDataSource( default_ds );
		if( dso == null )
		{
			throw new SQLException("û���ҵ�Ĭ������Դ���ã�");
		}
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );
		
		return new SQLQuery( default_ds,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
    /*
     * ���ָ������Դ�Ĳ�ѯ����
     */
    public static SQLQuery getSQLQuery( String DSName ) throws SQLException
    {
    	if( DSName == null )
    	{
    		throw new SQLException("û��ָ������Դ��");
    	}
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			initDataSource( dso );
		}
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );

		return new SQLQuery( DSName,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
	
	public static String getDefaultDS()
	{
		return default_ds;
	}
	
	public static void setDefaultDS( String ds )
	{
		default_ds = ds;
	}
	
	private static Dialect getDBDialect( String dialectType ) throws SQLException
	{
		if( dialectType == null )
		{
			throw new SQLException("����ȷ������Դ���ͣ�"); 
		}
		Dialect dialect = null;
		String type = dialectType.toLowerCase();
		if( type .equals( Constants.DATABASE_ORACLE ) )
		{
			dialect = new OracleDialect();
		}else if( type.equals( Constants.DATABASE_MYSQL ))
		{
			dialect = new MySQLDialect();
		}else if( type.equals( Constants.DATABASE_DB2 ))
		{
			dialect = new DB2Dialect();
		}else if( type.equals( Constants.DATABASE_INFORMIX ))
		{
			dialect = new InformixDialect();
		}else if ( type.equals(Constants.DATABASE_SQLSERVER))
		{
			dialect = new MSSQLDialect();
		}
		return dialect;
	}
}
