package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.novarise.webase.framework.QueryControl;
import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.util.GenerateSequenceUtil;

public class JQuery extends HttpServlet {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/json; charset=utf-8";
	/**
	 * Constructor of the object.
	 */
	public JQuery() {
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
		request.setCharacterEncoding("UTF-8");
		//request.setCharaterEncoding();
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();
		QueryControl control = new QueryControl();
		String loginFlag = (String)(request.getSession().getAttribute("LS.YHDM"));
		String htmlfile=" ";
		String proname = request.getParameter("proname");
		if (proname == null || proname.trim().length() == 0) {
			String error = SystemFunction.showError(101, "错误!没有要处理的事件", "错误!没有要处理的事件",request);
			out.println(error);
			return;
		}
		
		
        //处理列表JSON数据
        if(proname.substring(0,2).equals("MJ")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayJJJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
//      处理单条JSON数据
        if(proname.substring(0,2).equals("SJ")){
			try{
				if(loginFlag == null){
				    return ;
				}
				out.println(control.parseDisplaySigleJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
      //处理手机端组合的Json数据 组合数据
        if(proname.substring(0,2).equals("MB")){
			try{
				if(loginFlag == null){
				    return ;
				}
				out.println(control.parseDisplayMBJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        //处理手机端组合的Json数据
        if(proname.substring(0,2).equals("JJ")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				out.println(control.parseDisplayJJJSON(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
        if (proname.substring(0, 2).equals("KD")) {
            try
            {
              out.println(control.parseKdniaoJSON(proname, request));
              out.flush();
              out.close();
              return;
            }
            catch (Exception e)
            {
              e.printStackTrace();
              return;
            }
          }
        
//      生产唯一KEY键
        if(proname.substring(0,2).equals("KY")){
			try{
				/*if(loginFlag == null){
				    return ;
				}*/
				
				out.println(control.parseDisplayKEY(proname, request));
				out.flush();
				out.close();
				return;
			}catch(Exception e){
				e.printStackTrace();
				return;
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
