package com.novarise.webase.util;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;























import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.alibaba.fastjson.JSONObject;

public class SmartAqrCode {

	/**
	 * ��ȡtoken
	 * 
	 * @param url
	 * @param grantType
	 * @param appid
	 * @param secret
	 * @return
	 */
	public static String getAccessToken(String url, String grantType,
			String appid, String secret) {
		String access_token = "";
		String tokenUrl = url + "?grant_type=" + grantType + "&appid=" + appid
				+ "&secret=" + secret;
		Map params = new HashMap();
		Object result = HttpUtil.doPost(tokenUrl, params);
		JSONObject jsons = JSONObject.parseObject(result.toString());
		String expires_in = jsons.getString("expires_in");
		if (expires_in != null && Integer.parseInt(expires_in) > 1200) {
			access_token = jsons.getString("access_token");
		} else {
			System.out.println("�����ȡtokenʧ�ܣ�"+expires_in);
		}
		return access_token;
	}

	/**
	 * ��ȡС�����ά��
	 * 
	 * @param url
	 *            �ٷ���ȡ��ά���ַ
	 * @param width
	 *            ��ά��Ŀ�� int���� Ĭ�� 430
	 * @param access_token
	 * @param path
	 * @return
	 */
	public static InputStream createwxaqrcode(String url, String access_token,
			String path, String width) {
		url = url + "?access_token=" + access_token;
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("path", path);
		jsonParam.put("width", width);
		InputStream instreams = doWXPost(url, jsonParam);
		if (instreams == null) {
			System.out.println("�����ȡ��ά��ʧ�ܣ�");
		}
		return instreams;
	}
	
	
	
	/**
	  * ���������޸���С������ӿ�
	  * @param url
	  * @param access_token
	  * @param path
	  * @param width
	  * @return
	  */
	  public static InputStream getwxacodeunlimit(String url,String access_token,String path,String width){
	      String[] str = path.split("[?]");
	      path = str[0];
	      String scene = str[1];
	      url = url + "?access_token=" + access_token;
	      // ���ղ���json�б�
	      JSONObject jsonParam = new JSONObject();
	      jsonParam.put("scene", scene);
	      jsonParam.put("page", path);
	      jsonParam.put("width", Integer.parseInt(width));
	      jsonParam.put("auto_color", false);
	      Map<String,Object> line_color = new HashMap();
	      line_color.put("r", 0);
	      line_color.put("g", 0);
	      line_color.put("b", 0);
	      jsonParam.put("line_color", line_color);
	      InputStream instreams = doWXPost(url, jsonParam);
	      if(instreams==null){
	          System.out.println("�����ȡ��ά��ʧ�ܣ�");
	      }
	      return instreams;
	  }
	

