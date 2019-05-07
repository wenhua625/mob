package com.novarise.webase.util;

import java.sql.SQLException;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.config.MyDataSource;
import com.ripple.datasource.connection.ConnectionPool;
import com.ripple.datasource.dialect.Dialect;

import push.UMengPush;

public class UMengPushUtil {
	
	public static void main(String args[])
	{
		
		/*UMengPush push=new UMengPush("56975e8467e58e71fc002730", "sa7dqq7b5wvlo6vxcqwnosmugcg8tgpn");
		UMengPush ios_push=new UMengPush("5737e6f467e58e559f001ec5", "sc6jtxwkdbq7qsmgx10megbuzpnov0up");
		try {
			//push.sendAndroidBroadcast("获取密码","获取密码","你的密码是：38434");
			 ios_push.sendIOSUnicast("test ideng test","1ed08ea3ee03a427dce9b8fd49b56da8bc06a235caf8f037d327ca554788f567");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String aa="JJ0001";
		System.out.println(aa.substring(2,6));
	}
	
	/*
     * 获得推送对象
     */
    public static UMengPush getPush() throws SQLException
    {
    	
		
		return new UMengPush("56975e8467e58e71fc002730", "sa7dqq7b5wvlo6vxcqwnosmugcg8tgpn");
    }
    
    /*
     * 获得推送对象
     */
    public static UMengPush getIOSPush() throws SQLException
    {
    	
		
		return new UMengPush("569ee75b67e58e96240007ee", "1ycjkovvwlr5beywhh5565fsyvxqfweb");
    }
	
	

}
