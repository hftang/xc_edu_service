package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hftang
 * @date 2019-04-18 15:23
 * @desc
 */

@Service
public class AuthService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //从头取出jwt令牌

    public String getJwtFromHeader(HttpServletRequest request) {
        //取出头信息
        String authorization = request.getHeader("Authorization");

        if (StringUtils.isEmpty(authorization)) {
            return null;
        }
        if (!authorization.startsWith("Bearer ")) {
            return null;
        }
        String jwt = authorization.substring(7);
        return jwt;
    }

    //从cookie取出token

    public String getTokenFromCookie(HttpServletRequest request) {

        Map<String, String> cookieMap = CookieUtil.readCookie(request, "uid");
        String access_token = cookieMap.get("uid");
        if (StringUtils.isEmpty(access_token)) {
            return null;
        }
        return access_token;
    }

    //从redis取出jwt
    public long getExpire(String access_token) {
        String key = "user_token:" + access_token;
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);

       return expire;
    }


    //拒绝访问

}