	/**
	 * ����
	 * 
	 * @param url
	 * @param jsonParam
	 * @return
	 */
	public static InputStream doWXPost(String url, JSONObject jsonParam) {
		InputStream instreams = null;
		HttpPost httpRequst = new HttpPost(url);// ����HttpPost����
		try {
			StringEntity se = new StringEntity(jsonParam.toString(), "utf-8");
			se.setContentType("application/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "UTF-8"));
			httpRequst.setEntity(se);
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequst);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null) {
					instreams = httpEntity.getContent();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instreams;
	}

	/*
	 * @param instreams ��������
	 * 
	 * @param imgPath ͼƬ�ı���·��
	 * 
	 * @param fileName ͼƬ������
	 * 
	 * @return str ͼƬ�����ַ
	 */
	public static String saveToImgByInputStream(InputStream instreams,
			String imagePath, String fileName) {
		
		String url = SmartAqrCode.class.getResource("/").getPath();
		url = url.substring(1, url.length() - 16);
		
		//System.out.println("URL:"+url);
		
		try{
		File newFile = null;
		File fp = new File(url + imagePath);
		// ����Ŀ¼
		if (!fp.exists()) {
			fp.mkdirs();// Ŀ¼�����ڵ�����£�����Ŀ¼��
		}
		newFile = new File(url + imagePath+fileName);

		if (!newFile.exists()) {
			newFile.createNewFile();
		}

		
		OutputStream os = new FileOutputStream(newFile);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = instreams.read(buffer, 0, 8192)) != -1) {
		os.write(buffer, 0, bytesRead);
		}
		os.close();
		instreams.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return url + imagePath+fileName;
	}
	/**
     * ����ά��ͼƬ���Logo
     * 
     * @param qrPic
     * @param logoPic
     */
    public static void addLogo_QRCode(File qrPic, File logoPic, LogoConfig logoConfig, String urlPath)
    {
        try
        {
            if (!qrPic.isFile() || !logoPic.isFile())
            {
                System.out.print("file not find !");
                System.exit(0);
            }

            /**
             * ��ȡ��ά��ͼƬ����������ͼ����
             */
            BufferedImage image = ImageIO.read(qrPic);
            Graphics2D g = image.createGraphics();

            /**
             * ��ȡLogoͼƬ
             */
            BufferedImage logo = ImageIO.read(logoPic);

            int widthLogo = (int) (image.getWidth()/logoConfig.getLogoPart()*2.8); 
            int    heightLogo = (int) (image.getWidth()/logoConfig.getLogoPart() *2.8); //���ֶ�ά���������ε�

            // ����ͼƬ����λ��
            int x = (image.getWidth() - widthLogo) / 2;
            int y = (image.getHeight() - heightLogo) / 2 ;        
         
            //��ʼ����ͼƬ
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.drawRoundRect(x, y, widthLogo, heightLogo, 20, 20);
            g.setStroke(new BasicStroke(0.0f));
            g.setColor(logoConfig.getBorderColor());
            g.drawRect(x, y, widthLogo, heightLogo);

            g.dispose();

            ImageIO.write(image, "PNG", new File(urlPath));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
    public static void cutImage(String filePath, String urlPath, int x, int y, int w, int h)
            throws Exception {
        // ����ͨ��ImageIo�еķ���������һ��Image + InputStream���ڴ�
        ImageInputStream iis = ImageIO
                .createImageInputStream(new FileInputStream(filePath));
        // �ٰ���ָ����ʽ����һ��Reader��Reader����new�ģ�
        Iterator it = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader imagereader = (ImageReader) it.next();
        // ��ͨ��ImageReader�� InputStream
        imagereader.setInput(iis);

        // ���ø���Ȥ��Դ����
        ImageReadParam par = imagereader.getDefaultReadParam();
        par.setSourceRegion(new Rectangle(x, y, w, h));
        // �� reader�õ�BufferImage
        BufferedImage bi = imagereader.read(0, par);

        // ��BuffeerImageд��ͨ��ImageIO

        ImageIO.write(bi, "jpg", new File(filePath));

    }
    public static String getFileExt(String filePath) {
        FileInputStream fis = null;
        String extension = FilenameUtils.getExtension(filePath);
        try {
            fis = new FileInputStream(new File(filePath));
            byte[] bs = new byte[1];
            fis.read(bs);
            String type = Integer.toHexString(bs[0]&0xFF);
            if(JPG_HEX.equals(type))
                extension = JPG_EXT;
            if(PNG_HEX.equals(type))
                extension = PNG_EXT;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return extension;
    }
    private static final String JPG_HEX = "ff";
    private static final String PNG_HEX = "89";
    private static final String JPG_EXT = "jpg";
    private static final String PNG_EXT = "png";

    public static void cutPNG(String filePath, String outPath, int x,  
            int y, int width, int height) throws IOException {  
        ImageInputStream imageStream = null;  
        String ext =SmartAqrCode.getFileExt(filePath);
        InputStream input = new FileInputStream(new File(filePath));
        OutputStream out = new FileOutputStream(new File(outPath));
        try {  
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(ext);  
            ImageReader reader = readers.next();  
            imageStream = ImageIO.createImageInputStream(input);  
           // Iterator<ImageReader> readers = ImageIO.getImageReaders(new FileImageInputStream(srcFilePath)));
            reader.setInput(imageStream, true);  
            ImageReadParam param = reader.getDefaultReadParam();  
              
            System.out.println(reader.getWidth(0));  
            System.out.println(reader.getHeight(0));  
              
            Rectangle rect = new Rectangle(x, y, width, height);  
            param.setSourceRegion(rect);  
            BufferedImage bi = reader.read(0, param);  
            ImageIO.write(bi, "png", out);  
        } finally {  
            imageStream.close();  
        }  
    }  
	public static void main(String args[]) throws Exception {
		//SmartAqrCode.cutImage("E:\\QRCodeImage\\44005_smartaqr.png",20, 20, 100, 100,"E:\\QRCodeImage\\qr_smartaqr.png");

        String ext =SmartAqrCode.getFileExt("E:/Webase/Webase_Mob/WebRoot/salsa/product_photo/11000006_smartaqr.png");
        System.out.println("---"+ext);
		SmartAqrCode.cutImage("E:/Webase/Webase_Mob/WebRoot/salsa/product_photo/11000006_smartaqr.png",
				"E:/Webase/Webase_Mob/WebRoot/salsa/product_photo/11000006_smartaqr.png",20, 20, 340, 340);
		
		//SmartAqrCode.cutPNG("E:/Webase/Webase_Mob/WebRoot/salsa/product_photo/11000006_smartaqr.png",
		//		"E:/Webase/Webase_Mob/WebRoot/salsa/product_photo/11000006_smartaqr.png",20, 20, 340, 340);
		System.out.println("ok");
	    /*BufferedImage image = (BufferedImage)ImageIO.read(new File("E:\\QRCodeImage\\44005_smartaqr.png"));
	    image.getSubimage(10,10,40,40);
	    System.out.println("--"+image);
	    ImageIO.write(image,"png",new File("E:\\QRCodeImage\\qrq_FIle_smartaqr.png"));
	    */
	    //cutImage("E:\\QRCodeImage\\44005_smartaqr.png", 10, 10, 430, 400,"E:\\QRCodeImage\\qr__Code.png");
		/*String code="11000015";
		String access_token = getAccessToken(
				"https://api.weixin.qq.com/cgi-bin/token", "client_credential",
				"wxf85e801e63bb2164", "9287b9eb47c7f15a112f2c82f15c80c2");
		System.out.println("token:"+access_token);
		
		
		 URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+access_token);
         HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
         httpURLConnection.setRequestMethod("POST");// �ύģʽ
         // conn.setConnectTimeout(10000);//���ӳ�ʱ ��λ����
         // conn.setReadTimeout(2000);//��ȡ��ʱ ��λ����
         // ����POST�������������������
         httpURLConnection.setDoOutput(true);
         httpURLConnection.setDoInput(true);
         // ��ȡURLConnection�����Ӧ�������
         PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
         // �����������
         JSONObject paramJson = new JSONObject();
         paramJson.put("scene", "a-&q=999");
         paramJson.put("page", "pages/index/index");
         paramJson.put("width", 430);
         paramJson.put("auto_color", true);
         

         printWriter.write(paramJson.toString());
         // flush������Ļ���
         printWriter.flush();
         //��ʼ��ȡ����
         BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
         OutputStream os = new FileOutputStream(new File("D:abc1.png"));
         int len;
         byte[] arr = new byte[1024];
         while ((len = bis.read(arr)) != -1)
         {
             os.write(arr, 0, len);
             os.flush();
         }
         os.close();
         File qrcFile =new File("D:abc1.png");
         File logoFile =new File("E:\\QRCodeImage\\logo.jpg");
         String urlPath="E:\\QRCodeImage\\qrCode.png";
         LogoConfig logoConfig = new LogoConfig(); //LogoConfig������Logo������
         SmartAqrCode.addLogo_QRCode(qrcFile, logoFile, logoConfig,urlPath);
			*/
	}

	

	
}
