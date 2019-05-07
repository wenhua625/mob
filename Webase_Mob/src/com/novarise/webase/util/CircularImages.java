package com.novarise.webase.util;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.imageio.ImageIO;

public class CircularImages {

	//static String url = "http://avatar.csdn.net/3/1/7/1_qq_27292113.jpg?1488183229974";
	
	public static void main(String[] args) {
		try {
			// http://avatar.csdn.net/3/1/7/1_qq_27292113.jpg?1488183229974
			// 
			// 是产品地址 获取图片的流
			 BufferedImage url =    getUrlByBufferedImage("https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqoIXjSX9Nd1zIjicf7HrWiawRy50yp6saJ1PGotTHfb7LcQWYVwMsnBZff0DCq9228BDbv4f7Q3DDA/132");
			// 处理图片将其压缩成正方形的小图
			BufferedImage convertImage = scaleByPercentage(url, 100, 100);
			// 裁剪成圆形 （传入的图像必须是正方形的 才会 圆形 如果是长方形的比例则会变成椭圆的）
			convertImage = convertCircular(url);
			// 生成的图片位置
			String imagePath = "E:/QRCodeImage/Imag.png";
			ImageIO.write(convertImage, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath));
			System.out.println("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 缩小Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像
	 * 
	 * @param inputImage
	 * @param maxWidth
	 *            ：压缩后宽度
	 * @param maxHeight
	 *            ：压缩后高度
	 * @throws java.io.IOException
	 *             return
	 */
	public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight) throws Exception {
		// 获取原始图像透明度类型
		int type = inputImage.getColorModel().getTransparency();
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		// 开启抗锯齿
		RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// 使用高质量压缩
		renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		BufferedImage img = new BufferedImage(newWidth, newHeight, type);
		Graphics2D graphics2d = img.createGraphics();
		graphics2d.setRenderingHints(renderingHints);
		graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
		graphics2d.dispose();
		return img;
	}

	/**
	 * 通过网络获取图片
	 * 
	 * @param url
	 * @return
	 */
	public static BufferedImage getUrlByBufferedImage(String url) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			// 连接超时
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(25000);
			// 读取超时 --服务器响应比较慢,增大时间
			conn.setReadTimeout(25000);
			conn.setRequestMethod("GET");
			conn.addRequestProperty("Accept-Language", "zh-cn");
			conn.addRequestProperty("Content-type", "image/jpeg");
			conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
			conn.connect();
			BufferedImage bufImg = ImageIO.read(conn.getInputStream());
			conn.disconnect();
			return bufImg;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 传入的图像必须是正方形的 才会 圆形 如果是长方形的比例则会变成椭圆的
	 * 
	 * @param url
	 *            用户头像地址
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage convertCircular(BufferedImage bi1) throws IOException {
		
		// 透明底的图片
		BufferedImage bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1.getHeight());
		Graphics2D g2 = bi2.createGraphics();
		g2.setClip(shape);
		// 使用 setRenderingHint 设置抗锯齿
		g2.drawImage(bi1, 0, 0, null);
		// 设置颜色
		g2.setBackground(Color.green);
		g2.dispose();
		return bi2;
	}
}

