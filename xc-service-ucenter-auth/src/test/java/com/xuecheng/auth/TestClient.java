package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author hftang
 * @date 2019-04-08 15:51
 * @desc
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;


    //测试 springsecurity获取令牌


    @Test
    public void testclient01() {

        //从eureka中获取认证服务地址（因为认证服务spring security在认证服务中）
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //令牌申请的地址 此地址就是http://ip:port  "http://localhost:40400"
        URI uri = serviceInstance.getUri();

        String authUrl = uri + "/auth/oauth/token";

        //定义header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic("XcWebApp", "XcWebApp");
        header.add("Authorization", httpBasic);

        //定义 body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "password");
        body.add("username", "itcast");
        body.add("password", "123");


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);

        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

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


    }

    private String getHttpBasic(String clientId, String clientSecret) {

        String str = clientId + ":" + clientSecret;
        //将这个串进行base64编码
        byte[] encode = Base64Utils.encode(str.getBytes());

        return "Basic " + new String(encode);
    }


}
