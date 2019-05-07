package com.novarise.webase.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sourceforge.qrcode.QRCodeDecoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.czb.gateway.api.APITools;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.SQLUpdater;

public class QrCodeUtil {
//	艾修
	public static  String appid="wxf5c051f0a0f391bf";
	public static  String secret="48a12c83b46950a4fad16c283f328d7a";
	private static String weixinAuthUrl="https://api.weixin.qq.com/sns/jscode2session";
	
//	艾来客
//	public static  String appid="wxf85e801e63bb2164";
//	public static  String secret="9287b9eb47c7f15a112f2c82f15c80c2";
	
	
	
	
	public static JSONObject genQrCode(HttpServletRequest request, HttpServletResponse response){
		
		 Map<String , Object> reMap= new HashMap<String , Object>();
		 String product_id = request.getParameter("product_id");
		 String rq = request.getParameter("rq");
		 if(product_id == null || product_id.equals("")){
			 reMap.put("result", "fail");
			 reMap.put("msg", "产品ID-product_id为空");
			 JSONObject json_result=JSONObject.parseObject(JSON.toJSONString(reMap));
			return json_result;
		}
		 if(rq == null || rq.equals("")){
			 reMap.put("result", "fail");
			 reMap.put("msg", "rq日期不能为空,");
			 JSONObject json_result=JSONObject.parseObject(JSON.toJSONString(reMap));
			return json_result;
		}
		 String key= GenerateSequenceUtil.generateSequenceNo();
//		 String insert_sql="insert into qc_list(qc_Id,qr_code,rq,zdr,product_id,PRODUCT_CODE,PRODUCT_CODE2,brand_code,product_name,product_size,product_untl,product_color,gas_type,gas_style) "+
//				           " select '@@KEY,',PRODUCT_CODE2+(select right(DATENAME(yy,GETDATE()),2)+DATENAME(mm,GETDATE())+DATENAME(dd,GETDATE())+dbo.lpad('0',3,convert(varchar,isnull(max(RIGHT(qr_code,3)),0)+1)) from qc_list where dbo.getdatestr1(rq)=dbo.getdatestr1(getdate())) qr_code,"+
//                          "  getdate(),'@@LS.XM,',id,product_code,product_code2,brand_code,product_name,product_size,product_untl,product_color,gas_type,gas_style "+
//                         "  from product_list where ID='@@Id,'";
		 String insert_sql="insert into qc_list(qc_Id,qr_code,rq,zdr,product_id,PRODUCT_CODE,PRODUCT_CODE2,brand_code,product_name,product_size,product_untl,product_color,gas_type,gas_style) "+
		           " select '@@KEY,',PRODUCT_CODE2+(select  CONVERT(varchar, convert(datetime,'@@rq,', 20), 12)+dbo.lpad('0',3,convert(varchar,isnull(max(RIGHT(qr_code,3)),0)+1)) from qc_list where dbo.getdatestr1(rq)=dbo.getdatestr1('@@rq,')  and ( product_id=a.id or product_code2 =a.product_code2)) qr_code,"+
                "  convert(datetime,'@@rq,', 20),'@@LS.XM,',id,product_code,product_code2,brand_code,product_name,product_size,product_untl,product_color,gas_type,gas_style "+
               "  from product_list a where ID='@@Id,'";

	        insert_sql = insert_sql.replaceAll("@@KEY,", key);
	        insert_sql = insert_sql.replaceAll("@@LS.XM,", "系统");
	        insert_sql = insert_sql.replaceAll("@@Id,", product_id);
	        insert_sql = insert_sql.replaceAll("@@rq,", rq.toString());
	        
	        System.out.println("--------insert_sql:"+insert_sql);
	       String select_sql="select qc_id,qr_code,product_id,brand_code,product_code2,product_code,product_name,product_color,gas_type,gas_style from qc_list where qc_id='"+key+"'";
	       
	       String updater_sql="update qc_list set wx_qrcode='@@wx_qrcode,' where  qc_id='"+key+"'";
	    
	        try {

				SQLUpdater updater = DSManager.getSQLUpdater();
				int updaterNum=updater.executeUpdate(insert_sql);
				if(updaterNum>0){
					SQLQuery query = DSManager.getSQLQuery();
					String data[][]=query.ArrayQuery(select_sql);
					
					if(data.length == 0){
						reMap.put("result", "fail");
						reMap.put("msg", "二维码为插入，无法生成");
						JSONObject obj=JSONObject.parseObject(JSON.toJSONString(reMap));
					    return obj;
					}
					reMap.put("qc_id", data[0][0]);
					reMap.put("qr_code", data[0][1]);
					reMap.put("product_id", data[0][2]);
					reMap.put("brand_code", data[0][3]);
					reMap.put("product_code2", data[0][4]);
					reMap.put("product_code", data[0][5]);
					reMap.put("product_name", data[0][6]);
					reMap.put("product_color", data[0][7]);
					reMap.put("gas_type", data[0][8]);
					reMap.put("gas_style", data[0][9]);
					
						
					//微信二维码 存放位置
					Map<String , Object> qrMap= QrCodeUtil.parseSmartProductAqr( key, data[0][3]);
					
					//识别微信二维码，返回二维码串
					 if(qrMap.get("qrcodeUrl") == null || qrMap.get("qrcodeUrl").equals("")){
						 reMap.put("result", "fail");
							reMap.put("msg", "生成二维码失败");
							JSONObject obj=JSONObject.parseObject(JSON.toJSONString(reMap));
						    return obj;
					 }
					String wx_qrcode = QrCodeUtil.decodeQrcode(new File((String) qrMap.get("qrcodeUrl")));
					if(wx_qrcode == null || wx_qrcode.equals("")){
						reMap.put("result", "fail");
						reMap.put("msg", "识别二维码失败");
						JSONObject obj=JSONObject.parseObject(JSON.toJSONString(reMap));
					    return obj;
					}
					

		    	//	QrCodeUtil.getQrCodeImg(wx_qrcode,(String)qrMap.get("qrcodeUrl"));
					updater_sql = updater_sql.replaceAll("@@wx_qrcode,", wx_qrcode);
					reMap.put("wx_qrcode", wx_qrcode);
					
					int wxNum=updater.executeUpdate(updater_sql);
					if(wxNum == 0){
						reMap.put("result", "fail");
						reMap.put("msg", "修改qc_list的wx_qrcode出错");
						JSONObject obj=JSONObject.parseObject(JSON.toJSONString(reMap));
					    return obj;
					}

					reMap.put("qrcodeurl", qrMap.get("qrcode"));
					reMap.put("result", "ok");
					
					
					
					
				}
				
				
			} catch (SQLException e) {
				reMap.put("result", "fail");
				reMap.put("msg",e.toString());
				e.printStackTrace();
				System.out.println("-------"+e);
				JSONObject obj=JSONObject.parseObject(JSON.toJSONString(reMap));
			    return obj;
			} catch (Exception e) {
				reMap.put("result", "fail");
				reMap.put("msg",e.toString());
				JSONObject obj=JSONObject.parseObject(JSON.toJSONString(reMap));
			    return obj;
			}
	        
	        JSONObject obj=JSONObject.parseObject(JSON.toJSONString(reMap));
	        return obj;
	}
	/**
	 * 
	 * @param content  内容
	 * @param imgPath  路径
	 */
	public static void main(String[] args) {
		QrCodeUtil.getQrCodeImg("你说什么","E:\\Webase\\Webase_Mob\\WebRoot\\salsa\\product_photo\\product\\22.png");
	}
	public static void getQrCodeImg(String content, String imgPath) {
		   int width=430;      //图片的宽度
	        int height=430;     //图片的高度
	        String format="png";    //图片的格式
	        

	        /**
	         * 定义二维码的参数
	         */
	        HashMap hints=new HashMap();
	        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");    //指定字符编码为“utf-8”
	        hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.M);  //指定二维码的纠错等级为中级
	        hints.put(EncodeHintType.MARGIN, 0);    //设置图片的边距

