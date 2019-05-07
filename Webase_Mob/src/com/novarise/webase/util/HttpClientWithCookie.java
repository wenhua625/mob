package com.novarise.webase.util;



import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

/**
 * ��������url����
 * ÿһ������Ҫ��ȡSet-Cookieͷ��ϢȻ����ʹ�õ�ʱ��ƴ������д��Cookie
 *
 * Cookie verCode ����ڶ�������ʱЯ�� 
 * Cookie JSESSIONID ������������SessionId����ǰ���󲻷��أ�
 * 
 * @author yuyu
 *
 */
public class HttpClientWithCookie {

    /**
     * ��ȡSet-Cookie д��session�У�Ȼ��������ͷ��Ϣ��ʱ��д��
     */
    public static byte[] doGet(HttpServletRequest request, String url, Map<String,String> param) throws Exception{
        //�������url���ز�Ϊ�յ�ʱ�򣬶�ȡ����
        InputStream  input=null;
        byte[] data = null;//����ַ����ݵ�����
        if(StringUtils.isNotBlank(url)){
            try{
                //���������ͷ��Ϣ
                URL urlInfo = new URL(url+'?'+getParam(param));
                URLConnection connection = urlInfo.openConnection();
                connection.addRequestProperty("Host", urlInfo.getHost());//����ͷ��Ϣ
                connection.addRequestProperty("Connection", "keep-alive");
                connection.addRequestProperty("Accept", "*/*");
                connection.addRequestProperty("Cookie", getSessionIncookie(request));//���û�ȡ��cookie
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
                connection.connect();
                // ��ȡ������Ӧͷ�ֶ�
                getCookieToSession(request,connection);
                //��ȡ�����������Ϣ
                input = connection.getInputStream();//���巵�����ݵĸ�ʽ           
                data =  new byte[input.available()];
                input.read(data);

            }catch(Exception e){
                throw new Exception("��ȡUrl����ʧ�ܣ�"+url,e);
            }finally {
                try{
                    input.close();
                }catch(Exception e){}
            }
        }
        return data;
       
    }

    /**
     * ��ȡpost���� 
     * @param request
     * @param string
     * @param param
     * @return
     */
    public static byte[] doPost(HttpServletRequest request, String url,
            Map<String, String> param) throws Exception{
        //�������url���ز�Ϊ�յ�ʱ�򣬶�ȡ����
        InputStream  input=null;
        PrintWriter out = null;
        byte[] data = null;//����ַ����ݵ�����
        if(StringUtils.isNotBlank(url)){
            try{
                //���������ͷ��Ϣ
                URL urlInfo = new URL(url);
                URLConnection connection = urlInfo.openConnection();
                connection.addRequestProperty("Host", urlInfo.getHost());//����ͷ��Ϣ
                connection.addRequestProperty("Connection", "keep-alive");
                connection.addRequestProperty("Accept", "*/*");
                connection.addRequestProperty("Cookie", getSessionIncookie(request));//���û�ȡ��cookie
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64)");
                // ����POST�������������������
                connection.setDoOutput(true);
                connection.setDoInput(true);
                // ��ȡURLConnection�����Ӧ�������
                out = new PrintWriter(connection.getOutputStream());
                // �����������
                out.print(getParam(param));
                // flush������Ļ���
                out.flush();
                // ��ȡ������Ӧͷ�ֶ�
                getCookieToSession(request,connection);
                //��ȡ�����������Ϣ
                input = connection.getInputStream();//���巵�����ݵĸ�ʽ           
                data =  new byte[input.available()];
                input.read(data);

            }catch(Exception e){
                throw new Exception("��ȡUrl����ʧ�ܣ�"+url,e);
            }finally {
                try{
                    input.close();
                }catch(Exception e){}
            }
        }
        return data;
    }


    /**
     * ��ȡ��session�б����cookie
     * @param request
     * @return
     */
    private static String getSessionIncookie(HttpServletRequest request) {
        String back="";
        HttpSession session = request.getSession();

        String  verCode=(String) session.getAttribute("verCode");
        String  JSESSIONID=(String) session.getAttribute("sid");

        System.out.println(verCode);
        System.out.println(JSESSIONID);

        if(verCode!=null){
            back=verCode;
        }

        if(JSESSIONID!=null){
            if(!StringUtils.isEmpty(back)){
                back+=" ";
            }
            back+=JSESSIONID;
        }

        System.out.println(back);
        return back;
    }

    /**
     * �������ͷ��Ϣ��ȡ��session��
     * @param request
     * @param connection
     */
    private static void getCookieToSession(HttpServletRequest request,
            URLConnection connection) {

        Map<String, List<String>> map = connection.getHeaderFields();
        for (String key : map.keySet()) {
            System.out.println(key + "--->" + map.get(key));
        }
        HttpSession session = request.getSession();

        List<String> cookie=map.get("Set-Cookie");
        if(cookie!=null){

            String verCode=getCookieBySet("verCode",cookie.get(0));

            String JSESSIONID=getCookieBySet("sid",cookie.get(0));

            if(verCode!=null){
                session.setAttribute("verCode",verCode);
            }

            if(JSESSIONID!=null){
                session.setAttribute("sid",JSESSIONID);
            }
        }

    }

    /**
     * ��ȡverCode=b5pcogZaFGikpAc1mQ+G5wOJGBWtXLsHafpf5wlgF5s=; Path=/SSODAO/; HttpOnly
     * ��cookie��Ϣ
     */
    public static String getCookieBySet(String name,String set){

        String regex=name+"=(.*?);";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =pattern.matcher(set);
        if(matcher.find()){
            return matcher.group();
        }
        return null;

    }

    /**
     * �������� key=123&v=456 ����
     * @param param
     * @return
     */
    public static String getParam(Map<String,String> param){
        StringBuilder str=new StringBuilder();
        int size=0;
        for (Map.Entry<String, String> m :param.entrySet())  {
            str.append(m.getKey());
            str.append("=");
            str.append(m.getValue());
            if(size<param.size()-1){
                str.append("&");
            }
            size++;
        }
        System.out.println(str.toString());
        return str.toString();
    }

}

