package com.pingan.scf.core.server.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class CryptionUtil2
{
  private static int RSA_KEY_LENGTH = 2048;
  private static String STRING_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  public static final String publicKeyString = "publicKeyString";
  public static final String privateKeyString = "privateKeyString";
  
  private static int getRandom(int count)
  {
    return (int)Math.round(Math.random() * count);
  }
  
  public static String getRandomString(int length)
  {
    StringBuffer sb = new StringBuffer();
    int len = STRING_POOL.length();
    for (int i = 0; i < length; i++) {
      sb.append(STRING_POOL.charAt(getRandom(len - 1)));
    }
    return sb.toString();
  }
  
  public static HashMap<String, String> getRsaRandomKey()
    throws NoSuchAlgorithmException, InvalidKeySpecException
  {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    keyPairGen.initialize(RSA_KEY_LENGTH);
    KeyPair keyPair = keyPairGen.generateKeyPair();
    
    RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
    
    RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
    HashMap<String, String> keyMap = new HashMap();
    
    keyMap.put("publicKeyString", Base64.encodeBase64URLSafeString(publicKey.getEncoded()));
    
    keyMap.put("privateKeyString", Base64.encodeBase64URLSafeString(privateKey.getEncoded()));
    
    return keyMap;
  }
  
  public static PrivateKey getRsaPrivateKey(String privateKeyData)
  {
    PrivateKey privateKey = null;
    try
    {
      byte[] decodeKey = Base64.decodeBase64(privateKeyData);
      
      PKCS8EncodedKeySpec x509 = new PKCS8EncodedKeySpec(decodeKey);
      
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      
      privateKey = keyFactory.generatePrivate(x509);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    catch (InvalidKeySpecException e)
    {
      e.printStackTrace();
    }
    return privateKey;
  }
  
  public static PublicKey getRsaPublicKey(String publicKeyData)
  {
    PublicKey publicKey = null;
    try
    {
      byte[] decodeKey = Base64.decodeBase64(publicKeyData);
      X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodeKey);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      publicKey = keyFactory.generatePublic(x509);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    catch (InvalidKeySpecException e)
    {
      e.printStackTrace();
    }
    return publicKey;
  }
  
  public static String rsaEncrypt(String data, Key key)
  {
    try
    {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
      cipher.init(1, key);
      byte[] encrypted = cipher.doFinal(data.getBytes());
      return new String(Base64.encodeBase64(encrypted));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String rsaDecrypt(String encrypted, Key key)
  {
    try
    {
      byte[] data = Base64.decodeBase64(encrypted);
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(2, key);
      byte[] decrypted = cipher.doFinal(data);
      return new String(decrypted);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String getAesRandomPassword()
  {
    return getRandomString(16);
  }
  
  public static String aesEncrypt(String textCode, String key)
    throws Exception
  {
    try
    {
      Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
      int blockSize = cipher.getBlockSize();
      
      byte[] dataBytes = textCode.getBytes("UTF-8");
      int plaintextLength = dataBytes.length;
      if (plaintextLength % blockSize != 0) {
        plaintextLength += blockSize - plaintextLength % blockSize;
      }
      byte[] plaintext = new byte[plaintextLength];
      System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
      
      SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
      IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());
      
      cipher.init(1, keyspec, ivspec);
      byte[] encrypted = cipher.doFinal(plaintext);
      
      return Base64.encodeBase64URLSafeString(encrypted);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static String aesDecrypt(String data, String key)
    throws Exception
  {
    try
    {
      byte[] encrypted = Base64.decodeBase64(data);
      
      Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
      SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
      IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());
      
      cipher.init(2, keyspec, ivspec);
      
      byte[] original = cipher.doFinal(encrypted);
      return new String(original, "UTF-8");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static Map<String, String> sort(Map<String, String> map)
  {
    Map<String, String> sortedMap = new TreeMap(new Comparator<String>()
    {
      public int compare(String key1, String key2)
      {
        return key1.compareTo(key2);
      }
    });
    sortedMap.putAll(map);
    return sortedMap;
  }


//  public static Map<String, String> sort(Map<String, String> map)
//  {
//    Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>()
//    {
//     public int compare(String key1, String key2){
//    	 return key1.compareTo(key2);
//     }
//
//	
//    }
//    );
//    sortedMap.putAll(map);
//    return sortedMap;
//  }
}
