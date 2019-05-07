package com.novarise.webase.util;




import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.*;
import java.nio.charset.Charset;
 
/**
 * @author YinHuiHua
 * @version v1.0
 * @create 2018/8/18
 **/
public class HttpFileUtils {
    public static void upload(String localFile,String url){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            // 把一个普通参数和文件上传给下面这个地址 是一个servlet
            HttpPost httpPost = new HttpPost(url);
       
            // 把文件转换成流对象FileBody
            FileBody bin = new FileBody(new File(localFile));
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    // 相当于<input type="file" name="file"/>
                    .addPart("file", bin)
                    // 相当于<input type="text" name="userName" value=userName>
                    .build();
          //  System.out.println(reqEntity.getContentLength());
            httpPost.setEntity(reqEntity);
            // 发起请求 并返回请求的响应
            response = httpClient.execute(httpPost);
            System.out.println("The response value of token:" + response.getFirstHeader("token"));
            // 获取响应对象
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                // 打印响应长度
                System.out.println("Response content length: " + resEntity.getContentLength());
                // 打印响应内容
                System.out.println(EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
            }
            // 销毁
            EntityUtils.consume(resEntity);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(httpClient != null){
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    public static void main(String[] args){
        upload("C:\\Users\\Administrator\\Desktop\\video\\3ccef36a08ee31fd178f633929dd66ca.mp4","http://ad-kcc.dderp.cn:10080/vod/upload");
    }
}

