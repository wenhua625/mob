package com.novarise.webase.util;

public class HttpPostModel {

	private int responseCode;
	private String responseString;
	
	/**
	 * ����HTTP״ֵ̬��200������ɹ�
	 * @param responseCode
	 */
	public void set_ResponseCode(int responseCode){
		this.responseCode=responseCode;
	}
	
	/**
	 * ��ȡHTTP״ֵ̬��200������ɹ�
	 * @return
	 */
	public int get_ResonseCode(){
		return this.responseCode;
	}
	
	/**
	 * ����HTTP����ֵ
	 * @param responseString
	 */
	public void set_ResponseString(String responseString){
		this.responseString=responseString;
	}
	
	/**
	 * ��ȡHTTP����ֵ
	 * @return
	 */
	public String get_ResponseString(){
		return this.responseString;
	}
	
}