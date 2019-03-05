package com.xuecheng.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author hftang
 * @date 2019-03-04 13:33
 * @desc
 */
@Controller
@RequestMapping("/freemarker")
public class TestController {

    //先初始化 okhttp 不然会报错的
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/banner")
    public String bannerIndex(Map<String,Object> map){

        ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = entity.getBody();

       map.putAll(body);//设置模型数据

        System.out.printf("model:"+body);

        return "index_banner";
    }

    @RequestMapping("/test1")
    public String freemarker(Map<String,Object> map){
        map.put("name","hftangAAA");

        return "test1";
    }
}
