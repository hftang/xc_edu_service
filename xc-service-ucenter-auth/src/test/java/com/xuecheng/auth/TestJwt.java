package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;

/**
 * @author hftang
 * @date 2019-04-08 14:06
 * @desc
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {

    @Test
    public void testCreateJwt() {

        //秘钥库文件
        String keystore = "xc.keystore";
        //密码库的密码
        String keystore_password = "xuechengkeystore";
        //秘钥的别名
        String alias = "xckey";
        //秘钥的访问密码
        String key_password = "xuecheng";

        //秘钥库文件的路径
        ClassPathResource classPathResource = new ClassPathResource(keystore);
        //秘钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, keystore_password.toCharArray());
        //秘钥对 公钥 和 私钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, key_password.toCharArray());
        //获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //jwt的内容
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "汤化峰");
        String jsonString = JSON.toJSONString(hashMap);

        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(jsonString, new RsaSigner(aPrivate));

        //生成jwt令牌编码
        String jwtEncoded = jwt.getEncoded();

        System.out.println(jwtEncoded);


    }

    //校验jwt令牌
    @Test
    public void testVerify() {

        //公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnASXh9oSvLRLxk901HANYM6KcYMzX8vFPnH/To2R+SrUVw1O9rEX6m1+rIaMzrEKPm12qPjVq3HMXDbRdUaJEXsB7NgGrAhepYAdJnYMizdltLdGsbfyjITUCOvzZ/QgM1M4INPMD+Ce859xse06jnOkCUzinZmasxrmgNV3Db1GtpyHIiGVUY0lSO1Frr9m5dpemylaT0BV3UwTQWVW9ljm6yR3dBncOdDENumT5tGbaDVyClV0FEB1XdSKd7VjiDCDbUAUbDTG1fm3K9sx7kO1uMGElbXLgMfboJ963HEJcU01km7BmFntqI5liyKheX+HBUCD4zbYNPw236U+7QIDAQAB-----END PUBLIC KEY-----";
        //jwt令牌
        String jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoi5rGk5YyW5bOwIn0.G1EfCNYDD9NcNIyXPrEDu2b5DhnrXY3Q7M7vA2pFztGhAamOvpsUkWHf8bcjisnOzYkBPMXsuPu64H5DB3_f2BbDjmF7jh9Y9RmI7DzOKaZ4aCcoIsZ-F0TO_10pwUIh0ju7-FCTuM1tvvFJvAs81qp5mVfTor9F0Ciyp6Q00QqGrUDbPVc-C114Cx9Qxmi22Yzqj6St4KfAJhPoM6c9pUE_Q8d-d_8GfN85MsCIX_SdLz7XceUkWJ_OkoW9ksqALQHeHr_Um2mkGPwCAar_1REzq34UTSxc5Hz0atVTe86I2Vq9fD32hN0wSEtpvlEb3R_Cc28milAHIstefQknjA";


        //开始校验
        Jwt jwtObj = JwtHelper.decodeAndVerify(jwt, new RsaVerifier(publicKey));
        //拿到jwt令牌当中自定义的内容
        String claims = jwtObj.getClaims();

        System.out.println(claims);


    }


}
