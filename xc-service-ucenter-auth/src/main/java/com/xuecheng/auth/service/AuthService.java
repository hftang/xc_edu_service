package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hftang
 * @date 2019-04-08 17:34
 * @desc
 */
@Service
@Slf4j
public class AuthService {

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;


    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //用户认证来申请令牌
    public AuthToken apply(String username, String password, String clientId, String clientSecret) {

        //请求springsecurity来生成令牌

        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLY_TOKEN_FAIL);
        }

        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //保存令牌到 redis中
        String content = JSON.toJSONString(authToken);

        //将令牌存入到 redis
        boolean b = this.saveToken(access_token, content, tokenValiditySeconds);

        if (!b) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }

        return authToken;
    }

    //保存令牌到 redis中

    /***
     *
     * @param access_token 用户身份令牌
     * @param content 内容就是AuthToken对象内容
     * @param ttl 过期时间
     * @return
     */
    private boolean saveToken(String access_token, String content, long ttl) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        //如果小于0 标示没查到
        return expire > 0;
    }

    /***
     * 从redis查询令牌
     * @param token
     * @return
     */
    public AuthToken getUserToken(String token) {
        String key = "user_token:" + token;
        //令牌名称
        String userTokenString = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(userTokenString)) {
            return null;
        }
        AuthToken authToken = null;
        try {
            log.error("userTokenString====>>>", userTokenString);
            authToken = JSON.parseObject(userTokenString, AuthToken.class);
        } catch (Exception e) {
            log.error("exception====>>>", e.getMessage());
            return null;
        }
        return authToken;
    }

    //从redis中删除token
    public boolean delToken(String access_token) {
        String name = "user_token:" + access_token;
        Boolean delete = stringRedisTemplate.delete(name);
        Long expire = stringRedisTemplate.getExpire(name, TimeUnit.SECONDS);


        return true;
    }

    //申请令牌
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {

        //从eureka中获取认证服务地址（因为认证服务spring security在认证服务中）
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //令牌申请的地址 此地址就是http://ip:port  "http://localhost:40400"
        URI uri = serviceInstance.getUri();

        String authUrl = uri + "/auth/oauth/token";

        //定义header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization", httpBasic);

        //定义 body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        ResponseEntity<Map> exchange = null;
        try {
            exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        } catch (Exception e) {
            String message = e.getMessage();
            e.printStackTrace();
            System.out.println(message);
        }

        //对错误发生的理解
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }

            }
        });

        //申请的令牌的信息
        Map bodytoken = exchange.getBody();
        System.out.println(bodytoken);

        if (bodytoken == null || bodytoken.get("access_token") == null || bodytoken.get("refresh_token") == null || bodytoken.get("jti") == null) {


            //看出现了说明错误，有错误就抛出去
            if (bodytoken != null && bodytoken.get("error_description") != null) {
                String error_description = (String) bodytoken.get("error_description");

                if (error_description.indexOf("UserDetailsService returned null") >= 0) {
                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                } else if (error_description.indexOf("坏的凭证") >= 0) {
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }
            }


            return null;
        }

        AuthToken authToken = new AuthToken();
        authToken.setAccess_token((String) bodytoken.get("jti"));//用户身份令牌
        authToken.setRefresh_token((String) bodytoken.get("refresh_token"));//刷新令牌
        authToken.setJwt_token((String) bodytoken.get("access_token"));//jwt令牌


        return authToken;
    }


    private String getHttpBasic(String clientId, String clientSecret) {

        String str = clientId + ":" + clientSecret;
        //将这个串进行base64编码
        byte[] encode = Base64Utils.encode(str.getBytes());

        return "Basic " + new String(encode);
    }
}
