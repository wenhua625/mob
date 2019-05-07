package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.novarise.webase.BConstants;
import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.framework.WebControl;
import com.novarise.webase.util.GetConfigureInfo;
import com.novarise.webase.util.LikeQrCode;
import com.novarise.webase.util.QrCodeUtil;
import com.novarise.webase.util.WXAppletUserInfo;
import com.novarise.webase.util.WeChatCorporatePayment;
import com.novarise.webase.util.WeiXinUtil;
import com.novarise.webase.xml.XmlUtil;

public class Work extends HttpServlet {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/json; charset=utf-8";
	/**
	 * Constructor of the object.
	 */
	public Work() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}



	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType(CONTENT_TYPE);
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();
		WebControl control = new WebControl();
		//UMengPush push=new UMengPush("56975e8467e58e71fc002730", "sa7dqq7b5wvlo6vxcqwnosmugcg8tgpn");
		
		String loginFlag = (String)(request.getSession().getAttribute("LS.YHDM"));
		String htmlfile=" ";
		String proname = request.getParameter("proname");
		if (proname == null || proname.trim().length() == 0) {
			String error = SystemFunction.showError(101, "错误!没有要处理的事件", "错误!没有要处理的事件",request);
			out.println(error);
			return;
		}
		
		
		//微信授权   艾来客
		if(proname.equals("AUTHWXOPEN")){
			try{
				out.println(WXAppletUserInfo.getSessionKeyOropenid(request));
				return;
			}catch(Exception e){
				e.printStackTrace();
				System.out.println();
				String error = SystemFunction.showError(101, "系统微信授权时出错", "系统微信授权时出错",request);
				out.println(error);
				return    ;
			}
		}
		//微信授权   艾修
		if(proname.equals("AUTHWXOPEN1")){
			try{
				out.println(QrCodeUtil.getSessionKeyOropenid(request));
				return;
			}catch(Exception e){
				e.printStackTrace();
				System.out.println();
				String error = SystemFunction.showError(101, "系统微信授权时出错", "系统微信授权时出错",request);
				out.println(error);
				return ;   
			}
		}
		//解密数据，获取手机号
		if(proname.equals("AUTHWXPHONE")){
			try{
				out.println(WeiXinUtil.deencry(request));
			}catch(Exception e){
				e.printStackTrace(out);
				String error = SystemFunction.showError(101, "系统微信授权时出错", "系统微信授权时出错",request);
				out.println(error);
				return;
			}	
		}
		//根据经纬度，获取省市县
		if(proname.equals("AUTHWXADDRESS")){
			try{
				out.println(GetConfigureInfo.getAdd(request));
			}catch(Exception e){
				e.printStackTrace(out);
				String error = SystemFunction.showError(101, "系统获取省市县出错", "系统获取省市县出错",request);
				out.println(error);
				return;
			}	
		}
		
		if(proname.equals("QUIT")){
			try{
				control.parseDisplayQUIT( request, response);
			}catch(Exception e){
				String error = SystemFunction.showError(101, "系统退出时出错", "系统退出时出错",request);
				out.println(error);
				return;
			}
			
		}
		
		//艾订货登录，先获取前缀，再登录
		if(proname.equals("LOGON")){
			//System.out.println("Login..");
			try{
				
				out.println( control.parseDisplayQX(request, response));
			
				return;
			}catch(Exception e){
				String error = SystemFunction.showError(101, "系统登陆时出错", "系统登陆时出错["+e.getMessage()+"]",request);
				out.println(error);
				return;
			}
			
			
		}
		
		//自动登录，直接登录
				if(proname.equals("LOGONAUTO")){
					//System.out.println("Login..");
					try{
						
						out.println( control.parseDisplayQXAUTO(request, response));
					
						return;
					}catch(Exception e){
						String error = SystemFunction.showError(101, "系统登陆时出错", "系统登陆时出错["+e.getMessage()+"]",request);
						out.println(error);
						return;
					}
					
					
				}
		//艾订货登录，短信验证码登录
		if(proname.equals("LOGONSMS")){
			//System.out.println("Login..");
			try{
				out.println( control.parseDisplayQXSMS(request, response));
				return;
			}catch(Exception e){
				String error = SystemFunction.showError(101, "系统登陆时出错", "系统登陆时出错["+e.getMessage()+"]",request);
				out.println(error);
				return;
			}
			
			
		}
		//客串串微信登录
		if(proname.equals("LOGONWX")){
			//System.out.println("Login..");
			try{
				out.println( control.parseDisplayWXQX(request, response));
				return;
			}catch(Exception e){
				String error = SystemFunction.showError(101, "系统登陆时出错", "系统登陆时出错["+e.getMessage()+"]",request);
				out.println(error);
				return;
			}
			
			
		}
		
		
		
		
		if(proname.substring(0,4).equals("TREE")){
			try{
				if(loginFlag == null){
				    return ;
				}
				response.setContentType("text/xml;charset=GB2312");
				
				htmlfile = control.parseDisplayTREE(proname, request, response);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
		if(proname.substring(0,4).equals("JREE")){
			try{
				if(loginFlag == null){
				    return ;
				}
				htmlfile = control.parseDisplayJSONTREE(proname, request, response);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
		
//		Extjs TRee
		if(proname.substring(0,4).equals("EREE")){
			try{
				///response.setContentType("text/xml;charset=GB2312");
				if(loginFlag == null){
				    return ;
				}
				out.println(control.parseDisplayTREE(proname, request, response));
				out.flush();
				out.close();
				return;
				//htmlfile = control.parseDisplayTREE(proname, request, response);
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
		
		//极光直接推送
		if(proname.substring(0,2).equals("PS")){
			try{
				
				out.println( WebControl.parseDisplayPushMsg(proname, request));
				return ;
				
				
				
			}catch(Exception e){
				htmlfile = "处理["+proname+"]时出错 "+e.toString();
				
			}
			
		}
		
		if(proname.substring(0,2).equals("IN")){
			try{
				
				out.println( WebControl.parseModifyIN(proname, request, response));
				return ;
				//如果有目标，则直接forward到目标页
				
				
			}catch(Exception e){
				htmlfile = "处理["+proname+"]时出错 "+e.toString();
				
			}
			
		}
		
		
		
		
		if(proname.substring(0,2).equals("FW")){
			try{
				control.parseForward(proname, request, response);
			}catch(Exception e){
				htmlfile = "处理["+proname+"]时出错 "+e.toString();
				
			}
			
		}
        
		
		
		//处理可执行程序
        if(proname.substring(0,3).equals("EXE")){
            try{
                String path = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT)+"\\PT850\\Pt_ud.exe";
                Runtime.getRuntime().exec(path);
                
                htmlfile = "正在处理上传数据";
            }catch(Exception e){
                htmlfile = "处理[EXE]时出错 "+e.toString();
                
            }
            
        }
        //开通POS的Lincese
        if(proname.substring(0,3).equals("POS")){
            try{
            	WebControl.parseLicense(proname);
            	htmlfile ="POS终端已开通";
            }catch(Exception e){
                htmlfile = "处理[POS]时出错 "+e.toString();
                
            }
            
        }
        //处理下拉JSON数据
        if(proname.substring(0,2).equals("XL")){
			try{
				
				boolean isTG=false;
				if(loginFlag == null){
					//验证签名
					String sign=request.getParameter("sign");
					if (sign == null ) sign="";
					if(sign.equals("FBFC49BEF0AB55874D8EECCFB557B501")){
						isTG=true;
					}else isTG=false;
				      
					
				}else isTG=true;
				if(!isTG)
				{
					out.println("{result:'fail'}");
				}else{
					out.println(control.parseDisplayXLJSON(proname, request));
					out.flush();
					out.close();
				}
				
				
				
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        //处理下拉JSON数据
        if(proname.substring(0,2).equals("AL")){
			try{
				out.println(control.parseDisplayXLJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        //处理列表JSON数据
        if(proname.substring(0,2).equals("MJ")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayMJJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        //处理微信支付
        if(proname.substring(0,2).equals("WX")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayWXPay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        //处理高汇通支付
        if(proname.substring(0,3).equals("GHT")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayGHTQuickPay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        //甬易支付
        if(proname.substring(0,4).equals("YYZF")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayYYZFQuickPay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
      //处理微信JSAPI支付
        if(proname.substring(0,2).equals("WJ")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayWXJSAPIPay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        //处理微信H5支付
        if(proname.substring(0,2).equals("H5")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayWXH5Pay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
      //处理微信扫描支付
        if(proname.substring(0,2).equals("WS")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayWSPay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
     
      //处理微信转账
        if(proname.substring(0,2).equals("WZ")){
			try{
				if(loginFlag == null){
					JSONObject json_result = new JSONObject();
					json_result.put("result", "fail");
					json_result.put("reason", "无效链接，无法打赏!");
					out.println(json_result);
				    return ;
				}
				out.println(control.parseDisplayWXEnterPricePay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        //处理微信转账
        if(proname.substring(0,3).equals("WCZ")){
			try{
				out.println(WeChatCorporatePayment.parseDisplayWXEnterPricePay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
      //处理微信红包
        if(proname.substring(0,2).equals("WH")){
			try{
				if(loginFlag == null){
				    return ;
				}
				out.println(control.parseDisplayWXRedPackPay(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
        
      //处理短信
        if(proname.substring(0,2).equals("DX")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayAliSms(request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        
       
        
//      上传文件
        if(proname.substring(0, 2).equals("UE")){
        	try{
				out.println(control.parseUpLoadFile(htmlfile,proname,request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
//      上传视频文件
        if(proname.substring(0, 2).equals("VE")){
        	try{
				out.println(control.parseUpLoadVideoFile(htmlfile,proname,request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
//      生成店铺二维码
        if(proname.substring(0, 2).equals("EW")){
        	try{
        		if(proname.equals("EW0001"))
        			out.println(control.parseSmartAqr(htmlfile,proname,request, response));
        		else  if(proname.equals("EW0002"))
        			out.println(control.parseSmartProductAqr(request, response));
        		else  if(proname.equals("EW0003"))
            			out.println(control.parseProductAqr(request, response));
        		else  if(proname.equals("EW0004"))
        			out.println(QrCodeUtil.genQrCode(request, response));
        		else  if(proname.equals("EWP001"))
        			out.println(LikeQrCode.parseProductAq(request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        // 获取access_token
        if(proname.substring(0, 2).equals("HQ")){
        	try{
				out.println(control.parseAccessToken(request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        // 获取店铺里的openid
        if(proname.substring(0, 2).equals("OP")){
        	try{
				out.println(control.parseOPenid(request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
//      下载图片
        if(proname.substring(0, 2).equals("DE")){
        	try{
				out.println(control.parseDownloadFile(htmlfile,proname,request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
        
//      获得银行号码
        if(proname.substring(0, 2).equals("BK")){
        	try{
				out.println(control.getBankInfo(request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
       
        
		out.print(htmlfile);
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
