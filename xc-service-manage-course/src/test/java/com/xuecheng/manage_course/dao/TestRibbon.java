package com.xuecheng.manage_course.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author hftang
 * @date 2019-03-12 13:39
 * @desc
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {
    @Autowired
    RestTemplate restTemplate;

    @Test
    public void test01(){

        String serviceId="XC-SERVICE-MANAGE-CMS";

        ResponseEntity<Map> map = restTemplate.getForEntity("http://"+serviceId + "/cms/page/get/5a754adf6abb500ad05688d9", Map.class);

        Map body = map.getBody();
        System.out.println(body);

    }
}
