package com.novarise.webase.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageCompress {
    /**
     * ������
     * 
     * @param path
     *            ��Ҫѹ����ͼƬ·��
     * @param fileName
     *            ѹ�����ͼƬ·��
     * @param toFileName
     *            ѹ�����ͼƬ����
     * @param scale
     *            ѹ������ ���ܴ���1,Ĭ��0.5
     * @param quality
     *            ѹ��Ʒ�ʽ���0.1~1.0֮��
     * @param width
     *            ѹ�����ͼƬ�Ŀ��
     * @param height
     *            ѹ�����ͼƬ�ĸ߶� ����ֵ��void
     */
   public static void imageCompress(File file, String path,
            String toFileName, float scale, float quality, int width, int height) {
        try { // ԭͼ·�� ԭͼ���� Ŀ��·�� ѹ������0.5 0.75 ԭͼ��� ԭͼ�߶�
            long start = System.currentTimeMillis();
            Image image = javax.imageio.ImageIO.read(file);
            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);
            if (scale > 0.5)
                scale = 0.5f;// Ĭ��ѹ����Ϊ0.5��ѹ����Խ�󣬶��ڴ�ҪȥԽ�ߣ����ܵ����ڴ����
            // ���������������ѹ����
            float realscale = getRatio(imageWidth, imageHeight, width, height);
            float finalScale = Math.min(scale, realscale);// ȡѹ������С�Ľ���ѹ��
            imageWidth = (int) (finalScale * imageWidth);
            imageHeight = (int) (finalScale * imageHeight);

            image = image.getScaledInstance(imageWidth, imageHeight,
                    Image.SCALE_AREA_AVERAGING);
            // Make a BufferedImage from the Image.
            BufferedImage mBufferedImage = new BufferedImage(imageWidth,
                    imageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = mBufferedImage.createGraphics();

            g2.drawImage(image, 0, 0, imageWidth, imageHeight, Color.white,
                    null);
            g2.dispose();

            float[] kernelData2 = { -0.125f, -0.125f, -0.125f, -0.125f, 2,
                    -0.125f, -0.125f, -0.125f, -0.125f };
            Kernel kernel = new Kernel(3, 3, kernelData2);
            ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            mBufferedImage = cOp.filter(mBufferedImage, null);

            FileOutputStream out = new FileOutputStream(path + toFileName);
           // System.out.println(path + toFileName);
            // JPEGEncodeParam param =
            // encoder.getDefaultJPEGEncodeParam(bufferedImage);
            // param.setQuality(0.9f, true);
            // encoder.setJPEGEncodeParam(param);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder
                    .getDefaultJPEGEncodeParam(mBufferedImage);
            param.setQuality(quality, true);// Ĭ��0.75
            encoder.setJPEGEncodeParam(param);
            encoder.encode(mBufferedImage);
            out.close();
            long end = System.currentTimeMillis();
        //    System.out.println("ͼƬ��" + fileName + "��ѹ��ʱ�䣺" + (end - start)
         //           + "ms");
        } catch (FileNotFoundException fnf) {
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
            //ioe.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
    }

/*    public static void imageCompress(String path, String fileName,
            String toFileName, float scale, int width, int height) {
        imageCompress(path, fileName, toFileName, scale, 0.75f, width, height);
    }*/

    private static float getRatio(int width, int height, int maxWidth,
            int maxHeight) {// ���ѹ�����ʵķ���
        float Ratio = 1.0f;
        float widthRatio;
        float heightRatio;
        widthRatio = (float) maxWidth / width;
        heightRatio = (float) maxHeight / height;
        if (widthRatio < 1.0 || heightRatio < 1.0) {
            Ratio = widthRatio <= heightRatio ? widthRatio : heightRatio;
        }
        return Ratio;
    }

    public static byte[] convertImage2Type(String imageFile, String imageType)
            throws Exception {// ͼƬ��ʽת��
        File inputFile = new File(imageFile);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BufferedImage input = ImageIO.read(inputFile);
        ImageIO.write(input, imageType, output);
        return output.toByteArray();
    }

    public static void convertImage2TypePng(String imageFile, String imageType)
            throws Exception {// ͼƬ��ʽת��
        File inputFile = new File(imageFile);
        int suffixIndex = imageFile.lastIndexOf(".");
        String suffix = imageFile.substring(suffixIndex + 1);
        if (!"png".equals(suffix)) {// ���ԭͼƬ�Ĳ���PNG��ʽ��ͼƬ
            String fileName = imageFile.substring(0, suffixIndex + 1) + "png";
            File output = new File(fileName);
            BufferedImage input = ImageIO.read(inputFile);
            ImageIO.write(input, imageType, output);
            // ת����ɾ��ԭ�ļ�
            if (inputFile.exists())
                inputFile.delete();
        }
    }

}
