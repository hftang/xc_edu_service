package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hftang
 * @date 2019-04-08 15:06
 * @desc
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {

        //定义key
        String key = "user_token:72dba235-a9f9-46e2-9eec-433e73a23001";
        //定义value
        Map<String, String> value = new HashMap<>();
        value.put("jwt", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU1NDc1MDg4OCwianRpIjoiNzJkYmEyMzUtYTlmOS00NmUyLTllZWMtNDMzZTczYTIzMDAxIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.UjoxkMp4nolwHXGNNv1xpwZ4zhBGKmcrZ70ME-bk5abELV-Zcqj15ZLI8IFkkJoNPyoD5iEzgx2j-z3giNwd07Ty_0dI6EkCzScIK3Hn8d5eTx5dAa7ptfauH8DXRQySokCDoztjmchF4wKdGS4iWIwWWG5ns4Rec9wQco_vbfymFe1_Qv4fYzUTlvKpUAzwmrhcv6pj5xtHamfTe9jrNyIkx0grY1sPJ5DLrqFoVIPPxgjdF30J0LPsPR4Dn8bGPRJdyBUvHm3mzuAgpMhBGYyTWfNgeew2Om0NmZs8vX6yqdZRoZapb-XT4j5GIcKFx0J6g5giyUeWz1rzUG1i5w");
        value.put("refresh_token", "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiI3MmRiYTIzNS1hOWY5LTQ2ZTItOWVlYy00MzNlNzNhMjMwMDEiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU1NDc1MDg4OCwianRpIjoiZjc0YzkzNGQtMmM1Mi00NDc3LTlkNWUtOGFhMWI4YWE2ZGY5IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.l_jE40zWguw401odwWEF91MFRr8p8pP2RW5uAzwHC2ZQ9zsGK1MzoI8tXsEhXc4qVurApmDajop_nkh8u6E00xhsnth4ou0E4sDEs4GeA-5fioRiXrvSDvAsVRA3olfiU5njIEK-C-dBdjl2CcAVZ6epZNtaMsCaROITmPELU0jUTE-XKPIgaKmHurz7X7Ie95GEHCXXxTAX_ycHafoyoeAN6TznJPMnp_6knWaoxq3wakPo--DtTQmxDvRjXl6ONWF7kkeCeRHLokbQInenFx98Qlcn4PBT9_mEX4RQSyLobl_eOF0tB--AysBzCXAQ9zZYMIKl8vUNx_TrmpUUZw");

        //转成字符串
        String jsonString = JSON.toJSONString(value);

        //校验是否存在
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expire);
        //存储数据
        stringRedisTemplate.boundValueOps(key).set(jsonString,30, TimeUnit.SECONDS);

        //获取数据
        String s = stringRedisTemplate.opsForValue().get(key);

        System.out.println(s);


    }
}
