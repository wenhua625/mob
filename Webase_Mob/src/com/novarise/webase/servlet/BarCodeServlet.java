/*
 * Copyright 2002-2007 Jeremias Maerki or contributors to Barcode4J, as applicable
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novarise.webase.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import net.sf.json.JSONObject;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.dom4j.DocumentException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

import com.novarise.webase.util.EncoderHandler;
import com.novarise.webase.util.GenerateSequenceUtil;
import com.novarise.webase.util.HttpUtil;
import com.novarise.webase.util.MD5Util;

/**
 * Simple barcode servlet.
 *
 * @version $Id: BarcodeServlet.java,v 1.6 2007/01/19 12:26:55 jmaerki Exp $
 */
public class BarCodeServlet extends HttpServlet {

    /** Parameter name for the message */
    public static final String BARCODE_MSG                 = "msg";
    /** Parameter name for the barcode type */
    public static final String BARCODE_TYPE                = "type";
    /** Parameter name for the barcode height */
    public static final String BARCODE_HEIGHT              = "height";
    /** Parameter name for the module width */
    public static final String BARCODE_MODULE_WIDTH        = "mw";
    /** Parameter name for the wide factor */
    public static final String BARCODE_WIDE_FACTOR         = "wf";
    /** Parameter name for the quiet zone */
    public static final String BARCODE_QUIET_ZONE          = "qz";
    /** Parameter name for the human-readable placement */
    public static final String BARCODE_HUMAN_READABLE_POS  = "hrp";
    /** Parameter name for the output format */
    public static final String BARCODE_FORMAT              = "fmt";
    /** Parameter name for the image resolution (for bitmaps) */
    public static final String BARCODE_IMAGE_RESOLUTION    = "res";
    /** Parameter name for the grayscale or b/w image (for bitmaps) */
    public static final String BARCODE_IMAGE_GRAYSCALE     = "gray";
    /** Parameter name for the font size of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_SIZE = "hrsize";
    /** Parameter name for the font name of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_FONT = "hrfont";
    /** Parameter name for the pattern to format the human readable message */
    public static final String BARCODE_HUMAN_READABLE_PATTERN = "hrpattern";


    private Logger log = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
    	
    	JSONObject json_result = new JSONObject();
		String fee=request.getParameter("fee");
		String months=request.getParameter("months");
		String agent_code=request.getParameter("agent_code");
		
		if(agent_code ==null || "".equals(agent_code))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "AgentCode无法获取");
			return;
		}
		if(months ==null || "".equals(months))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "月份为空");
			return;
		}
	
		if(fee ==null || "".equals(fee))
		{
			json_result.put("result", "fail");
			json_result.put("reason", "费用为空");
			return ;
		}
		String ip=request.getParameter("ip_address");
		if(ip == null || "".equals(ip)){
			json_result.put("result", "fail");
			json_result.put("reason", "ip地址为空");
			return ;
		}
		String body = "艾订货";
		
		SortedMap params = new TreeMap();
		params.put("appid","wxb085018f401f8f5a"); //公众号id
		params.put("mch_id","1355185802");
		//params.put("device_info",""); //否
		String random=java.util.UUID.randomUUID().toString();
		random = random.replace("-", "");
		params.put("nonce_str",random);
		
		params.put("body",body);
		
		//params.put("detail","");//否
		//params.put("attach","");//否
		
		String out_trade_no=months+GenerateSequenceUtil.generateSequenceNo()+"H"+agent_code;
		params.put("out_trade_no",out_trade_no);
		params.put("fee_type","CNY");
		params.put("total_fee",fee);
		params.put("spbill_create_ip",ip);
		//params.put("time_start","");//否
		//params.put("time_expire","");//否
		//params.put("goods_tag","");//否
		params.put("notify_url","http://fsilon.dderp.cn/weixinpay");
		params.put("trade_type","NATIVE");
		
		//params.put("openid","oxhqXjj6njtlsKRB0hg2TMw4vCoM");
		params.put("product_id","1002");
		
		//params.put("limit_pay","");//否
		
		String sign=MD5Util.createSign("UTF-8", params);
		params.put("sign",sign);
		
		String requestXML = HttpUtil.getRequestXml(params);
		
		//System.out.println("wx pay result:"+requestXML);
		
		String result =HttpUtil.httpsRequest("https://api.mch.weixin.qq.com/pay/unifiedorder", "POST", requestXML);
		
		//System.out.println("response:"+result);
		
		
		
		
		SortedMap map = new TreeMap();
		
		try {
			 map = HttpUtil.doXmlParse(result);
			 EncoderHandler encoder = new EncoderHandler();  
			 String code_url=(String)map.get("code_url");
			 if (code_url != null)
			// System.out.println("code_url:"+code_url);
			    encoder.encoderQRCoder(code_url, response);
			 

		} catch (UnsupportedEncodingException e) {
			
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (DocumentException e) {
			
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
				
        
		

		
        

       
    }

   

}
