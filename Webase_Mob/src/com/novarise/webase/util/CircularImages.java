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
			// �ǲ�Ʒ��ַ ��ȡͼƬ����
			 BufferedImage url =    getUrlByBufferedImage("https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqoIXjSX9Nd1zIjicf7HrWiawRy50yp6saJ1PGotTHfb7LcQWYVwMsnBZff0DCq9228BDbv4f7Q3DDA/132");
			// ����ͼƬ����ѹ���������ε�Сͼ
			BufferedImage convertImage = scaleByPercentage(url, 100, 100);
			// �ü���Բ�� �������ͼ������������ε� �Ż� Բ�� ����ǳ����εı����������Բ�ģ�
			convertImage = convertCircular(url);
			// ���ɵ�ͼƬλ��
			String imagePath = "E:/QRCodeImage/Imag.png";
			ImageIO.write(convertImage, imagePath.substring(imagePath.lastIndexOf(".") + 1), new File(imagePath));
			System.out.println("ok");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��СImage���˷�������Դͼ�񰴸�����ȡ��߶����������ź��ͼ��
	 * 
	 * @param inputImage
	 * @param maxWidth
	 *            ��ѹ������
	 * @param maxHeight
	 *            ��ѹ����߶�
	 * @throws java.io.IOException
	 *             return
	 */
	public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight) throws Exception {
		// ��ȡԭʼͼ��͸��������
		int type = inputImage.getColorModel().getTransparency();
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		// ���������
		RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// ʹ�ø�����ѹ��
		renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		BufferedImage img = new BufferedImage(newWidth, newHeight, type);
		Graphics2D graphics2d = img.createGraphics();
		graphics2d.setRenderingHints(renderingHints);
		graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
		graphics2d.dispose();
		return img;
	}

	/**
	 * ͨ�������ȡͼƬ
	 * 
	 * @param url
	 * @return
	 */
	public static BufferedImage getUrlByBufferedImage(String url) {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			// ���ӳ�ʱ
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(25000);
			// ��ȡ��ʱ --��������Ӧ�Ƚ���,����ʱ��
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
	 * �����ͼ������������ε� �Ż� Բ�� ����ǳ����εı����������Բ��
	 * 
	 * @param url
	 *            �û�ͷ���ַ
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage convertCircular(BufferedImage bi1) throws IOException {
		
		// ͸���׵�ͼƬ
		BufferedImage bi2 = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1.getHeight());
		Graphics2D g2 = bi2.createGraphics();
		g2.setClip(shape);
		// ʹ�� setRenderingHint ���ÿ����
		g2.drawImage(bi1, 0, 0, null);
		// ������ɫ
		g2.setBackground(Color.green);
		g2.dispose();
		return bi2;
	}
}

