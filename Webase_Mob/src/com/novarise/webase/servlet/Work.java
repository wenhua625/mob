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
			String error = SystemFunction.showError(101, "����!û��Ҫ������¼�", "����!û��Ҫ������¼�",request);
			out.println(error);
			return;
		}
		
		
		//΢����Ȩ   ������
		if(proname.equals("AUTHWXOPEN")){
			try{
				out.println(WXAppletUserInfo.getSessionKeyOropenid(request));
				return;
			}catch(Exception e){
				e.printStackTrace();
				System.out.println();
				String error = SystemFunction.showError(101, "ϵͳ΢����Ȩʱ����", "ϵͳ΢����Ȩʱ����",request);
				out.println(error);
				return    ;
			}
		}
		//΢����Ȩ   ����
		if(proname.equals("AUTHWXOPEN1")){
			try{
				out.println(QrCodeUtil.getSessionKeyOropenid(request));
				return;
			}catch(Exception e){
				e.printStackTrace();
				System.out.println();
				String error = SystemFunction.showError(101, "ϵͳ΢����Ȩʱ����", "ϵͳ΢����Ȩʱ����",request);
				out.println(error);
				return ;   
			}
		}
		//�������ݣ���ȡ�ֻ���
		if(proname.equals("AUTHWXPHONE")){
			try{
				out.println(WeiXinUtil.deencry(request));
			}catch(Exception e){
				e.printStackTrace(out);
				String error = SystemFunction.showError(101, "ϵͳ΢����Ȩʱ����", "ϵͳ΢����Ȩʱ����",request);
				out.println(error);
				return;
			}	
		}
		//���ݾ�γ�ȣ���ȡʡ����
		if(proname.equals("AUTHWXADDRESS")){
			try{
				out.println(GetConfigureInfo.getAdd(request));
			}catch(Exception e){
				e.printStackTrace(out);
				String error = SystemFunction.showError(101, "ϵͳ��ȡʡ���س���", "ϵͳ��ȡʡ���س���",request);
				out.println(error);
				return;
			}	
		}
		
		if(proname.equals("QUIT")){
			try{
				control.parseDisplayQUIT( request, response);
			}catch(Exception e){
				String error = SystemFunction.showError(101, "ϵͳ�˳�ʱ����", "ϵͳ�˳�ʱ����",request);
				out.println(error);
				return;
			}
			
		}
		
		//��������¼���Ȼ�ȡǰ׺���ٵ�¼
		if(proname.equals("LOGON")){
			//System.out.println("Login..");
			try{
				
				out.println( control.parseDisplayQX(request, response));
			
				return;
			}catch(Exception e){
				String error = SystemFunction.showError(101, "ϵͳ��½ʱ����", "ϵͳ��½ʱ����["+e.getMessage()+"]",request);
				out.println(error);
				return;
			}
			
			
		}
		
		//�Զ���¼��ֱ�ӵ�¼
				if(proname.equals("LOGONAUTO")){
					//System.out.println("Login..");
					try{
						
						out.println( control.parseDisplayQXAUTO(request, response));
					
						return;
					}catch(Exception e){
						String error = SystemFunction.showError(101, "ϵͳ��½ʱ����", "ϵͳ��½ʱ����["+e.getMessage()+"]",request);
						out.println(error);
						return;
					}
					
					
				}
		//��������¼��������֤���¼
		if(proname.equals("LOGONSMS")){
			//System.out.println("Login..");
			try{
				out.println( control.parseDisplayQXSMS(request, response));
				return;
			}catch(Exception e){
				String error = SystemFunction.showError(101, "ϵͳ��½ʱ����", "ϵͳ��½ʱ����["+e.getMessage()+"]",request);
				out.println(error);
				return;
			}
			
			
		}
		//�ʹ���΢�ŵ�¼
		if(proname.equals("LOGONWX")){
			//System.out.println("Login..");
			try{
				out.println( control.parseDisplayWXQX(request, response));
				return;
			}catch(Exception e){
				String error = SystemFunction.showError(101, "ϵͳ��½ʱ����", "ϵͳ��½ʱ����["+e.getMessage()+"]",request);
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
		
		//����ֱ������
		if(proname.substring(0,2).equals("PS")){
			try{
				
				out.println( WebControl.parseDisplayPushMsg(proname, request));
				return ;
				
				
				
			}catch(Exception e){
				htmlfile = "����["+proname+"]ʱ���� "+e.toString();
				
			}
			
		}
		
		if(proname.substring(0,2).equals("IN")){
			try{
				
				out.println( WebControl.parseModifyIN(proname, request, response));
				return ;
				//�����Ŀ�꣬��ֱ��forward��Ŀ��ҳ
				
				
			}catch(Exception e){
				htmlfile = "����["+proname+"]ʱ���� "+e.toString();
				
			}
			
		}
		
		
		
		
		if(proname.substring(0,2).equals("FW")){
			try{
				control.parseForward(proname, request, response);
			}catch(Exception e){
				htmlfile = "����["+proname+"]ʱ���� "+e.toString();
				
			}
			
		}
        
		
		
		//�����ִ�г���
        if(proname.substring(0,3).equals("EXE")){
            try{
                String path = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT)+"\\PT850\\Pt_ud.exe";
                Runtime.getRuntime().exec(path);
                
                htmlfile = "���ڴ����ϴ�����";
            }catch(Exception e){
                htmlfile = "����[EXE]ʱ���� "+e.toString();
                
            }
            
        }
        //��ͨPOS��Lincese
        if(proname.substring(0,3).equals("POS")){
            try{
            	WebControl.parseLicense(proname);
            	htmlfile ="POS�ն��ѿ�ͨ";
            }catch(Exception e){
                htmlfile = "����[POS]ʱ���� "+e.toString();
                
            }
            
        }
        //��������JSON����
        if(proname.substring(0,2).equals("XL")){
			try{
				
				boolean isTG=false;
				if(loginFlag == null){
					//��֤ǩ��
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
        //��������JSON����
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
        
        //�����б�JSON����
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
        
        //����΢��֧��
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
        
        //����߻�֧ͨ��
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
        //���֧��
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
        
      //����΢��JSAPI֧��
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
        
        //����΢��H5֧��
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
        
      //����΢��ɨ��֧��
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
        
     
      //����΢��ת��
        if(proname.substring(0,2).equals("WZ")){
			try{
				if(loginFlag == null){
					JSONObject json_result = new JSONObject();
					json_result.put("result", "fail");
					json_result.put("reason", "��Ч���ӣ��޷�����!");
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
        //����΢��ת��
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
      //����΢�ź��
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
        
        
      //�������
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
        
       
        
//      �ϴ��ļ�
        if(proname.substring(0, 2).equals("UE")){
        	try{
				out.println(control.parseUpLoadFile(htmlfile,proname,request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
//      �ϴ���Ƶ�ļ�
        if(proname.substring(0, 2).equals("VE")){
        	try{
				out.println(control.parseUpLoadVideoFile(htmlfile,proname,request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
//      ���ɵ��̶�ά��
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
        // ��ȡaccess_token
        if(proname.substring(0, 2).equals("HQ")){
        	try{
				out.println(control.parseAccessToken(request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        // ��ȡ�������openid
        if(proname.substring(0, 2).equals("OP")){
        	try{
				out.println(control.parseOPenid(request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
//      ����ͼƬ
        if(proname.substring(0, 2).equals("DE")){
        	try{
				out.println(control.parseDownloadFile(htmlfile,proname,request, response));
        	}catch(Exception e)
        	{
        		htmlfile="error:"+e.toString();
        	}  
        }
        
        
//      ������к���
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