	        /**
	         * 生成二维码
	         */
	        try {
	            BitMatrix bitMatrix=new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,hints);
	            Path file=new File(imgPath).toPath();
	            MatrixToImageWriter.writeToPath(bitMatrix, format, file);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	}
	/**
	 * 生成二维码  
	 * a接口
	 * 
	 * @param request
	 * @param response
	 * @param config
	 * @throws Exception
	 */
	public static Map<String , Object> parseSmartProductAqr(String key,String brand_code)
		//	HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		
		 String access_token = SmartAqrCode.getAccessToken(
				"https://api.weixin.qq.com/cgi-bin/token", "client_credential",
				appid, secret);			
       	String smartpath = "pages/index/index?key="+key+"&brand_code="+brand_code;
		InputStream is = SmartAqrCode.createwxaqrcode(
				"https://api.weixin.qq.com/cgi-bin/wxaapp/createwxaqrcode",
				access_token, smartpath, "430");
		String qrcodeUrl = SmartAqrCode.saveToImgByInputStream(is, "salsa/product_photo/product/",key +"_"+brand_code+"_smartaqr.png");

		String ext =SmartAqrCode.getFileExt(qrcodeUrl);
		if(ext == "jpg")
			SmartAqrCode.cutImage(qrcodeUrl, qrcodeUrl, 20, 20, 430, 430);
		else if(ext == "png")
			SmartAqrCode.cutPNG(qrcodeUrl, qrcodeUrl, 20, 20, 430, 430);
            Map<String , Object> reMap = new HashMap<String , Object>(); 
		reMap.put("qrcodeUrl",qrcodeUrl);
		reMap.put("qrcode","salsa/product_photo/product/"+key +"_"+brand_code+"_smartaqr.png");
		return reMap;
	}
	  
	
	 /**  
     * 解码二维码  
     * @param imgPath  
     * @return String  
     */    
    public static String decoderQRCode(String imgPath) {    
    
        // QRCode 二维码图片的文件    
        File imageFile = new File(imgPath);    
        BufferedImage bufImg = null;    
        String decodedData = null;    
        try {    
            bufImg = ImageIO.read(imageFile);    
            QRCodeDecoder decoder = new QRCodeDecoder();    
            decodedData  = new String(decoder.decode(new MyQRCodeImage(bufImg)), "utf-8");

        } catch (IOException e) {    
            
            e.printStackTrace();    
        } catch (Exception dfe) {     
            dfe.printStackTrace();    
        }    
        return decodedData;    
    }  
    /**
     * 流图片解码zxing
     * @param   input
     * @return  String
     */
    public static String decodeQrcode(File input)  {

    	  BufferedImage image;
    	  Result result = null;
		try {
			image = ImageIO.read(input);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
	          BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

	          Map<DecodeHintType,Object> hints = new LinkedHashMap<DecodeHintType,Object>();
	          // 解码设置编码方式为：utf-8，
	          hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
	          //优化精度
	          hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	          //复杂模式，开启PURE_BARCODE模式
	          hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
	           try {
				result = new MultiFormatReader().decode(bitmap, hints);

			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          
          return result.getText();
    }
    
    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
    

	/**
	 * 获取微信小程序 session_key 和 openid
	 * 
	 * @author zhy
	 * @param code
	 *            调用微信登陆返回的Code
	 * @return
	 */
	public static JSONObject getSessionKeyOropenid(HttpServletRequest request) {
		// 微信端登录code值
		String wxCode = APITools.nvl(request.getParameter("code"));
		// //读取属性文件
		String requestUrl = APITools.nvl(weixinAuthUrl); 												
		Map<String, String> requestUrlParam = new HashMap<String, String>();
		requestUrlParam.put("appid",
				APITools.nvl(appid)); // 开发者设置中的appId
		requestUrlParam.put("secret",
				APITools.nvl(secret)); // 开发者设置中的appSecret
		requestUrlParam.put("js_code", wxCode); // 小程序调用wx.login返回的code
		requestUrlParam.put("grant_type", "authorization_code"); // 默认参数
		// 接口获取openid用户唯一标识
        
		JSONObject jsonObject = JSON.parseObject(HttpUtil.sendPost(requestUrl,
				requestUrlParam));
		return jsonObject;
	}
	
	
  
}
