package com.novarise.webase.xml;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

/*
 * Created on 2004-8-30
 *
 * ����ת��
 */


import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author YinHuiHua
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DataFormator {

        /**
        * ���ݴ�������ڷ���̫����
        *
        * @param cd ���� YYYYMMDD YYYY-MM-DD
        *
        * @return int Julian Day
        */
        public static int getJulianDay(String cd) {
                if (cd == null ) throw new NullPointerException("the import date is null") ;
                String date = cd.trim().replaceAll("-","").replaceAll(" ","");
                Calendar c = Calendar.getInstance();
                c.set(
                        (Integer.valueOf(date.substring(0, 4))).intValue(),
                        (Integer.valueOf(date.substring(4, 6))).intValue()-1,
                        (Integer.valueOf(date.substring(6, 8))).intValue());
                return c.get(6);
        }

        /**
        * �����ָ�����ȵ�ĳ���ַ�����ɵ��ַ���
        *
        * @param str Դ�ַ���
        * @param len ����
        *
        * @return String
        */
        public static String getString(String str, int len) {
                if (str == null)
                        return null;
                String s = str;
                while (s.length() < len) {
                        s = str + s;
                }
                return s.substring(0,len);
        }

        /**
        * �����ת����Ϊһ���ַ�������������ǰ����Ӷ�����ָ�����ַ�
        *
        * @param amount ���
        * @param len ����
        * @param digit ������С����λ��
        * @param fillString ����ַ���
        * @param point �Ƿ���ҪС����
        *
        * @return String
        */
        public static String getAmount(double amount, int len, int digit, String fillString ,boolean point){
                String am = String.valueOf(Math.round(amount * (Math.pow(10,digit))));
                if (am.equals("0")){
                        if(point) return  stringAddBOF("0."+getString("0", digit), len, fillString);
                        else return stringAddBOF("0"+getString("0", digit), len, fillString);
                }
                if (am.length()<=digit) am = stringAddBOF(am, digit+1, fillString);
                if (point) am = am.substring(0,am.length()-digit)+"."+am.substring(am.length()-digit,am.length());
                String result = getString(fillString,len-(am.length())) + am ;
                return result;
        }

        /**
        * ��һ���ַ����׼���һ���������ַ��������ӷ����ַ���β�����n���ַ�
        * ��Դ�ַ������ȴ��ڷ��س��ȣ��򷵻�ԭ�ַ���β�����n���ַ�
        *
        * @param str Դ�ַ���
        * @param len ����
        * @param fillString �����ַ���
        *
        * @return String ���ص��ַ���
        */
        public static String stringAddBOF(String str, int len, String fillString) {
                if (str == null) return str;
                if (str.length()>len) return str.substring(str.length()-len, str.length());
                String s = str;
                while (s.length()< len) {
                        s =  fillString + s;
                }
                return s.substring(s.length()-len, s.length());
        }

        /**
        * ��һ���ַ����׼���һ���������ַ��������ӷ����ַ���β�����n���ַ�
        * ��Դ�ַ������ȴ��ڷ��س��ȣ��򷵻�ԭ�ַ���β�����n���ַ�
        *
        * @param str Դ�ַ���
        * @param len ����
        * @param fillString �����ַ���
        *
        * @return String ���ص��ַ���
        */
        public static String stringAddEOF(String str, int len, String fillString) {
                if (str == null) return str;
                if (str.length()>len) return str.substring(0, len);
                String s = str;
                while (s.length()< len) {
                        s = s + fillString;
                }
                return s.substring(0, len);
        }

        /**
        * ��ĳ���ַ������׻�β���붨�����ַ�
        *
        * @param str Դ�ַ���
        * @param len ����
        *
        * @return String
        */
        public static String getDates(String str, int len) {
                if (str == null)
                        return null;
                String s = str;
                while (s.length() < len) {
                        s = str + s;
                }
                return s.substring(0,len);
        }

        /**
        * �����־�ȷ��ĳλ ��������
        *
        * @param str Դ�ַ���
        * @param len ����
        *
        * @return String
        */
        public static double number(double num, int dig) {
                BigDecimal b = new BigDecimal(num);
                return b.setScale(dig, BigDecimal.ROUND_HALF_UP).doubleValue();
        }


        public static  String parseNormalString(String numstr) {
          if(numstr.length() ==1)
           {
               numstr="0"+numstr;
          }
            return numstr;
         }

         public static String parseNotNullString(String str)
         {
             if(str==null||str.length() ==0)
             {
               return " ";
             }else
             {
               return str;
             }
         }

//date to String eg:2004-06-16---->20040616

     public static String dateToString(String date,String market)
     {
        StringBuffer r_date=new StringBuffer();
       if (date.length()==0 ) return "";

       int i=date.indexOf(market);


      while(i!=-1)
      {
         r_date.append(parseNormalString(date.substring(0,i))) ;
         date=date.substring(i+1);
         i=date.indexOf(market);
      }
      r_date.append(parseNormalString(date.substring(i+1))) ;
      return r_date.toString() ;
  }
}

