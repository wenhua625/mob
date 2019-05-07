package com.novarise.webase.util;

import java.util.Map;



public class GhtPay 
{
	/**
	 * 注
	 * GhtClient （请求url，商户号，子商户号（可空），终端号，密钥，加密方式，密钥）
	 */
    public static void main( String[] args )
    {
//    	//查询
    	/*GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com:443/entry.do", "102100000125", "", "20000147", "857e6g8y51b5k365f7v954s50u24h14w", "SHA256", "SEARCH");
    	SearchRqe request = new SearchRqe();
    	request.setOrder_no("123");
    	request.setVersion("2.0.0");
    	ghtClient.pageExecute(request);
    	try {
    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
    	System.out.println(resultMap);
    	}catch (Exception e)
    	{
    		e.printStackTrace();
    	}*/
    	
    	
//    	//大商户支付
//    	GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com/entry.do", "102100000125", "", "20000147", "857e6g8y51b5k365f7v954s50u24h14w", "SHA256", "PAY");
//    	PayRequest request = new PayRequest();
//    	request.setVersion("2.0.0");
//    	request.setOrder_no("12391811112317911321221837");
//    	request.setAmount("0.01");
//    	request.setNotify_url("https://testpay.sicpay.com:443/staging/notifyUrl.jsp");
//    	request.setReturn_url("http://localhost:8080/payDemo/paySubmit.jsp");
//    	request.setProduct_name("名字");
//    	request.setProduct_desc("描述");
//    	request.setProduct_type("类型");
//    	request.setBase64_memo("备注");
//    	request.setCurrency_type("CNY");
//    	request.setSett_currency_type("SNY");
//    	ghtClient.pageExecute(request);
//    	String requsetUrl = ghtClient.getRequestUrl();
//    	//将此url 在浏览器打开
//    	System.out.println(requsetUrl);
//    	
    	//一户一码支付
    	/*GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com:443/entry.do", "000000158120121", "19910000036", "20000125", "b4c80f875bf832f623df9f8231b4cd98", "SHA256", "PAY");
    	PayRequest request = new PayRequest();
    	request.setVersion("1.0.0");
    	request.setOrder_no("123981792837");
    	request.setAmount("0.01");
    	request.setNotify_url("https://testpay.sicpay.com:443/staging/notifyUrl.jsp");
    	request.setReturn_url("https://testpay.sicpay.com:443/staging/returnUrl.jsp");
    	request.setProduct_name("名字");
    	request.setProduct_desc("描述");
    	request.setProduct_type("类型");
    	request.setBase64_memo("备注");
    	request.setCurrency_type("CNY");
    	request.setSett_currency_type("SNY");
    	ghtClient.pageExecute(request);
    	String requsetUrl = ghtClient.getRequestUrl();
    	//将此url 在浏览器打开
    	System.out.println(requsetUrl);*/
    	
//    	//退款
//    	GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com:443/entry.do", "102100000125", "", "20000147", "857e6g8y51b5k365f7v954s50u24h14w", "SHA256", "REFUND");
//    	RefundRequest request = new RefundRequest();
//    	request.setVersion("2.0.0");
//    	//要退款的订单号
//    	request.setOrder_no("123");
//    	request.setRefund_no(Double.toString(System.currentTimeMillis()));
//    	request.setTotal_amount("0.01");
//    	request.setRefund_amount("0.01");
//    	request.setCurrency_type("CNY");
//    	request.setSett_currency_type("CNY");
//    	request.setNotify_url("https://testpay.sicpay.com:443/staging/refundNotifyUrl.jsp");
//    	ghtClient.pageExecute(request);
//    	try {
//    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
//    	System.out.println(resultMap);
//    	}catch (Exception e)
//    	{
//    		e.printStackTrace();
//    	}
    	
//    	//汇率查询
//    	GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com:443/entry.do", "102100000125", "", "20000147", "857e6g8y51b5k365f7v954s50u24h14w", "SHA256", "SEARCH_RATE");
//    	SearchRateRequest request = new SearchRateRequest();
//    	request.setCurrency_type("CNY");
//    	ghtClient.pageExecute(request);
//    	try {
//    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
//    	System.out.println(resultMap);
//    	}catch (Exception e)
//    	{
//    		e.printStackTrace();
//    	}
    	
//    	//退款查询
//    	GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com:443/entry.do", "102100000125", "", "20000147", "857e6g8y51b5k365f7v954s50u24h14w", "SHA256", "SEARCH_REFUND");
//    	SearchRefund request = new SearchRefund();
//    	request.setRefund_no("1625365");
//    	ghtClient.pageExecute(request);
//    	//调用resultTomap方式需要dom4j
//    	try {
//    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
//    	System.out.println(resultMap);
//    	}catch (Exception e)
//    	{
//    		e.printStackTrace();
//    	}
//    	
//    	//下载对账文件
//    	GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com:443/entry.do", "102100000125", "", "20000147", "857e6g8y51b5k365f7v954s50u24h14w", "SHA256", "DOWNLOAD_SETTLE_FILE");
//    	DownloadSettleFileRequest request = new DownloadSettleFileRequest();
//    	request.setSettle_date("20180611");
//    	ghtClient.pageExecute(request);
//    	System.out.println(ghtClient.getResultStr());

    	//验签
//    	RequestUtil requestUtil = new RequestUtil();
//    	Map<String,String[]> params = request.getParameterMap();
//    	TreeMap<String,String> treeParams = new TreeMap<String,String>();
//    	Set es = params.entrySet();
//    	Iterator it = es.iterator();
//    	while(it.hasNext()) {
//    		Map.Entry entry = (Map.Entry)it.next();
//    		String k = (String)entry.getKey();
//    		String v = ((String[]) params.get(k))[0];
//    		treeParams.put(k, v);
//    	}
//    	//Map要有序
//    	boolean signPass = requestUtil.verifySign(treeParams, "857e6g8y51b5k365f7v954s50u24h14w");
    	
    	
   	
//    	//预授权
//    	GhtClient ghtClient = new GhtClient("https://testpay.sicpay.com:443/entry.do", "102100000265", "", "20000222", "1ce63893425a8dd306f6f6a0caeafb62", "SHA256", "AUTH");
//    	AuthRequest request = new AuthRequest();
//    	request.setAmount("0.01");
//    	request.setVersion("2.0.0");
//    	request.setOrder_no("11231211123231123432534");
//    	request.setCurrency_type("CNY");
//    	request.setSett_currency_type("CNY");
//    	request.setProduct_name("name");
//    	request.setBank_code("UPOP");
//    	request.setReturn_url("https://testpay.sicpay.com:443/staging/returnUrl.jsp");
//    	ghtClient.pageExecute(request);
//    	String requestUrl = ghtClient.getRequestUrl();
//    	System.out.println(requestUrl);
    	
//    	//预授权确认
//    	GhtClient ghtClient = new GhtClient("http://120.31.132.119/backStageEntry.do", "102100000265", "", "20000222", "1ce63893425a8dd306f6f6a0caeafb62", "SHA256", "AUTHCOMP");
//    	AuthCompRequest request = new AuthCompRequest();
//    	request.setVersion("2.0.0");
//    	request.setOrder_no("1212312133");
//    	request.setOri_order_no("11231211123231432534");
//    	request.setAmount("0.01");
//    	ghtClient.pageExecute(request);
//    	try {
//    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
//    	System.out.println(resultMap);
//    	}catch (Exception e)
//    	{
//    		e.printStackTrace();
//    	}
    	
//    	//撤销
//    	GhtClient ghtClient = new GhtClient("http://120.31.132.119/backStageEntry.do", "102100000265", "", "20000222", "1ce63893425a8dd306f6f6a0caeafb62", "SHA256", "PAYC");
//    	PayCancelRequest request = new PayCancelRequest();
//    	request.setOrder_no("121231232132123133");
//    	request.setOri_order_no("123");
//    	request.setVersion("2.0.0");
//    	ghtClient.pageExecute(request);
//    	try {
//    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
//    	System.out.println(resultMap);
//    	}catch (Exception e) {
//    		e.printStackTrace();
//		}
    	
//    	//预授权撤销
//    	GhtClient ghtClient = new GhtClient("http://120.31.132.119/backStageEntry.do", "102100000265", "", "20000222", "1ce63893425a8dd306f6f6a0caeafb62", "SHA256", "AUTHC");
//    	AuthCancelRequest request = new AuthCancelRequest();
//    	request.setVersion("2.0.0");
//    	request.setOrder_no("32112312323");
//    	request.setOri_order_no("11231211123231123432534");
//    	ghtClient.pageExecute(request);
//    	try {
//    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
//    	System.out.println(resultMap);
//    	}catch (Exception e) {
//    		e.printStackTrace();
//		}
    	
//    	//预授权确认撤销
//    	GhtClient ghtClient = new GhtClient("http://120.31.132.119/backStageEntry.do", "102100000265", "", "20000222", "1ce63893425a8dd306f6f6a0caeafb62", "SHA256", "AUTHCOMPC");
//    	AuthCompCancelRequest request = new AuthCompCancelRequest();
//    	request.setVersion("2.0.0");
//    	request.setOrder_no("12312336546");
//    	request.setOri_order_no("1212312133");
//    	ghtClient.pageExecute(request);
//    	try {
//    	Map<String,String> resultMap = ghtClient.resultToMap(ghtClient.getResultStr());
//    	System.out.println(resultMap);
//    	}catch (Exception e) {
//    		e.printStackTrace();
//		}
    }
}

