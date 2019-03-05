package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author hftang
 * @date 2019-03-04 14:04
 * @desc
 */
@SpringBootApplication
public class FreeMarkerTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreeMarkerTestApplication.class,args);


    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
