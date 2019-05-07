package com.novarise.webase.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
 

 
public class GenerateSequenceUtil {
 
   
    /** The FieldPosition. */
    private static final FieldPosition HELPER_POSITION = new FieldPosition(0);
 
    /** This Format for format the data to special format. */
    private final static Format dateFormat = new SimpleDateFormat("MMddHHmmssS");
    
    private final static Format datelongFormat = new SimpleDateFormat("yyMMddHHmmssS");
 
    /** This Format for format the number to special format. */
    private final static NumberFormat numberFormat = new DecimalFormat("000");
 
    /** This int is the sequence number ,the default value is 0. */
    private static int seq = 0;
 
    private static final int MAX = 999;
 
    /**
     * 时间格式生成序列
     * @return String
     */
    public static synchronized String generateSequenceNo() {
 
        Calendar rightNow = Calendar.getInstance();
 
        StringBuffer sb = new StringBuffer();
 
        dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
 
        numberFormat.format(seq, sb, HELPER_POSITION);
 
        if (seq == MAX) {
            seq = 0;
        } else {
            seq++;
        }
 
       
 
        return sb.toString();
    }
    
    /**
     * 时间格式生成序列
     * @return String
     */
    public static synchronized String generateTradeSequenceNo() {
 
        Calendar rightNow = Calendar.getInstance();
 
        StringBuffer sb = new StringBuffer();
 
        datelongFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
 
        numberFormat.format(seq, sb, HELPER_POSITION);
 
        if (seq == MAX) {
            seq = 0;
        } else {
            seq++;
        }
 
       
 
        return sb.toString();
    }
    
    
    
    
    public static String getRandom(){
    	Random r = new Random();
        long num = Math.abs(r.nextLong() % 10000000000L);
        String s = String.valueOf(num);
        for(int i = 0; i < 10-s.length(); i++){
            s = "0" + s;
        }
         
        return s;
    }
    
    public static String get8Random(){
    	Random r = new Random();
        long num = Math.abs(r.nextLong() % 100000000L);
        String s = String.valueOf(num);
        for(int i = 0; i < 10-s.length(); i++){
            s = "0" + s;
        }
         
        return s;
    }
    
    public static void main(String args[])
    {
    	System.out.println("dfdf:"+GenerateSequenceUtil.generateTradeSequenceNo());
    }
}
