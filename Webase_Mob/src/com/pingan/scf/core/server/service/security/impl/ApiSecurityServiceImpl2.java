package com.pingan.scf.core.server.service.security.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.pingan.scf.core.server.entity.api.ApiCryptionResult;
import com.pingan.scf.core.server.entity.api.ApiRequestType;
import com.pingan.scf.core.server.entity.api.ApiRequestVo;
import com.pingan.scf.core.server.entity.api.ApiResponseVo;
import com.pingan.scf.core.server.service.ApiSecurityUserService;
import com.pingan.scf.core.server.service.security.ApiSecurityService;
import com.pingan.scf.core.server.util.CryptionUtil2;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiSecurityServiceImpl2
  implements ApiSecurityService
{
  private static final Logger log = LoggerFactory.getLogger(ApiSecurityServiceImpl2.class);
  @Autowired
  ApiSecurityUserService userService;
  
  public ApiSecurityUserService getUserService()
  {
    return this.userService;
  }
  
  public void setUserService(ApiSecurityUserService userService)
  {
    this.userService = userService;
  }
  
  public ApiCryptionResult encapsulateClientRequest(String requestNo, Map<String, Object> data)
  {
    ApiCryptionResult result = new ApiCryptionResult();
    log.info("encapsulateClientRequest start");
    try
    {
      String channelId = this.userService.getChennelId();
      
      String strApiServerPublishedPublicKey = this.userService.getApiServerPublishedPublicKey(channelId);
      PublicKey apiServerPublishedPublicKey = CryptionUtil2.getRsaPublicKey(strApiServerPublishedPublicKey);
      
      ApiRequestVo apiRequestVo = new ApiRequestVo();
      apiRequestVo.setChnId(channelId);
      apiRequestVo.setRequestNo(requestNo);
      apiRequestVo.setRequestType(ApiRequestType.API.name());
      SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
      apiRequestVo.setRequestTime(sdf.format(new Date()));
      apiRequestVo.setData(null);
      apiRequestVo.setJsonData(JSONObject.toJSONString(data, new SerializerFeature[] { SerializerFeature.WriteMapNullValue }));
      apiRequestVo.setAuthCode("");
      
      String randomPassword = CryptionUtil2.getAesRandomPassword();
      
      String calculatedSign = calculateThirdRequestSign(apiRequestVo, randomPassword);
      
      apiRequestVo.setSign(calculatedSign);
      
      String apiRequestData = JSONObject.toJSONString(apiRequestVo, new SerializerFeature[] { SerializerFeature.WriteMapNullValue });
      
      String encryptedApiRequestData = CryptionUtil2.aesEncrypt(apiRequestData, randomPassword);
      
      String aesEncryptedKey = CryptionUtil2.rsaEncrypt(randomPassword, apiServerPublishedPublicKey);
      
      Map<String, Object> requestData = new HashMap();
      requestData.put("channelId", channelId);
      requestData.put("aesEncryptedKey", aesEncryptedKey);
      requestData.put("encryptedApiRequestData", encryptedApiRequestData);
      
      result.setData(requestData);
    }
    catch (Exception exp)
    {
      result.setCode("10199");
      
      result.setMessage("封装API请求参数错误(" + exp.getMessage() + ")");
      exp.printStackTrace();
    }
    return result;
  }
  
  public ApiRequestVo decapsulateClientRequest(Map<String, Object> params)
  {
    ApiRequestVo apiRequestVo = null;
    log.info("decapsulateClientRequest start");
    try
    {
      if ((params == null) || (!params.containsKey("channelId")) || (!params.containsKey("aesEncryptedKey")) || (!params.containsKey("encryptedApiRequestData"))) {
        return new ApiRequestVo("10220", "第三方请求参数格式有误");
      }
      String channelId = (String)params.get("channelId");
      String aesEncryptedKey = (String)params.get("aesEncryptedKey");
      String encryptedApiRequestData = (String)params.get("encryptedApiRequestData");
      if ((channelId == null) || (!StringUtils.isNumeric(channelId))) {
        return new ApiRequestVo("10230", "第三方请求参数中渠道号有误");
      }
      String strApiServerPublishedPrivateKey = this.userService.getApiServerPublishedPrivateKey(channelId);
      PrivateKey apiServerPublishedPrivateKey = CryptionUtil2.getRsaPrivateKey(strApiServerPublishedPrivateKey);
      
      String aesKey = CryptionUtil2.rsaDecrypt(aesEncryptedKey, apiServerPublishedPrivateKey);
      if (aesKey == null) {
        return new ApiRequestVo("10240", "第三方请求参数随机密码解密失败");
      }
      String requestText = CryptionUtil2.aesDecrypt(encryptedApiRequestData, aesKey);
      apiRequestVo = (ApiRequestVo)JSONObject.toJavaObject(JSON.parseObject(StringUtils.trim(requestText)), ApiRequestVo.class);
      
      String calculatedSign = calculateThirdRequestSign(apiRequestVo, aesKey);
   
      System.out.println("calculatedSign:"+calculatedSign);
      System.out.println("apiRequestVo.getSign():"+apiRequestVo.getSign());
      if (!StringUtils.equals(calculatedSign, apiRequestVo.getSign())) {
        return new ApiRequestVo("10250", "第三方请求数据可能被纂改");
      }
      SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
//      if (System.currentTimeMillis() - sdf.parse(apiRequestVo.getRequestTime()).getTime() > 180000L) {
//        return new ApiRequestVo("10260", "第三方请求时间超时");
//      }
      apiRequestVo.setData((Map)JSON.parseObject(apiRequestVo.getJsonData(), Map.class));
      apiRequestVo.setJsonData("");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return new ApiRequestVo("10299", "解密第三方请求数据失败(" + e.getMessage() + ")");
    }
    return apiRequestVo;
  }
  
  public ApiCryptionResult encapsulateApiServerResponse(ApiRequestVo apiRequestVo, Map<String, Object> responseData)
  {
    ApiCryptionResult result = new ApiCryptionResult();
    log.info("encapsulateOcResponse start");
    try
    {
      String strApiClientPublishedPublicKey = this.userService.getApiClientPublishedPublicKey(apiRequestVo.getChnId());
      PublicKey apiClientPublishedPublicKey = CryptionUtil2.getRsaPublicKey(strApiClientPublishedPublicKey);
      
      ApiResponseVo apiResponseVo = new ApiResponseVo();
      apiResponseVo.setChnId(apiRequestVo.getChnId());
      apiResponseVo.setRequestNo(apiRequestVo.getRequestNo());
      apiResponseVo.setTimestamp(System.currentTimeMillis());
      apiResponseVo.setData(null);
      apiResponseVo.setJsonData(JSONObject.toJSONString(responseData, new SerializerFeature[] { SerializerFeature.WriteMapNullValue }));
      apiRequestVo.setAuthCode("");
      
      String randomPassword = CryptionUtil2.getAesRandomPassword();
      
      String calculatedSign = calculateOcResponseSign(apiResponseVo, randomPassword);
      
      apiResponseVo.setSignature(calculatedSign);
      
      String apiResponseData = JSONObject.toJSONString(apiResponseVo, new SerializerFeature[] { SerializerFeature.WriteMapNullValue });
      
      String encryptedApiResponseData = CryptionUtil2.aesEncrypt(apiResponseData, randomPassword);
      
      String aesEncryptedKey = CryptionUtil2.rsaEncrypt(randomPassword, apiClientPublishedPublicKey);
      
      responseData = new HashMap();
      responseData.put("channelId", apiRequestVo.getChnId());
      responseData.put("aesEncryptedKey", aesEncryptedKey);
      responseData.put("encryptedApiRequestData", encryptedApiResponseData);
      
      result.setData(responseData);
    }
    catch (Exception exp)
    {
      result = new ApiCryptionResult("10399", "封装API应答数据错误(" + exp.getMessage() + ")");
      exp.printStackTrace();
    }
    return result;
  }
  
  public ApiCryptionResult decapsulateApiServerResponse(Map<String, Object> responseData)
  {
    ApiCryptionResult apiCryptionResult = new ApiCryptionResult();
    log.info("decapsulateOcResponse start");
    try
    {
      if ((responseData == null) || (!responseData.containsKey("channelId")) || (!responseData.containsKey("aesEncryptedKey")) || (!responseData.containsKey("encryptedApiRequestData"))) {
        return new ApiCryptionResult("10400", "API服务端应答数据格式有误");
      }
      String channelId = (String)responseData.get("channelId");
      String aesEncryptedKey = (String)responseData.get("aesEncryptedKey");
      String encryptedApiRequestData = (String)responseData.get("encryptedApiRequestData");
      if ((channelId == null) || (!StringUtils.isNumeric(channelId))) {
        return new ApiCryptionResult("10410", "API服务端应答数据中渠道号有误");
      }
      String strApiClientPublishedPrivateKey = this.userService.getApiClientPublishedPrivateKey(channelId);
      PrivateKey apiClientPublishedPrivateKey = CryptionUtil2.getRsaPrivateKey(strApiClientPublishedPrivateKey);
      
      String aesKey = CryptionUtil2.rsaDecrypt(aesEncryptedKey, apiClientPublishedPrivateKey);
      if (aesKey == null) {
        return new ApiCryptionResult("10440", "API服务端应答数据随机密码解密失败");
      }
      String responseText = CryptionUtil2.aesDecrypt(encryptedApiRequestData, aesKey);
      ApiResponseVo apiResponseVo = (ApiResponseVo)JSONObject.toJavaObject(JSON.parseObject(StringUtils.trim(responseText)), ApiResponseVo.class);
      
      String calculatedSign = calculateOcResponseSign(apiResponseVo, aesKey);
      
      
      System.out.println("calculatedSign--:"+calculatedSign);
      System.out.println("apiResponseVo.getSignature()加密:"+apiResponseVo.getSignature());
      if (!StringUtils.equals(calculatedSign, apiResponseVo.getSignature())) {
        return new ApiCryptionResult("10450", "API服务端应答数据可能被纂改");
      }
      if (System.currentTimeMillis() - apiResponseVo.getTimestamp() > 180000L) {
        return new ApiCryptionResult("10460", "API服务端应答数据超时");
      }
      apiCryptionResult.setData(apiResponseVo.getData());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return new ApiCryptionResult("10499", "解密API服务端应答数据失败(" + e.getMessage() + ")");
    }
    return apiCryptionResult;
  }
  
  private String calculateThirdRequestSign(ApiRequestVo apiRequestVo, String randomPassword)
    throws UnsupportedEncodingException
  {
   // Map<String, Object> sortedValueMap = sort(apiRequestVo.getData());
    
    StringBuilder sb = new StringBuilder("");
    sb.append("chnId=" + apiRequestVo.getChnId());
    sb.append("&requestType=" + apiRequestVo.getRequestType());
    sb.append("&requestNo=" + apiRequestVo.getRequestNo());
    sb.append("&requestTime=" + apiRequestVo.getRequestTime());
    sb.append("&authCode" + apiRequestVo.getAuthCode());
    sb.append("&data=" + apiRequestVo.getJsonData());
    
    return DigestUtils.sha256Hex(new String((sb.toString() + randomPassword)));
  }
  
  private String calculateOcResponseSign(ApiResponseVo apiResponseVo, String randomPassword)
    throws UnsupportedEncodingException
  {
 //   Map<String, Object> sortedValueMap = sort(apiResponseVo.getData());
    
    StringBuilder sb = new StringBuilder("");
    sb.append("chnId=" + apiResponseVo.getChnId());
    sb.append("&requestNo=" + apiResponseVo.getRequestNo());
    sb.append("&timestamp=" + apiResponseVo.getTimestamp());
    sb.append("&responseCode=" + apiResponseVo.getResponseCode());
    sb.append("&responseMessage=" + apiResponseVo.getResponseMessage());
    sb.append("&data=" + apiResponseVo.getJsonData());
    log.info("response=" + sb.toString());
    
    return DigestUtils.sha256Hex(new String((sb.toString() + randomPassword)));
  }
  
  private static Map<String, Object> sort(Map<String, Object> map)
  {
    Map<String, Object> sortedMap = new TreeMap<String, Object>(new Comparator<String>()
    {
     public int compare(String key1, String key2){
    	 return key1.compareTo(key2);
     }
    }
    );
    sortedMap.putAll(map);
    return sortedMap;
  }
  
  public String post(String apiUrl, String requestNo, Map<String, Object> data)
  {
    log.info("Api post start: {}", apiUrl);
    
    ApiCryptionResult requestData = encapsulateClientRequest(requestNo, data);
    if (!"0".equals(requestData.getCode())) {
      return JSONObject.toJSONString(requestData);
    }
    HttpHeaders headers = new HttpHeaders();
    MediaType type = MediaType.parseMediaType("application/json; charset=utf-8");
    
    headers.add("Accept", MediaType.APPLICATION_JSON.toString());
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(requestData.getData(), headers);
    
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(new FastJsonHttpMessageConverter());
    
    ResponseEntity<Map> responseData = restTemplate.postForEntity(apiUrl, requestEntity, Map.class, new Object[0]);
    
    ApiCryptionResult responseResult = decapsulateApiServerResponse((Map)responseData.getBody());
    log.info("Api post end: {} with result:{}:{}", new Object[] { apiUrl, responseResult.getCode(), responseResult.getMessage() });
   
      return JSONObject.toJSONString(responseResult);
   
  }
}
