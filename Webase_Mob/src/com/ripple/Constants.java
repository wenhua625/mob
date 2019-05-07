package com.ripple;

public interface Constants
{
	//���ݿ�����
	public static final String DATABASE_INFORMIX = "informix";
	public static final String DATABASE_HSQLDB = "hsqldb";
	public static final String DATABASE_ORACLE = "oracle";
	public static final String DATABASE_SQLSERVER = "sqlserver";
	public static final String DATABASE_DB2 = "db2";
	public static final String DATABASE_SYBASE = "sybase";
	public static final String DATABASE_MYSQL = "mysql";
	public static final String DATABASE_POSTGRESQL = "postgresql";
	//���ݿ����ӻ�������
    public static int StatementLevel  = 0;
    public static int ConnectionLevel = 1;
    //��������
    public static int TRANSACTION_JDBC = 0;
    public static int TRANSACTION_JTA  = 1;
    //������뼶��
    public static final int ISOLATION_READ_UNCOMMITTED = 1;
    public static final int ISOLATION_READ_COMMITTED   = 2;
    public static final int ISOLATION_REPEATABLE_READ  = 4;
    public static final int ISOLATION_SERIALIZABLE     = 8;
}
