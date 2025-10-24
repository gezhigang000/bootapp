package com.renlijia.bootapp.core.test;

import com.renlijia.keyservice.bouncycastle.util.RSAUtil;

import java.security.PublicKey;

public class Test {
    
    public static void main(String[] args){
        String k = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOrTd3ReKEiXz9oX7cM8Wdo/ajWa+juDY1cFp1IDAmqppnS8SHuWaGZZb1p694ZspeD5EQ56OlWJj+MBdfw7qPErNCTX/Ig7Gs1H6WUMUh6Ll5LJXjJ3/DeWD2TWtTq3PiODTS+x1RIKR/GUOmscP2pdxNED3Ng7N2svd1IP9tmwIDAQAB";
        try {
            PublicKey publicKey = RSAUtil.getPublicKey(k);
            System.out.println(RSAUtil.encrypt("s",k));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
