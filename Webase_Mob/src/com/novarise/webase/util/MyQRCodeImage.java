package com.novarise.webase.util;

import java.awt.image.BufferedImage;

import jp.sourceforge.qrcode.data.QRCodeImage;



public class MyQRCodeImage implements QRCodeImage {
    BufferedImage bufferedImage;

    public MyQRCodeImage(BufferedImage bufferedImage){
        this.bufferedImage=bufferedImage;
    }

    public int getHeight() {
        return bufferedImage.getHeight();
    }

    public int getPixel(int arg0, int arg1) {
        return bufferedImage.getRGB(arg0, arg1);
    }

    public int getWidth() {
        return bufferedImage.getWidth();
    }

}


//public class J2SEImage  implements QRCodeImage {    
//        BufferedImage bufImg;    
//    
//        public J2SEImage(BufferedImage bufImg) {    
//            this.bufImg = bufImg;    
//        }    
//    
//        public int getWidth() {    
//            return bufImg.getWidth();    
//        }    
//    
//        public int getHeight() {    
//            return bufImg.getHeight();    
//        }    
//    
//        public int getPixel(int x, int y) {    
//            return bufImg.getRGB(x, y);    
//        }    
//	
//}
